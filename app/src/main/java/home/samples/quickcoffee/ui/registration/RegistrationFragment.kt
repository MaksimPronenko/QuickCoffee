package home.samples.quickcoffee.ui.registration

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import home.samples.quickcoffee.R
import home.samples.quickcoffee.databinding.FragmentRegistrationBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "RegistrationFragment"

@AndroidEntryPoint
class RegistrationFragment : Fragment() {

    companion object {
        private val REQUEST_PERMISSIONS: Array<String> = buildList {
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_NETWORK_STATE)
            add(Manifest.permission.INTERNET)
        }.toTypedArray()
    }

    @Inject
    lateinit var registrationViewModelFactory: RegistrationViewModelFactory
    private val viewModel: RegistrationViewModel by viewModels { registrationViewModelFactory }

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val launcher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { map ->
            if (map.values.all { it }) {
                Toast.makeText(context, "Permissions granted", Toast.LENGTH_SHORT).show()
                viewModel.handlePermissionsStateChange(true)
            } else {
                Toast.makeText(context, "Permissions not granted", Toast.LENGTH_SHORT).show()
                viewModel.handlePermissionsStateChange(false)
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        checkPermissions()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Log.d(TAG, "Функция onViewCreated() запущена")

        binding.emailEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && viewModel.emailState == null) {
                viewModel.email = binding.emailEditText.text.toString()
                viewModel.handleEnteredEmail()
            }
        }

        binding.emailEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "emailEditText - afterTextChanged(s) сработала. s = $s")
                if (viewModel.emailState != null) {
                    viewModel.email = s.toString()
                    viewModel.handleEnteredEmail()
                }
            }
        })

        binding.passwordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "passwordEditText - afterTextChanged(s) сработала. s = $s")
                if (viewModel.password1State != null) {
                    viewModel.password1 = s.toString()
                    viewModel.handleEnteredPasswords()
                }
            }
        })

        binding.repeatPasswordEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                Log.d(TAG, "repeatPasswordEditText - afterTextChanged(s) сработала. s = $s")
                if (viewModel.password2State != null) {
                    viewModel.password2 = s.toString()
                    viewModel.handleEnteredPasswords()
                }
            }
        })

        binding.registrationButton.setOnClickListener {
            viewModel.password1 = binding.passwordEditText.text.toString()
            viewModel.password2 = binding.repeatPasswordEditText.text.toString()
            viewModel.handleEnteredPasswords()
            binding.emailEditText.clearFocus()
            binding.passwordEditText.clearFocus()
            binding.repeatPasswordEditText.clearFocus()
            viewModel.register()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.registrationError.collect { error ->
                    if (error) {
                        Toast.makeText(
                            requireContext(),
                            getText(R.string.registration_error),
                            Toast.LENGTH_LONG
                        ).show()
                    } else {
                        Toast.makeText(
                            requireContext(),
                            viewModel.registrationResult?.token
                                ?: getString(R.string.token_is_missing),
                            Toast.LENGTH_LONG
                        ).show()
                        findNavController().navigate(
                            R.id.action_RegistrationFragment_to_LoginFragment
                        )
                    }
                }
            }
        }

        statesProcessing()
    }

    private fun statesProcessing() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            is RegistrationVMState.WorkingState -> {
                                showMainObjectsOrError(error = false)
                                binding.registrationButton.isEnabled = true
                                binding.emailLayout.boxBackgroundColor
                                emailFieldRefresh(state.emailState ?: true)
                                password1FieldRefresh(state.password1State ?: true)
                                password2FieldRefresh(state.password2State ?: true)
                            }

                            RegistrationVMState.RegistrationProcess -> {
                                showMainObjectsOrError(error = false)
                                binding.registrationButton.isEnabled = false
                            }

                            RegistrationVMState.NoPermission -> {
                                showMainObjectsOrError(error = true)
                            }
                        }
                    }
            }
        }
    }

    private fun showMainObjectsOrError(error: Boolean) {
        binding.noPermissionError.isVisible = error
        binding.emailTitle.isVisible = !error
        binding.emailLayout.isVisible = !error
        binding.passwordTitle.isVisible = !error
        binding.passwordLayout.isVisible = !error
        binding.repeatPasswordTitle.isVisible = !error
        binding.repeatPasswordLayout.isVisible = !error
        binding.registrationButton.isVisible = !error
    }

    private fun emailFieldRefresh(state: Boolean) {
        if (state) binding.emailLayout.boxBackgroundColor =
            requireContext().getColor(R.color.transparent)
        else binding.emailLayout.boxBackgroundColor =
            requireContext().getColor(R.color.error_background)
    }

    private fun password1FieldRefresh(state: Boolean) {
        if (state) binding.passwordLayout.boxBackgroundColor =
            requireContext().getColor(R.color.transparent)
        else binding.passwordLayout.boxBackgroundColor =
            requireContext().getColor(R.color.error_background)
    }

    private fun password2FieldRefresh(state: Boolean) {
        if (state) binding.repeatPasswordLayout.boxBackgroundColor =
            requireContext().getColor(R.color.transparent)
        else binding.repeatPasswordLayout.boxBackgroundColor =
            requireContext().getColor(R.color.error_background)
    }

    private fun checkPermissions() {
        val isAllGranted = REQUEST_PERMISSIONS.all { permission ->
            context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    permission
                )
            } == PackageManager.PERMISSION_GRANTED
        }
        if (isAllGranted) {
            Toast.makeText(context, "Permissions granted", Toast.LENGTH_SHORT).show()
        } else {
            launcher.launch(REQUEST_PERMISSIONS)
        }
        viewModel.handlePermissionsStateChange(isAllGranted)
    }
}
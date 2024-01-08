package home.samples.quickcoffee.ui.cafe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import home.samples.quickcoffee.R
import home.samples.quickcoffee.databinding.FragmentCafeBinding
import home.samples.quickcoffee.models.CafeItem
import home.samples.quickcoffee.ui.ViewModelState
import home.samples.quickcoffee.ui.adapters.CafeAdapter
import home.samples.quickcoffee.ui.menu.ARG_CAFE_ID
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ARG_TOKEN = "token"
private const val TAG = "CafeFragment"

@AndroidEntryPoint
class CafeFragment : Fragment() {

    @Inject
    lateinit var cafeViewModelFactory: CafeViewModelFactory
    private val viewModel: CafeViewModel by viewModels { cafeViewModelFactory }

    private var _binding: FragmentCafeBinding? = null
    private val binding get() = _binding!!

    private var tokenFr: String? = null

    private lateinit var cafeAdapter: CafeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tokenFr = arguments?.getString(ARG_TOKEN)
        viewModel.token = tokenFr
        Log.d(TAG, viewModel.token.toString())

        cafeAdapter = CafeAdapter(
            context = requireContext(),
            onClick = { cafeItem -> onCafeChosenClick(cafeItem) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCafeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Функция onViewCreated() запущена")

        viewModel.loadCafesLocations(viewModel.token)
        binding.cafesRecycler.adapter = cafeAdapter
        statesProcessing()
    }

    private fun statesProcessing() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                val infoText = "token = ${viewModel.token}"
                                binding.information.text = infoText
                            }

                            ViewModelState.Loaded -> {
                                binding.information.text = viewModel.cafeItemsList.toString()
                                viewModel.cafeFlow.onEach {
                                    cafeAdapter.setData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)

                                viewModel.firstLoading = false
                            }

                            ViewModelState.Error -> {
                                val infoText = "Error"
                                binding.information.text = infoText
                            }
                        }
                    }
            }
        }
    }

    private fun onCafeChosenClick(cafe: CafeItem) {
        val bundle =
            Bundle().apply {
                putInt(
                    ARG_CAFE_ID,
                    cafe.id
                )
            }
        findNavController().navigate(R.id.action_CafeFragment_to_MenuFragment, bundle)
    }
}
package home.samples.quickcoffee.ui.cafe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
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

    private lateinit var cafeAdapter: CafeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.token = arguments?.getString(ARG_TOKEN) ?: ""
        Log.d(TAG, viewModel.token)

        viewModel.clearOrder()      // Удаление данных прошлого заказа
        viewModel.startLocation()   // Определение местоположения устройства

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
        viewModel.loadCafesLocations()
        binding.cafesRecycler.adapter = cafeAdapter
        statesProcessing()
    }

    private fun statesProcessing() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            CafeVMState.Loading -> {
                                binding.progress.isVisible = true
                                binding.loadingError.isVisible = false
                                binding.cafesRecycler.isVisible = false
                            }

                            CafeVMState.Loaded -> {
                                binding.progress.isVisible = true
                                binding.loadingError.isVisible = false
                                binding.cafesRecycler.isVisible = true
                                viewModel.cafeFlow.onEach {
                                    cafeAdapter.setData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }

                            CafeVMState.DistancesLoaded -> {
                                binding.progress.isVisible = false
                                binding.loadingError.isVisible = false
                                binding.cafesRecycler.isVisible = true
                                viewModel.cafeFlow.onEach {
                                    cafeAdapter.setData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)

                            }

                            CafeVMState.Error -> {
                                binding.progress.isVisible = false
                                binding.loadingError.isVisible = true
                                binding.cafesRecycler.isVisible = false
                            }
                        }
                    }
            }
        }
    }

    private fun onCafeChosenClick(cafe: CafeItem) {
        val bundle =
            Bundle().apply {
                putString(
                    ARG_TOKEN,
                    viewModel.token
                )
                putInt(
                    ARG_CAFE_ID,
                    cafe.id
                )
            }
        findNavController().navigate(R.id.action_CafeFragment_to_MenuFragment, bundle)
    }

    override fun onStop() {
        super.onStop()
        viewModel.fusedClient.removeLocationUpdates(viewModel.locationCallback)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
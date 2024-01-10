package home.samples.quickcoffee.ui.menu

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
import dagger.hilt.android.AndroidEntryPoint
import home.samples.quickcoffee.databinding.FragmentMenuBinding
import home.samples.quickcoffee.ui.adapters.CafeMenuAdapter
import home.samples.quickcoffee.ui.cafe.ARG_TOKEN
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ARG_CAFE_ID = "cafe_id"
private const val TAG = "MenuFragment"

@AndroidEntryPoint
class MenuFragment : Fragment() {

    @Inject
    lateinit var menuViewModelFactory: MenuViewModelFactory
    private val viewModel: MenuViewModel by viewModels { menuViewModelFactory }

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var cafeMenuAdapter: CafeMenuAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.token = arguments?.getString(ARG_TOKEN)
        viewModel.id = arguments?.getInt(ARG_CAFE_ID)
        Log.d(TAG, "token = ${viewModel.token}, id = ${viewModel.id}")

        cafeMenuAdapter = CafeMenuAdapter(
            context = requireContext(),
            onMinusClick = { itemPosition -> viewModel.onMinusClick(itemPosition) },
            onPlusClick = { itemPosition -> viewModel.onPlusClick(itemPosition) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Функция onViewCreated() запущена")
        viewModel.loadCafeMenu()
        binding.cafesRecycler.adapter = cafeMenuAdapter
        statesProcessing()
    }

    private fun statesProcessing() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            MenuVMState.Loading -> {
                                binding.progress.isVisible = true
                                binding.loadingError.isVisible = false
                                binding.cafesRecycler.isVisible = false
                            }

                            MenuVMState.Loaded -> {
                                binding.progress.isVisible = true
                                binding.loadingError.isVisible = false
                                binding.cafesRecycler.isVisible = true
                                viewModel.menuFlow.onEach {
                                    cafeMenuAdapter.setData(it)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)

                            }

                            MenuVMState.Error -> {
                                binding.progress.isVisible = false
                                binding.loadingError.isVisible = true
                                binding.cafesRecycler.isVisible = false
                            }
                        }
                    }
            }
        }
    }
}
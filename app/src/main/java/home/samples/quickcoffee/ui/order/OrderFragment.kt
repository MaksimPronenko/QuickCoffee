package home.samples.quickcoffee.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import home.samples.quickcoffee.R
import home.samples.quickcoffee.databinding.FragmentOrderBinding
import home.samples.quickcoffee.ui.ViewModelState
import home.samples.quickcoffee.ui.adapters.OrderAdapter
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "OrderFragment"

@AndroidEntryPoint
class OrderFragment : Fragment() {

    @Inject
    lateinit var orderViewModelFactory: OrderViewModelFactory
    private val viewModel: OrderViewModel by viewModels { orderViewModelFactory }

    private var _binding: FragmentOrderBinding? = null
    private val binding get() = _binding!!

    private lateinit var orderAdapter: OrderAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        orderAdapter = OrderAdapter(
            context = requireContext(),
            onMinusClick = { itemPosition -> viewModel.onMinusClick(itemPosition) },
            onPlusClick = { itemPosition -> viewModel.onPlusClick(itemPosition) }
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Функция onViewCreated() запущена")
        viewModel.loadOrder()
        binding.orderRecycler.adapter = orderAdapter

        binding.payButton.setOnClickListener {
            Log.d(TAG, "order = ${viewModel.order}")
            val orderIsNotEmpty = viewModel.checkOrder()
            if (orderIsNotEmpty) {
                findNavController().navigate(R.id.action_OrderFragment_to_RegistrationFragment)
            }
            else {
                Toast.makeText(context, requireContext().getString(R.string.order_is_empty), Toast.LENGTH_SHORT).show()
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.orderChannel.collect { menu ->
                    orderAdapter.setData(menu)
                    menu.forEachIndexed { index, menuItem ->
                        Log.d(TAG, "item$index.quantity = ${menuItem.quantity}")
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
                            ViewModelState.Loading -> {
                                binding.progress.isVisible = true
                                binding.loadingError.isVisible = false
                                binding.orderRecycler.isVisible = false
                                binding.payButton.isVisible = false
                            }

                            ViewModelState.Loaded -> {
                                binding.progress.isVisible = false
                                binding.loadingError.isVisible = false
                                binding.orderRecycler.isVisible = true
                                binding.payButton.isVisible = true
                                viewModel.orderChannel.collect { menu ->
                                    orderAdapter.setData(menu)
                                    menu.forEachIndexed { index, menuItem ->
                                        Log.d(TAG, "item$index.quantity = ${menuItem.quantity}")
                                    }
                                }
                            }

                            ViewModelState.Error -> {
                                binding.progress.isVisible = false
                                binding.loadingError.isVisible = true
                                binding.orderRecycler.isVisible = false
                                binding.payButton.isVisible = false
                            }
                        }
                    }
            }
        }
    }
}
package home.samples.quickcoffee.ui.order

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.addCallback
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
import home.samples.quickcoffee.ui.cafe.ARG_TOKEN
import home.samples.quickcoffee.ui.menu.ARG_CAFE_ID
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        Log.d(TAG, "Функция onCreate() запущена")
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
        Log.d(TAG, "Функция onCreateView() запущена")
        _binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Функция onViewCreated() запущена")
        viewModel.loadOrder()
        binding.orderRecycler.adapter = orderAdapter

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Log.d(TAG, "Нажата системная кнопка Назад")
            val bundle =
                Bundle().apply {
                    putString(
                        ARG_TOKEN,
                        ""
                    )
                    putInt(
                        ARG_CAFE_ID,
                        0
                    )
                }
            findNavController().navigate(
                R.id.action_OrderFragment_to_MenuFragment,
                bundle
            )
        }

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
                viewModel.orderChannel.collect { order ->
                    order.forEachIndexed { index, orderItem ->
                        Log.d(TAG, "orderItem$index.quantity = ${orderItem.quantity}")
                    }
                    orderAdapter.setData(order)
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
                                binding.orderInformation.isVisible = false
                                binding.orderRecycler.isVisible = false
                                binding.payButton.isVisible = false
                            }

                            ViewModelState.Loaded -> {
                                binding.progress.isVisible = false
                                binding.loadingError.isVisible = false
                                binding.orderInformation.isVisible = true
                                binding.orderRecycler.isVisible = true
                                binding.payButton.isVisible = true
                                viewModel.orderFlow.onEach { order ->
                                    order.forEachIndexed { index, orderItem ->
                                        Log.d(TAG, "orderItem$index.quantity = ${orderItem.quantity}")
                                    }
                                    orderAdapter.setData(order)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }

                            ViewModelState.Error -> {
                                binding.progress.isVisible = false
                                binding.loadingError.isVisible = true
                                binding.orderInformation.isVisible = false
                                binding.orderRecycler.isVisible = false
                                binding.payButton.isVisible = false
                            }
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
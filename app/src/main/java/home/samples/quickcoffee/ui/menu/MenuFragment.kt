package home.samples.quickcoffee.ui.menu

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
import home.samples.quickcoffee.databinding.FragmentMenuBinding
import home.samples.quickcoffee.ui.ViewModelState
import home.samples.quickcoffee.ui.adapters.CafeMenuAdapter
import home.samples.quickcoffee.ui.cafe.ARG_TOKEN
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ARG_CAFE_ID = "cafeId"
private const val TAG = "MenuFragment"

@AndroidEntryPoint
class MenuFragment : Fragment() {

    @Inject
    lateinit var menuViewModelFactory: MenuViewModelFactory
    private val viewModel: MenuViewModel by viewModels { menuViewModelFactory }

    private var _binding: FragmentMenuBinding? = null
    private val binding get() = _binding!!

    private lateinit var cafeMenuAdapter: CafeMenuAdapter

    private var newCafeId: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "Функция onCreate() запущена")
        val newToken = arguments?.getString(ARG_TOKEN) ?: ""
        if (newToken != "") viewModel.token = newToken
//            viewModel.token = arguments?.getString(ARG_TOKEN) ?: ""
        newCafeId = arguments?.getInt(ARG_CAFE_ID) ?: 0
        Log.d(TAG, "token = ${viewModel.token}, cafeId = $newCafeId")

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
        Log.d(TAG, "Функция onCreateView() запущена")
        _binding = FragmentMenuBinding.inflate(inflater, container, false)
        viewModel.checkCafeId(newCafeId)
        newCafeId = 0
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d(TAG, "Функция onViewCreated() запущена")

        binding.menuRecycler.adapter = cafeMenuAdapter

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            Log.d(TAG, "Нажата системная кнопка Назад")
            val bundle =
                Bundle().apply {
                    putString(
                        ARG_TOKEN,
                        viewModel.token
                    )
                }
            findNavController().navigate(
                R.id.action_MenuFragment_to_CafeFragment,
                bundle
            )
        }

        binding.backButton.setOnClickListener {
            Log.d(TAG, "Нажата кнопка Назад")
            val bundle =
                Bundle().apply {
                    putString(
                        ARG_TOKEN,
                        viewModel.token
                    )
                }
            findNavController().navigate(
                R.id.action_MenuFragment_to_CafeFragment,
                bundle
            )
        }

        binding.paymentButton.setOnClickListener {
            val orderIsNotEmpty = viewModel.payment()
            if (orderIsNotEmpty) findNavController().navigate(R.id.action_MenuFragment_to_OrderFragment)
            else Toast.makeText(
                context,
                requireContext().getString(R.string.order_is_empty),
                Toast.LENGTH_SHORT
            ).show()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.menuChannel.collect { menu ->
                    menu.forEachIndexed { index, menuItem ->
                        Log.d(TAG, "item$index.quantity = ${menuItem.quantity}")
                    }
                    Log.d(TAG, "Загружаем данные из Channel")
                    cafeMenuAdapter.setData(menu)
                }
            }
        }

        statesProcessing()
    }

    private fun statesProcessing() {
        Log.d(TAG, "Функция statesProcessing() запущена")
//        viewLifecycleOwner.lifecycleScope
//            .launchWhenStarted {
//        viewLifecycleOwner.lifecycleScope.repeatOnLifecycle(Lifecycle.State.RESUMED) {
        viewLifecycleOwner.lifecycleScope.launch {
//            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                Log.d(TAG, "MenuVMState.Loading")
                                binding.progress.isVisible = true
                                binding.loadingError.isVisible = false
                                binding.menuRecycler.isVisible = false
                                binding.paymentButton.isVisible = false
                            }

                            ViewModelState.Loaded -> {
                                Log.d(TAG, "MenuVMState.Loaded")
                                binding.progress.isVisible = false
                                binding.loadingError.isVisible = false
                                binding.menuRecycler.isVisible = true
                                binding.paymentButton.isVisible = true
                                viewModel.menuFlow.onEach { menu ->
                                    menu.forEachIndexed { index, menuItem ->
                                        Log.d(
                                            TAG,
                                            "item$index.quantity = ${menuItem.quantity}"
                                        )
                                    }
                                    Log.d(TAG, "Загружаем данные из Flow")
                                    cafeMenuAdapter.setData(menu)
                                }.launchIn(viewLifecycleOwner.lifecycleScope)
                            }

                            ViewModelState.Error -> {
                                Log.d(TAG, "MenuVMState.Error")
                                binding.progress.isVisible = false
                                binding.loadingError.isVisible = true
                                binding.menuRecycler.isVisible = false
                                binding.paymentButton.isVisible = false
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
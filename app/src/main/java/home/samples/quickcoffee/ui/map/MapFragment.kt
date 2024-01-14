package home.samples.quickcoffee.ui.map

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import home.samples.quickcoffee.R
import home.samples.quickcoffee.databinding.FragmentMapBinding
import home.samples.quickcoffee.ui.ViewModelState
import home.samples.quickcoffee.ui.cafe.ARG_TOKEN
import home.samples.quickcoffee.ui.menu.ARG_CAFE_ID
import kotlinx.coroutines.launch
import org.osmdroid.api.IMapController
import org.osmdroid.tileprovider.tilesource.TileSourceFactory
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.overlay.ItemizedIconOverlay
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus
import org.osmdroid.views.overlay.OverlayItem
import org.osmdroid.views.overlay.ScaleBarOverlay
import javax.inject.Inject

private const val TAG = "MapFragment"

@AndroidEntryPoint
class MapFragment : Fragment() {

    @Inject
    lateinit var mapViewModelFactory: MapViewModelFactory
    private val viewModel: MapViewModel by viewModels { mapViewModelFactory }

    private var _binding: FragmentMapBinding? = null
    private val binding get() = _binding!!

    private lateinit var mapController: IMapController

    private lateinit var scaleBarOverlay: ScaleBarOverlay

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.token = arguments?.getString(ARG_TOKEN) ?: ""
        Log.d(TAG, viewModel.token)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mapController = binding.map.controller

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
                R.id.action_MapFragment_to_CafeFragment,
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
                R.id.action_MapFragment_to_CafeFragment,
                bundle
            )
        }

        binding.map.setBuiltInZoomControls(false)

        viewModel.loadCafesLocations()

//        viewModel.showMap()
        val dm: DisplayMetrics = requireContext().resources.displayMetrics
        scaleBarOverlay = ScaleBarOverlay(binding.map)
        scaleBarOverlay.setCentred(true)
        scaleBarOverlay.setTextSize(40.0F)
        scaleBarOverlay.setScaleBarOffset(dm.widthPixels / 2, 10)
        binding.map.overlays.add(scaleBarOverlay)

        viewLifecycleOwner.lifecycleScope.launch {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.state
                    .collect { state ->
                        when (state) {
                            ViewModelState.Loading -> {
                                Log.d(TAG, "ViewModelState.Loading")
                                binding.progress.isVisible = true
                                binding.map.isVisible = false
                                binding.loadingError.isVisible = false
                            }

                            ViewModelState.Loaded -> {
                                binding.progress.isVisible = false
                                binding.map.isVisible = true
                                binding.loadingError.isVisible = false
                                Log.d(TAG, "ViewModelState.Loaded")
                                binding.map.setTileSource(TileSourceFactory.MAPNIK)
                                mapController.setZoom(11.0)
                                mapController.setCenter(
                                    GeoPoint(
                                        viewModel.latitudeCenter!!,
                                        viewModel.longitudeCenter!!
                                    )
                                )
                                Log.d(
                                    TAG,
                                    "Установлены координаты центра: (${viewModel.latitudeCenter}; ${viewModel.longitudeCenter})."
                                )
                                showMarkers()
                            }

                            ViewModelState.Error -> {
                                Log.d(TAG, "ViewModelState.Error")
                                binding.progress.isVisible = false
                                binding.map.isVisible = false
                                binding.loadingError.isVisible = true
                            }
                        }
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.map.onResume()
    }

    override fun onPause() {
        super.onPause()
        binding.map.onPause()
    }

    private fun showMarkers() {
        if (viewModel.cafeList.isNotEmpty()) {
            val items = ArrayList<OverlayItem>()
            viewModel.cafeList.forEach { cafe ->
                val name = cafe.name
                val longitude = cafe.point.longitude.toDouble()
                val latitude = cafe.point.latitude.toDouble()

                val marker: Drawable? =
                    AppCompatResources.getDrawable(requireContext(), R.drawable.cafe_marker)
                val overlayItem = OverlayItem(
                    name,
                    "",
                    GeoPoint(latitude, longitude)
                )
                overlayItem.setMarker(marker)
                items.add(
                    overlayItem
                )
            }
            val overlay = ItemizedOverlayWithFocus(items, object :
                ItemizedIconOverlay.OnItemGestureListener<OverlayItem> {
                override fun onItemSingleTapUp(index: Int, item: OverlayItem): Boolean {
                    Log.d(
                        TAG,
                        "Короткое нажатие на маркер кафе"
                    )
                    return true
                }

                override fun onItemLongPress(index: Int, item: OverlayItem): Boolean {
                    Log.d(
                        TAG,
                        "Длинное нажатие на маркер кафе"
                    )
                    onCafeChosenClick(index)
                    return false
                }
            }, context)
            overlay.setFocusItemsOnTap(true)
            binding.map.overlays.clear()
            binding.map.overlays.add(scaleBarOverlay)
            binding.map.overlays.add(overlay)
            binding.map.invalidate()
            Log.d(
                "MarkersTest",
                "Сработала showMarkers()."
            )
        } else {
            Log.d(
                "MarkersTest",
                "Не сработала showMarkers(), из-за пустого списка маркеров."
            )
        }
    }

    private fun onCafeChosenClick(index: Int) {
        val id = viewModel.cafeList[index].id
        Log.d(TAG, "onCafeChosenClick(), где cafe.id = $id")
        val bundle =
            Bundle().apply {
                putString(
                    ARG_TOKEN,
                    viewModel.token
                )
                putInt(
                    ARG_CAFE_ID,
                    id
                )
            }
        findNavController().navigate(R.id.action_MapFragment_to_MenuFragment, bundle)
    }
}
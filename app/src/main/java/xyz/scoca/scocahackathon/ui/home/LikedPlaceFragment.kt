package xyz.scoca.scocahackathon.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.libraries.places.api.model.Place
import xyz.scoca.scocahackathon.databinding.FragmentLikedPlaceBinding
import xyz.scoca.scocahackathon.model.PlaceData
import xyz.scoca.scocahackathon.ui.home.adapter.LikedPlaceAdapter
import xyz.scoca.scocahackathon.util.extension.OnItemClickListener
import xyz.scoca.scocahackathon.util.extension.snack
import xyz.scoca.scocahackathon.viewmodel.LikedPlaceViewModel

class LikedPlaceFragment : Fragment() {
    private lateinit var binding: FragmentLikedPlaceBinding
    private lateinit var viewModel: LikedPlaceViewModel
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLikedPlaceBinding.inflate(inflater, container, false)
        viewModel = ViewModelProvider(requireActivity()).get(LikedPlaceViewModel::class.java)

        viewModel.readAllPlaceData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
            } else {
                binding.tvEmpty.visibility = View.INVISIBLE
            }
            setRecyclerViewAdapter(it)
        }

        return binding.root
    }

    private fun setRecyclerViewAdapter(placeList: List<PlaceData>) {

        val mLayoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.rvLikedPlace.layoutManager = mLayoutManager
        binding.rvLikedPlace.adapter =
            LikedPlaceAdapter(requireContext(), placeList, object : OnItemClickListener {
                override fun onClick(position: Int) {
                    delete(placeList[position])
                }
            })
    }

    private fun delete(placeData: PlaceData) {
        placeData.let {
            viewModel.deletePlace(placeData)
            snack(requireView(), "${placeData.place_name} Deleted.")
        }
    }
}
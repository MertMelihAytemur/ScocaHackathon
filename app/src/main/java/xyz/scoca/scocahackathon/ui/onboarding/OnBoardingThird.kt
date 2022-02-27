package xyz.scoca.scocahackathon.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import xyz.scoca.scocahackathon.R
import xyz.scoca.scocahackathon.data.local.ClientPreferences
import xyz.scoca.scocahackathon.databinding.FragmentOnBoardingThirdBinding

class OnBoardingThird : Fragment() {
    private lateinit var binding : FragmentOnBoardingThirdBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingThirdBinding.inflate(inflater,container,false)

        binding.btnFinish.setOnClickListener {
            ClientPreferences(requireContext()).setOnBoardingState(true)
            findNavController().navigate(R.id.action_viewPagerFragment_to_homeFragment)
        }
        return binding.root
    }
}
package xyz.scoca.scocahackathon.ui.splash

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import xyz.scoca.scocahackathon.R
import xyz.scoca.scocahackathon.data.local.ClientPreferences
import xyz.scoca.scocahackathon.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment() {
    private lateinit var binding : FragmentSplashScreenBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSplashScreenBinding.inflate(inflater,container,false)

        binding.splashAnimation.speed = 1.5f
        Handler().postDelayed({
            if(ClientPreferences(requireContext()).isOnBoardingDone()){
                findNavController().navigate(R.id.action_splashScreenFragment_to_homeFragment)
            }else{
                findNavController().navigate(R.id.action_splashScreenFragment_to_viewPagerFragment)
            }
        },4000)
        return binding.root
    }

}
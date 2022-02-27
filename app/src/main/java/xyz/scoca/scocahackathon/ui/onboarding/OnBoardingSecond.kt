package xyz.scoca.scocahackathon.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import xyz.scoca.scocahackathon.databinding.FragmentOnBoardingSecondBinding

class OnBoardingSecond : Fragment() {
    private lateinit var binding : FragmentOnBoardingSecondBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingSecondBinding.inflate(inflater,container,false)
        return binding.root
    }

}
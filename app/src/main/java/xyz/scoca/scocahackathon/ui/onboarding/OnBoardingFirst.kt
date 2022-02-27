package xyz.scoca.scocahackathon.ui.onboarding

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import xyz.scoca.scocahackathon.databinding.FragmentOnBoardingFirstBinding

class OnBoardingFirst : Fragment() {
    private lateinit var binding : FragmentOnBoardingFirstBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOnBoardingFirstBinding.inflate(inflater,container,false)
        return binding.root
    }

}
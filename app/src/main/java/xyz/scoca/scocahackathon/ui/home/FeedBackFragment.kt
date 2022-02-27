package xyz.scoca.scocahackathon.ui.home

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import xyz.scoca.scocahackathon.R
import xyz.scoca.scocahackathon.databinding.FragmentFeedBackBinding
import xyz.scoca.scocahackathon.util.extension.snack

class FeedBackFragment : Fragment() {
    private lateinit var binding : FragmentFeedBackBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFeedBackBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initListener()
        binding.btnFeedback.setOnClickListener {
            sendFeedBack()
        }
    }

    private fun sendFeedBack(){
        val message : String = binding.etFeedback.text.toString()
        val databaseReference  = FirebaseFirestore.getInstance()
        val feedBack : MutableMap<String,Any> = HashMap()
        feedBack["feedBack"] = message
        feedBack["feedback_point"] = binding.rbFeedback.rating.toString()

        databaseReference.collection("feedbacks")
            .add(feedBack)
            .addOnSuccessListener {
                snack(requireView(),"Feedback sent successfully")
                findNavController().navigate(R.id.action_feedBackFragment_to_homeFragment)
            }
            .addOnFailureListener {
                snack(requireView(),it.message.toString())
                binding.etFeedback.text.clear()
            }
    }

    private fun initListener(){
        binding.etFeedback.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                checkFields()
            }
        })
    }

    private fun checkFields() {
        if (!binding.etFeedback.text.isNullOrEmpty()) {
            binding.btnFeedback.isEnabled = true
            binding.btnFeedback.alpha = 1F
        } else {
            binding.btnFeedback.isEnabled = false
            binding.btnFeedback.alpha = 0.2F
        }
    }
}
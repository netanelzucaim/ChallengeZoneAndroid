package com.idz.ChallengeZone

import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.idz.ChallengeZone.adapter.postAdapter.PostsRecyclerAdapter
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.Post
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.ktx.Firebase
import com.idz.ChallengeZone.databinding.FragmentAskAiBinding
import com.idz.ChallengeZone.databinding.FragmentProfileBinding
import com.idz.ChallengeZone.model.User
import com.squareup.picasso.Picasso
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch

class AskAiFragment : Fragment() {

    private var binding: FragmentAskAiBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentAskAiBinding.inflate(inflater, container, false)
        val view = inflater.inflate(R.layout.fragment_ask_ai, container, false)

        modelCall()
        return binding?.root
    }

    fun modelCall() {
        var textV = binding?.demotext

        val apikey = "AIzaSyCdqlIZetWBvJ_wsJ_1Q8CIqBX20ml5bgk"
//        val generativeModel = Firebase.vertexAI.generativeModel(
//            modelName = "gemini-2.0-flash-001", // Replace with a valid model name
//            apiKey = apikey
//        )
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-pro-latest",
            apiKey = apikey
        )

        val prompt = "Write a story about a magic backpack."
//        val inputContent = content() {
//            text("Does this look store-bought or homemade?")
//        }
        MainScope().launch {
            val response = generativeModel.generateContent(prompt)
            textV?.text = response.text
            Log.d("TAG", "response is ${response.toString()}")
        }
    }
}
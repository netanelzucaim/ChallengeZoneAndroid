package com.idz.ChallengeZone

import  android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.idz.ChallengeZone.adapter.PostsRecyclerAdapter
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.Post
import android.widget.ProgressBar
import androidx.lifecycle.ViewModelProvider
import com.idz.ChallengeZone.databinding.FragmentPostsListBinding
import androidx.fragment.app.viewModels

interface OnItemClickListenerPosts {
    fun onItemClick(position: Int)
    fun onItemClick(post: Post?)
}

class PostsListFragment : Fragment() {

    private val viewModel: PostsListViewModel by viewModels()
    private var adapter: PostsRecyclerAdapter? = null
    private var binding: FragmentPostsListBinding? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostsListBinding.inflate(inflater, container, false)
//        viewModel = ViewModelProvider(this)[PostsListViewModel::class.java]

        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_posts_list, container, false)

        binding?.recyclerView?.setHasFixedSize(true)

        val layoutManager = LinearLayoutManager(context)

        binding?.recyclerView?.layoutManager = layoutManager

//        adapter = PostsRecyclerAdapter(viewModel?.posts)
        adapter = PostsRecyclerAdapter(viewModel.posts.value)
        viewModel.posts.observe(viewLifecycleOwner) {
            adapter?.update(it)
            adapter?.notifyDataSetChanged()
            binding?.progressBar?.visibility = View.GONE
        }
        binding?.swipeToRefresh?.setOnRefreshListener(viewModel::refreshAllPosts)
        Model.shared.loadingState.observe(viewLifecycleOwner) { state ->
            binding?.swipeToRefresh?.isRefreshing = state == Model.LoadingState.LOADING
        }

        adapter?.listener = object : OnItemClickListenerPosts{
            override fun onItemClick(position: Int) {
                Log.d("TAG", "On click Activity listener on position $position")
            }

            override fun onItemClick(post: Post?) {
//
//                val action = PostsListFragmentDirections.actionPostsListFragmentToPostDetailsFragment(post!!)
//                Navigation.findNavController(view).navigate(action)


                post?.let {
//                    val action = PostsListFragmentDirections.actionPostsListFragmentToPostDetailsFragment(it)
//                    binding?.root?.let {
//                        Navigation.findNavController(it).navigate(action)
//                    }
                }
            }
        }
//        recyclerView.adapter = adapter
        binding?.recyclerView?.adapter = adapter

//        val action = PostsListFragmentDirections.actionGlobalAddPostFragment()
//        binding?.addPostButton?.setOnClickListener(Navigation.createNavigateOnClickListener(action))
        return binding?.root
    }


override fun onResume() {
    super.onResume()
    getAllPosts()
}
override fun onDestroy() {
    super.onDestroy()
    binding = null
}
private fun getAllPosts() {
    binding?.progressBar?.visibility = View.VISIBLE
//    Model.shared.getAllPosts {
//        viewModel?.updatePosts(it)
//        adapter?.set(it)
//        adapter?.notifyDataSetChanged()
//
//        binding?.progressBar?.visibility = View.GONE
//    }
    viewModel.refreshAllPosts()
}
}
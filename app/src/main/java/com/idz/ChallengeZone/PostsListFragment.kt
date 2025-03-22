package com.idz.ChallengeZone

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.idz.ChallengeZone.adapter.postAdapter.PostsRecyclerAdapter
import com.idz.ChallengeZone.model.Post
import com.idz.ChallengeZone.databinding.FragmentPostsListBinding
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.viewmodel.PostViewModel

interface OnItemClickListenerPosts {
    fun onItemClick(position: Int)
    fun onItemClick(post: Post?)
}

class PostsListFragment : Fragment() {

    private val viewModel: PostViewModel by viewModels()
    private var adapter: PostsRecyclerAdapter? = null
    private var binding: FragmentPostsListBinding? = null

    private fun <T> LiveData<T>.observe(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentPostsListBinding.inflate(inflater, container, false)

        binding?.recyclerView?.setHasFixedSize(true)
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)

        adapter = PostsRecyclerAdapter(viewModel.posts.value, viewLifecycleOwner, "home")
        viewModel.posts.observe(viewLifecycleOwner) {
            adapter?.update(it)
            adapter?.notifyDataSetChanged()
            binding?.progressBar?.visibility = View.GONE
        }
        binding?.swipeToRefresh?.setOnRefreshListener(viewModel::refreshAllPosts)
        viewModel.loadingState.observe(viewLifecycleOwner) { state ->
            binding?.swipeToRefresh?.isRefreshing = state == Model.LoadingState.LOADING
        }

        adapter?.listener = object : OnItemClickListenerPosts {
            override fun onItemClick(position: Int) {
                Log.d("TAG", "On click Activity listener on position $position")
            }

            override fun onItemClick(post: Post?) {
                // Handle item click
            }
        }
        binding?.recyclerView?.adapter = adapter

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
        viewModel.refreshAllPosts()
        viewModel.posts.observe(viewLifecycleOwner) {
            adapter?.update(it)
            adapter?.notifyDataSetChanged()
            binding?.progressBar?.visibility = View.GONE
        }
    }
}
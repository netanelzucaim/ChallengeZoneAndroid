package com.idz.ChallengeZone.adapter.postAdapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.idz.ChallengeZone.model.Post

import  com.idz.ChallengeZone.OnItemClickListenerPosts
import com.idz.ChallengeZone.databinding.PostListRowBinding

//class PostsRecyclerAdapter(private val posts: MutableList<Post>?): RecyclerView.Adapter<PostViewHolder>() {
class PostsRecyclerAdapter(private var posts: List<Post>?): RecyclerView.Adapter<PostViewHolder>() {
        var listener: OnItemClickListenerPosts? = null
        fun update(posts: List<Post>?) {
            this.posts = posts
        }

    fun set(posts: List<Post>?) {
            this.posts = posts
        }
        override fun getItemCount(): Int = posts?.size ?: 0

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
            val inflaor = LayoutInflater.from(parent.context)
            val binding = PostListRowBinding.inflate(inflaor, parent, false)

            return PostViewHolder(binding, listener)
        }

        override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
            holder.bind(
                post = posts?.get(position),
                position = position
            )
        }
    }
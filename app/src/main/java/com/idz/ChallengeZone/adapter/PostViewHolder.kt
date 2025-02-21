package com.idz.ChallengeZone.adapter

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.idz.ChallengeZone.OnItemClickListenerPosts
import com.idz.ChallengeZone.R
import com.idz.ChallengeZone.databinding.PostListRowBinding
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.Post
import com.squareup.picasso.Picasso

class PostViewHolder(
    private val binding: PostListRowBinding,
    listener: OnItemClickListenerPosts?
): RecyclerView.ViewHolder(binding.root) {
    private var post: Post? = null

    init {
        itemView.setOnClickListener {
            Log.d("TAG", "On click listener on position $adapterPosition")
            listener?.onItemClick(post)
        }
    }

    fun bind(post: Post?, position: Int) {
        this.post = post
        binding.senderTextView?.text = post?.sender
        binding.contentTextView?.text = post?.content
//        Model.shared.users.observeForever { users ->
//            users?.forEach { user ->
//                Log.d("TAG", "User: ${user.userName}")
//            }
//        }
        post?.postPic?.let {
            if (it.isNotBlank()) {
                Picasso.get()
                    .load(it)
                    .placeholder(R.drawable.avatar)
                    .into(binding.postPicImageView)
            }
        }

        Model.shared.getOtherUser(post?.sender) { user ->
            user?.avatarUrl?.let { avatarUrl ->
                if (avatarUrl.isNotBlank()) {
                    Picasso.get()
                        .load(avatarUrl)
                        .placeholder(R.drawable.avatar)
                        .into(binding.avatarImageView)
                }
            }
        }
    }
}
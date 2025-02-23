package com.idz.ChallengeZone.adapter.postAdapter

import android.util.Log
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
        binding.dateTextView?.text = post?.lastUpdated?.let { java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(it)) }//            users?.forEach { user ->
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
        Model.shared.users.observeForever { users ->
            users?.forEach { user ->
                Log.d("TAG", "User from dao is: ${user.json}")

            }
        }
//        Model.shared.getOtherUser(post?.sender) { user ->
//            user?.avatarUrl?.let { avatarUrl ->
//                if (avatarUrl.isNotBlank()) {
//                    Picasso.get()
//                        .load(avatarUrl)
//                        .placeholder(R.drawable.avatar)
//                        .into(binding.avatarImageView)
//                }
//            }
//        }
        Model.shared.otherUser.observeForever { user ->
                Log.d("TAG", "User from dao of lagziel is: ${user!!.json}")
        }
//        Model.shared.getLoggedUser().observeForever { user ->
//            Log.d("TAG", "current user from dao is: ${user?.json}")
//        }
        Model.shared.getOtherUser(post?.sender).observeForever { user ->
            Log.d("TAG", "User from dao avatarUrl is: ${user?.json}")
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
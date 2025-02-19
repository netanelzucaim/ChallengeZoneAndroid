package com.idz.ChallengeZone.adapter

import android.util.Log
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.idz.ChallengeZone.OnItemClickListenerPosts
import com.idz.ChallengeZone.R
import com.idz.ChallengeZone.databinding.PostListRowBinding
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
//                listener?.onItemClick(adapterPosition)
                listener?.onItemClick(post)
            }
        }

        fun bind(post: Post?, position: Int) {
            this.post = post
            binding.senderTextView?.text = post?.sender
            binding.contentTextView?.text = post?.content

            post?.postPic?.let {
                if (it.isNotBlank()) {
                    Picasso.get()
                        .load(it)
                        .placeholder(R.drawable.avatar)
                        .into(binding.postPicImageView)
                }
            }
        }
    }
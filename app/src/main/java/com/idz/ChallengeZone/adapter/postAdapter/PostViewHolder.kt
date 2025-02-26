package com.idz.ChallengeZone.adapter.postAdapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.idz.ChallengeZone.OnItemClickListenerPosts
import com.idz.ChallengeZone.R
import com.idz.ChallengeZone.databinding.PostListRowBinding
import com.idz.ChallengeZone.model.Model
import com.idz.ChallengeZone.model.Post
import com.squareup.picasso.Picasso
import android.view.View
import androidx.navigation.Navigation
import com.idz.ChallengeZone.PostsListFragmentDirections

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
        Model.shared.getLoggedUser().observeForever { loggedUser ->
            if (post?.sender == loggedUser?.id) {
                Log.d("TAG","post sender is  ${post?.sender}")
                Log.d("TAG","logged user for deletion is  ${loggedUser?.userName}")
                binding.deleteButton.visibility = View.VISIBLE
                binding.editButton.visibility = View.VISIBLE
                binding.deleteButton.setOnClickListener {
                    Log.d("TAG", "Delete button clicked")
                    post?.let {
                        Model.shared.deletePost(it) {
                            Log.d("TAG", "Deleted post successfully")
                        }
                    }
                }
                binding.editButton.setOnClickListener {
                    post?.let {
                        val action = PostsListFragmentDirections.actionPostListFragmentToEditPostFragment(it)
                        binding.root.let { view ->
                            Navigation.findNavController(view).navigate(action)
                        }
                    }
                }
            } else {
                binding.deleteButton.visibility = View.GONE
                binding.editButton.visibility = View.GONE
            }
        }
        this.post = post
        binding.contentTextView?.text = post?.content
        binding.dateTextView?.text = post?.lastUpdated?.let { java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(it)) }
        post?.postPic?.let { avatarUrl ->
            if (avatarUrl.isNotBlank()) {
                Picasso.get()
                    .load(avatarUrl)
                    .placeholder(R.drawable.avatar)
                    .into(object : com.squareup.picasso.Target {
                        override fun onBitmapLoaded(bitmap: android.graphics.Bitmap?, from: Picasso.LoadedFrom?) {
                            binding.postPicImageView.setImageBitmap(bitmap)
                            binding.postPicImageView.visibility = View.VISIBLE
                        }

                        override fun onBitmapFailed(e: java.lang.Exception?, errorDrawable: android.graphics.drawable.Drawable?) {
                            binding.postPicImageView.setImageDrawable(errorDrawable)
                            binding.postPicImageView.visibility = View.VISIBLE
                        }

                        override fun onPrepareLoad(placeHolderDrawable: android.graphics.drawable.Drawable?) {
                            binding.postPicImageView.setImageDrawable(placeHolderDrawable)
                        }
                    })
            }
        }
        Model.shared.users.observeForever { users ->
            users?.forEach { user ->
                Log.d("TAG", "User from dao is: ${user.json}")
            }
        }

        Model.shared.getOtherUser(post?.sender).observeForever { user ->
            Log.d("TAG", "User from dao avatarUrl is: ${user?.json}")
            user?.userName.let { username ->
                binding.senderTextView?.text = username
            }
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
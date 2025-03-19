package com.idz.ChallengeZone.adapter.postAdapter

import android.util.Log
import androidx.recyclerview.widget.RecyclerView
import com.idz.ChallengeZone.OnItemClickListenerPosts
import com.idz.ChallengeZone.R
import com.idz.ChallengeZone.databinding.PostListRowBinding
import com.idz.ChallengeZone.model.Post
import com.squareup.picasso.Picasso
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.idz.ChallengeZone.viewmodel.AuthViewModel
import com.idz.ChallengeZone.viewmodel.PostViewModel
import com.idz.ChallengeZone.viewmodel.UserViewModel
import com.idz.ChallengeZone.PostsListFragmentDirections
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
class PostViewHolder(
    private val binding: PostListRowBinding,
    listener: OnItemClickListenerPosts?,
    private val sourceScreen: String
) : RecyclerView.ViewHolder(binding.root) {
    private var post: Post? = null

    private lateinit var postViewModel: PostViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var userViewModel: UserViewModel

    init {
        setupViewModels()
        itemView.setOnClickListener {
            listener?.onItemClick(post)
        }
    }

    private fun setupViewModels() {
        val activity = itemView.context as? FragmentActivity
        if (activity != null) {
            postViewModel = ViewModelProvider(activity)[PostViewModel::class.java]
            authViewModel = ViewModelProvider(activity)[AuthViewModel::class.java]
            userViewModel = ViewModelProvider(activity)[UserViewModel::class.java]
        }
    }

    fun bind(post: Post?, position: Int, lifecycleOwner: LifecycleOwner) {
        this.post = post
        binding.contentTextView?.text = post?.content
        binding.dateTextView?.text = post?.lastUpdated?.let { java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", java.util.Locale.getDefault()).format(java.util.Date(it)) }

        authViewModel.getLoggedUser(lifecycleOwner).observe(lifecycleOwner) { loggedUser ->
            if (post?.sender == loggedUser?.id) {
                binding.deleteButton.visibility = View.VISIBLE
                binding.editButton.visibility = View.VISIBLE
                binding.deleteButton.setOnClickListener {
                    post?.let {
                        postViewModel.deletePost(it) {
                            // Handle post deletion
                        }
                    }
                }
                binding.editButton.setOnClickListener {
                    post?.let {
                        navigateToEditPost(binding.root, it)
                    }
                }
            } else {
                binding.deleteButton.visibility = View.GONE
                binding.editButton.visibility = View.GONE
            }
        }

        userViewModel.getOtherUser(lifecycleOwner, post?.sender).observe(lifecycleOwner) { user ->
            user?.userName?.let { username ->
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
    }

    private fun navigateToEditPost(view: View, post: Post) {
        val navController = Navigation.findNavController(view)
        val action = PostsListFragmentDirections.actionGlobalEditPostFragment(post, sourceScreen)
        navController.navigate(action)
    }

    private fun <T> LiveData<T>.observeOnce(lifecycleOwner: LifecycleOwner, observer: Observer<T>) {
        observe(lifecycleOwner, object : Observer<T> {
            override fun onChanged(value: T) {
                observer.onChanged(value)
                removeObserver(this)
            }
        })
    }
}
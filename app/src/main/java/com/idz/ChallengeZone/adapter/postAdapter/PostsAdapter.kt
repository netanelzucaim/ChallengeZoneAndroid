package com.idz.ChallengeZone.adapter.postAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.idz.ChallengeZone.R
import com.idz.ChallengeZone.model.Post

class PostsAdapter(private val posts: MutableList<Post>?): BaseAdapter() {

        override fun getCount(): Int = posts?.size ?: 0

        override fun getItem(position: Int): Any {
            TODO("Not yet implemented")
        }

        override fun getItemId(position: Int): Long  = 0

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

            val inflator = LayoutInflater.from(parent?.context)
            val view = convertView ?: inflator.inflate(R.layout.post_list_row, parent, false).apply {
            }

            val post = posts?.get(position)

            val senderTextView: TextView? = view?.findViewById(R.id.senderTextView)
            val contentTextView: TextView? = view?.findViewById(R.id.contentTextView)

            senderTextView?.text = post?.sender
            contentTextView?.text = post?.content

            return view!!
        }
    }
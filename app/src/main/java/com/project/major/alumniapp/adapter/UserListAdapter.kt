package com.project.major.alumniapp.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.major.alumniapp.R
import com.project.major.alumniapp.activities.ProfileActivity
import com.project.major.alumniapp.models.User

class UserListAdapter(private val context: Context, private val userList: List<User>) : RecyclerView.Adapter<UserListAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.user_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val user1 = userList[position]
        holder.cardView.visibility = View.VISIBLE
        if (user1.user_image != null) {
            Glide.with(context)
                    .load(user1.user_image)
                    .into(holder.userlogo)
        } else {
            holder.userlogo.setImageResource(R.drawable.profle_user)
        }
        holder.username.text = user1.user_name
        holder.email.text = user1.email
        holder.cardView.setOnClickListener {
            val intent = Intent(context, ProfileActivity::class.java)
            intent.putExtra("uid", user1.uid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class ViewHolder internal constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var cardView: CardView
        var userlogo: ImageView
        var username: TextView
        var email: TextView

        init {
            cardView = itemView.findViewById(R.id.userCardView)
            userlogo = itemView.findViewById(R.id.profilePicuser)
            username = itemView.findViewById(R.id.username)
            email = itemView.findViewById(R.id.email)
        }
    }

}
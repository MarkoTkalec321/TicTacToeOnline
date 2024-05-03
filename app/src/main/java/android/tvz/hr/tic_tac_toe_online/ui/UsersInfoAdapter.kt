package android.tvz.hr.tic_tac_toe_online.ui

import android.annotation.SuppressLint
import android.tvz.hr.tic_tac_toe_online.databinding.UserItemLayoutBinding
import android.tvz.hr.tic_tac_toe_online.model.UserInfo
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

class UsersInfoAdapter(private val userList: MutableList<UserInfo>) : RecyclerView.Adapter<UsersInfoAdapter.UserInfoHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserInfoHolder {
        val binding = UserItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserInfoHolder(binding)
    }

    override fun onBindViewHolder(holder: UserInfoHolder, position: Int) {
        val userInfo = userList[position]
        holder.bind(userInfo)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    inner class UserInfoHolder(private val binding: UserItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(userInfo: UserInfo) {
            with(binding) {
                usernameTextView.text = userInfo.username
                gameCountTextView.text = "Games Played: ${userInfo.game_count}"
                winRateTextView.text = "Win Rate: ${String.format("%.2f", userInfo.win_rate * 100)}%"
            }
        }
    }

    fun updateData(items: List<UserInfo>) {
        userList.clear()
        userList.addAll(items)
        notifyDataSetChanged()
    }
}


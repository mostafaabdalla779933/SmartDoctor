package com.smartdoctor.smartdoctor.feature.doctors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartdoctor.smartdoctor.common.firebase.data.UserModel
import com.smartdoctor.smartdoctor.databinding.ItemDoctorBinding


class DoctorsAdapter(
    val onItemClicked: (UserModel) -> Unit,
    val onEditClicked: (UserModel) -> Unit,
    val onDeleteClicked: (UserModel, Int) -> Unit
) :
    ListAdapter<UserModel, DoctorsAdapter.ViewHolder>(
        UserDiffCallback()
    ) {

    inner class ViewHolder(private val rowView: ItemDoctorBinding) :
        RecyclerView.ViewHolder(rowView.root) {
        fun onBind(item: UserModel, position: Int) {
            rowView.apply {

                tvName.text = item.name
                Glide.with(itemView.context).load(item.profileUrl).into(ivProfile)

                root.setOnClickListener {
                    onItemClicked(item)
                }

                ivEdit.setOnClickListener {
                    onEditClicked(item)
                }

                ivDelete.setOnClickListener {
                    onDeleteClicked(item,position)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDoctorBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position), position)
    }

    class UserDiffCallback : DiffUtil.ItemCallback<UserModel>() {
        override fun areItemsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem.userId == newItem.userId
        }

        override fun areContentsTheSame(oldItem: UserModel, newItem: UserModel): Boolean {
            return oldItem == newItem
        }
    }
}
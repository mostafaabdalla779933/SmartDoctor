package com.smartdoctor.smartdoctor.feature.doctors

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.smartdoctor.smartdoctor.common.firebase.data.UserModel
import com.smartdoctor.smartdoctor.databinding.ItemDoctorInquiryBinding


class DoctorsInquiriesAdapter(
    val onItemClicked: (UserModel) -> Unit,
    val onInquiryClicked: (UserModel) -> Unit
) :
    ListAdapter<UserModel, DoctorsInquiriesAdapter.ViewHolder>(
        UserDiffCallback()
    ) {

    inner class ViewHolder(private val rowView: ItemDoctorInquiryBinding) :
        RecyclerView.ViewHolder(rowView.root) {
        fun onBind(item: UserModel, position: Int) {
            rowView.apply {

                tvName.text = item.name
                Glide.with(itemView.context).load(item.profileUrl).into(ivProfile)

                root.setOnClickListener {
                    onItemClicked(item)
                }

                btnInquiry.setOnClickListener {
                    onInquiryClicked(item)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDoctorInquiryBinding.inflate(
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
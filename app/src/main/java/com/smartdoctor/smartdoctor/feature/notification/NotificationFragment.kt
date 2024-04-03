package com.smartdoctor.smartdoctor.feature.notification

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.firebase.data.NotificationModel
import com.smartdoctor.smartdoctor.common.firebase.data.UserType
import com.smartdoctor.smartdoctor.common.navigateWithAnimation
import com.smartdoctor.smartdoctor.databinding.FragmentNotificationBinding
import com.smartdoctor.smartdoctor.databinding.ItemNotificationBinding

class NotificationFragment : BaseFragment<FragmentNotificationBinding>() {

    val adapter by lazy {
        NotificationAdapter {
            if(FirebaseHelp.user?.userType == UserType.User.value) {
            findNavController().navigateWithAnimation(
                R.id.chatFragment,
                bundleOf(
                    "roomId" to FirebaseHelp.getRoomID(
                        doctorID = it.toUserDoctor?.userId ?: "",
                        patientID = FirebaseHelp.getUserID()
                    ),
                    "userToSend" to it.toUserDoctor
                )
            )
            } else {
                findNavController().navigateWithAnimation(
                    R.id.chatFragment,
                    bundleOf(
                        "roomId" to FirebaseHelp.getRoomID(
                            doctorID = FirebaseHelp.getUserID() ,
                            patientID = it.toUserPatient?.userId ?: ""
                        ),
                        "userToSend" to it.toUserPatient
                    )
                )
            }
        }
    }

    override fun initBinding() = FragmentNotificationBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {

        binding.apply {
            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            rvNotifications.adapter = adapter
        }
        getData()
    }


    private fun getData() {
        showLoading()
        FirebaseHelp.getAllObjects<NotificationModel>(FirebaseHelp.NOTIFICATION, {
            hideLoading()
            adapter.submitList(it.filter { e -> e.toUserPatient?.userId == FirebaseHelp.getUserID() || e.toUserDoctor?.userId == FirebaseHelp.getUserID() }.sortedByDescending { e -> e.hash?.toLong() ?: 0L }.toMutableList())
        }, {
            hideLoading()
            showErrorMsg(it)

        })
    }


}


class NotificationAdapter(val onItemClicked: (NotificationModel) -> Unit) :
    ListAdapter<NotificationModel, NotificationAdapter.ViewHolder>(
        UserDiffCallback()
    ) {

    inner class ViewHolder(private val binding: ItemNotificationBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun onBind(item: NotificationModel) {
            binding.apply {
                if(FirebaseHelp.user?.userType == UserType.User.value) {
                    tvNewMessage.text =
                        "Doctor ${item.fromDoctor?.name} transferred your inquiry to doctor ${item.toUserDoctor?.name}"
                }else {
                    tvNewMessage.text =
                        "Doctor ${item.fromDoctor?.name} transferred patient ${item.toUserPatient?.name} to you"
                }
                tvDate.text = item.date

                root.setOnClickListener {
                    onItemClicked(item)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemNotificationBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position))
    }

    class UserDiffCallback : DiffUtil.ItemCallback<NotificationModel>() {
        override fun areItemsTheSame(
            oldItem: NotificationModel,
            newItem: NotificationModel
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: NotificationModel,
            newItem: NotificationModel
        ): Boolean {
            return oldItem == newItem
        }
    }
}
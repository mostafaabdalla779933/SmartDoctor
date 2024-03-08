package com.smartdoctor.smartdoctor.feature.inquiries


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.navigateWithAnimation
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.firebase.data.MessageModel
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.firebase.data.UserModel
import com.smartdoctor.smartdoctor.databinding.FragmentAllInquiriesBinding
import com.smartdoctor.smartdoctor.databinding.ItemRoomBinding


class AllInquiriesFragment : BaseFragment<FragmentAllInquiriesBinding>() {
    override fun initBinding() = FragmentAllInquiriesBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {

        binding.apply {
            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        getRooms()
    }


    private fun getRooms() {
        showLoading()
        FirebaseHelp.getAllObjects<MessageModel>(
            FirebaseHelp.Messages,
            {
                hideLoading()
                val list =
                    it.filter { e -> e.receiverId == FirebaseHelp.getUserID() || e.senderId == FirebaseHelp.getUserID() }
                        .toMutableList()
                list.sortByDescending { it.hash }
                val roomItems = mutableListOf<MessageModel>()
                val roomsId = list.map { e -> e.roomId }.toSet()

                roomsId.forEach { roomId ->
                    list.find { e -> e.roomId == roomId }?.let { roomItems.add(it) }
                }

                binding.rvMessages.adapter = MessagesAdapter(roomItems) {
                    it.sender?.let { user ->
                        findNavController().navigateWithAnimation(
                            R.id.chatFragment,
                            bundleOf(
                                "roomId" to (it.roomId ?: ""),
                                "userToSend" to if (it.senderId == FirebaseHelp.getUserID()) {
                                    it.receiver ?: UserModel()
                                } else user
                            )
                        )
                    }
                }


            }, {
                hideLoading()
                showMessage(it)
                findNavController().popBackStack()
            })
    }


}


class MessagesAdapter(
    var list: MutableList<MessageModel>,
    val onItemClick: (MessageModel) -> Unit
) :
    RecyclerView.Adapter<MessagesAdapter.ViewHolder>() {

    inner class ViewHolder(private val rowView: ItemRoomBinding) :
        RecyclerView.ViewHolder(rowView.root) {
        fun onBind(item: MessageModel, position: Int) {
            rowView.apply {

                root.setOnClickListener {
                    onItemClick(item)
                }
                tvUsername.text =
                    if (item.sender?.userId == FirebaseHelp.getUserID()) item.receiver?.name else item.sender?.name
                tvMessage.text = item.message ?: (if(item.url!= null) "image" else "")
                tvMessageDate.text = item.date
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemRoomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position], position)
    }
}
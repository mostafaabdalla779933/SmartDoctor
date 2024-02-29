package com.smartdoctor.smartdoctor.feature.inquiries

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.donationinstitutions.donationinstitutions.common.getString
import com.smartdoctor.smartdoctor.common.firebase.data.MessageModel
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentChatBinding
import com.smartdoctor.smartdoctor.databinding.ItemRecievedBinding
import com.smartdoctor.smartdoctor.databinding.ItemSenderBinding
import java.text.SimpleDateFormat
import java.util.Date


class ChatFragment : BaseFragment<FragmentChatBinding>() {

    private lateinit var adapter : ChatAdapter
    private lateinit var viewModel: ChatViewModel
    private val args:ChatFragmentArgs by navArgs()
    override fun initBinding()=FragmentChatBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        viewModel = ViewModelProvider(this)[ChatViewModel::class.java]

        bindView()
        getMessages()

    }

    private fun bindView(){
        adapter = ChatAdapter(mutableListOf(),FirebaseHelp.getUserID())

        binding.apply {
            rv.adapter = adapter

            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            tb.title = args.userToSend.name

            btnSend.setOnClickListener {
                if (etMessage.getString().isNotEmpty()) {
                    addMessage(
                        MessageModel(
                            senderId = FirebaseHelp.getUserID(),
                            receiverId = args.userToSend.userId,
                            message = etMessage.getString(),
                            hash = System.currentTimeMillis(),
                            roomId = args.roomId,
                            date = SimpleDateFormat("hh:mm:a dd MMM yyyy").format(Date(System.currentTimeMillis())),
                            senderName = FirebaseHelp.user?.name,
                            sender = FirebaseHelp.user,
                            receiver = args.userToSend,
                        )
                    )
                }
            }
        }


        viewModel.messagesLiveData.observe(viewLifecycleOwner){
            adapter.list = it.toMutableList()
            adapter.notifyDataSetChanged()
        }

    }

    private fun addMessage(messageModel: MessageModel) {

        FirebaseHelp.addObject<MessageModel>(
            messageModel,
            FirebaseHelp.Messages,
            messageModel.hash?.toString() ?: "",
            {
//                adapter.list.add(0, messageModel)
//                adapter.notifyItemRangeInserted(0, 1)
//                binding.rv.smoothScrollToPosition(0)
                binding.etMessage.setText("")
            },
            {
                showMessage(it)
            })
    }


    private fun getMessages() {
        showLoading()
        FirebaseHelp.getAllObjectsCondition<MessageModel>(
            FirebaseHelp.Messages,
            "roomId", args.roomId , { list ->
                hideLoading()
                list.sortByDescending { it.hash }
                adapter.list = list.toMutableList()
                adapter.notifyDataSetChanged()
                viewModel.updateMessage(args.roomId ?: "")
            }, {
                hideLoading()
                Log.i("main", "getMessages:  $it")
                showMessage(it)
                findNavController().popBackStack()
            })
    }

}



class ChatAdapter(var list:MutableList<MessageModel?>, var userId:String):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class SenderViewHolder(private val rowView: ItemSenderBinding): RecyclerView.ViewHolder(rowView.root){
        fun onBind(item: MessageModel?, position: Int){
            rowView.apply {
                tvMessage.text = item?.message
                tvDate.text = item?.date
                Glide.with(root.context).load(FirebaseHelp.user?.profileUrl).into(img)
            }
        }
    }


    inner class ReceivedViewHolder(private val rowView: ItemRecievedBinding): RecyclerView.ViewHolder(rowView.root){
        fun onBind(item: MessageModel?, position: Int){
            rowView.apply {
                tvMessage.text = item?.message
                tvDate.text = item?.date
                Glide.with(root.context).load(item?.sender?.profileUrl).into(img)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            SENDER ->{
                SenderViewHolder(ItemSenderBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
            else ->{
                ReceivedViewHolder(ItemRecievedBinding.inflate(LayoutInflater.from(parent.context),parent,false))
            }
        }

    }

    override fun getItemCount(): Int =list.size

    override fun getItemViewType(position: Int): Int {
        return  if(list[position]?.senderId == userId ){
            SENDER
        }else{
            REC
        }
    }


    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is SenderViewHolder ->{
                holder.onBind(list[position],position)
            }
            is ReceivedViewHolder ->{
                holder.onBind(list[position],position)
            }
        }
    }

    companion object{
        const val SENDER = 1
        const val REC = 2
    }
}
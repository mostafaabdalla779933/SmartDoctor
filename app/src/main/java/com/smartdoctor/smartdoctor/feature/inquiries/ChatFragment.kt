package com.smartdoctor.smartdoctor.feature.inquiries

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.donationinstitutions.donationinstitutions.common.getLoading
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

        UploadImageService.liveData.observe(viewLifecycleOwner){ url->
            url?.let {
               addImage(url)
               UploadImageService.liveData.postValue(null)
            }
        }

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

            btnSendImage.setOnClickListener {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){
                    chooseUserPhotoFromGallery33()
                }else{
                    chooseUserPhotoFromGallery()
                }
            }
        }


        viewModel.messagesLiveData.observe(viewLifecycleOwner){
            if(adapter.list != it.toMutableList()) {
                adapter.list = it.toMutableList()
                adapter.notifyDataSetChanged()
            }
        }

    }


    private fun addImage(url:String){
        addMessage(
            MessageModel(
                senderId = FirebaseHelp.getUserID(),
                receiverId = args.userToSend.userId,
                message = null,
                url = url,
                hash = System.currentTimeMillis(),
                roomId = args.roomId,
                date = SimpleDateFormat("hh:mm:a dd MMM yyyy").format(Date(System.currentTimeMillis())),
                senderName = FirebaseHelp.user?.name,
                sender = FirebaseHelp.user,
                receiver = args.userToSend,
            )
        )
    }


    private fun addMessage(messageModel: MessageModel) {
        binding.btnSend.isEnabled = false

        FirebaseHelp.addObject<MessageModel>(
            messageModel,
            FirebaseHelp.Messages,
            messageModel.hash?.toString() ?: "",
            {
                binding.btnSend.isEnabled = true
                try {
                    binding.rv.smoothScrollToPosition(0)
                } catch (_:Exception){}

                binding.etMessage.setText("")
            },
            {
                binding.btnSend.isEnabled = true
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


    @SuppressLint("InlinedApi")
    private fun chooseUserPhotoFromGallery33() {
        try {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {

                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                )
            } else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startForGallery.launch(intent)
            }
        } catch (e: Exception) {
            showErrorMsg("something went wrong")
        }
    }


    private fun chooseUserPhotoFromGallery() {
        try {
            if (ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED) {

                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )
            } else {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startForGallery.launch(intent)
            }
        } catch (e: Exception) {
            showErrorMsg("something went wrong")
        }
    }


    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var accept = false
            permissions.entries.forEach {
                accept = it.value
            }
            if (accept) {
                startForGallery.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                )
            } else {
                showErrorMsg("permission denied")
            }
        }


    private val startForGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val intent = Intent(requireContext(),UploadImageService::class.java)
                    intent.putExtra(FirebaseHelp.Image,uri)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        requireContext().startForegroundService(intent);
                    } else {
                        requireContext().startService(intent);
                    }
                    showMessage("uploading your image")
                }
            }
        }

}



class ChatAdapter(var list:MutableList<MessageModel?>, var userId:String):
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class SenderViewHolder(private val rowView: ItemSenderBinding): RecyclerView.ViewHolder(rowView.root){
        fun onBind(item: MessageModel, position: Int){
            rowView.apply {
                tvMessage.text = item.message
                tvDate.text = item.date
                Glide.with(root.context).load(item.sender?.profileUrl).into(img)
                Glide.with(root.context).load(item.url).placeholder(getLoading(itemView.context)).into(image)
                when (item.message) {
                    null, "" -> {
                        image.visibility = View.VISIBLE
                        tvMessage.visibility = View.GONE
                    }
                    else -> {
                        image.visibility = View.GONE
                        tvMessage.visibility = View.VISIBLE
                    }
                }
            }
        }
    }


    inner class ReceivedViewHolder(private val rowView: ItemRecievedBinding): RecyclerView.ViewHolder(rowView.root){
        fun onBind(item: MessageModel, position: Int){
            rowView.apply {
                tvMessage.text = item.message
                tvDate.text = item.date
                Glide.with(root.context).load(item.sender?.profileUrl).into(img)
                Glide.with(root.context).load(item.url).placeholder(getLoading(itemView.context)).into(image)
                when (item.message) {
                    null, "" -> {
                        image.visibility = View.VISIBLE
                        tvMessage.visibility = View.GONE
                    }
                    else -> {
                        image.visibility = View.GONE
                        tvMessage.visibility = View.VISIBLE
                    }
                }
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
                list.getOrNull(position)?.let { holder.onBind(it,position) }
            }
            is ReceivedViewHolder ->{
                list.getOrNull(position)?.let { holder.onBind(it,position) }
            }
        }
    }

    companion object{
        const val SENDER = 1
        const val REC = 2
    }
}
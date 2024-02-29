package com.smartdoctor.smartdoctor.feature.inquiries

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.firebase.data.MessageModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class ChatViewModel :BaseViewModel() {


    val messagesLiveData = MutableLiveData<MutableList<MessageModel>>(mutableListOf())


    fun updateMessage(roomId:String){
        viewModelScope.launchCatching(
            block = {
                while (true){
                    FirebaseHelp.getAllObjectsCondition<MessageModel>(
                        FirebaseHelp.Messages,
                        "roomId", roomId ?: "", { list ->
                            list.sortByDescending { it.hash }
                            messagesLiveData.postValue(list)
                        }, {
                            Log.i("main", "getMessages:  $it")
                        })
                    delay(1000)
                }

            }, onError = ::handleError
        )
    }



    fun handleError(t: Throwable) {
        Log.i("main", "handleError: $t")
        t.printStackTrace()
        val error = when (t) {
            is UnknownHostException -> {
                "No Internet"
            }
            else -> {
                "Something Went Wrong"
            }
        }

    }
}


open class BaseViewModel : ViewModel() {


    inline fun CoroutineScope.launchCatching(
        noinline block: suspend CoroutineScope.() -> Unit,
        crossinline onError: (Throwable) -> Unit,
    ) {
        launch(
            CoroutineExceptionHandler { _, throwable -> onError(throwable) },
            block = block
        )
    }
}
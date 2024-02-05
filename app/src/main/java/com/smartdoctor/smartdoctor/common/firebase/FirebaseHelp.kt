package com.donationinstitutions.donationinstitutions.common.firebase

import android.content.Context
import android.net.Uri
import android.util.Log
import android.webkit.MimeTypeMap
import com.donationinstitutions.donationinstitutions.common.firebase.data.UserModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference


object FirebaseHelp {

    val auth: FirebaseAuth = Firebase.auth
    val fireStore = Firebase.firestore

    var user: UserModel? =  null

    const val MESSAGE = "message"
    const val RELOAD = "reload"
    const val USERS = "all_users"
    const val DONATION = "DONATION"
    const val EFFORT = "EFFORT"
    const val COUNT = "COUNT"
    const val NOTIFICATION = "NOTIFICATION"

    fun getUserID() = auth.currentUser?.uid ?: ""

    fun uploadImageToCloudStorage(context: Context, imageFileUri: Uri, imageType: String,onSuccess :(String)->Unit,onFailure :(Exception)-> Unit){

        val sRef : StorageReference = FirebaseStorage.getInstance().reference.child(
            imageType + System.currentTimeMillis() + "." + getFileExtension(context, imageFileUri) )

        sRef.putFile(imageFileUri)
            .addOnSuccessListener { taskSnapshot ->
                Log.e("Firebase Image URL", taskSnapshot.metadata!!.reference!!.downloadUrl.toString())
                taskSnapshot.metadata!!.reference!!.downloadUrl
                    .addOnSuccessListener { url ->
                        Log.e("Downloadable Image URL", url.toString())
                        onSuccess(url.toString())
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    private fun getFileExtension(context: Context, uri: Uri?) : String? {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(context.contentResolver.getType(uri!!))
    }


    fun getUser(onSuccess: (UserModel)->Unit, onFailure: (String) -> Unit){
        fireStore.collection(USERS).document(auth.currentUser?.uid?:"").get().
        addOnSuccessListener { document ->
            document.toObject(UserModel::class.java)?.let{
                onSuccess(it)
            } ?: kotlin.run {
                onFailure( "something wrong")
            }
        }.addOnFailureListener { e ->
            onFailure(e.localizedMessage ?: "something wrong")
        }
    }


    fun logout(){
        auth.signOut()
    }


    inline fun <reified T> addObject(
        objectToSet:Any,
        collection: String,
        document: String,
        crossinline onSuccess: () -> Unit,
        crossinline onFailure: (String) -> Unit
    ) {
        fireStore.collection(collection)
            .document(document).set(objectToSet, SetOptions.merge())
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { e ->
                onFailure(e.localizedMessage ?: "something wrong")
            }

    }


    inline fun <reified T> getObject(
        collection: String,
        document: String,
        crossinline onSuccess: (T) -> Unit,
        crossinline onFailure: (String) -> Unit
    ) {
        fireStore.collection(collection).document(document).get().addOnSuccessListener { document ->
            document.toObject(T::class.java)?.let {
                onSuccess(it)
            } ?: kotlin.run {
                onFailure("something wrong")
            }
        }.addOnFailureListener { e ->
            onFailure(e.localizedMessage ?: "something wrong")
        }
    }


    inline fun <reified T> getAllObjects(
        collection: String,
        crossinline onSuccess: (MutableList<T>) -> Unit,
        crossinline onFailure: (String) -> Unit
    ) {
        fireStore.collection(collection).get().addOnSuccessListener { documents ->
            val list = mutableListOf<T>()
            documents.forEach { document ->
                list.add(document.toObject(T::class.java))
            }
            onSuccess(list)
        }.addOnFailureListener { e ->
            onFailure(e.localizedMessage ?: "something wrong")
        }
    }

    inline fun <reified T> deleteObject(
        collection: String, document: String,
        crossinline onSuccess: () -> Unit,
        crossinline onFailure: (String) -> Unit
    ) {
        fireStore.collection(collection).document(document).delete()
            .addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener { e ->
                onFailure(e.localizedMessage ?: "something wrong")
            }
    }

    fun deleteImageFromCloudStorage(imageUrl: String,onSuccess :()->Unit, onFailure :(Exception)-> Unit){
        val photoRef: StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl)

        photoRef.delete().addOnSuccessListener { // File deleted successfully
            onSuccess()
        }.addOnFailureListener { // Uh-oh, an error occurred!
            onFailure(it)
        }
    }

}
package com.smartdoctor.smartdoctor.feature.specialties

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.SystemClock
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.JobIntentService
import androidx.core.app.NotificationCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.google.firebase.firestore.SetOptions
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.firebase.data.SpecialtyModel
import com.smartdoctor.smartdoctor.common.showMessage


class AddSpecialtiesService : JobIntentService() {

    var isFinished = false
    var isFailed = false

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        progress()
        signUp(intent)
        stopSelf()
        return Service.START_STICKY
    }

    override fun onHandleWork(intent: Intent) {}

    private fun signUp(intent: Intent?) {
        intent?.extras?.getParcelable<SpecialtyModel>(FirebaseHelp.Specialties)?.let { specialty ->
            specialty.uri?.let {
                uploadImage(it, specialty)
            } ?: kotlin.run {
                addDisease(specialty  = specialty)
            }
        }
    }

    private fun uploadImage(uri: Uri, specialty: SpecialtyModel) {
        FirebaseHelp.uploadImageToCloudStorage(this, uri, "disease", { url ->
            specialty.url = url
            addDisease(specialty = specialty)
        }, {
            isFailed = true
            showMessage(it.localizedMessage ?: "something wrong")
        })
    }

    private fun addDisease(specialty: SpecialtyModel) {
        specialty.uri = null
        FirebaseHelp
            .fireStore.collection(FirebaseHelp.Specialties)
            .document(specialty.hash ?: "").set(specialty, SetOptions.merge())
            .addOnSuccessListener {
                isFinished = true
                showMessage("data sent")
            }.addOnFailureListener { e ->
                isFailed = true
                showMessage("failed  ${e.localizedMessage}")
            }
    }

    private fun progress() {
        val progressMax = 100

        val notificationManager =
            this.getSystemService(AppCompatActivity.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(
                "Channel_id_progress", "Channel_name_progress", NotificationManager.IMPORTANCE_LOW
            )

            notificationChannel.description = "Channel_description_progress"
            notificationManager.createNotificationChannel(notificationChannel)
        }
        val notificationBuilder = NotificationCompat.Builder(this, "Channel_id_progress")

        notificationBuilder.setAutoCancel(true)
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setTicker(resources.getString(R.string.app_name))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)
            .setAutoCancel(false)
            .setOngoing(true)
            .setContentTitle("uploading your data")
            .setContentText("upload in progress")
            .setOnlyAlertOnce(true)
            .setProgress(progressMax, 0, true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForeground(101, notificationBuilder.build())
        } else {
            notificationManager.notify(101, notificationBuilder.build())
        }

        Thread(Runnable {
            SystemClock.sleep(500)
            var progress = 0
            while (progress <= progressMax) {
                if (isFinished || isFailed) {
                    break
                }
                notificationBuilder.setProgress(progressMax, progress, false)
                    .setAutoCancel(false)
                notificationManager.notify(101, notificationBuilder.build())
                SystemClock.sleep(1000)
                if (progress < 80 || (progress >= 80 && isFinished))
                    progress += 20
            }
            Intent("custom-event-name").also {
                it.putExtra(FirebaseHelp.MESSAGE, FirebaseHelp.RELOAD)
                LocalBroadcastManager.getInstance(this).sendBroadcast(it);
            }
            if (isFailed) {
                notificationBuilder.setContentText("upload failed")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setAutoCancel(true)
            } else {
                notificationBuilder.setContentText("upload finished")
                    .setProgress(0, 0, false)
                    .setOngoing(false)
                    .setAutoCancel(true)
            }
            notificationManager.notify(101, notificationBuilder.build())
        }).start()
    }
}
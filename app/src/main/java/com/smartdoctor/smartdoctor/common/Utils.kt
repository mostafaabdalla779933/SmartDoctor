package com.donationinstitutions.donationinstitutions.common

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Matrix
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.media.ExifInterface
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.IdRes
import androidx.core.app.ActivityCompat
import androidx.core.net.toUri
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavOptions
import com.google.android.material.textfield.TextInputEditText
import com.smartdoctor.smartdoctor.R
import java.math.RoundingMode
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern


fun TextInputEditText.getString(): String {
    return this.text.toString().trim()
}


fun EditText.getString(): String {
    return this.text.toString().trim()
}


fun EditText.getInt(): Int = try {
    this.text.toString().toInt()
} catch (e: Exception) {
   0
}

fun String.getIntFromString(): Int = try {
    this.toInt()
} catch (e: Exception) {
    0
}

fun EditText.getDouble(): Double = try {
    this.text.toString().toDouble()
} catch (e: Exception) {
    0.0
}

fun String.getDouble(): Double = try {
    this.toDouble()
} catch (e: Exception) {
    0.0
}


fun TextInputEditText.isStringEmpty(): Boolean {
    return this.text.toString().trim().isEmpty()
}

fun EditText.isStringEmpty(): Boolean {
    return this.text.toString().trim().isEmpty()
}


fun isValidEmail(email: String): Boolean {
    return Pattern.compile(
        "^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$"
    ).matcher(email).matches()
}





fun Uri.getPath(context: Context): String {
    var result: String? = null
    val proj = arrayOf(MediaStore.Images.Media.DATA)
    val cursor: Cursor = context.contentResolver.query(
        this, proj, null, null, null)!!
    if (cursor.moveToFirst()) {
        val column_index: Int = cursor.getColumnIndexOrThrow(proj[0])
        result = cursor.getString(column_index)
    }
    cursor.close()
    if (result == null) {
        result = "Not found"
    }
    return result
}


fun getRotated(path: String):Float{
    return when(ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)){
        ExifInterface.ORIENTATION_ROTATE_90 -> 90F
        ExifInterface.ORIENTATION_ROTATE_180 -> 180F
        ExifInterface.ORIENTATION_ROTATE_270 ->  270F
        ExifInterface.ORIENTATION_NORMAL-> 0F
        else ->0F
    }
}


fun ImageView.setImageFromUri(uri: Uri, context: Context){
    uri.getPath(context).let{ path ->
        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
            .let { bitmap ->
                this.setImageBitmap(bitmap.rotateImage(getRotated(path)))
            }
    }
}


fun Bitmap.rotateImage(angle: Float): Bitmap? {
    Matrix().let { matrix ->
        matrix.postRotate(angle)
        return Bitmap.createBitmap(
            this, 0, 0, this.width,this.height,
            matrix, true
        )
    }
}

fun Double.roundOffDecimal(): Double {
    val df = DecimalFormat("#.##")
    df.roundingMode = RoundingMode.FLOOR
    return df.format(this).toDouble()
}

fun Context.showMessage(message:String){
    Toast.makeText(this,message, Toast.LENGTH_LONG).show()
}

fun getLocation(lat:Double, long:Double,context:Context,location:(String)->Unit){
    Geocoder(context, Locale("in"))
        .getAddress(lat,long) { address: android.location.Address? ->
            if (address != null) {
                location("${address.countryName?:""} ${address.adminArea}" )
            }else{
                context.showMessage("something went wrong")
            }
        }
}


fun Geocoder.getAddress(
    latitude: Double,
    longitude: Double,
    address: (android.location.Address?) -> Unit
) {
    try {
        address(getFromLocation(latitude, longitude, 1)?.firstOrNull())
    } catch(e: Exception) {
        address(null)
    }
}


fun Fragment.requestPermissionToLocation(code:Int) {

    ActivityCompat.requestPermissions(
        requireActivity(), arrayOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ), code
    )
}


fun Fragment.checkPermissions(): Boolean {
    if (ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    ) {
        return true
    }
    return false
}

fun Fragment.isLocationEnabled(): Boolean {
    val locationManager: LocationManager =
        requireContext().getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
        LocationManager.NETWORK_PROVIDER
    )
}


fun Date.toString(format: String, locale: Locale = Locale.getDefault()): String {
    val formatter = SimpleDateFormat(format, locale)
    return formatter.format(this)
}

fun getCurrentDateTime(): Date {
    return Calendar.getInstance().time
}

fun getDistance( startLatitude:Double?,startLongitude:Double?, endLatitude:Double?,endLongitude:Double?) :Float{
    if(startLongitude == null || startLatitude == null || endLatitude  == null || endLongitude == null){
        return 0F
    }
    val startLocation = Location("a")
    startLocation.latitude = startLatitude
    startLocation.longitude = startLongitude
    val endLocation = Location("b")
    endLocation.latitude = endLatitude
    endLocation.longitude = endLongitude
    return startLocation.distanceTo(endLocation)
}


@SuppressLint("Range")
fun Uri.getName(context: Context):String{
    var result: String? = null
    if (this.scheme == "content") {
        val cursor: Cursor? = context.contentResolver.query(this, null, null, null, null)
        try {
            if (cursor != null && cursor.moveToFirst()) {
                result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
            }
        } finally {
            cursor?.close()
        }
    }
    if (result == null) {
        result = this.path
        val cut = result!!.lastIndexOf('/')
        if (cut != -1) {
            result = result.substring(cut + 1)
        }
    }
    return result

}


fun String.calculateAge(): Int {
    val currentDate = Calendar.getInstance().time
    val birthDate = SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.US).parse(this)

    val currentCalendar = Calendar.getInstance()
    currentCalendar.time = currentDate

    val birthCalendar = Calendar.getInstance()
    birthCalendar.time = birthDate

    var age = currentCalendar.get(Calendar.YEAR) - birthCalendar.get(Calendar.YEAR)

    // Check if the birthday has occurred this year
    if (currentCalendar.get(Calendar.DAY_OF_YEAR) < birthCalendar.get(Calendar.DAY_OF_YEAR)) {
        age--
    }

    return age
}


fun NavController.navigateWithAnimation(@IdRes resId: Int,bundle: Bundle = bundleOf()) {
    this.navigate(
        resId,
        bundle,
        NavOptions.Builder()
            .setEnterAnim(R.anim.slide_in_right)
            .setExitAnim(R.anim.slide_out_left)
            .setPopEnterAnim(R.anim.slide_in_left)
            .setPopExitAnim(R.anim.slide_out_right)
            .build()
    )
}




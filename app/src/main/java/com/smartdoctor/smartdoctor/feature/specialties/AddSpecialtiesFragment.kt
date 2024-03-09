package com.smartdoctor.smartdoctor.feature.specialties

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.firebase.data.SpecialtyModel
import com.smartdoctor.smartdoctor.common.getString
import com.smartdoctor.smartdoctor.common.isStringEmpty
import com.smartdoctor.smartdoctor.common.setImageFromUri
import com.smartdoctor.smartdoctor.common.showMessage
import com.smartdoctor.smartdoctor.databinding.FragmentAddSpecialtiesBinding


class AddSpecialtiesFragment : BaseFragment<FragmentAddSpecialtiesBinding>() {

    private var selectedUri: Uri? = null
    override fun initBinding() = FragmentAddSpecialtiesBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {

        binding.apply {
            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            ivAddImage.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    chooseUserPhotoFromGallery33()
                } else {
                    chooseUserPhotoFromGallery()
                }
            }


            btnAdd.setOnClickListener {
                when {
                    selectedUri == null -> {
                        showErrorMsg("select photo")
                    }

                    etName.isStringEmpty() -> {
                        showErrorMsg("fill name")
                    }

                    else -> {
                        val intent = Intent(requireContext(),AddSpecialtiesService::class.java)
                        val specialtyModel = SpecialtyModel(
                            name = etName.getString(),
                            hash = System.currentTimeMillis().toString(),
                            uri = selectedUri
                        )
                        intent.putExtra(FirebaseHelp.Specialties, specialtyModel)

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            requireContext().startForegroundService(intent);
                        } else {
                            requireContext().startService(intent);
                        }
                        requireContext().showMessage("uploading your data")
                        findNavController().popBackStack()
                    }

                }
            }
        }

    }


    @SuppressLint("InlinedApi")
    private fun chooseUserPhotoFromGallery33() {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                )
            } else {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startForGallery.launch(intent)
            }
        } catch (e: Exception) {
            showErrorMsg("something went wrong")
        }
    }


    private fun chooseUserPhotoFromGallery() {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )
            } else {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

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
                    selectedUri = uri
                    binding.ivAddImage.setImageFromUri(uri, requireContext())

                }
            }
        }

}
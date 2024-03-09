package com.smartdoctor.smartdoctor.feature.doctors

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.DatePicker
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.donationinstitutions.donationinstitutions.common.base.DateFragmentForBirth
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.firebase.data.UserModel
import com.smartdoctor.smartdoctor.common.firebase.data.UserType
import com.smartdoctor.smartdoctor.common.getString
import com.smartdoctor.smartdoctor.common.isStringEmpty
import com.smartdoctor.smartdoctor.common.isValidEmail
import com.smartdoctor.smartdoctor.common.setImageFromUri
import com.smartdoctor.smartdoctor.common.showMessage
import com.smartdoctor.smartdoctor.common.firebase.data.SpecialtyModel
import com.smartdoctor.smartdoctor.databinding.FragmentAddDoctorBinding
import com.smartdoctor.smartdoctor.databinding.SpinnerItemBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class AddDoctorFragment : BaseFragment<FragmentAddDoctorBinding>(),
    DatePickerDialog.OnDateSetListener {

    private var selectedUri: Uri? = null
    private var selectedDate: Date? = null
    private var listOfUsers = mutableListOf<UserModel>()
    private var listOfSpecialties = mutableListOf<SpecialtyModel>()
    private var adapter: SpecialtiesSpinnerAdapter? = null
    override fun initBinding() = FragmentAddDoctorBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {


        binding.apply {
            spinnerSpecialization.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
                    Glide.with(requireContext()).load(listOfSpecialties.getOrNull(position)?.url).into(ivSpecialization)
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            ivDoctor.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    chooseUserPhotoFromGallery33()
                } else {
                    chooseUserPhotoFromGallery()
                }
            }

            tvBirthDate.setOnClickListener {
                DateFragmentForBirth(this@AddDoctorFragment).also {
                    it.dialog?.window?.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
                }.show(parentFragmentManager, "date")
            }

            btnAddDoctor.setOnClickListener {
                validate()
            }

            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

        }

        getData()
    }

    private fun getData() {
        showLoading()
        FirebaseHelp.getAllObjects<UserModel>(FirebaseHelp.USERS, {
            getSpecialties()
            listOfUsers = it
        }, {
            hideLoading()
            requireContext().showMessage(it)
            findNavController().popBackStack()
        })
    }


    private fun getSpecialties() {
        FirebaseHelp.getAllObjects<SpecialtyModel>(FirebaseHelp.Specialties, {
            hideLoading()
            if(it.isEmpty()){
                showMessage("there is no specialties")
                findNavController().popBackStack()
                return@getAllObjects
            }
            listOfSpecialties = it
            adapter = SpecialtiesSpinnerAdapter(
                requireContext(),
                it
            )
            binding.spinnerSpecialization.adapter = adapter
        }, {
            hideLoading()
            showMessage(it)
            findNavController().popBackStack()
        })

    }


    private fun validate() {
        binding.apply {
            when {
                selectedUri == null -> {
                    showErrorMsg("select photo")
                }

                etName.isStringEmpty() -> {
                    showErrorMsg("fill name")
                }

                etBio.isStringEmpty() -> {
                    showErrorMsg("fill bio")
                }

                etJobNumber.isStringEmpty() -> {
                    showErrorMsg("fill job number")
                }

                isValidEmail(etEmail.getString()).not() -> {
                    showErrorMsg("invalid email")
                }

                listOfUsers.none { e -> e.email == etEmail.getString() }.not() -> {
                    showErrorMsg("this email already in use")
                }

                etPassword.isStringEmpty() -> {
                    showErrorMsg("fill password")
                }

                etPassword.getString() != etConfirmPassword.getString() -> {
                    showErrorMsg("invalid confirmation password")
                }

                selectedDate == null -> {
                    showErrorMsg("fill birth date")
                }

                else -> {
                    signWithFirebase()
                }

            }
        }

    }

    private fun signWithFirebase() {
        binding.apply {
            val intent = Intent(requireContext(), AddDoctorService::class.java)
            val user = UserModel(
                email = etEmail.getString(),
                password = etPassword.getString(),
                name = etName.getString(),
                uri = selectedUri,
                userType = UserType.Doctor.value,
                bio = etBio.getString(),
                specialty = listOfSpecialties.getOrNull(spinnerSpecialization.selectedItemPosition),
                birthDate = selectedDate.toString(),
                jobNumber = etJobNumber.getString()
            )
            intent.putExtra(FirebaseHelp.USERS, user)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                requireContext().startForegroundService(intent);
            } else {
                requireContext().startService(intent);
            }
            requireContext().showMessage("uploading your data")
            findNavController().popBackStack()
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
                    binding.ivDoctor.setImageFromUri(uri, requireContext())

                }
            }
        }


    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = Calendar.getInstance()
        calendar[year, month] = dayOfMonth
        selectedDate = Date(calendar.timeInMillis)
        val date = SimpleDateFormat("dd/MM/yyyy").format(selectedDate)
        binding.tvBirthDate.text = date
    }


}


class SpecialtiesSpinnerAdapter(
    val context: Context,
    var list: MutableList<SpecialtyModel>
) : BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            SpinnerItemBinding.inflate(LayoutInflater.from(context), parent, false)
                .let { binding ->
                    view = binding.root
                    vh = ItemHolder(binding)
                    view.tag = vh
                }
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }

        vh.onBind(list[position], position)
        return view
    }

    override fun getItem(position: Int): SpecialtyModel {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ItemHolder(private val row: SpinnerItemBinding) {
        fun onBind(item: SpecialtyModel, position: Int) {
            row.tvName.text = item.name
        }
    }

}
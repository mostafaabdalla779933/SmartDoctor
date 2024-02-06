package com.smartdoctor.smartdoctor.feature.register

import android.app.DatePickerDialog
import android.content.Intent
import android.view.Window
import android.widget.DatePicker
import androidx.navigation.fragment.findNavController
import com.donationinstitutions.donationinstitutions.common.base.BaseFragment
import com.donationinstitutions.donationinstitutions.common.base.DateFragmentForBirth
import com.donationinstitutions.donationinstitutions.common.calculateAge
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.donationinstitutions.donationinstitutions.common.firebase.data.UserModel
import com.donationinstitutions.donationinstitutions.common.firebase.data.UserState
import com.donationinstitutions.donationinstitutions.common.firebase.data.UserType
import com.donationinstitutions.donationinstitutions.common.getString
import com.donationinstitutions.donationinstitutions.common.isStringEmpty
import com.donationinstitutions.donationinstitutions.common.isValidEmail
import com.donationinstitutions.donationinstitutions.common.showMessage
import com.google.firebase.firestore.SetOptions
import com.smartdoctor.smartdoctor.databinding.FragmentRegisterBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date

class RegisterFragment : BaseFragment<FragmentRegisterBinding>(), DatePickerDialog.OnDateSetListener {

    private var selectedDate: Date? = null
    private var age:Int? = null
    private var listOfUsers = mutableListOf<UserModel>()

    override fun initBinding() = FragmentRegisterBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {

        handleCustomBack {
            requireActivity().finish()
        }

        binding.apply {
            llBirthDate.setOnClickListener{
                DateFragmentForBirth(this@RegisterFragment).also {
                    it.dialog?.window?.requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
                }.show(parentFragmentManager, "date")
            }

            btnLogin.setOnClickListener {
                findNavController().popBackStack()
            }

            btnRegister.setOnClickListener{
                validate()
            }
        }

        getData()
    }


    private fun validate() {
        binding.apply {
            when {
                etName.isStringEmpty() -> {
                    showErrorMsg("fill name")
                }


                etMobile.isStringEmpty() -> {
                    showErrorMsg("fill mobile")
                }

                selectedDate == null ->{
                    showErrorMsg("fill birth date")
                }

                etIdNumber.isStringEmpty() -> {
                    showErrorMsg("fill ID number")
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

                else -> {
                    signWithFirebase()
                }
            }
        }
    }

    private fun signWithFirebase() {
        binding.apply {
            val user = UserModel(
                email = etEmail.getString(),
                password = etPassword.getString(),
                name = etName.getString(),
                mobile = etMobile.getString(),
                userType = UserType.User.value,
                birthDate = selectedDate?.toString(),
                idNumber = etIdNumber.getString(),
                userState = UserState.Accepted.value
            )
            signUp(user)
        }
    }


    private fun signUp(user: UserModel) {
        showLoading()
        FirebaseHelp.auth.createUserWithEmailAndPassword(
            user.email ?: "",
            user.password ?: ""
        )
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    addUser(id = task.result.user?.uid ?: "", userModel = user)
                } else {
                    hideLoading()
                    showMessage(task.exception?.localizedMessage ?: "something wrong")
                }
            }

    }



    private fun addUser(id:String,userModel: UserModel) {
        userModel.userId = id
        userModel.uri = null
        FirebaseHelp
            .fireStore.collection(FirebaseHelp.USERS)
            .document(id).set(userModel, SetOptions.merge())
            .addOnSuccessListener {
                hideLoading()
                showMessage("Success")
                findNavController().navigate(RegisterFragmentDirections.actionRegisterFragmentToUserDashboardFragment())
            }.addOnFailureListener { e ->
                hideLoading()
                showMessage("failed  ${e.localizedMessage}")
            }

    }

    private fun getData() {
        showLoading()
        FirebaseHelp.getAllObjects<UserModel>(FirebaseHelp.USERS, {
            hideLoading()
            listOfUsers = it
        }, {
            hideLoading()
            requireContext().showMessage(it)
            findNavController().popBackStack()
        })
    }






    override fun reload() {}

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar  = Calendar.getInstance()
        calendar[year, month] = dayOfMonth
        selectedDate = Date(calendar.timeInMillis)
        val date = SimpleDateFormat("dd/MM/yyyy").format(selectedDate)
        binding.tvDayPicker.text = date.split("/")[0]
        binding.tvMonthPicker.text = date.split("/")[1]
        binding.tvYearPicker.text = date.split("/")[2]
        age = selectedDate?.toString()?.calculateAge() ?: 0
        binding.tvAge.text = age.toString()
    }


}
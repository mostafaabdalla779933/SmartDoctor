package com.smartdoctor.smartdoctor.feature.doctors

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.navigateWithAnimation
import com.smartdoctor.smartdoctor.common.showMessage
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.firebase.data.UserModel
import com.smartdoctor.smartdoctor.common.firebase.data.UserState
import com.smartdoctor.smartdoctor.common.firebase.data.UserType
import com.smartdoctor.smartdoctor.databinding.FragmentPatientAllDoctorsBinding

class PatientAllDoctorsFragment : BaseFragment<FragmentPatientAllDoctorsBinding>() {

    private val adapter by lazy {
        DoctorsInquiriesAdapter(
            onItemClicked = { doctor ->
                val args = Bundle().apply {
                    putParcelable("doctor", doctor)
                }
                findNavController().navigateWithAnimation(
                    R.id.doctorProfileFragment,
                    args
                )
            }, onInquiryClicked = { doctor ->

                findNavController().navigateWithAnimation(
                    R.id.chatFragment,
                    bundleOf(
                        "roomId" to FirebaseHelp.getRoomID(
                            doctorID = doctor.userId ?: "",
                            patientID = FirebaseHelp.getUserID()
                        ),
                        "userToSend" to doctor
                    )
                )

            })
    }

    override fun initBinding() = FragmentPatientAllDoctorsBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        binding.apply {
            tb.setOnClickListener {
                findNavController().popBackStack()
            }

            rvDoctors.adapter = adapter
        }
        getData()
    }

    private fun getData() {
        showLoading()
        FirebaseHelp.getAllObjects<UserModel>(FirebaseHelp.USERS, {
            hideLoading()
            adapter.submitList(it.filter { user -> user.userType == UserType.Doctor.value && user.userState == UserState.Accepted.value })
        }, {
            hideLoading()
            requireContext().showMessage(it)
        })
    }

}
package com.smartdoctor.smartdoctor.feature.doctors

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.donationinstitutions.donationinstitutions.common.firebase.data.UserModel
import com.donationinstitutions.donationinstitutions.common.firebase.data.UserState
import com.donationinstitutions.donationinstitutions.common.firebase.data.UserType
import com.donationinstitutions.donationinstitutions.common.navigateWithAnimation
import com.donationinstitutions.donationinstitutions.common.showMessage
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.base.BaseFragment
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
            adapter.submitList(it.filter { user -> user.userType == UserType.Doctor.value && user.userState != UserState.Deleted.value })
        }, {
            hideLoading()
            requireContext().showMessage(it)
        })
    }

}
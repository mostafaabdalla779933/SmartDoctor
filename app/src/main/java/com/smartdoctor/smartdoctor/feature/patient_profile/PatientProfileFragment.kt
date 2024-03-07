package com.smartdoctor.smartdoctor.feature.patient_profile

import androidx.navigation.fragment.findNavController
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentPatientProfileBinding

class PatientProfileFragment : BaseFragment<FragmentPatientProfileBinding>() {

    override fun initBinding() = FragmentPatientProfileBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {

        FirebaseHelp.user?.apply {
            binding.apply {
                btnLogout.setOnClickListener {
                    FirebaseHelp.logout()
                    requireActivity().finish()
                    requireActivity().startActivity(requireActivity().intent)
                }

                tb.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }

                tvName.text = name
                tvIdNumber.text = idNumber
                tvMobileNumber.text = mobile

            }
        }
    }


}
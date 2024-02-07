package com.smartdoctor.smartdoctor.feature.doctors

import androidx.navigation.fragment.findNavController
import com.donationinstitutions.donationinstitutions.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentDoctorsBinding

class DoctorsFragment : BaseFragment<FragmentDoctorsBinding>() {
    override fun initBinding() = FragmentDoctorsBinding.inflate(layoutInflater)
    override fun onFragmentCreated() {

        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAdd.setOnClickListener {
                findNavController().navigate(DoctorsFragmentDirections.actionDoctorsFragmentToAddDoctorFragment())
            }
        }

    }

    override fun reload() {}

}
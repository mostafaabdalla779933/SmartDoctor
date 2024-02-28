package com.smartdoctor.smartdoctor.feature.patient_profile

import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentPatientProfileBinding

class PatientProfileFragment : BaseFragment<FragmentPatientProfileBinding>() {
    override fun initBinding() = FragmentPatientProfileBinding.inflate(layoutInflater)
    override fun onFragmentCreated() {

    }

    override fun reload() {}

}
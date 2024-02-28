package com.smartdoctor.smartdoctor.feature.diseases

import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentPatientDiseasesBinding

class PatientDiseasesFragment : BaseFragment<FragmentPatientDiseasesBinding>() {
    override fun initBinding() = FragmentPatientDiseasesBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {

    }

    override fun reload() {}

}
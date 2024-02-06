package com.smartdoctor.smartdoctor.feature.doctors

import com.donationinstitutions.donationinstitutions.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentDoctorsBinding

class DoctorsFragment : BaseFragment<FragmentDoctorsBinding>() {
    override fun initBinding() = FragmentDoctorsBinding.inflate(layoutInflater)
    override fun onFragmentCreated() {

    }

    override fun reload() {}

}
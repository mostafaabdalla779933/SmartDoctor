package com.smartdoctor.smartdoctor.feature.my_profile

import com.donationinstitutions.donationinstitutions.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentMyProfileBinding

class MyProfileFragment  : BaseFragment<FragmentMyProfileBinding>() {
    override fun initBinding() = FragmentMyProfileBinding.inflate(layoutInflater)
    override fun onFragmentCreated() {

    }

    override fun reload() {}

}
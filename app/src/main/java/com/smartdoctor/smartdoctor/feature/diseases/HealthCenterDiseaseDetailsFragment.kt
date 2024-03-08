package com.smartdoctor.smartdoctor.feature.diseases

import androidx.navigation.fragment.navArgs
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentHealthCenterDiseaseDetailsBinding

class HealthCenterDiseaseDetailsFragment : BaseFragment<FragmentHealthCenterDiseaseDetailsBinding>() {

    private val args : HealthCenterDiseaseDetailsFragmentArgs by navArgs()
    override fun initBinding() = FragmentHealthCenterDiseaseDetailsBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {

    }
}
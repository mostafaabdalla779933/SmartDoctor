package com.smartdoctor.smartdoctor.feature.diseases

import androidx.navigation.fragment.findNavController
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentPatientDiseasesBinding

class PatientDiseasesFragment : BaseFragment<FragmentPatientDiseasesBinding>() {
    override fun initBinding() = FragmentPatientDiseasesBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {

        binding.apply {

            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }

    }


}
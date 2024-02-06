package com.smartdoctor.smartdoctor.feature.dashboard


import com.donationinstitutions.donationinstitutions.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentHealthCareDashboardBinding


class HealthCareDashboardFragment : BaseFragment<FragmentHealthCareDashboardBinding>() {
    override fun initBinding() = FragmentHealthCareDashboardBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        handleCustomBack {
            requireActivity().finish()
        }
    }

    override fun reload() {}

}
package com.smartdoctor.smartdoctor.feature.dashboard

import com.donationinstitutions.donationinstitutions.common.base.BaseFragment
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.databinding.FragmentUserDashboardBinding

class UserDashboardFragment : BaseFragment<FragmentUserDashboardBinding>() {
    override fun initBinding() = FragmentUserDashboardBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        handleCustomBack {
            requireActivity().finish()
        }

        binding.apply {
            ivProfile.setOnClickListener {
                FirebaseHelp.logout()
                requireActivity().finish()
                requireActivity().startActivity(requireActivity().intent)
            }
        }
    }

    override fun reload() {
    }

}
package com.smartdoctor.smartdoctor.feature.dashboard

import androidx.navigation.fragment.findNavController
import com.smartdoctor.smartdoctor.common.base.BaseFragment
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
                findNavController().navigate(UserDashboardFragmentDirections.actionUserDashboardFragmentToPatientProfileFragment())
            }

            tvFingerScan.setOnClickListener {
                findNavController().navigate(UserDashboardFragmentDirections.actionUserDashboardFragmentToFingerScanningFragment())
            }

            ivNotification.setOnClickListener {
                findNavController().navigate(UserDashboardFragmentDirections.actionUserDashboardFragmentToNotificationFragment())
            }
        }
    }

    override fun reload() {
    }

}
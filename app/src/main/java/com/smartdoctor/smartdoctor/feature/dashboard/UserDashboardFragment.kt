package com.smartdoctor.smartdoctor.feature.dashboard

import androidx.navigation.fragment.findNavController
import com.donationinstitutions.donationinstitutions.common.base.BaseFragment
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.databinding.FragmentUserDashboardBinding
import com.smartdoctor.smartdoctor.feature.finger_scanning.FingerScanningFragment

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
package com.smartdoctor.smartdoctor.feature.dashboard

import androidx.navigation.fragment.findNavController
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.donationinstitutions.donationinstitutions.common.navigateWithAnimation
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.databinding.FragmentDoctorDashboardBinding


class DoctorDashboardFragment : BaseFragment<FragmentDoctorDashboardBinding>() {
    override fun initBinding() = FragmentDoctorDashboardBinding.inflate(layoutInflater)
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

            ivNotification.setOnClickListener {
                findNavController().navigate(DoctorDashboardFragmentDirections.actionDoctorDashboardFragmentToNotificationFragment())
            }

            tvInquiries.setOnClickListener {
                findNavController().navigateWithAnimation(R.id.allInquiriesFragment)
            }

            tvClosedInquiries.setOnClickListener {
                findNavController().navigateWithAnimation(R.id.allInquiriesFragment)
            }
        }

    }

    override fun reload() {
    }

}
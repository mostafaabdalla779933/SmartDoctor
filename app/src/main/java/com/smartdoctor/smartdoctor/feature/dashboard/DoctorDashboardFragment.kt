package com.smartdoctor.smartdoctor.feature.dashboard

import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.navigateWithAnimation
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
                findNavController().navigateWithAnimation(
                    R.id.doctorProfileFragment,
                    bundleOf(
                        "doctor" to null
                    )
                )
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
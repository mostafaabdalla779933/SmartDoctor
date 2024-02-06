package com.smartdoctor.smartdoctor.feature.dashboard


import androidx.navigation.fragment.findNavController
import com.donationinstitutions.donationinstitutions.common.base.BaseFragment
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.databinding.FragmentHealthCareDashboardBinding


class HealthCareDashboardFragment : BaseFragment<FragmentHealthCareDashboardBinding>() {
    override fun initBinding() = FragmentHealthCareDashboardBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        handleCustomBack {
            requireActivity().finish()
        }

        binding.apply {
            ivLogout.setOnClickListener {
                FirebaseHelp.logout()
                requireActivity().finish()
                requireActivity().startActivity(requireActivity().intent)
            }

            tvDoctors.setOnClickListener {
                findNavController().navigate(HealthCareDashboardFragmentDirections.actionHealthCareDashboardFragmentToDoctorsFragment())
            }
        }
    }

    override fun reload() {}

}
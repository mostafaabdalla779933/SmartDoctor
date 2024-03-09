package com.smartdoctor.smartdoctor.feature.dashboard


import androidx.navigation.fragment.findNavController
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.navigateWithAnimation
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

            tvDiagnosis.setOnClickListener {
                findNavController().navigateWithAnimation(
                    R.id.healthCenterAllDiseasesFragment
                )
            }

            tvSpecialization.setOnClickListener {
                findNavController().navigateWithAnimation(
                    R.id.allSpecialtiesFragment
                )
            }

            tvDoctors.setOnClickListener {
                findNavController().navigate(HealthCareDashboardFragmentDirections.actionHealthCareDashboardFragmentToDoctorsFragment())
            }
        }
    }

    override fun reload() {}

}
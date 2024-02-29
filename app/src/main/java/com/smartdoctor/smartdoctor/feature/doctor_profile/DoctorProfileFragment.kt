package com.smartdoctor.smartdoctor.feature.doctor_profile

import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentDoctorProfileBinding

class DoctorProfileFragment : BaseFragment<FragmentDoctorProfileBinding>() {

    private val args: DoctorProfileFragmentArgs by navArgs()

    override fun initBinding() = FragmentDoctorProfileBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        binding.tb.setOnClickListener {
            findNavController().popBackStack()
        }
        showData()
    }

    private fun showData() {
        binding.apply {
            args.doctor.apply {
                tvName.text = name
                tvBio.text = bio
                tvJobNumber.text = jobNumber
                tvSpecialization.text = specialization
                tvEmail.text = email
                tvBirthDate.text = birthDate
                Glide.with(requireContext()).load(profileUrl).into(ivDoctor)
            }
        }
    }

}
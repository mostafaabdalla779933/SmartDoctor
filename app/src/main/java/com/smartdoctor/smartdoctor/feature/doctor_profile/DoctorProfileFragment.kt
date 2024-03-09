package com.smartdoctor.smartdoctor.feature.doctor_profile

import android.view.View
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.navigateWithAnimation
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.getDayMonthAndYear
import com.smartdoctor.smartdoctor.databinding.FragmentDoctorProfileBinding

class DoctorProfileFragment : BaseFragment<FragmentDoctorProfileBinding>() {

    private val args: DoctorProfileFragmentArgs by navArgs()

    override fun initBinding() = FragmentDoctorProfileBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        binding.apply {
            tb.setOnClickListener {
                findNavController().popBackStack()
            }

            btnLogout.setOnClickListener {
                FirebaseHelp.logout()
                requireActivity().finish()
                requireActivity().startActivity(requireActivity().intent)
            }

            btnInquiry.setOnClickListener {
                findNavController().navigateWithAnimation(
                    R.id.chatFragment,
                    bundleOf(
                        "roomId" to FirebaseHelp.getRoomID(
                            doctorID = args.doctor?.userId ?: "",
                            patientID = FirebaseHelp.getUserID()
                        ),
                        "userToSend" to args.doctor
                    )
                )
            }
        }
        showData()
    }

    private fun showData() {
        binding.apply {
            args.doctor?.let {
                tvName.text = it.name
                tvBio.text = it.bio
                tvJobNumber.text = it.jobNumber
                tvSpecialization.text = it.specialty?.name
                tvEmail.text = it.email
                tvBirthDate.text = it.birthDate?.getDayMonthAndYear()
                Glide.with(requireContext()).load(it.profileUrl).into(ivDoctor)
                btnLogout.visibility = View.GONE
            } ?: kotlin.run {
                FirebaseHelp.user?.let { user ->
                    tvName.text = user.name
                    tvBio.text = user.bio
                    tvJobNumber.text = user.jobNumber
                    tvSpecialization.text = user.specialty?.name
                    tvEmail.text = user.email
                    tvBirthDate.text = user.birthDate?.getDayMonthAndYear()
                    Glide.with(requireContext()).load(user.profileUrl).into(ivDoctor)
                    btnInquiry.visibility = View.GONE
                }
            }
        }
    }

}
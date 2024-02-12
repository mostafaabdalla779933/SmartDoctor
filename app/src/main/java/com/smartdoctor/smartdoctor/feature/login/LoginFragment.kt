package com.smartdoctor.smartdoctor.feature.login

import androidx.navigation.fragment.findNavController
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.donationinstitutions.donationinstitutions.common.firebase.data.UserState
import com.donationinstitutions.donationinstitutions.common.firebase.data.UserType
import com.donationinstitutions.donationinstitutions.common.getString
import com.donationinstitutions.donationinstitutions.common.isStringEmpty
import com.smartdoctor.smartdoctor.databinding.FragmentLoginBinding

class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override fun initBinding() = FragmentLoginBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {

        binding.apply {
            btnRegister.setOnClickListener {
                findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToRegisterFragment())
            }

            btnLogin.setOnClickListener {
                validate()
            }

        }
    }

    override fun reload() {}


    private fun validate() {
        if (binding.etEmail.isStringEmpty()) {
            showErrorMsg("fill Username")
        } else if (binding.etPassword.isStringEmpty()) {
            showErrorMsg("fill password")
        } else {
            signIn()
        }
    }

    private fun signIn() {
        showLoading()
        FirebaseHelp.auth
            .signInWithEmailAndPassword(binding.etEmail.getString(), binding.etPassword.getString())
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    hideLoading()
                    check()
                } else {
                    hideLoading()
                    showErrorMsg(task.exception?.localizedMessage ?: "something wrong")
                }
            }
    }

    private fun check() {
        FirebaseHelp.getUser({
            hideLoading()
            FirebaseHelp.user = it

            when (it.userState) {
                UserState.Pending.value -> {
                    showErrorMsg("Pending account, please try again later")
                    FirebaseHelp.logout()
                }

                UserState.Deleted.value -> {
                    showErrorMsg("account deleted")
                    FirebaseHelp.logout()
                }

                UserState.Rejected.value -> {
                    showErrorMsg("account Rejected")
                    FirebaseHelp.logout()
                }

                UserState.Accepted.value -> {
                    when (it.userType) {
                        UserType.HealthCenter.value -> {
                            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToHealthCareDashboardFragment())
                        }

                        UserType.Doctor.value -> {
                            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToDoctorDashboardFragment())
                        }

                        UserType.User.value -> {
                            findNavController().navigate(LoginFragmentDirections.actionLoginFragmentToUserDashboardFragment())
                        }
                    }
                }
            }
        }, {
            hideLoading()
            showErrorMsg(it)
        })
    }

}
package com.smartdoctor.smartdoctor.feature


import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.firebase.data.UserState
import com.smartdoctor.smartdoctor.common.firebase.data.UserType
import com.smartdoctor.smartdoctor.databinding.FragmentSplashBinding
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


class SplashFragment : BaseFragment<FragmentSplashBinding>() {
    override fun initBinding() = FragmentSplashBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        checkNotificationPermission()
    }

    private fun navigate() {

        if (FirebaseHelp.getUserID().isNotEmpty()) {
            showLoading()
            FirebaseHelp.getUser({
                hideLoading()
                FirebaseHelp.user = it
                when (it.userState) {
                    UserState.Pending.value -> {
                        showErrorMsg("Pending account, please try again later")
                        FirebaseHelp.logout()
                        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                    }

                    UserState.Deleted.value -> {
                        showErrorMsg("account deleted")
                        FirebaseHelp.logout()
                        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                    }

                    UserState.Rejected.value -> {
                        showErrorMsg("account Rejected")
                        FirebaseHelp.logout()
                        findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
                    }

                    UserState.Accepted.value -> {
                        when (it.userType) {
                            UserType.HealthCenter.value -> {
                                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToHealthCareDashboardFragment())
                            }

                            UserType.Doctor.value -> {
                                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToDoctorDashboardFragment())
                            }

                            UserType.User.value -> {
                                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToUserDashboardFragment())
                            }
                        }
                    }
                }


            }, {
                hideLoading()
                showErrorMsg(it)
            })
        } else {
            lifecycleScope.launch {
                delay(500)
                findNavController().navigate(SplashFragmentDirections.actionSplashFragmentToLoginFragment())
            }
        }
    }

    private fun checkNotificationPermission() {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
            ) {

                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                )
            } else {
                navigate()
            }
        } catch (e: Exception) {
            navigate()
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            navigate()

        }

}
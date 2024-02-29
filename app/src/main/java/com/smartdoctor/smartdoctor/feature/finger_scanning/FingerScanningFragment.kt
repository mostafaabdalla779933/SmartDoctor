package com.smartdoctor.smartdoctor.feature.finger_scanning

import android.os.Handler
import android.os.Looper
import androidx.navigation.fragment.findNavController
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.databinding.FragmentFingerScanningBinding
import java.util.Random

class FingerScanningFragment : BaseFragment<FragmentFingerScanningBinding>() {
    override fun initBinding() = FragmentFingerScanningBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        binding.apply {
            ivFinger.setOnLongClickListener {
                showLoading()
                Handler(Looper.getMainLooper()).postDelayed({
                    hideLoading()
                    tvHeartPulse.text = generateRandomValue(60, 100).toString()
                    tvStress.text = "${generateRandomValue(2, 7)}%"
                    tvOxygenRatio.text = "${generateRandomValue(95, 100)}%"
                }, 1000)
            }

            ivFinger.setOnClickListener {
                showMessage("long click")
            }

            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
    }

    private fun generateRandomValue(min: Int, max: Int): Int {
        val random = Random()
        return random.nextInt((max - min) + 1) + min
    }



}
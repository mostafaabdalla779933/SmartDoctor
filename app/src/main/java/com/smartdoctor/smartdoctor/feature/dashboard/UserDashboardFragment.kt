package com.smartdoctor.smartdoctor.feature.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.donationinstitutions.donationinstitutions.common.base.BaseFragment
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.databinding.FragmentUserDashboardBinding


class UserDashboardFragment : BaseFragment<FragmentUserDashboardBinding>() {
    override fun initBinding()=FragmentUserDashboardBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
    }

    override fun reload() {
    }

}
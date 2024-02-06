package com.smartdoctor.smartdoctor.feature.register

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.donationinstitutions.donationinstitutions.common.base.BaseFragment
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.databinding.FragmentRegisterBinding


class RegisterFragment : BaseFragment<FragmentRegisterBinding>() {
    override fun initBinding()=FragmentRegisterBinding.inflate(layoutInflater)
    override fun onFragmentCreated() {
    }

    override fun reload() {
    }

}
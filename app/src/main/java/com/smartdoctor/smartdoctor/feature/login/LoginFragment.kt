package com.smartdoctor.smartdoctor.feature.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.donationinstitutions.donationinstitutions.common.base.BaseFragment
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.databinding.FragmentLoginBinding


class LoginFragment : BaseFragment<FragmentLoginBinding>() {
    override fun initBinding()=FragmentLoginBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
    }

    override fun reload() {

    }

}
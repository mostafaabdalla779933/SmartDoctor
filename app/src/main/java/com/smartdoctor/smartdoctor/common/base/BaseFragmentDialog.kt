package com.donationinstitutions.donationinstitutions.common.base

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import androidx.viewbinding.ViewBinding

abstract class BaseFragmentDialog<V : ViewBinding>() : DialogFragment() {

    lateinit var binding: V

    open lateinit var progressDialog: ProgressDialog

    abstract fun initBinding(): V

    abstract fun onDialogCreated()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        super.onCreateView(inflater, container, savedInstanceState)

        binding = initBinding()
        if (context != null) progressDialog = ProgressDialog(requireContext())


        dialog?.apply {
            window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            // window?.setBackgroundDrawable(ResourcesCompat.getDrawable(resources, R.drawable.trans,null))
            window?.clearFlags(WindowManager.LayoutParams.ANIMATION_CHANGED)
            setCancelable(false)
        }

        onDialogCreated()
        return binding.root
    }


    open fun showLoading() {
        progressDialog.show()
    }

    open fun hideLoading() {
        progressDialog.dismiss()
    }
}
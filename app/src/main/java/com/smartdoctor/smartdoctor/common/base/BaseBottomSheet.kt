package com.donationinstitutions.donationinstitutions.common.base

import android.app.Dialog
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewbinding.ViewBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

abstract class BaseBottomSheet<V : ViewBinding> : BottomSheetDialogFragment() {

    open lateinit var binding: V

    abstract fun initBinding(): V

    abstract fun onBottomSheetCreated()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = initBinding()
        onBottomSheetCreated()
        return binding.root
    }

    override fun onStart() {
        super.onStart()
        val sheetContainer = requireView().parent as? ViewGroup ?: return
        sheetContainer.layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return BottomSheetDialog(requireContext(), theme).apply {
            behavior.isFitToContents = false
            behavior.expandedOffset = getHeight()
            behavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
    }

    open fun getHeight(): Int = 56.dp

//    open fun getHeight(): Int = (Resources.getSystem().displayMetrics.heightPixels * 0.2).toInt()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
       // setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    private val Int.dp: Int
        get() = (this * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
}
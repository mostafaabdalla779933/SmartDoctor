package com.smartdoctor.smartdoctor.feature.inquiries

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import androidx.navigation.fragment.findNavController
import com.donationinstitutions.donationinstitutions.common.base.BaseFragmentDialog
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.firebase.data.UserModel
import com.smartdoctor.smartdoctor.common.firebase.data.UserState
import com.smartdoctor.smartdoctor.common.firebase.data.UserType
import com.smartdoctor.smartdoctor.common.showMessage
import com.smartdoctor.smartdoctor.databinding.FragmentTransferInquiryBinding
import com.smartdoctor.smartdoctor.databinding.SpinnerItemBinding

class TransferInquiryFragment : BaseFragmentDialog<FragmentTransferInquiryBinding>() {

    private var doctorsAdapter:DoctorsAdapter? = null
    override fun initBinding() = FragmentTransferInquiryBinding.inflate(layoutInflater)

    override fun onDialogCreated() {

        binding.apply {
            btnConfirm.setOnClickListener {
                findNavController().popBackStack()
            }

            ivClose.setOnClickListener {
                findNavController().popBackStack()
            }

            spinnerSpecialization.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
//                    spinnerDoctor.adapter = ArrayAdapter(
//                        requireContext(),
//                        R.layout.spinner_item,
//                        listOfDoctors
//                            .filter { doc -> doc.specialization == it.listOfSpecialties[position].name }
//                            .map { e -> e.name }
//                    )
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }

            spinnerDoctor.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parentView: AdapterView<*>?,
                    selectedItemView: View?,
                    position: Int,
                    id: Long
                ) {
//                    spinnerDoctor.adapter = ArrayAdapter(
//                        requireContext(),
//                        R.layout.spinner_item,
//                        listOfDoctors
//                            .filter { doc -> doc.specialization == it.listOfSpecialties[position].name }
//                            .map { e -> e.name }
//                    )
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }
        }
        getData()
    }


    private fun getData() {
        showLoading()
        FirebaseHelp.getAllObjects<UserModel>(FirebaseHelp.USERS, {
            hideLoading()
            doctorsAdapter = DoctorsAdapter(
                requireContext(),
                it.filter { e -> e.userId != FirebaseHelp.user?.userId && e.userState == UserState.Accepted.value && FirebaseHelp.user?.userType == UserType.Doctor.value }
                    .toMutableList()
            )

            binding.spinnerDoctor.adapter = doctorsAdapter

            binding.spinnerSpecialization.adapter = ArrayAdapter(
                requireContext(),
                R.layout.spinner_item,
                arrayOf("spec1","spec2")
            )
        }, {
            hideLoading()
            findNavController().popBackStack()
            requireContext().showMessage(it)
        })
    }


    override fun getTheme(): Int {
        super.getTheme()
        return R.style.DialogStyle
    }


}


class DoctorsAdapter(
    val context: Context,
    var list: MutableList<UserModel>
) : BaseAdapter() {


    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {

        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            SpinnerItemBinding.inflate(LayoutInflater.from(context), parent, false)
                .let { binding ->
                    view = binding.root
                    vh = ItemHolder(binding)
                    view.tag = vh
                }
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }

        vh.onBind(list[position], position)
        return view
    }

    override fun getItem(position: Int): UserModel {
        return list[position]
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    inner class ItemHolder(private val row: SpinnerItemBinding) {
        fun onBind(item: UserModel, position: Int) {
            row.tvName.text = item.name
        }
    }

}
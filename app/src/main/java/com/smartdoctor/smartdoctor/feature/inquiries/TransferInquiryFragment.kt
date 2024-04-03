package com.smartdoctor.smartdoctor.feature.inquiries

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.donationinstitutions.donationinstitutions.common.base.BaseFragmentDialog
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.firebase.data.NotificationModel
import com.smartdoctor.smartdoctor.common.firebase.data.SpecialtyModel
import com.smartdoctor.smartdoctor.common.firebase.data.UserModel
import com.smartdoctor.smartdoctor.common.firebase.data.UserState
import com.smartdoctor.smartdoctor.common.firebase.data.UserType
import com.smartdoctor.smartdoctor.common.showMessage
import com.smartdoctor.smartdoctor.databinding.FragmentTransferInquiryBinding
import com.smartdoctor.smartdoctor.databinding.SpinnerItemBinding
import com.smartdoctor.smartdoctor.feature.doctors.SpecialtiesSpinnerAdapter
import java.text.SimpleDateFormat
import java.util.Date

class TransferInquiryFragment : BaseFragmentDialog<FragmentTransferInquiryBinding>() {

    private var doctorsAdapter: DoctorsAdapter? = null
    private var specialtiesAdapter: SpecialtiesSpinnerAdapter? = null
    private var listOfSpecialties = mutableListOf<SpecialtyModel>()
    private var listOfDoctors = mutableListOf<UserModel>()
    private var selectedListOfDoctors = mutableListOf<UserModel>()
    private val args: TransferInquiryFragmentArgs by navArgs()
    override fun initBinding() = FragmentTransferInquiryBinding.inflate(layoutInflater)

    override fun onDialogCreated() {

        binding.apply {
            btnConfirm.setOnClickListener {
                selectedListOfDoctors.getOrNull(spinnerDoctor.selectedItemPosition)?.let { doctor ->

                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        "doctor",
                        doctor
                    )
                    findNavController().popBackStack()


                } ?: kotlin.run {
                    requireContext().showMessage("select doctor")
                }
            }

            ivClose.setOnClickListener {
                findNavController().popBackStack()
            }

            spinnerSpecialization.onItemSelectedListener =
                object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parentView: AdapterView<*>?,
                        selectedItemView: View?,
                        position: Int,
                        id: Long
                    ) {
                        listOfSpecialties.getOrNull(position)?.let { spec ->
                            doctorsAdapter?.list =
                                listOfDoctors.filter { e -> e.specialty?.hash == spec.hash }
                                    .toMutableList().also { selectedListOfDoctors = it }
                            doctorsAdapter?.notifyDataSetChanged()
                        }
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
                }

                override fun onNothingSelected(parentView: AdapterView<*>?) {}
            }
        }
        getDoctors()
    }


    private fun getDoctors() {
        showLoading()
        FirebaseHelp.getAllObjects<UserModel>(FirebaseHelp.USERS, {
            val list =
                it.filter { e -> e.userId != FirebaseHelp.user?.userId && e.userState == UserState.Accepted.value && e.userType == UserType.Doctor.value }
                    .toMutableList()
            listOfDoctors = list
            if (list.isEmpty()) {
                requireContext().showMessage("there is no doctors")
                findNavController().popBackStack()
                return@getAllObjects
            }
            getSpecialties()
            doctorsAdapter = DoctorsAdapter(
                requireContext(),
                list
            )
            binding.spinnerDoctor.adapter = doctorsAdapter

        }, {
            hideLoading()
            findNavController().popBackStack()
            requireContext().showMessage(it)
        })
    }


    private fun getSpecialties() {
        FirebaseHelp.getAllObjects<SpecialtyModel>(FirebaseHelp.Specialties, {
            hideLoading()
            if (it.isEmpty()) {
                requireContext().showMessage("there is no specialties")
                findNavController().popBackStack()
                return@getAllObjects
            }
            listOfSpecialties = it

            specialtiesAdapter = SpecialtiesSpinnerAdapter(
                requireContext(),
                it
            )
            binding.spinnerSpecialization.adapter = specialtiesAdapter

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
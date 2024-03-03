package com.smartdoctor.smartdoctor.feature.doctors

import androidx.navigation.fragment.findNavController
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.firebase.data.UserModel
import com.smartdoctor.smartdoctor.common.firebase.data.UserState
import com.smartdoctor.smartdoctor.common.firebase.data.UserType
import com.smartdoctor.smartdoctor.common.showMessage
import com.smartdoctor.smartdoctor.databinding.FragmentDoctorsBinding

class DoctorsFragment : BaseFragment<FragmentDoctorsBinding>() {


    private val adapter by lazy {
        DoctorsAdapter(onItemClicked = { doctor ->
//            findNavController().navigate(
//                TeachersFragmentDirections
//                    .actionTeachersFragmentToTeacherDetailsFragment(teacher)
//            )
        }, onEditClicked = { doctor ->

        }, onDeleteClicked = { doctor, position ->
            deleteTeacher(doctor, position)
        })
    }
    override fun initBinding() = FragmentDoctorsBinding.inflate(layoutInflater)
    override fun onFragmentCreated() {

        binding.apply {
            ivBack.setOnClickListener {
                findNavController().popBackStack()
            }

            ivAdd.setOnClickListener {
                findNavController().navigate(DoctorsFragmentDirections.actionDoctorsFragmentToAddDoctorFragment())
            }

            rvDoctors.adapter = adapter
        }
        getData()
    }

    override fun reload() {
        getData()
    }


    private fun getData() {
        showLoading()
        FirebaseHelp.getAllObjects<UserModel>(FirebaseHelp.USERS, {
            hideLoading()
            adapter.submitList(it.filter { user -> user.userType == UserType.Doctor.value && user.userState != UserState.Deleted.value })
        }, {
            hideLoading()
            requireContext().showMessage(it)
        })
    }


    private fun deleteTeacher(teacher: UserModel, position: Int) {
        showLoading()
        teacher.userState = UserState.Deleted.value
        FirebaseHelp.addObject<UserModel>(teacher,FirebaseHelp.USERS, teacher.userId ?: "", {
            hideLoading()
            val list = adapter.currentList.toMutableList()
            list.removeAt(position)
            adapter.submitList(list)
        }, {
            hideLoading()
            showErrorMsg(it)
        })
    }

}
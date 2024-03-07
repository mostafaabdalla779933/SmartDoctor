package com.smartdoctor.smartdoctor.feature.diseases

import androidx.navigation.fragment.findNavController
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.firebase.data.DiseaseModel
import com.smartdoctor.smartdoctor.databinding.FragmentPatientDiseasesBinding

class PatientDiseasesFragment : BaseFragment<FragmentPatientDiseasesBinding>() {

    private val adapter by lazy {
        DiseasesAdapter(list = mutableListOf(), onItemClick = { disease ->

        }, onDeleteClicked = { disease ->
        })
    }
    override fun initBinding() = FragmentPatientDiseasesBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {

        binding.apply {
            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            rvDiseases.adapter = adapter
        }
        getData()
    }


    private fun getData() {
        showLoading()
        FirebaseHelp.getAllObjects<DiseaseModel>(FirebaseHelp.DISEASE, {
            hideLoading()
            adapter.list = it
            adapter.notifyDataSetChanged()

        }, {
            hideLoading()
            showErrorMsg(it)
        })
    }


}
package com.smartdoctor.smartdoctor.feature.disease_dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.donationinstitutions.donationinstitutions.common.base.BaseFragmentDialog
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.firebase.data.QuestionModel
import com.smartdoctor.smartdoctor.databinding.FragmentDiseaseDialogBinding
import com.smartdoctor.smartdoctor.databinding.ItemInstructionsBinding
import com.smartdoctor.smartdoctor.databinding.ItemScanDiseaseQuestionBinding
import com.smartdoctor.smartdoctor.feature.diseases.ScanQuestionAdapter

class DiseaseDialogFragment : BaseFragmentDialog<FragmentDiseaseDialogBinding>() {

    private val args:DiseaseDialogFragmentArgs by navArgs()

    private val adapter by lazy {
        InstructionsAdapter(list = mutableListOf())
    }

    override fun initBinding() = FragmentDiseaseDialogBinding.inflate(layoutInflater)

    override fun onDialogCreated() {
        binding.apply {

            Glide.with(requireContext()).load(args.disease.profileUrl).into(ivDisease)

            rvInstructions.adapter = adapter
            tvName.text = args.disease.name
            tvDiseaseState.text = if(args.flag) "You are suffering from" else "You don't suffering from"
            if(args.flag){
                adapter.list = args.disease.treatments ?: mutableListOf()
                adapter.notifyDataSetChanged()
            }else {
                rvInstructions.visibility = View.GONE
                tvTitle.visibility = View.GONE
            }

            btnHome.setOnClickListener {
                findNavController().popBackStack(R.id.userDashboardFragment,false)
            }
        }
    }

    override fun getTheme(): Int {
        super.getTheme()
        return R.style.DialogStyle
    }

}


class InstructionsAdapter(
    var list: MutableList<String>
) :
    RecyclerView.Adapter<InstructionsAdapter.ViewHolder>() {

    inner class ViewHolder(private val rowView: ItemInstructionsBinding) :
        RecyclerView.ViewHolder(rowView.root) {
        fun onBind(item: String, position: Int) {
            rowView.apply {
                tvTreatment.text = item
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemInstructionsBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position], position)
    }
}
package com.smartdoctor.smartdoctor.feature.diseases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.firebase.data.DiagnoseModel
import com.smartdoctor.smartdoctor.common.firebase.data.QuestionModel
import com.smartdoctor.smartdoctor.databinding.FragmentHealthCenterDiseaseDetailsBinding
import com.smartdoctor.smartdoctor.databinding.ItemDiseaseBinding
import com.smartdoctor.smartdoctor.databinding.ItemHealthSymptomBinding
import com.smartdoctor.smartdoctor.databinding.ItemInstructionsBinding

class HealthCenterDiseaseDetailsFragment : BaseFragment<FragmentHealthCenterDiseaseDetailsBinding>() {

    private val args : HealthCenterDiseaseDetailsFragmentArgs by navArgs()
    private val questionsAdapter: QuestionsAdapter by lazy {
        QuestionsAdapter(mutableListOf())
    }
    private val diagnosisAdapter: DiagnosisAdapter by lazy {
        DiagnosisAdapter(mutableListOf())
    }
    private val treatmentsAdapter: TreatmentsAdapter by lazy {
        TreatmentsAdapter(mutableListOf())
    }

    override fun initBinding() = FragmentHealthCenterDiseaseDetailsBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        binding.apply {
            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            tb.title = args.disease.name
            rvSymptoms.adapter = questionsAdapter
            questionsAdapter.list = args.disease.questions ?: mutableListOf()
            questionsAdapter.notifyDataSetChanged()

            rvDiseases.adapter = diagnosisAdapter
            diagnosisAdapter.list = args.disease.diagnoses ?: mutableListOf()
            diagnosisAdapter.notifyDataSetChanged()

            rvInstructions.adapter = treatmentsAdapter
            treatmentsAdapter.list = args.disease.treatments ?: mutableListOf()
            treatmentsAdapter.notifyDataSetChanged()
        }
    }
}

class QuestionsAdapter(var list: MutableList<QuestionModel>) :
    RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {

    inner class ViewHolder(private var rowView: ItemHealthSymptomBinding) :
        RecyclerView.ViewHolder(rowView.root) {

        fun onBind(item: QuestionModel) {
            rowView.apply {
                tvQuestion.text = item.question
                tvAnswer.text = if(item.answer == true) "Yes" else "No"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHealthSymptomBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }
}

class DiagnosisAdapter(var list: MutableList<DiagnoseModel>) :
    RecyclerView.Adapter<DiagnosisAdapter.ViewHolder>() {

    inner class ViewHolder(private var rowView: ItemDiseaseBinding) :
        RecyclerView.ViewHolder(rowView.root) {

        fun onBind(item: DiagnoseModel) {
            rowView.apply {
                ivDelete.visibility = View.GONE
                tvYesNumber.text = item.numberOfQuestion.toString()
              //  tvDiseaseName.text = item.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemDiseaseBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(list[position])
    }
}

class TreatmentsAdapter(var list: MutableList<String>) :
    RecyclerView.Adapter<TreatmentsAdapter.ViewHolder>() {

    inner class ViewHolder(private var rowView: ItemInstructionsBinding) :
        RecyclerView.ViewHolder(rowView.root) {

        fun onBind(item: String) {
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
        holder.onBind(list[position])
    }
}
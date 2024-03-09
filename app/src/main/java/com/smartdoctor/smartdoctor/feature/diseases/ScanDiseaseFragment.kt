package com.smartdoctor.smartdoctor.feature.diseases


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.firebase.data.QuestionModel
import com.smartdoctor.smartdoctor.databinding.FragmentScanDiseaseBinding
import com.smartdoctor.smartdoctor.databinding.ItemScanDiseaseQuestionBinding


class ScanDiseaseFragment : BaseFragment<FragmentScanDiseaseBinding>() {

    private val args: ScanDiseaseFragmentArgs by navArgs()
    private val adapter by lazy {
        ScanQuestionAdapter(list = mutableListOf())
    }

    override fun initBinding() = FragmentScanDiseaseBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {

        args.disease.apply {
            binding.apply {
                rvDiseases.adapter = adapter

                adapter.list =
                    questions?.map { it.copy(answer = null) }?.toMutableList() ?: mutableListOf()
                adapter.notifyDataSetChanged()
                tb.setNavigationOnClickListener {
                    findNavController().popBackStack()
                }
                tb.title = name

                btnConfirm.setOnClickListener {
                    when {
                        adapter.list.none { e -> e.answer == null }.not() -> {
                            showErrorMsg("answer all questions")
                        }

                        args.disease.diagnoses?.none { e -> e.numberOfQuestion == adapter.list.filter { e -> e.answer == true }.size } == false  -> {
                            findNavController().navigate(R.id.diseaseDialogFragment, bundleOf("flag" to true,"disease" to args.disease))
                        }

                        else -> {
                            findNavController().navigate(R.id.diseaseDialogFragment, bundleOf("flag" to false,"disease" to args.disease))
                        }

                    }

                }

            }
        }

    }

}


class ScanQuestionAdapter(
    var list: MutableList<QuestionModel>
) :
    RecyclerView.Adapter<ScanQuestionAdapter.ViewHolder>() {

    inner class ViewHolder(private val rowView: ItemScanDiseaseQuestionBinding) :
        RecyclerView.ViewHolder(rowView.root) {
        fun onBind(item: QuestionModel, position: Int) {
            rowView.apply {
                tvQuestion.text = item.question
                radioGroup.setOnCheckedChangeListener { radioGroup, i ->
                    item.answer = i == yes.id
                }

                yes.isChecked = radioGroup.checkedRadioButtonId == yes.id
                no.isChecked = radioGroup.checkedRadioButtonId == no.id
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemScanDiseaseQuestionBinding.inflate(
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
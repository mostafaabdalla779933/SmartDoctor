package com.smartdoctor.smartdoctor.feature.add_symptoms

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.firebase.data.DiagnoseModel
import com.smartdoctor.smartdoctor.common.firebase.data.DiseaseModel
import com.smartdoctor.smartdoctor.common.firebase.data.QuestionModel
import com.smartdoctor.smartdoctor.common.getInt
import com.smartdoctor.smartdoctor.common.getString
import com.smartdoctor.smartdoctor.common.isStringEmpty
import com.smartdoctor.smartdoctor.common.setImageFromUri
import com.smartdoctor.smartdoctor.common.showMessage
import com.smartdoctor.smartdoctor.databinding.FragmentAddSymptomsBinding
import com.smartdoctor.smartdoctor.databinding.ItemDiseaseBinding
import com.smartdoctor.smartdoctor.databinding.ItemSymptomQuestionBinding
import com.smartdoctor.smartdoctor.databinding.ItemTreatmentBinding

class AddSymptomsFragment : BaseFragment<FragmentAddSymptomsBinding>() {


    private var selectedUri: Uri? = null
    private val questionsAdapter: QuestionsAdapter by lazy {
        QuestionsAdapter(mutableListOf())
    }
    private val diagnosisAdapter: DiagnosisAdapter by lazy {
        DiagnosisAdapter(mutableListOf())
    }
    private val treatmentsAdapter: TreatmentsAdapter by lazy {
        TreatmentsAdapter(mutableListOf())
    }

    override fun initBinding() = FragmentAddSymptomsBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        binding.apply {
            ivTakePhoto.setOnClickListener {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    chooseUserPhotoFromGallery33()
                } else {
                    chooseUserPhotoFromGallery()
                }
            }

            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            btnInquiry.setOnClickListener {
                when {
                    etQuestions.isStringEmpty() -> {
                        showErrorMsg("fill question")
                    }

                    radioGroup.checkedRadioButtonId == -1 -> {
                        showErrorMsg("invalid answer")
                    }

                    else -> {
                        questionsAdapter.list.add(
                            QuestionModel(
                                question = etQuestions.getString(),
                                answer = yes.isChecked
                            )
                        )
                        questionsAdapter.notifyDataSetChanged()
                        etQuestions.setText("")
                    }
                }
            }
            rvSymptoms.adapter = questionsAdapter

            btnAddDisease.setOnClickListener {
                when {
                    etNumber.isStringEmpty() -> {
                        showErrorMsg("fill yes number")
                    }

                    else -> {
                        diagnosisAdapter.list.add(
                            DiagnoseModel(
                                numberOfQuestion = etNumber.getInt()
                            )
                        )
                        diagnosisAdapter.notifyDataSetChanged()
                        etNumber.setText("")
                        etDiseaseName.setText("")
                    }
                }
            }
            rvDiseases.adapter = diagnosisAdapter

            btnAddTreatment.setOnClickListener {
                when {
                    etTreatment.isStringEmpty() -> {
                        showErrorMsg("fill yes number")
                    }

                    else -> {
                        treatmentsAdapter.list.add(
                            etTreatment.getString()
                        )
                        treatmentsAdapter.notifyDataSetChanged()
                        etTreatment.setText("")
                    }
                }
            }
            rvTreatments.adapter = treatmentsAdapter

            selectedUri?.let {
                ivSymptom.setImageFromUri(it, requireContext())
            }

            btnSave.setOnClickListener {
                validate()
            }
        }
    }

    private fun validate() {
        binding.apply {
            when {
                selectedUri == null -> {
                    showErrorMsg("select photo")
                }

                etName.isStringEmpty() -> {
                    showErrorMsg("fill name")
                }

                questionsAdapter.list.isEmpty() ->{
                    showErrorMsg("fill Questions")
                }

                diagnosisAdapter.list.isEmpty() ->{
                    showErrorMsg("fill Diagnosis")
                }

                treatmentsAdapter.list.isEmpty() ->{
                    showErrorMsg("fill treatments")
                }

                else -> {
                    val intent = Intent(requireContext(), AddDiseaseService::class.java)
                    val disease = DiseaseModel(
                        hash = System.currentTimeMillis().toString(),
                        uri = selectedUri,
                        name = etName.getString(),
                        mobile = etNumber.getString(),
                        questions = questionsAdapter.list,
                        diagnoses = diagnosisAdapter.list,
                        treatments = treatmentsAdapter.list
                    )
                    intent.putExtra(FirebaseHelp.DISEASE, disease)

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        requireContext().startForegroundService(intent);
                    } else {
                        requireContext().startService(intent);
                    }
                    requireContext().showMessage("uploading your data")
                    findNavController().popBackStack()
                }
            }
        }

    }

    @SuppressLint("InlinedApi")
    private fun chooseUserPhotoFromGallery33() {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_MEDIA_IMAGES
                    )
                )
            } else {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startForGallery.launch(intent)
            }
        } catch (e: Exception) {
            showErrorMsg("something went wrong")
        }
    }


    private fun chooseUserPhotoFromGallery() {
        try {
            if (ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED
            ) {

                requestMultiplePermissions.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    )
                )
            } else {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)

                startForGallery.launch(intent)
            }
        } catch (e: Exception) {
            showErrorMsg("something went wrong")
        }
    }

    private val requestMultiplePermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            var accept = false
            permissions.entries.forEach {
                accept = it.value
            }
            if (accept) {
                startForGallery.launch(
                    Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                    )
                )
            } else {
                showErrorMsg("permission denied")
            }
        }

    private val startForGallery =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    selectedUri = uri
                    binding.ivSymptom.setImageFromUri(uri, requireContext())
                }
            }
        }

}


class QuestionsAdapter(var list: MutableList<QuestionModel>) :
    RecyclerView.Adapter<QuestionsAdapter.ViewHolder>() {

    inner class ViewHolder(private var rowView: ItemSymptomQuestionBinding) :
        RecyclerView.ViewHolder(rowView.root) {

        fun onBind(item: QuestionModel) {
            rowView.apply {
                tvQuestion.text = item.question
                tvAnswer.text = if(item.answer == true) "Yes" else "No"
                ivDelete.setOnClickListener {
                    list.removeAt(layoutPosition)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSymptomQuestionBinding.inflate(
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
                tvYesNumber.text = item.numberOfQuestion.toString()
              //  tvDiseaseName.text = item.name
                ivDelete.setOnClickListener {
                    list.removeAt(layoutPosition)
                    notifyDataSetChanged()
                }
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

    inner class ViewHolder(private var rowView: ItemTreatmentBinding) :
        RecyclerView.ViewHolder(rowView.root) {

        fun onBind(item: String) {
            rowView.apply {
                tvTreatment.text = item
                ivDelete.setOnClickListener {
                    list.removeAt(layoutPosition)
                    notifyDataSetChanged()
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemTreatmentBinding.inflate(
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
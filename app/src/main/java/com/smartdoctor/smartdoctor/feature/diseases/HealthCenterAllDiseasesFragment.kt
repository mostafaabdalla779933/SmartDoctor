package com.smartdoctor.smartdoctor.feature.diseases

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.firebase.data.DiseaseModel
import com.smartdoctor.smartdoctor.common.firebase.data.UserType
import com.smartdoctor.smartdoctor.common.navigateWithAnimation
import com.smartdoctor.smartdoctor.databinding.FragmentHealthCenterAllDiseasesBinding
import com.smartdoctor.smartdoctor.databinding.ItemHealthCenterDiseaseBinding


class HealthCenterAllDiseasesFragment : BaseFragment<FragmentHealthCenterAllDiseasesBinding>() {


    private val adapter by lazy {
        DiseasesAdapter(list = mutableListOf(), onItemClick = { disease ->

        }, onDeleteClicked = { disease ->
            deleteItem(disease)
        })
    }

    override fun initBinding() = FragmentHealthCenterAllDiseasesBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        binding.apply {
            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }

            rvDiseases.adapter = adapter
        }
        initMenu()

        getData()
    }

    override fun reload() {
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


    private fun deleteItem(diseaseModel: DiseaseModel) {
        showLoading()
        FirebaseHelp.deleteObject<DiseaseModel>(
            FirebaseHelp.DISEASE,
            diseaseModel.hash ?: "",
            {
                hideLoading()
                adapter.list.remove(diseaseModel)
                adapter.notifyDataSetChanged()

            },
            {
                hideLoading()
                showMessage(it)
            })
    }

    private fun initMenu() {
        setHasOptionsMenu(true)
        binding.tb.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.add -> {
                    findNavController().navigateWithAnimation(
                        R.id.addSymptomsFragment
                    )
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }
        }
    }

}


class DiseasesAdapter(
    var list: MutableList<DiseaseModel>,
    val onItemClick: (DiseaseModel) -> Unit,
    val onDeleteClicked: (DiseaseModel) -> Unit
) :
    RecyclerView.Adapter<DiseasesAdapter.ViewHolder>() {

    inner class ViewHolder(private val rowView: ItemHealthCenterDiseaseBinding) :
        RecyclerView.ViewHolder(rowView.root) {
        fun onBind(item: DiseaseModel, position: Int) {
            rowView.apply {

                if(FirebaseHelp.user?.userType != UserType.HealthCenter.value){
                    ivDelete.visibility = View.GONE
                }

                root.setOnClickListener {
                    onItemClick(item)
                }

                ivDelete.setOnClickListener {
                    onDeleteClicked(item)
                }
                Glide.with(itemView.context).load(item.profileUrl).into(ivDisease)
                tvName.text = item.name
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHealthCenterDiseaseBinding.inflate(
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
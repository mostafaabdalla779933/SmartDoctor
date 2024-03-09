package com.smartdoctor.smartdoctor.feature.specialties

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.donationinstitutions.donationinstitutions.common.firebase.FirebaseHelp
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.firebase.data.DiseaseModel
import com.smartdoctor.smartdoctor.common.firebase.data.SpecialtyModel
import com.smartdoctor.smartdoctor.common.navigateWithAnimation
import com.smartdoctor.smartdoctor.databinding.FragmentAllSpecialtiesBinding
import com.smartdoctor.smartdoctor.databinding.ItemSpecialtiesBinding


class AllSpecialtiesFragment : BaseFragment<FragmentAllSpecialtiesBinding>() {

    val adapter by lazy {
        SpecialtiesAdapter { item, position ->
            deleteItem(item)
        }
    }

    override fun initBinding() = FragmentAllSpecialtiesBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        binding.apply {
            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
            rvSpecialties.adapter = adapter

        }

        initMenu()
        getData()
    }

    override fun reload() {
        getData()
    }

    private fun getData() {
        showLoading()
        FirebaseHelp.getAllObjects<SpecialtyModel>(FirebaseHelp.Specialties, {
            hideLoading()
            adapter.submitList(it)
        }, {
            hideLoading()
            showMessage(it)
        })
    }

    private fun deleteItem(specialtyModel: SpecialtyModel) {
        showLoading()
        FirebaseHelp.deleteObject<DiseaseModel>(
            FirebaseHelp.Specialties,
            specialtyModel.hash ?: "",
            {
                hideLoading()
                val list = adapter.currentList.toMutableList()
                list.remove(specialtyModel)
                adapter.submitList(list)
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
                        R.id.addSpecialtiesFragment
                    )
                    true
                }

                else -> super.onOptionsItemSelected(item)
            }
        }
    }

}


class SpecialtiesAdapter(
    val onDeleteClicked: (SpecialtyModel, Int) -> Unit
) :
    ListAdapter<SpecialtyModel, SpecialtiesAdapter.ViewHolder>(
        UserDiffCallback()
    ) {

    inner class ViewHolder(private val rowView: ItemSpecialtiesBinding) :
        RecyclerView.ViewHolder(rowView.root) {
        fun onBind(item: SpecialtyModel, position: Int) {
            rowView.apply {

                tvName.text = item.name
                Glide.with(itemView.context).load(item.url).into(ivImage)
                ivDelete.setOnClickListener {
                    onDeleteClicked(item, position)
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemSpecialtiesBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.onBind(getItem(position), position)
    }

    class UserDiffCallback : DiffUtil.ItemCallback<SpecialtyModel>() {
        override fun areItemsTheSame(oldItem: SpecialtyModel, newItem: SpecialtyModel): Boolean {
            return oldItem.hash == newItem.hash
        }

        override fun areContentsTheSame(oldItem: SpecialtyModel, newItem: SpecialtyModel): Boolean {
            return oldItem == newItem
        }
    }
}
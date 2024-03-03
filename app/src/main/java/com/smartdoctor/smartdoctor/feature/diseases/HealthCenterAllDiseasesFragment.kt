package com.smartdoctor.smartdoctor.feature.diseases

import androidx.navigation.fragment.findNavController
import com.smartdoctor.smartdoctor.R
import com.smartdoctor.smartdoctor.common.base.BaseFragment
import com.smartdoctor.smartdoctor.common.navigateWithAnimation
import com.smartdoctor.smartdoctor.databinding.FragmentHealthCenterAllDiseasesBinding

class HealthCenterAllDiseasesFragment : BaseFragment<FragmentHealthCenterAllDiseasesBinding>() {

    override fun initBinding() = FragmentHealthCenterAllDiseasesBinding.inflate(layoutInflater)

    override fun onFragmentCreated() {
        binding.apply {
            tb.setNavigationOnClickListener {
                findNavController().popBackStack()
            }
        }
        initMenu()
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
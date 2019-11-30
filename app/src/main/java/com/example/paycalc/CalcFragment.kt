package com.example.paycalc

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.paycalc.databinding.FragmentCalcBinding


class CalcFragment : Fragment() {

    private lateinit var binding: FragmentCalcBinding

    /**
     * Lazily initialize our [MainViewModel].
     */
    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this.activity!!).get(MainViewModel::class.java)
    }


    companion object {
        fun newInstance() = CalcFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_calc, container, false)

        subscribeUi()

        return binding.root
    }

    override fun onResume() {
        // Reload the input data from the view model
        binding.etRwage.setText(viewModel.regularWages.toString())
        binding.etSwage.setText(viewModel.supplementalWages.toString())
        binding.etPficaWage.setText(viewModel.preFICAWages.toString())
        binding.etPtaxWage.setText(viewModel.preTaxWages.toString())

        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.menu_calc, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_calc -> actionCalc()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun actionCalc(): Boolean {
        val regularWages = binding.etRwage.text.toString().toFloat()
        val supplementalWages = binding.etSwage.text.toString().toFloat()

        val preFICAWages = binding.etPficaWage.text.toString().toFloat()
        val preTaxWages = binding.etPtaxWage.text.toString().toFloat()

        viewModel.regularWages = regularWages
        viewModel.supplementalWages = supplementalWages
        viewModel.preFICAWages = preFICAWages
        viewModel.preTaxWages = preTaxWages

        viewModel.calc()

        return true
    }

    private fun subscribeUi() {
        viewModel.totalWages.observe(this, Observer {
            binding.totalWagesValue.text = it.toString()
        })

        viewModel.oasdiTax.observe(this, Observer {
            binding.oasdiValue.text = it.toString()
        })

        viewModel.medicareTax.observe(this, Observer {
            binding.medicareValue.text = it.toString()
        })

        viewModel.additionalMedicareTax.observe(this, Observer {
            binding.additionalMedicareValue.text = it.toString()
        })

        viewModel.federalTax.observe(this, Observer {
            binding.federalValue.text = it.toString()
        })

        viewModel.stateTax.observe(this, Observer {
            binding.stateValue.text = it.toString()
        })

        viewModel.netPay.observe(this, Observer {
            binding.netPayValue.text = it.toString()
        })
    }

}

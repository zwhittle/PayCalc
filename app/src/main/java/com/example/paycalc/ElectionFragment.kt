package com.example.paycalc


import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.paycalc.databinding.FragmentElectionBinding

/**
 * A simple [Fragment] subclass.
 */
class ElectionFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var binding: FragmentElectionBinding
    private var selectedFrequency = ""
    private var selectedState = ""
    private var selectedFedStatus = ""
    private var selectedAlabamaExemption = ""
    private var selectedArizonaPercentage = ""

    private val viewModel: MainViewModel by lazy {
        ViewModelProviders.of(this.activity!!).get(MainViewModel::class.java)
    }

    companion object {
        fun newInstance() = CalcFragment()
        private const val LOG_TAG = "ElectionFragment"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        setHasOptionsMenu(true)
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_election, container,false)

        subscribeUi()

        initializeSpinners()

        return binding.root
    }

    override fun onResume() {
        // Reload the input data from the view model

        binding.etFedAllowances.setText(viewModel.fedAllowances.toString())
        binding.etFedAddlAmount.setText(viewModel.fedAdditionalAmount.toString())

        selectedFrequency = viewModel.taxFrequency
        val frequencies = resources.getStringArray(R.array.frequencies)
        val frequenciesPosition = frequencies.indexOf(selectedFrequency)
        binding.spinnerFrequency.setSelection(frequenciesPosition)

        selectedState = viewModel.stateElectionState
        val states = resources.getStringArray(R.array.states)
        val statesPosition = states.indexOf(selectedState)
        binding.spinnerState.setSelection(statesPosition)

        selectedFedStatus = viewModel.fedMaritalStatus
        val fedStatuses = resources.getStringArray(R.array.fed_marital_statuses)
        val fedStatusesPosition = fedStatuses.indexOf(selectedFedStatus)
        binding.spinnerFedMaritalStatus.setSelection(fedStatusesPosition)

        // Dependents
        binding.etDependents.setText(viewModel.dependents.toString())

        // Alabama Exemption
        selectedAlabamaExemption = viewModel.alabamaExemption
        val alabamaExemptions = resources.getStringArray(R.array.alabama_exemptions)
        val alabamaExemptionsPosition = alabamaExemptions.indexOf(selectedAlabamaExemption)
        binding.spinnerAlabamaExemption.setSelection(alabamaExemptionsPosition)

        // Arizona ConstantRate
        selectedArizonaPercentage = viewModel.arizonaConstantRate
        val arizonaPercentages = resources.getStringArray(R.array.arizona_percentages)
        val arizonaPercentagesPosition = arizonaPercentages.indexOf(selectedArizonaPercentage)
        binding.spinnerArizonaPercentage.setSelection(arizonaPercentagesPosition)

        super.onResume()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_election, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> actionSave()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun actionSave(): Boolean {

        val fedAllowances = binding.etFedAllowances.text.toString().toInt()
        val fedAdditionalAmount = binding.etFedAddlAmount.text.toString().toFloat()

        // Update Frequency
        viewModel.taxFrequency = selectedFrequency

        // Update Federal Election data
        viewModel.fedMaritalStatus = selectedFedStatus
        viewModel.fedAllowances = fedAllowances
        viewModel.fedAdditionalAmount = fedAdditionalAmount

        // Update State Election data
        viewModel.stateElectionState = selectedState

        // Dependents
        val dependents = binding.etDependents.text.toString().toInt()
        viewModel.dependents = dependents

        // Alabama
        viewModel.alabamaExemption = selectedAlabamaExemption

        // Arizona
        viewModel.arizonaConstantRate = selectedArizonaPercentage

        return true
    }

    private fun initializeSpinners() {
        initializeFrequencySpinner()
        initializeStateSpinner()
        initializeFedMaritalSpinner()
        initializeAlabamaExemptionSpinner()
        initializeArizonaConstantPercentageSpinner()
    }

    private fun initializeFrequencySpinner() {
        val frequencySpinner = binding.spinnerFrequency

        ArrayAdapter.createFromResource(binding.root.context, R.array.frequencies, android.R.layout.simple_spinner_item).also { adapter ->
            frequencySpinner.adapter = adapter
        }

        frequencySpinner.onItemSelectedListener = this
    }

    private fun initializeFedMaritalSpinner() {
        val fedMaritalSpinner = binding.spinnerFedMaritalStatus

        ArrayAdapter.createFromResource(binding.root.context, R.array.fed_marital_statuses, android.R.layout.simple_spinner_item).also { adapter ->
            fedMaritalSpinner.adapter = adapter
        }

        fedMaritalSpinner.onItemSelectedListener = this
    }

    private fun initializeStateSpinner() {
        val stateSpinner = binding.spinnerState
        ArrayAdapter.createFromResource(binding.root.context, R.array.states, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            stateSpinner.adapter = adapter
        }
        stateSpinner.onItemSelectedListener = this
    }

    private fun initializeAlabamaExemptionSpinner() {
        val alabamaExemptionSpinner = binding.spinnerAlabamaExemption

        ArrayAdapter.createFromResource(binding.root.context, R.array.alabama_exemptions, android.R.layout.simple_spinner_item).also { adapter ->
            alabamaExemptionSpinner.adapter = adapter
        }

        alabamaExemptionSpinner.onItemSelectedListener = this
    }

    private fun initializeArizonaConstantPercentageSpinner() {
        val arizonaPercentageSpinner = binding.spinnerArizonaPercentage

        ArrayAdapter.createFromResource(binding.root.context, R.array.arizona_percentages, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            arizonaPercentageSpinner.adapter = adapter
        }

        arizonaPercentageSpinner.onItemSelectedListener = this
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        when (parent?.id) {
            R.id.spinner_frequency -> onFrequencySelected(position)
            R.id.spinner_state -> onStateSelected(position)
            R.id.spinner_fed_marital_status -> onFedMaritalSelected(position)
            R.id.spinner_alabama_exemption -> onAlabamaExemptionSelected(position)
            R.id.spinner_arizona_percentage -> onArizonaPercentageSpinnerSelected(position)
        }
    }

    private fun onFrequencySelected(position: Int) {
        val frequencies = resources.getStringArray(R.array.frequencies)
        val frequency = frequencies[position]

        selectedFrequency = frequency
    }

    private fun onStateSelected(position: Int) {
        val states = resources.getStringArray(R.array.states)
        val state = states[position]

        if (state == Constants.States.ALABAMA) {
            binding.labelAlabamaExemption.visibility = View.VISIBLE
            binding.spinnerAlabamaExemption.visibility = View.VISIBLE

            binding.labelDependents.visibility = View.VISIBLE
            binding.etDependents.visibility = View.VISIBLE
        } else {
            binding.labelAlabamaExemption.visibility = View.GONE
            binding.spinnerAlabamaExemption.visibility = View.GONE

            binding.labelDependents.visibility = View.GONE
            binding.etDependents.visibility = View.GONE
        }

        if (state == Constants.States.ARIZONA) {
            binding.labelArizonaPercent.visibility = View.VISIBLE
            binding.spinnerArizonaPercentage.visibility = View.VISIBLE
        } else {
            binding.labelArizonaPercent.visibility = View.GONE
            binding.spinnerArizonaPercentage.visibility = View.GONE
        }

        if (state == Constants.States.ARKANSAS) {
            binding.labelDependents.visibility = View.VISIBLE
            binding.etDependents.visibility = View.VISIBLE
        } else {
            binding.labelDependents.visibility = View.GONE
            binding.labelDependents.visibility = View.GONE
        }

        selectedState = state
    }

    private fun onFedMaritalSelected(position: Int) {
        val statuses = resources.getStringArray(R.array.fed_marital_statuses)
        val status = statuses[position]

        selectedFedStatus = status
    }

    private fun onAlabamaExemptionSelected(position: Int) {
        val exemptions = resources.getStringArray(R.array.alabama_exemptions)
        val exemption = exemptions[position]

        selectedAlabamaExemption = exemption
    }

    private fun onArizonaPercentageSpinnerSelected(position: Int) {
        val percentages = resources.getStringArray(R.array.arizona_percentages)
        val percentage = percentages[position]

        selectedArizonaPercentage = percentage
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        // Do nothing
    }

    private fun subscribeUi() {

    }
}

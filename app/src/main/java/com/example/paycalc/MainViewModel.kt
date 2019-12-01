package com.example.paycalc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.paycalc.taxes.federal.AdditionalMedicare
import com.example.paycalc.taxes.federal.FederalWithholding
import com.example.paycalc.taxes.federal.Medicare
import com.example.paycalc.taxes.federal.OASDI
import com.example.paycalc.taxes.state.AlabamaStateWithholding
import com.example.paycalc.taxes.state.StateWithholding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MainViewModel : ViewModel() {

    /**
     * Election values
     */
    // Frequency
    var taxFrequency: String = Constants.Frequencies.BIWEEKLY

    // Federal Martial Status
    var fedMaritalStatus: String = Constants.FederalMaritalStatuses.SINGLE

    // Federal Allowances
    var fedAllowances: Int = 0

    // Federal Additional Amount
    var fedAdditionalAmount: Float = 0f

    // State
    var stateElectionState: String = Constants.States.ILLINOIS

    // State Marital Status
    var stateMaritalStatus: String? = null

    // State Allowances
    var stateAllowances: Int? = 0

    // State Additional Amount
    var stateAdditionalAmount: Float = 0f

    // Alabama Exemptions
    var alabamaExemption: String = Constants.AlabamaExemptions.MARRIED_FILING_JOINTLY

    // Alabama Dependents
    var alabamaDependents: Int = 2

    /**
     * Calculation Values
     */

    // Regular Wages
    var regularWages: Float = 0f

    // Supplemental Wages
    var supplementalWages: Float = 0f

    // Total Wages
    private val _grossWages = MutableLiveData<Float>()
    val grossWages: LiveData<Float>
        get() = _grossWages

    // Pre-FICA Wages
    var preFICAWages: Float = 0f

    // Pre-Tax Wages
    var preTaxWages: Float = 0f

    // OASDI Wages
    private val _oasdiWages = MutableLiveData<Float>()
    val oasdiWages: LiveData<Float>
        get() = _oasdiWages

    // OASDI
    private val _oasdiTax = MutableLiveData<Float>()
    val oasdiTax: LiveData<Float>
        get() = _oasdiTax

    // Medicare Wages
    private val _medicareWages = MutableLiveData<Float>()
    val medicareWages: LiveData<Float>
        get() = _medicareWages

    // Medicare
    private val _medicareTax = MutableLiveData<Float>()
    val medicareTax: LiveData<Float>
        get() = _medicareTax

    // Additional Medicare Wages
    private val _additionalMedicareWages = MutableLiveData<Float>()
    val additionalMedicareWages: LiveData<Float>
        get() = _additionalMedicareWages

    // Additional Medicare Tax
    private val _additionalMedicareTax = MutableLiveData<Float>()
    val additionalMedicareTax: LiveData<Float>
        get() = _additionalMedicareTax

    // Federal Regular Taxable Wages
    private val _federalRegularWages = MutableLiveData<Float>()
    val federalRegularwages: LiveData<Float>
        get() = _federalRegularWages

    // Federal Supplemental Taxable Wages
    private val _federalSupplementalWages = MutableLiveData<Float>()
    val federalSupplementalWages: LiveData<Float>
        get() = _federalSupplementalWages

    // Federal Tax
    private val _federalTax = MutableLiveData<Float>()
    val federalTax: LiveData<Float>
        get() = _federalTax

    // State Taxable Wages
    private val _stateWages = MutableLiveData<Float>()
    val stateWages: LiveData<Float>
        get() = _stateWages

    // State Tax
    private val _stateTax = MutableLiveData<Float>()
    val stateTax: LiveData<Float>
        get() = _stateTax

    // Total Tax
    private val _totaltax = MutableLiveData<Float>()
    val totalTax: LiveData<Float>
        get() = _totaltax

    // Net Pay
    private val _netPay = MutableLiveData<Float>()
    val netPay: LiveData<Float>
        get() = _netPay

    // Create a Coroutine scope using a job to be able to cancel when needed
    private var viewModelJob = Job()

    // the Coroutine runs using the Main (UI) dispatcher
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main)

    fun calc() {
        calcGrossWages()
        calcTaxes()
        calcNetPay()
    }

    private fun calcGrossWages() {
        val regWages = regularWages
        val suppWages = supplementalWages

        _grossWages.value = regWages + suppWages
    }

    private fun calcStateWages() {
        val twages = _grossWages.value
        val ptWages = preTaxWages

        _stateWages.value = twages?.minus(ptWages)
    }

    private fun calcTaxes() {
        var total = 0f

        val oasdi = calcOASDI()
        total += oasdi

        val medicare = calcMedicare()
        total += medicare

        val addlMedicare = calcAdditionalMedicare()
        total += addlMedicare

        val federal = calcFederalTax()
        total += federal

        val state = calcStateTax()
        total += state

        _totaltax.value = total
    }

    private fun calcOASDI(): Float {
        val tax = OASDI(
            regularWages,
            supplementalWages,
            preFICAWages
        ).result()

        _oasdiTax.value = tax
        return tax
    }

    private fun calcMedicare(): Float {
        val tax = Medicare(
            regularWages,
            supplementalWages,
            preFICAWages
        ).result()

        _medicareTax.value = tax
        return tax
    }

    private fun calcAdditionalMedicare(): Float {
        val tax = AdditionalMedicare(
            regularWages,
            supplementalWages,
            preFICAWages
        ).result()

        _additionalMedicareTax.value = tax
        return tax
    }

    private fun calcStateTax(): Float {
        val tax = if (stateElectionState == Constants.States.ALABAMA) {
            AlabamaStateWithholding(
                frequency = taxFrequency,
                regWages = regularWages,
                supWages = supplementalWages,
                deductions = preTaxWages,
                exemption = alabamaExemption,
                fedAmount = _federalTax.value ?: 0f,
                dependents = alabamaDependents
            ).result()
        } else {

            StateWithholding(
                state = stateElectionState,
                frequency = taxFrequency,
                regWages = regularWages,
                supWages = supplementalWages,
                deductions = preTaxWages
            ).result()
        }

        _stateTax.value = tax
        return tax
    }

    private fun calcFederalTax(): Float {
        val tax = FederalWithholding(
            frequency = taxFrequency,
            maritalStatus = fedMaritalStatus,
            allowances = fedAllowances,
            addlAmount = fedAdditionalAmount,
            regWages = regularWages,
            supWages = supplementalWages,
            deductions = preTaxWages
        ).result()

        _federalTax.value = tax
        return tax
    }

    private fun calcNetPay(): Float {
        val gross = _grossWages.value ?: 0f
        val deductions = preTaxWages
        val taxes = _totaltax.value ?: 0f

        val net = (gross.minus(deductions)).minus(taxes)

        _netPay.value = net
        return net
    }
}
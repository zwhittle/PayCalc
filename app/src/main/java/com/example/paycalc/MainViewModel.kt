package com.example.paycalc

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job

class MainViewModel : ViewModel() {

    /**
     * Election values
     */

    // Federal Martial Status
    var fedMaritalStatus: String = "Single"

    // Federal Allowances
    var fedAllowances: Int = 0

    // Federal Additional Amount
    var fedAdditionalAmount: Float = 0f

    // State
    var stateElectionState: String = "Illinois"

    // State Marital Status
    var stateMaritalStatus: String? = null

    // State Allowances
    var stateAllowances: Int? = 0

    // State Additional Amount
    var stateAdditionalAmount: Float = 0f

    /**
     * Calculation Values
     */

    // Regular Wages
    var regularWages: Float

    // Supplemental Wages
    var supplementalWages: Float

    // Total Wages
    private val _totalWages = MutableLiveData<Float>()
    val totalWages: LiveData<Float>
        get() = _totalWages

    // Pre-FICA Wages
    var preFICAWages: Float

    // Pre-Tax Wages
    var preTaxWages: Float

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
    private val coroutineScope = CoroutineScope(viewModelJob + Dispatchers.Main )

    init {
        // initialize calculation values
        regularWages = 0f
        supplementalWages = 0f
        preFICAWages = 0f
        preTaxWages = 0f
    }

    fun updateRegularWages(wages: Float) {
        regularWages = wages
    }

    fun updateSupplementalWages(wages: Float) {
        supplementalWages = wages
    }

    fun updatePreFICAWages(wages: Float) {
        preFICAWages = wages
    }

    fun updatePreTaxWages(wages: Float) {
        preTaxWages = wages
    }

    fun calc() {
        calcTotalWages()
        calcTaxes()
        calcNetPay()
    }

    private fun calcTotalWages() {
        val regWages = regularWages
        val suppWages = supplementalWages

        _totalWages.value = regWages + suppWages
    }

    private fun calcOASDIWages() {
        val twages = _totalWages.value
        val pfWages = preFICAWages

        var ssaWages = twages?.minus(pfWages)
        if (ssaWages != null) {
            if (ssaWages > 132900f) {
                ssaWages = 132900f
            }
        }

        _oasdiWages.value = ssaWages
    }

    private fun calcMedicareWages() {
        val twages = _totalWages.value
        val pfWages = preFICAWages

        val medWages = twages?.minus(pfWages)

        _medicareWages.value = medWages
    }

    private fun calcAdditionalMedicareWages() {
        val twages = _totalWages.value
        val pfWages = preFICAWages
        val threshold = 200000f

        val ficaWages = twages?.minus(pfWages)
        var addMedWages = ficaWages?.minus(threshold)

        if (addMedWages != null) {
            if (addMedWages < 0f) {
                addMedWages = 0f
            }
        }

        _additionalMedicareWages.value = addMedWages
    }

    private fun calcFedRegWages() {
        val twages = _totalWages.value
        val regWages = regularWages
        val ptWages = preTaxWages

        val pct = regWages.div(twages!!)
        val adjPreTaxWages = ptWages.times(pct)

        _federalRegularWages.value = regWages.minus(adjPreTaxWages)
    }

    private fun calcFedSuppWages() {
        val twages = _totalWages.value
        val suppWages = supplementalWages
        val ptWages = preTaxWages

        val pct = suppWages.div(twages!!)
        val adjPreTaxWages = ptWages.times(pct)

        _federalSupplementalWages.value = suppWages.minus(adjPreTaxWages)
    }

    private fun calcFederalWages() {
        calcFedRegWages()
        calcFedSuppWages()
    }

    private fun calcStateWages() {
        val twages = _totalWages.value
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
        calcOASDIWages()
        val wages = _oasdiWages.value

        val tax = wages?.times(0.062f)  ?: 0f

        _oasdiTax.value = tax
        return tax
    }

    private fun calcMedicare(): Float {
        calcMedicareWages()
        val wages = _medicareWages.value

        val tax = wages?.times(0.0145f) ?: 0f

        _medicareTax.value = tax
        return tax
    }

    private fun calcAdditionalMedicare(): Float {
        calcAdditionalMedicareWages()
        val wages = _additionalMedicareWages.value

        val tax = wages?.times(0.009f) ?: 0f

        _additionalMedicareTax.value = tax
        return tax
    }

    private fun calcStateTax(): Float {
        calcStateWages()
        val wages = _stateWages.value ?: 0f

        val tax = calcStateTaxFlat(wages, stateElectionState)

        _stateTax.value = tax
        return tax
    }

    private fun calcStateTaxFlat(wages: Float, state: String): Float {

        val rate = when (state) {
            Constants.States.COLORADO -> 0.0463f
            Constants.States.ILLINOIS -> 0.0495f
            Constants.States.INDIANA -> 0.0323f
            Constants.States.KENTUCKY -> 0.05f
            Constants.States.MASSACHUSETTS -> 0.0505f
            Constants.States.MICHIGAN -> 0.0425f
            Constants.States.NORTH_CAROLINA -> 0.0525f
            Constants.States.PENNSYLVANIA -> 0.0307f
            Constants.States.UTAH -> 0.0495f
            else -> 0f
        }

        return wages * rate
    }

    private fun calcFederalTax(): Float {
        calcFederalWages()

        val regWages = _federalRegularWages.value ?: 0f
        val suppWages = _federalSupplementalWages.value ?: 0f

        val suppTax = suppWages.times(0.22f)

        val regTax = calcFederalRegularTax(regWages)

        val additionalAmount = fedAdditionalAmount

        val tax = regTax + suppTax + additionalAmount

        _federalTax.value = tax
        return tax
    }

    private fun calcFederalRegularTax(wages: Float): Float {

        return when (fedMaritalStatus) {
            Constants.FederalMaritalStatuses.SINGLE -> fedRegTaxBWSingle(wages)
            Constants.FederalMaritalStatuses.MARRIED -> fedRegTaxBWMarried(wages)
            Constants.FederalMaritalStatuses.MARRIED_SINGLE -> fedRegTaxBWSingle(wages)
            else -> 0f
        }
    }
    
    private fun fedRegTaxBWSingle(wages: Float): Float {
        val allowanceReduction = fedAllowances * 161.50f
        val adjWages = wages - allowanceReduction


        return when {
            adjWages <= 146f -> 0f
            adjWages <= 519f -> (adjWages - 146f) * 0.1f
            adjWages <= 1664f -> 37.30f + ((adjWages - 519f) * 0.12f)
            adjWages <= 3385f -> 174.70f + ((adjWages - 1664f) * 0.22f)
            adjWages <= 6328f -> 553.32f + ((adjWages - 3385f) * 0.24f)
            adjWages <= 7996f -> 1259.64f + ((adjWages - 6328f) * 0.32f)
            adjWages <= 19773f -> 1793.40f + ((adjWages - 7996f) * 0.35f)
            else -> 5915.35f + ((adjWages - 19773f) * 0.37f)
        }
    }

    private fun fedRegTaxBWMarried(wages: Float): Float {
        val allowanceReduction = fedAllowances * 161.50f
        val adjWages = wages - allowanceReduction
        
        return when {
            adjWages <= 454f -> 0f
            adjWages <= 1200f -> (adjWages - 454f) * 0.1f
            adjWages <= 3490f -> 74.60f + ((adjWages - 1200f) * 0.12f)
            adjWages <= 6931f -> 349.40f + ((adjWages - 3490f) * 0.22f)
            adjWages <= 12817f -> 1106.42f + ((adjWages - 6931f) * 0.24f)
            adjWages <= 16154f -> 2519.06f + ((adjWages - 12817f) * 0.32f)
            adjWages <= 24006f -> 3586.90f + ((adjWages - 16154f) * 0.35f)
            else -> 6335.10f + ((adjWages - 24006f) * 0.37f)
        }
    }

    private fun calcNetPay(): Float {
        val gross = _totalWages.value ?: 0f
        val deductions = preTaxWages
        val taxes = _totaltax.value ?: 0f

        val net = (gross.minus(deductions)).minus(taxes)

        _netPay.value = net
        return net
    }
}
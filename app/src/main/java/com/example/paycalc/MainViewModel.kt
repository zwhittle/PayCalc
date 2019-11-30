package com.example.paycalc

import android.content.Intent
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
        calcOASDI()
        calcMedicare()
        calcAdditionalMedicare()
        calcFederalTax()
        calcStateTax()
    }

    private fun calcTotalWages() {
        val regWages = regularWages
        val suppWages = supplementalWages

        _totalWages.value = regWages + suppWages
    }

    private fun calcOASDIWages() {
        val twages = _totalWages.value
        val pfWages = preFICAWages

        var ssaWages = twages?.minus(pfWages!!)
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

        val medWages = twages?.minus(pfWages!!)

        _medicareWages.value = medWages
    }

    private fun calcAdditionalMedicareWages() {
        val twages = _totalWages.value
        val pfWages = preFICAWages
        val threshold = 200000f

        val ficaWages = twages?.minus(pfWages!!)
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

        val pct = regWages?.div(twages!!)
        val adjPreTaxWages = ptWages?.times(pct!!)

        _federalRegularWages.value = regWages?.minus(adjPreTaxWages!!)
    }

    private fun calcFedSuppWages() {
        val twages = _totalWages.value
        val suppWages = supplementalWages
        val ptWages = preTaxWages

        val pct = suppWages?.div(twages!!)
        val adjPreTaxWages = ptWages?.times(pct!!)

        _federalSupplementalWages.value = suppWages?.minus(adjPreTaxWages!!)
    }

    private fun calcFederalWages() {
        calcFedRegWages()
        calcFedSuppWages()
    }

    private fun calcStateWages() {
        val twages = _totalWages.value
        val ptWages = preTaxWages

        _stateWages.value = twages?.minus(ptWages!!)
    }

    private fun calcOASDI() {
        calcOASDIWages()
        val wages = _oasdiWages.value
        _oasdiTax.value = wages?.times(0.062f)
    }

    private fun calcMedicare() {
        calcMedicareWages()
        val wages = _medicareWages.value
        _medicareTax.value = wages?.times(0.0145f)
    }

    private fun calcAdditionalMedicare() {
        calcAdditionalMedicareWages()
        val wages = _additionalMedicareWages.value
        _additionalMedicareTax.value = wages?.times(0.009f)
    }

    private fun calcStateTax() {
        calcStateWages()
        val wages = _stateWages.value

        val state = "Illinois"

        val rate = when (state) {
            "Illinois" -> 0.0495f
            else -> 0f
        }

        _stateTax.value = wages?.times(rate)
    }

    private fun calcFederalTax() {
        calcFederalWages()

        val regWages = _federalRegularWages.value
        val suppWages = _federalSupplementalWages.value

        val suppTax = suppWages?.times(0.22f)

        val regTax: Float = when {
            regWages!! <= 146f -> 0f
            regWages <= 519f -> (regWages - 146f) * 0.1f
            regWages <= 1664f -> 37.30f + ((regWages - 519f) * 0.12f)
            regWages <= 3385f -> 174.70f + ((regWages - 1664f) * 0.22f)
            regWages <= 6328f -> 553.32f + ((regWages - 3385f) * 0.24f)
            regWages <= 7996f -> 1259.64f + ((regWages - 6328f) * 0.32f)
            regWages <= 19773 -> 1793.40f + ((regWages - 7996f) * 0.35f)
            else -> 5915.35f + ((regWages - 19773f) * 0.37f)
        }

        _federalTax.value = regTax + suppTax!!
    }

    fun swapFragments(intent: Intent) {

    }
}
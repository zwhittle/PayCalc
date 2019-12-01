package com.example.paycalc.taxes

import android.util.Log

//interface TaxInterface {
//    var result: Float
//    var hasFlatRate: Boolean
//    var flatRate: Float
//    var hasWagesLimit: Boolean
//    var wagesLimit: Float
//    var hasAmountLimit: Boolean
//    var amountLimit: Float
//
//    fun calcWages()
//    fun calcAmount()
//    fun calcRegularAmount()
//    fun calcFlatRateAmount() : Float
//    fun result() : Float
//}

abstract class Tax(private val regWages: Float, private val supWages: Float, private val deductions: Float) {

    private var grossWages = 0f
    private var subjectWages = 0f
    var regularTaxableWages = 0f
    var supplementalTaxableWages = 0f
    var taxableWages = 0f
    open var hasSupplementalTax = false
    open var supplementalRate = 0f
    open var amount = 0f
    open var hasFlatRate = false
    open var flatRate = 0f
    open var hasWagesLimit = false
    open var wagesLimit = 0f
    open var hasWagesFloor = false
    open var wagesFloor = 0f
    open var hasAmountLimit = false
    open var amountLimit = 0f

    private val logTag: String = this.javaClass.simpleName

    private fun calcWages() {
        grossWages = regWages + supWages
        subjectWages = grossWages - deductions

        if (hasWagesFloor && subjectWages < wagesFloor) {
            subjectWages = 0f
        }

        taxableWages = if (hasWagesLimit && subjectWages > wagesLimit) {
            wagesLimit
        } else {
            subjectWages
        }

        regularTaxableWages = taxableWages * (regWages / grossWages)
        supplementalTaxableWages = taxableWages * (supWages / grossWages)


        Log.d(logTag, "gross: $grossWages")
        Log.d(logTag, "subject: $subjectWages")
        Log.d(logTag, "taxable: $taxableWages")
        Log.d(logTag, "regTaxable: $regularTaxableWages")
        Log.d(logTag, "supTaxable: $supplementalTaxableWages")
    }

    private fun calcAmount() {
        calcWages()

        var tax = if (hasFlatRate) {
            calcFlatRateAmount()
        } else {
            calcRegularAmount()
        }

        if (hasSupplementalTax) {
            tax += calcSupplementalAmount()
        }

        amount = if (hasAmountLimit && tax > amountLimit) {
            amountLimit
        } else  {
            tax
        }
    }

    open fun calcRegularAmount(): Float {
        return 0f
    }

    open fun calcSupplementalAmount(): Float {
        return 0f
    }

    private fun calcFlatRateAmount(): Float {
        return taxableWages * flatRate
    }

    fun result(): Float {
        calcAmount()
        return amount
    }
}
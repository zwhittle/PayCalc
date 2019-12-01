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

/**
 * Tax parent class intended to be extended by most individual taxes. Stores a number of properties
 * and functions that are common across taxes
 */

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

    // Calculate gross, subject, and taxable wages using the passed raw wage values.
    // Adjusts the resulting subject wages if the tax has a wage floor (ex: Additional Medicare)
    // Adjusts the resulting taxable wages if the tax has a wage limit (ex: OASDI)
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

    // Calculates the actual amount of the tax
    // Not meant to be overridden; override the calcRegularAmount and calcSupplementalAmount
    // functions instead
    private fun calcAmount() {
        calcWages()

        var regularTax = if (hasFlatRate) {
            calcFlatRateAmount()
        } else {
            calcRegularAmount()
        }

        val supplementalTax = if (hasSupplementalTax) {
            calcSupplementalAmount()
        } else {
            0f
        }

        regularTax = if (hasAmountLimit && regularTax > amountLimit) {
            amountLimit
        } else  {
            regularTax
        }

        amount = regularTax + supplementalTax
    }

    // Intended to be overridden by the child Tax to define the calculation for any regular tax
    open fun calcRegularAmount(): Float {
        return 0f
    }

    // Intended to be overridden by the child Tax to define the calculation for any supplemental tax
    open fun calcSupplementalAmount(): Float {
        return 0f
    }

    // No need to override any amount calculations if the tax is a simple flat rate
    // Just set hasFlatRate to true and flatRate to the appropriate rate
    private fun calcFlatRateAmount(): Float {
        return taxableWages * flatRate
    }

    // Call this function to run all the calculations and return the amount to withhold
    fun result(): Float {
        calcAmount()
        return amount
    }
}
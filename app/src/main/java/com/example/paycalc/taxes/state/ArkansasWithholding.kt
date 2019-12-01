package com.example.paycalc.taxes.state

import android.util.Log
import com.example.paycalc.Utils
import com.example.paycalc.taxtables.state.Arkansas.Withholding
import kotlin.math.round

/**
 * State Withholding for Arkansas
 * Does not inherit Tax because it has a specific calculation
 * Uses Dependents from the State Election
 */
class ArkansasWithholding(
    frequency: String,
    private val exemptions: Int,
    private val regWages: Float,
    private val supWages: Float,
    private val deductions: Float) {

    // Fields for use in calculations
    private var annualGross = 0f
    private var periodsPerYear = 0

    // Arkansas has a flat standard deduction
    private var standardDeduction = 2200f

    // Log tag
    private var logTag = this.javaClass.simpleName

    // Calculate and store the annual gross wages and # of periods per year for later use
    init {
        Log.d(logTag, "frequency: $frequency")
        periodsPerYear = Utils.periodsPerYear(frequency)

        annualGross = annualGrossTaxableWages()
    }

    // Calculate the taxable wages for the current period
    private fun currentTaxableWages(): Float {
        val taxableWages = regWages + supWages - deductions

        Log.d(logTag, "currentTaxableWages: $taxableWages")

        return taxableWages
    }

    // Convert the current period taxable wages to an annual amount
    private fun annualGrossTaxableWages(): Float {
        val grossWages = currentTaxableWages() * periodsPerYear

        Log.d(logTag, "annualGrossTaxableWages: $grossWages")

        return grossWages
    }

    // Calculate the net taxable income
    private fun netTaxableIncome(): Float {
        var netTaxableIncome = annualGross - standardDeduction

        if (netTaxableIncome < 50000f) {
            // Do some weird shit to adjust wages to be at the midpoint of the $100 range
            // example $23,324 becomes $23,350

            val rangeLowReduced = round(netTaxableIncome / 100)
            Log.d(logTag, "rangeHighReduced: $rangeLowReduced")

            val rangeLow = rangeLowReduced * 100
            Log.d(logTag, "rangeHigh: $rangeLow")

            val rangeMid = rangeLow + 50f
            Log.d(logTag, "rangeMid: $rangeMid")

            netTaxableIncome = rangeMid
        }

        Log.d(logTag, "netTaxableIncome: $netTaxableIncome")

        return netTaxableIncome
    }

    // Calculate the annual gross tax using the appropriate tax table
    private fun annualGrossTax(): Float {
        val wages = netTaxableIncome()
        val rate: Float
        val minusAdj: Float

        when {
            wages < Withholding.UPPER_ONE -> {
                rate = Withholding.PCT_ONE
                minusAdj = Withholding.MINUS_ONE
            }
            wages < Withholding.UPPER_TWO -> {
                rate = Withholding.PCT_TWO
                minusAdj = Withholding.MINUS_TWO
            }
            wages < Withholding.UPPER_THREE -> {
                rate = Withholding.PCT_THREE
                minusAdj = Withholding.MINUS_THREE
            }
            wages < Withholding.UPPER_FOUR -> {
                rate = Withholding.PCT_FOUR
                minusAdj = Withholding.MINUS_FOUR
            }
            wages < Withholding.UPPER_FIVE -> {
                rate = Withholding.PCT_FIVE
                minusAdj = Withholding.MINUS_FIVE
            }
            else -> {
                rate = Withholding.PCT_SIX
                minusAdj = Withholding.MINUS_SIX
            }
        }

        var tax = (wages * rate) - minusAdj

        tax = round(tax)

        Log.d(logTag, "tax: $tax")

        return tax
    }

    // Calculate the annual personal credits amount (exemptions * $26)
    private fun annualPersonalCredits(): Float {
        val credits = exemptions * 26f

        Log.d(logTag, "credits: $credits")

        return credits
    }

    // Calculate the annual net tax (annual gross tax - annual personal credits)
    private fun annualNetTax(): Float {
        val netTax = annualGrossTax() - annualPersonalCredits()

        Log.d(logTag, "netTax: $netTax")

        return netTax
    }

    // Call this function to run all the calcs and return the final amount
    fun result(): Float {
        val finalTax = annualNetTax() / periodsPerYear

        Log.d(logTag, "finalTax: $finalTax")

        return finalTax
    }
}
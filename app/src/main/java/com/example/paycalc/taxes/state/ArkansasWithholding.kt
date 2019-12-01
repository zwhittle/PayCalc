package com.example.paycalc.taxes.state

import android.util.Log
import com.example.paycalc.Utils
import com.example.paycalc.brackets.state.Arkansas.Withholding
import kotlin.math.round

class ArkansasWithholding(
    frequency: String,
    private val exemptions: Int,
    private val regWages: Float,
    private val supWages: Float,
    private val deductions: Float) {

    private var annualGross = 0f
    private var periodsPerYear = 0

    private var standardDeduction = 2200f

    private var logTag = this.javaClass.simpleName

    init {
        Log.d(logTag, "frequency: $frequency")
        periodsPerYear = Utils.periodsPerYear(frequency)

        annualGross = annualGrossTaxableWages()
    }

    private fun taxableWages(): Float {
        val taxableWages = regWages + supWages - deductions

        Log.d(logTag, "taxableWages: $taxableWages")

        return taxableWages
    }

    private fun annualGrossTaxableWages(): Float {
        val grossWages = taxableWages() * periodsPerYear

        Log.d(logTag, "annualGrossTaxableWages: $grossWages")

        return grossWages
    }

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

    private fun annualPersonalCredits(): Float {
        val credits = exemptions * 26f

        Log.d(logTag, "credits: $credits")

        return credits
    }

    private fun annualNetTax(): Float {
        val netTax = annualGrossTax() - annualPersonalCredits()

        Log.d(logTag, "netTax: $netTax")

        return netTax
    }

    fun result(): Float {
        val finalTax = annualNetTax() / periodsPerYear

        Log.d(logTag, "finalTax: $finalTax")

        return finalTax
    }
}
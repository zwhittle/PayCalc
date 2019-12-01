package com.example.paycalc.taxes.state

import android.util.Log
import com.example.paycalc.Constants
import com.example.paycalc.Utils
import com.example.paycalc.brackets.state.Alabama.*

class AlabamaStateWithholding(
    frequency: String,
    private val regWages: Float,
    private val supWages: Float,
    private val deductions: Float,
    private val exemption: String,
    private val fedAmount: Float,
    private val dependents: Int
) {

    private var amount = 0f
    private var gross = 0f
    private var periodsPerYear = 0

    private val logTag: String = this.javaClass.simpleName

    init {
        Log.d(logTag, "frequency: $frequency")
        periodsPerYear = Utils.periodsPerYear(frequency)

        gross = grossWages()
    }

    private fun calc() {
        amount = computeTax() / periodsPerYear

        Log.d(logTag, "amount: $amount")
    }

    private fun grossTaxableWages(): Float {
        val totalReductions = standardDeduction() + federalAmount() + personalExemption() + dependentsReduction()

        Log.d(logTag, "totalReductions: $totalReductions")

        val grossTaxableWages = gross - totalReductions

        Log.d(logTag, "grossTaxableWages: $grossTaxableWages")
        return grossTaxableWages
    }

    private fun taxableWages(): Float {
        val taxableWages = regWages + supWages - deductions

        Log.d(logTag, "taxableWages: $taxableWages")

        return taxableWages
    }

    private fun grossWages(): Float {
        val grossWages = taxableWages() * periodsPerYear

        Log.d(logTag, "grossWages: $grossWages")

        return grossWages
    }

    private fun standardDeduction(): Float {
        val standardDeduction =  when (exemption) {
            Constants.AlabamaExemptions.ZERO -> stdDedZeroSingle()
            Constants.AlabamaExemptions.SINGLE -> stdDedZeroSingle()
            Constants.AlabamaExemptions.MARRIED_FILING_SEPARATELY -> {
                stdDedMS()
            }
            Constants.AlabamaExemptions.MARRIED_FILING_JOINTLY -> {
                stdDedM()
            }
            Constants.AlabamaExemptions.HEAD_OF_FAMILY -> {
                stdDedH()
            }
            else -> 0f
        }

        Log.d(logTag, "standardDeduction: $standardDeduction")

        return standardDeduction
    }

    private fun stdDedZeroSingle(): Float {
        val stdDedZeroSingle =  when {
            gross <= StdDedZeroSingle.UPPER_ONE -> StdDedZeroSingle.MAX_DEDUCTION
            gross < StdDedZeroSingle.UPPER_TWO -> {
                val excess = gross - StdDedZeroSingle.UPPER_TWO
                val increments = excess / StdDedZeroSingle.INCREMENT_STEP
                val reduction = increments * StdDedZeroSingle.INCREMENT_VALUE
                return StdDedZeroSingle.MAX_DEDUCTION - reduction
            }
            else -> StdDedZeroSingle.MIN_DEDUCTION
        }

        Log.d(logTag, "stdDedZeroSingle: $stdDedZeroSingle")

        return stdDedZeroSingle
    }

    private fun stdDedMS(): Float {
        val stdDedMS = when {
            gross <= StdDedMS.UPPER_ONE -> StdDedMS.MAX_DEDUCTION
            gross < StdDedMS.UPPER_TWO -> {
                val excess = gross - StdDedMS.UPPER_TWO
                val increments = excess / StdDedMS.INCREMENT_STEP
                val reduction = increments * StdDedMS.INCREMENT_VALUE
                return StdDedMS.MAX_DEDUCTION - reduction
            }
            else -> StdDedMS.MIN_DEDUCTION
        }

        Log.d(logTag, "stdDedMS: $stdDedMS")

        return stdDedMS
    }

    private fun stdDedM(): Float {
        val stdDedM = when {
            gross <= StdDedM.UPPER_ONE -> StdDedM.MAX_DEDUCTION
            gross < StdDedM.UPPER_TWO -> {
                val excess = gross - StdDedM.UPPER_TWO
                val increments = excess / StdDedM.INCREMENT_STEP
                val reduction = increments * StdDedM.INCREMENT_VALUE
                return StdDedM.MAX_DEDUCTION - reduction
            }
            else -> StdDedM.MIN_DEDUCTION
        }

        Log.d(logTag, "stdDedM: $stdDedM")

        return stdDedM
    }

    private fun stdDedH(): Float {
        val stdDedH = when {
            gross <= StdDedH.UPPER_ONE -> StdDedH.MAX_DEDUCTION
            gross < StdDedH.UPPER_TWO -> {
                val excess = gross - StdDedH.UPPER_TWO
                val increments = excess / StdDedH.INCREMENT_STEP
                val reduction = increments * StdDedH.INCREMENT_VALUE
                return StdDedH.MAX_DEDUCTION - reduction
            }
            else -> StdDedH.MIN_DEDUCTION
        }

        Log.d(logTag, "stdDedH: $stdDedH")

        return stdDedH
    }

    private fun federalAmount(): Float {
        val federalAmount = fedAmount * periodsPerYear

        Log.d(logTag, "federalAmount: $federalAmount")

        return federalAmount
    }

    private fun personalExemption(): Float {
        val personalExemption = when (exemption) {
            Constants.AlabamaExemptions.ZERO -> 0f
            Constants.AlabamaExemptions.SINGLE -> 1500f
            Constants.AlabamaExemptions.MARRIED_FILING_SEPARATELY -> 1500f
            Constants.AlabamaExemptions.MARRIED_FILING_JOINTLY -> 3000f
            Constants.AlabamaExemptions.HEAD_OF_FAMILY -> 3000f
            else -> 0f
        }

        Log.d(logTag, "personalExemption: $personalExemption")

        return personalExemption
    }

    private fun dependentsReduction(): Float {
        val dependentsReduction = when {
            gross <= 20000f -> dependents * 1000f
            gross <= 100000f -> dependents * 500f
            else -> dependents * 300f
        }

        Log.d(logTag, "dependentsReduction: $dependentsReduction")

        return dependentsReduction
    }

    private fun computeTax(): Float {
        val tax = if (exemption == Constants.AlabamaExemptions.MARRIED_FILING_JOINTLY) {
            computeTaxM()
        } else {
            computeTaxZSOMS()
        }

        Log.d(logTag, "tax: $tax")

        return tax
    }

    private fun computeTaxZSOMS(): Float {
        var remainingWages = grossTaxableWages()
        var tax1 = 0f
        var tax2 = 0f
        var tax3 = 0f

        if (remainingWages > WHZSOMS.AMOUNT_ONE) {
            tax1 += WHZSOMS.AMOUNT_ONE * WHZSOMS.PCT_ONE
            remainingWages -= WHZSOMS.AMOUNT_ONE
            Log.d(logTag, "tax1: $tax1")
        }

        if (remainingWages > WHZSOMS.AMOUNT_TWO) {
            tax2 += WHZSOMS.AMOUNT_TWO * WHZSOMS.PCT_TWO
            remainingWages -= WHZSOMS.AMOUNT_TWO
            Log.d(logTag, "tax2: $tax2")
        } else {
            tax2 += (remainingWages - WHZSOMS.AMOUNT_TWO) * WHZSOMS.PCT_TWO
            remainingWages -= remainingWages
            Log.d(logTag, "tax2: $tax2")
        }

        tax3 += remainingWages * WHZSOMS.PCT_THREE
        Log.d(logTag, "tax3: $tax3")

        val tax = tax1 + tax2 + tax3
        Log.d(logTag, "computeTaxZSOMS: $tax")

        return tax
    }

    private fun computeTaxM(): Float {
        var remainingWages = grossTaxableWages()
        var tax1 = 0f
        var tax2 = 0f

        if (remainingWages > WHM.AMOUNT_ONE) {
            tax1 += WHM.AMOUNT_ONE * WHM.PCT_ONE
            remainingWages -= WHM.AMOUNT_ONE

            Log.d(logTag, "tax1: $tax1")
        }

        if (remainingWages > WHM.AMOUNT_TWO) {
            tax2 += WHM.AMOUNT_TWO * WHM.PCT_TWO
            remainingWages -= WHM.AMOUNT_TWO
            Log.d(logTag, "tax2: $tax2")
        } else {
            tax2 += (remainingWages - WHM.AMOUNT_TWO) * WHM.PCT_TWO
            Log.d(logTag, "tax2: $tax2")
            remainingWages -= remainingWages
        }

        val tax3 = remainingWages * WHM.PCT_THREE
        Log.d(logTag, "tax3: $tax3")

        val tax = tax1 + tax2 + tax3

        Log.d(logTag, "computeTaxM: $tax")

        return tax
    }

    fun result(): Float {
        calc()
        return amount
    }

}
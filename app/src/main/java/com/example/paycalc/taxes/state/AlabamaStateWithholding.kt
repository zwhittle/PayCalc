package com.example.paycalc.taxes.state

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

    init {
        gross = grossWages()
        periodsPerYear = Utils.periodsPerYear(frequency)
    }

    private fun calc() {
        amount = computeTax() / periodsPerYear
    }

    private fun grossTaxableWages(): Float {
        val totalReductions = standardDeduction() + fedAmount + personalExemption() + dependentsReduction()
        return grossWages() - totalReductions
    }

    private fun taxableWages(): Float {
        return regWages + supWages - deductions
    }

    private fun grossWages(): Float {
        return taxableWages() * periodsPerYear
    }

    private fun standardDeduction(): Float {
        return when (exemption) {
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
    }

    private fun stdDedZeroSingle(): Float {
        return when {
            gross <= StdDedZeroSingle.UPPER_ONE -> StdDedZeroSingle.MAX_DEDUCTION
            gross < StdDedZeroSingle.UPPER_TWO -> {
                val excess = gross - StdDedZeroSingle.UPPER_TWO
                val increments = excess / StdDedZeroSingle.INCREMENT_STEP
                val reduction = increments * StdDedZeroSingle.INCREMENT_VALUE
                return StdDedZeroSingle.MAX_DEDUCTION - reduction
            }
            else -> StdDedZeroSingle.MIN_DEDUCTION
        }
    }

    private fun stdDedMS(): Float {
        return when {
            gross <= StdDedMS.UPPER_ONE -> StdDedMS.MAX_DEDUCTION
            gross < StdDedMS.UPPER_TWO -> {
                val excess = gross - StdDedMS.UPPER_TWO
                val increments = excess / StdDedMS.INCREMENT_STEP
                val reduction = increments * StdDedMS.INCREMENT_VALUE
                return StdDedMS.MAX_DEDUCTION - reduction
            }
            else -> StdDedMS.MIN_DEDUCTION
        }
    }

    private fun stdDedM(): Float {
        return when {
            gross <= StdDedM.UPPER_ONE -> StdDedM.MAX_DEDUCTION
            gross < StdDedM.UPPER_TWO -> {
                val excess = gross - StdDedM.UPPER_TWO
                val increments = excess / StdDedM.INCREMENT_STEP
                val reduction = increments * StdDedM.INCREMENT_VALUE
                return StdDedM.MAX_DEDUCTION - reduction
            }
            else -> StdDedM.MIN_DEDUCTION
        }
    }

    private fun stdDedH(): Float {
        return when {
            gross <= StdDedH.UPPER_ONE -> StdDedH.MAX_DEDUCTION
            gross < StdDedH.UPPER_TWO -> {
                val excess = gross - StdDedH.UPPER_TWO
                val increments = excess / StdDedH.INCREMENT_STEP
                val reduction = increments * StdDedH.INCREMENT_VALUE
                return StdDedH.MAX_DEDUCTION - reduction
            }
            else -> StdDedH.MIN_DEDUCTION
        }
    }

    private fun personalExemption(): Float {
        return when (exemption) {
            Constants.AlabamaExemptions.ZERO -> 0f
            Constants.AlabamaExemptions.SINGLE -> 1500f
            Constants.AlabamaExemptions.MARRIED_FILING_SEPARATELY -> 1500f
            Constants.AlabamaExemptions.MARRIED_FILING_JOINTLY -> 3000f
            Constants.AlabamaExemptions.HEAD_OF_FAMILY -> 3000f
            else -> 0f
        }
    }

    private fun dependentsReduction(): Float {
        return when {
            gross <= 20000f -> dependents * 1000f
            gross <= 100000f -> dependents * 500f
            else -> dependents * 300f
        }
    }

    private fun computeTax(): Float {
        val tax = 0f
        val remainingWages = grossWages()

        return if (exemption == Constants.AlabamaExemptions.MARRIED_FILING_JOINTLY) {
            computeTaxM(remainingWages, tax)
        } else {
            computeTaxZSOMS(remainingWages, tax)
        }
    }

    private fun computeTaxZSOMS(remainingWages: Float, tax: Float): Float {
        var remainingWages = remainingWages
        var tax = tax

        if (remainingWages > WHZeroSOMS.AMOUNT_ONE) {
            tax += WHZeroSOMS.AMOUNT_ONE * WHZeroSOMS.PCT_ONE
            remainingWages -= WHZeroSOMS.AMOUNT_ONE
        }

        if (remainingWages > WHZeroSOMS.AMOUNT_TWO) {
            tax += WHZeroSOMS.AMOUNT_TWO * WHZeroSOMS.PCT_TWO
            remainingWages -= WHZeroSOMS.AMOUNT_TWO
        } else {
            tax += (remainingWages - WHZeroSOMS.AMOUNT_TWO) * WHZeroSOMS.PCT_TWO
        }

        if (remainingWages > WHZeroSOMS.AMOUNT_THREE) {
            tax += (remainingWages - WHZeroSOMS.AMOUNT_THREE) * WHZeroSOMS.PCT_THREE
        } else {
            tax += (remainingWages - WHZeroSOMS.AMOUNT_TWO) * WHZeroSOMS.PCT_THREE
        }

        return tax
    }

    private fun computeTaxM(remainingWages: Float, tax: Float): Float {
        var remainingWages = remainingWages
        var tax = tax

        if (remainingWages > WHM.AMOUNT_ONE) {
            tax += WHM.AMOUNT_ONE * WHM.PCT_ONE
            remainingWages -= WHM.AMOUNT_ONE
        }

        if (remainingWages > WHM.AMOUNT_TWO) {
            tax += WHM.AMOUNT_TWO * WHM.PCT_TWO
            remainingWages -= WHM.AMOUNT_TWO
        } else {
            tax += (remainingWages - WHM.AMOUNT_TWO) * WHM.PCT_TWO
        }

        if (remainingWages > WHM.AMOUNT_THREE) {
            tax += (remainingWages - WHM.AMOUNT_THREE) * WHM.PCT_THREE
        } else {
            tax += (remainingWages - WHM.AMOUNT_TWO) * WHM.PCT_THREE
        }

        return tax
    }

    fun result(): Float {
        calc()
        return amount
    }

}
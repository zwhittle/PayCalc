package com.example.paycalc.taxes

import com.example.paycalc.Constants

class FederalWithholding(
    private val frequency: String = Constants.Frequencies.BIWEEKLY,
    private val maritalStatus: String,
    private val allowances: Int,
    private val addlAmount: Float,
    regWages: Float,
    supWages: Float,
    deductions: Float
) : Tax(regWages, supWages, deductions) {

    override var hasSupplementalTax = true
    override var supplementalRate = 0.22f

    private var allowanceAmount = 0f
    private var adjustedWages = 0f

    override fun calcRegularAmount(): Float {

        if (regularTaxableWages == 0f) {
            return 0f
        }

        val regAmount = when (frequency) {
            Constants.Frequencies.BIWEEKLY -> calcBiWeeklyRegular()
            else -> 0f
        }

        return regAmount + addlAmount
    }

    private fun calcBiWeeklyRegular(): Float {
        adjustedWages = regularTaxableWages - allowanceAmount()

        return when (maritalStatus) {
            Constants.FederalMaritalStatuses.SINGLE -> fedRegTaxBWSingle(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED -> fedRegTaxBWMarried(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED_SINGLE -> fedRegTaxBWSingle(adjustedWages)
            else -> 0f
        }
    }

    private fun allowanceAmount(): Float {
        return when (frequency) {
            Constants.Frequencies.DAILY -> allowances * Constants.AllowanceAmounts.DAILY
            Constants.Frequencies.WEEKLY -> allowances * Constants.AllowanceAmounts.WEEKLY
            Constants.Frequencies.BIWEEKLY -> allowances * Constants.AllowanceAmounts.BIWEEKLY
            Constants.Frequencies.SEMIMONTHLY -> allowances * Constants.AllowanceAmounts.SEMIMONTHLY
            Constants.Frequencies.MONTHLY -> allowances * Constants.AllowanceAmounts.MONTHLY
            Constants.Frequencies.QUARTERLY -> allowances * Constants.AllowanceAmounts.QUARTERLY
            Constants.Frequencies.SEMIANNUALLY -> allowances * Constants.AllowanceAmounts.SEMIANNUALLY
            Constants.Frequencies.ANNUALlY -> allowances * Constants.AllowanceAmounts.ANNUAL
            else -> 0f
        }
    }

    private fun fedRegTaxBWMarried(wages: Float): Float {
        return when {
            wages <= 454f -> 0f
            wages <= 1200f -> (wages - 454f) * 0.1f
            wages <= 3490f -> 74.60f + ((wages - 1200f) * 0.12f)
            wages <= 6931f -> 349.40f + ((wages - 3490f) * 0.22f)
            wages <= 12817f -> 1106.42f + ((wages - 6931f) * 0.24f)
            wages <= 16154f -> 2519.06f + ((wages - 12817f) * 0.32f)
            wages <= 24006f -> 3586.90f + ((wages - 16154f) * 0.35f)
            else -> 6335.10f + ((wages - 24006f) * 0.37f)
        }
    }

    private fun fedRegTaxBWSingle(wages: Float): Float {
        return when {
            wages <= 146f -> 0f
            wages <= 519f -> (wages - 146f) * 0.1f
            wages <= 1664f -> 37.30f + ((wages - 519f) * 0.12f)
            wages <= 3385f -> 174.70f + ((wages - 1664f) * 0.22f)
            wages <= 6328f -> 553.32f + ((wages - 3385f) * 0.24f)
            wages <= 7996f -> 1259.64f + ((wages - 6328f) * 0.32f)
            wages <= 19773f -> 1793.40f + ((wages - 7996f) * 0.35f)
            else -> 5915.35f + ((wages - 19773f) * 0.37f)
        }
    }


    override fun calcSupplementalAmount(): Float {
        return supplementalTaxableWages * supplementalRate
    }
}
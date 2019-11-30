package com.example.paycalc.taxes

import com.example.paycalc.Constants
import com.example.paycalc.brackets.federal.*

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
            Constants.Frequencies.WEEKLY -> calcWeeklyRegular()
            Constants.Frequencies.BIWEEKLY -> calcBiweeklyRegular()
            Constants.Frequencies.SEMIMONTHLY -> calcSemimonthlyRegular()
            Constants.Frequencies.MONTHLY -> calcMonthlyRegular()
            Constants.Frequencies.QUARTERLY -> calcQuarterlyRegular()
            Constants.Frequencies.SEMIANNUALLY -> calcSemiannuallyRegular()
            Constants.Frequencies.ANNUALLY -> calcAnnuallyRegular()
            Constants.Frequencies.DAILY -> calcDailyRegular()
            else -> 0f
        }

        return regAmount + addlAmount
    }

    private fun calcWeeklyRegular(): Float {
        adjustedWages = regularTaxableWages - allowanceAmount()

        return when (maritalStatus) {
            Constants.FederalMaritalStatuses.SINGLE -> fedRegTaxWeeklySingle(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED -> fedRegTaxBiweeklyMarried(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED_SINGLE -> fedRegTaxWeeklySingle(adjustedWages)
            else -> 0f
        }
    }

    private fun calcBiweeklyRegular(): Float {
        adjustedWages = regularTaxableWages - allowanceAmount()

        return when (maritalStatus) {
            Constants.FederalMaritalStatuses.SINGLE -> fedRegTaxBiweeklySingle(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED -> fedRegTaxBiweeklyMarried(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED_SINGLE -> fedRegTaxBiweeklySingle(adjustedWages)
            else -> 0f
        }
    }

    private fun calcSemimonthlyRegular(): Float {
        adjustedWages = regularTaxableWages - allowanceAmount()

        return when (maritalStatus) {
            Constants.FederalMaritalStatuses.SINGLE -> fedRegTaxSemimonthlySingle(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED -> fedRegTaxSemimonthlyMarried(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED_SINGLE -> fedRegTaxSemimonthlySingle(adjustedWages)
            else -> 0f
        }
    }

    private fun calcMonthlyRegular(): Float {
        adjustedWages = regularTaxableWages - allowanceAmount()

        return when (maritalStatus) {
            Constants.FederalMaritalStatuses.SINGLE -> fedRegTaxMonthlySingle(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED -> fedRegTaxMonthlyMarried(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED_SINGLE -> fedRegTaxMonthlySingle(adjustedWages)
            else -> 0f
        }
    }

    private fun calcQuarterlyRegular(): Float {
        adjustedWages = regularTaxableWages - allowanceAmount()

        return when (maritalStatus) {
            Constants.FederalMaritalStatuses.SINGLE -> fedRegTaxQuarterlySingle(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED -> fedRegTaxQuarterlyMarried(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED_SINGLE -> fedRegTaxQuarterlySingle(adjustedWages)
            else -> 0f
        }
    }

    private fun calcSemiannuallyRegular(): Float {
        adjustedWages = regularTaxableWages - allowanceAmount()

        return when (maritalStatus) {
            Constants.FederalMaritalStatuses.SINGLE -> fedRegTaxSemiannuallySingle(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED -> fedRegTaxSemiannuallyMarried(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED_SINGLE -> fedRegTaxSemiannuallySingle(adjustedWages)
            else -> 0f
        }
    }

    private fun calcAnnuallyRegular(): Float {
        adjustedWages = regularTaxableWages - allowanceAmount()

        return when (maritalStatus) {
            Constants.FederalMaritalStatuses.SINGLE -> fedRegTaxAnnuallySingle(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED -> fedRegTaxAnnuallyMarried(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED_SINGLE -> fedRegTaxAnnuallySingle(adjustedWages)
            else -> 0f
        }
    }

    private fun calcDailyRegular(): Float {
        adjustedWages = regularTaxableWages - allowanceAmount()

        return when (maritalStatus) {
            Constants.FederalMaritalStatuses.SINGLE -> fedRegTaxDailySingle(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED -> fedRegTaxDailyMarried(adjustedWages)
            Constants.FederalMaritalStatuses.MARRIED_SINGLE -> fedRegTaxDailySingle(adjustedWages)
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
            Constants.Frequencies.ANNUALLY -> allowances * Constants.AllowanceAmounts.ANNUAL
            else -> 0f
        }
    }
    
    private fun fedRegTaxWeeklySingle(wages: Float): Float {
        return when {
            wages <= WeeklySingle.OVER_ONE -> WeeklySingle.GIVEN_ONE
            wages <= WeeklySingle.OVER_TWO -> (wages - WeeklySingle.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= WeeklySingle.OVER_THREE -> WeeklySingle.GIVEN_TWO + ((wages - WeeklySingle.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= WeeklySingle.OVER_FOUR -> WeeklySingle.GIVEN_THREE + ((wages - WeeklySingle.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= WeeklySingle.OVER_FIVE -> WeeklySingle.GIVEN_FOUR + ((wages - WeeklySingle.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= WeeklySingle.OVER_SIX -> WeeklySingle.GIVEN_FIVE + ((wages - WeeklySingle.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= WeeklySingle.GIVEN_SEVEN -> WeeklySingle.GIVEN_SIX + ((wages - WeeklySingle.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> WeeklySingle.GIVEN_SEVEN + ((wages - WeeklySingle.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        } 
    }

    private fun fedRegTaxWeeklyMarried(wages: Float): Float {
        return when {
            wages <= WeeklyMarried.OVER_ONE -> WeeklyMarried.GIVEN_ONE
            wages <= WeeklyMarried.OVER_TWO -> (wages - WeeklyMarried.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= WeeklyMarried.OVER_THREE -> WeeklyMarried.GIVEN_TWO + ((wages - WeeklyMarried.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= WeeklyMarried.OVER_FOUR -> WeeklyMarried.GIVEN_THREE + ((wages - WeeklyMarried.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= WeeklyMarried.OVER_FIVE -> WeeklyMarried.GIVEN_FOUR + ((wages - WeeklyMarried.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= WeeklyMarried.OVER_SIX -> WeeklyMarried.GIVEN_FIVE + ((wages - WeeklyMarried.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= WeeklyMarried.GIVEN_SEVEN -> WeeklyMarried.GIVEN_SIX + ((wages - WeeklyMarried.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> WeeklyMarried.GIVEN_SEVEN + ((wages - WeeklyMarried.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxBiweeklySingle(wages: Float): Float {
        return when {
            wages <= BiWeeklySingle.OVER_ONE -> BiWeeklySingle.GIVEN_ONE
            wages <= BiWeeklySingle.OVER_TWO -> (wages - BiWeeklySingle.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= BiWeeklySingle.OVER_THREE -> BiWeeklySingle.GIVEN_TWO + ((wages - BiWeeklySingle.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= BiWeeklySingle.OVER_FOUR -> BiWeeklySingle.GIVEN_THREE + ((wages - BiWeeklySingle.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= BiWeeklySingle.OVER_FIVE -> BiWeeklySingle.GIVEN_FOUR + ((wages - BiWeeklySingle.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= BiWeeklySingle.OVER_SIX -> BiWeeklySingle.GIVEN_FIVE + ((wages - BiWeeklySingle.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= BiWeeklySingle.GIVEN_SEVEN -> BiWeeklySingle.GIVEN_SIX + ((wages - BiWeeklySingle.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> BiWeeklySingle.GIVEN_SEVEN + ((wages - BiWeeklySingle.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxBiweeklyMarried(wages: Float): Float {
        return when {
            wages <= BiweeklyMarried.OVER_ONE -> BiweeklyMarried.GIVEN_ONE
            wages <= BiweeklyMarried.OVER_TWO -> (wages - BiweeklyMarried.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= BiweeklyMarried.OVER_THREE -> BiweeklyMarried.GIVEN_TWO + ((wages - BiweeklyMarried.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= BiweeklyMarried.OVER_FOUR -> BiweeklyMarried.GIVEN_THREE + ((wages - BiweeklyMarried.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= BiweeklyMarried.OVER_FIVE -> BiweeklyMarried.GIVEN_FOUR + ((wages - BiweeklyMarried.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= BiweeklyMarried.OVER_SIX -> BiweeklyMarried.GIVEN_FIVE + ((wages - BiweeklyMarried.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= BiweeklyMarried.GIVEN_SEVEN -> BiweeklyMarried.GIVEN_SIX + ((wages - BiweeklyMarried.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> BiweeklyMarried.GIVEN_SEVEN + ((wages - BiweeklyMarried.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxSemimonthlySingle(wages: Float): Float {
        return when {
            wages <= SemiMonthlySingle.OVER_ONE -> SemiMonthlySingle.GIVEN_ONE
            wages <= SemiMonthlySingle.OVER_TWO -> (wages - SemiMonthlySingle.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= SemiMonthlySingle.OVER_THREE -> SemiMonthlySingle.GIVEN_TWO + ((wages - SemiMonthlySingle.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= SemiMonthlySingle.OVER_FOUR -> SemiMonthlySingle.GIVEN_THREE + ((wages - SemiMonthlySingle.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= SemiMonthlySingle.OVER_FIVE -> SemiMonthlySingle.GIVEN_FOUR + ((wages - SemiMonthlySingle.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= SemiMonthlySingle.OVER_SIX -> SemiMonthlySingle.GIVEN_FIVE + ((wages - SemiMonthlySingle.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= SemiMonthlySingle.GIVEN_SEVEN -> SemiMonthlySingle.GIVEN_SIX + ((wages - SemiMonthlySingle.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> SemiMonthlySingle.GIVEN_SEVEN + ((wages - SemiMonthlySingle.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxSemimonthlyMarried(wages: Float): Float {
        return when {
            wages <= SemiMonthlyMarried.OVER_ONE -> SemiMonthlyMarried.GIVEN_ONE
            wages <= SemiMonthlyMarried.OVER_TWO -> (wages - SemiMonthlyMarried.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= SemiMonthlyMarried.OVER_THREE -> SemiMonthlyMarried.GIVEN_TWO + ((wages - SemiMonthlyMarried.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= SemiMonthlyMarried.OVER_FOUR -> SemiMonthlyMarried.GIVEN_THREE + ((wages - SemiMonthlyMarried.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= SemiMonthlyMarried.OVER_FIVE -> SemiMonthlyMarried.GIVEN_FOUR + ((wages - SemiMonthlyMarried.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= SemiMonthlyMarried.OVER_SIX -> SemiMonthlyMarried.GIVEN_FIVE + ((wages - SemiMonthlyMarried.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= SemiMonthlyMarried.GIVEN_SEVEN -> SemiMonthlyMarried.GIVEN_SIX + ((wages - SemiMonthlyMarried.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> SemiMonthlyMarried.GIVEN_SEVEN + ((wages - SemiMonthlyMarried.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxMonthlySingle(wages: Float): Float {
        return when {
            wages <= MonthlySingle.OVER_ONE -> MonthlySingle.GIVEN_ONE
            wages <= MonthlySingle.OVER_TWO -> (wages - MonthlySingle.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= MonthlySingle.OVER_THREE -> MonthlySingle.GIVEN_TWO + ((wages - MonthlySingle.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= MonthlySingle.OVER_FOUR -> MonthlySingle.GIVEN_THREE + ((wages - MonthlySingle.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= MonthlySingle.OVER_FIVE -> MonthlySingle.GIVEN_FOUR + ((wages - MonthlySingle.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= MonthlySingle.OVER_SIX -> MonthlySingle.GIVEN_FIVE + ((wages - MonthlySingle.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= MonthlySingle.GIVEN_SEVEN -> MonthlySingle.GIVEN_SIX + ((wages - MonthlySingle.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> MonthlySingle.GIVEN_SEVEN + ((wages - MonthlySingle.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxMonthlyMarried(wages: Float): Float {
        return when {
            wages <= MonthlyMarried.OVER_ONE -> MonthlyMarried.GIVEN_ONE
            wages <= MonthlyMarried.OVER_TWO -> (wages - MonthlyMarried.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= MonthlyMarried.OVER_THREE -> MonthlyMarried.GIVEN_TWO + ((wages - MonthlyMarried.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= MonthlyMarried.OVER_FOUR -> MonthlyMarried.GIVEN_THREE + ((wages - MonthlyMarried.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= MonthlyMarried.OVER_FIVE -> MonthlyMarried.GIVEN_FOUR + ((wages - MonthlyMarried.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= MonthlyMarried.OVER_SIX -> MonthlyMarried.GIVEN_FIVE + ((wages - MonthlyMarried.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= MonthlyMarried.GIVEN_SEVEN -> MonthlyMarried.GIVEN_SIX + ((wages - MonthlyMarried.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> MonthlyMarried.GIVEN_SEVEN + ((wages - MonthlyMarried.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxQuarterlySingle(wages: Float): Float {
        return when {
            wages <= QuarterlySingle.OVER_ONE -> QuarterlySingle.GIVEN_ONE
            wages <= QuarterlySingle.OVER_TWO -> (wages - QuarterlySingle.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= QuarterlySingle.OVER_THREE -> QuarterlySingle.GIVEN_TWO + ((wages - QuarterlySingle.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= QuarterlySingle.OVER_FOUR -> QuarterlySingle.GIVEN_THREE + ((wages - QuarterlySingle.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= QuarterlySingle.OVER_FIVE -> QuarterlySingle.GIVEN_FOUR + ((wages - QuarterlySingle.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= QuarterlySingle.OVER_SIX -> QuarterlySingle.GIVEN_FIVE + ((wages - QuarterlySingle.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= QuarterlySingle.GIVEN_SEVEN -> QuarterlySingle.GIVEN_SIX + ((wages - QuarterlySingle.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> QuarterlySingle.GIVEN_SEVEN + ((wages - QuarterlySingle.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxQuarterlyMarried(wages: Float): Float {
        return when {
            wages <= QuarterlyMarried.OVER_ONE -> QuarterlyMarried.GIVEN_ONE
            wages <= QuarterlyMarried.OVER_TWO -> (wages - QuarterlyMarried.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= QuarterlyMarried.OVER_THREE -> QuarterlyMarried.GIVEN_TWO + ((wages - QuarterlyMarried.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= QuarterlyMarried.OVER_FOUR -> QuarterlyMarried.GIVEN_THREE + ((wages - QuarterlyMarried.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= QuarterlyMarried.OVER_FIVE -> QuarterlyMarried.GIVEN_FOUR + ((wages - QuarterlyMarried.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= QuarterlyMarried.OVER_SIX -> QuarterlyMarried.GIVEN_FIVE + ((wages - QuarterlyMarried.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= QuarterlyMarried.GIVEN_SEVEN -> QuarterlyMarried.GIVEN_SIX + ((wages - QuarterlyMarried.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> QuarterlyMarried.GIVEN_SEVEN + ((wages - QuarterlyMarried.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxSemiannuallySingle(wages: Float): Float {
        return when {
            wages <= SemiAnnuallySingle.OVER_ONE -> SemiAnnuallySingle.GIVEN_ONE
            wages <= SemiAnnuallySingle.OVER_TWO -> (wages - SemiAnnuallySingle.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= SemiAnnuallySingle.OVER_THREE -> SemiAnnuallySingle.GIVEN_TWO + ((wages - SemiAnnuallySingle.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= SemiAnnuallySingle.OVER_FOUR -> SemiAnnuallySingle.GIVEN_THREE + ((wages - SemiAnnuallySingle.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= SemiAnnuallySingle.OVER_FIVE -> SemiAnnuallySingle.GIVEN_FOUR + ((wages - SemiAnnuallySingle.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= SemiAnnuallySingle.OVER_SIX -> SemiAnnuallySingle.GIVEN_FIVE + ((wages - SemiAnnuallySingle.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= SemiAnnuallySingle.GIVEN_SEVEN -> SemiAnnuallySingle.GIVEN_SIX + ((wages - SemiAnnuallySingle.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> SemiAnnuallySingle.GIVEN_SEVEN + ((wages - SemiAnnuallySingle.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxSemiannuallyMarried(wages: Float): Float {
        return when {
            wages <= SemiAnnuallyMarried.OVER_ONE -> SemiAnnuallyMarried.GIVEN_ONE
            wages <= SemiAnnuallyMarried.OVER_TWO -> (wages - SemiAnnuallyMarried.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= SemiAnnuallyMarried.OVER_THREE -> SemiAnnuallyMarried.GIVEN_TWO + ((wages - SemiAnnuallyMarried.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= SemiAnnuallyMarried.OVER_FOUR -> SemiAnnuallyMarried.GIVEN_THREE + ((wages - SemiAnnuallyMarried.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= SemiAnnuallyMarried.OVER_FIVE -> SemiAnnuallyMarried.GIVEN_FOUR + ((wages - SemiAnnuallyMarried.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= SemiAnnuallyMarried.OVER_SIX -> SemiAnnuallyMarried.GIVEN_FIVE + ((wages - SemiAnnuallyMarried.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= SemiAnnuallyMarried.GIVEN_SEVEN -> SemiAnnuallyMarried.GIVEN_SIX + ((wages - SemiAnnuallyMarried.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> SemiAnnuallyMarried.GIVEN_SEVEN + ((wages - SemiAnnuallyMarried.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxAnnuallySingle(wages: Float): Float {
        return when {
            wages <= AnnuallySingle.OVER_ONE -> AnnuallySingle.GIVEN_ONE
            wages <= AnnuallySingle.OVER_TWO -> (wages - AnnuallySingle.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= AnnuallySingle.OVER_THREE -> AnnuallySingle.GIVEN_TWO + ((wages - AnnuallySingle.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= AnnuallySingle.OVER_FOUR -> AnnuallySingle.GIVEN_THREE + ((wages - AnnuallySingle.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= AnnuallySingle.OVER_FIVE -> AnnuallySingle.GIVEN_FOUR + ((wages - AnnuallySingle.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= AnnuallySingle.OVER_SIX -> AnnuallySingle.GIVEN_FIVE + ((wages - AnnuallySingle.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= AnnuallySingle.GIVEN_SEVEN -> AnnuallySingle.GIVEN_SIX + ((wages - AnnuallySingle.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> AnnuallySingle.GIVEN_SEVEN + ((wages - AnnuallySingle.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxAnnuallyMarried(wages: Float): Float {
        return when {
            wages <= AnnuallyMarried.OVER_ONE -> AnnuallyMarried.GIVEN_ONE
            wages <= AnnuallyMarried.OVER_TWO -> (wages - AnnuallyMarried.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= AnnuallyMarried.OVER_THREE -> AnnuallyMarried.GIVEN_TWO + ((wages - AnnuallyMarried.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= AnnuallyMarried.OVER_FOUR -> AnnuallyMarried.GIVEN_THREE + ((wages - AnnuallyMarried.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= AnnuallyMarried.OVER_FIVE -> AnnuallyMarried.GIVEN_FOUR + ((wages - AnnuallyMarried.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= AnnuallyMarried.OVER_SIX -> AnnuallyMarried.GIVEN_FIVE + ((wages - AnnuallyMarried.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= AnnuallyMarried.GIVEN_SEVEN -> AnnuallyMarried.GIVEN_SIX + ((wages - AnnuallyMarried.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> AnnuallyMarried.GIVEN_SEVEN + ((wages - AnnuallyMarried.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxDailySingle(wages: Float): Float {
        return when {
            wages <= DailySingle.OVER_ONE -> DailySingle.GIVEN_ONE
            wages <= DailySingle.OVER_TWO -> (wages - DailySingle.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= DailySingle.OVER_THREE -> DailySingle.GIVEN_TWO + ((wages - DailySingle.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= DailySingle.OVER_FOUR -> DailySingle.GIVEN_THREE + ((wages - DailySingle.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= DailySingle.OVER_FIVE -> DailySingle.GIVEN_FOUR + ((wages - DailySingle.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= DailySingle.OVER_SIX -> DailySingle.GIVEN_FIVE + ((wages - DailySingle.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= DailySingle.GIVEN_SEVEN -> DailySingle.GIVEN_SIX + ((wages - DailySingle.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> DailySingle.GIVEN_SEVEN + ((wages - DailySingle.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }

    private fun fedRegTaxDailyMarried(wages: Float): Float {
        return when {
            wages <= DailyMarried.OVER_ONE -> DailyMarried.GIVEN_ONE
            wages <= DailyMarried.OVER_TWO -> (wages - DailyMarried.OVER_ONE) * FederalPercentages.PCT_ONE
            wages <= DailyMarried.OVER_THREE -> DailyMarried.GIVEN_TWO + ((wages - DailyMarried.OVER_TWO) * FederalPercentages.PCT_TWO)
            wages <= DailyMarried.OVER_FOUR -> DailyMarried.GIVEN_THREE + ((wages - DailyMarried.OVER_THREE) * FederalPercentages.PCT_THREE)
            wages <= DailyMarried.OVER_FIVE -> DailyMarried.GIVEN_FOUR + ((wages - DailyMarried.OVER_FOUR) * FederalPercentages.PCT_FOUR)
            wages <= DailyMarried.OVER_SIX -> DailyMarried.GIVEN_FIVE + ((wages - DailyMarried.OVER_FIVE) * FederalPercentages.PCT_FIVE)
            wages <= DailyMarried.GIVEN_SEVEN -> DailyMarried.GIVEN_SIX + ((wages - DailyMarried.OVER_SIX) * FederalPercentages.PCT_SIX)
            else -> DailyMarried.GIVEN_SEVEN + ((wages - DailyMarried.OVER_SEVEN) * FederalPercentages.PCT_SEVEN)
        }
    }
    
    override fun calcSupplementalAmount(): Float {
        return supplementalTaxableWages * supplementalRate
    }
}
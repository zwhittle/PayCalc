package com.example.paycalc

object Utils {

    fun periodsPerYear(frequency: String): Int{
        return when (frequency) {
            Constants.Frequencies.WEEKLY -> 52
            Constants.Frequencies.BIWEEKLY -> 26
            Constants.Frequencies.SEMIMONTHLY -> 24
            Constants.Frequencies.MONTHLY -> 12
            Constants.Frequencies.QUARTERLY -> 4
            Constants.Frequencies.SEMIANNUALLY -> 2
            Constants.Frequencies.ANNUALLY -> 1
            Constants.Frequencies.DAILY -> 260
            else -> 0
        }
    }
}
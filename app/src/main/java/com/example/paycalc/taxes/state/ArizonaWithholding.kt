package com.example.paycalc.taxes.state

import com.example.paycalc.Constants
import com.example.paycalc.taxes.Tax

/**
 * State Withholding Tax for Arizona
 * Employees elect a flat rate percentage
 */

class ArizonaWithholding(private val percentage: String, regWages: Float, supWages: Float, deductions: Float) :
    Tax(regWages,
        supWages,
        deductions) {

    override var hasFlatRate = true
    override var flatRate = constantRate()

    private fun constantRate(): Float {
        return when (percentage) {
            Constants.ArizonaConstantRates.ZERO_POINT_EIGHT -> 0.008f
            Constants.ArizonaConstantRates.ONE_POINT_THREE -> 0.013f
            Constants.ArizonaConstantRates.ONE_POINT_EIGHT -> 0.018f
            Constants.ArizonaConstantRates.TWO_POINT_SEVEN -> 0.027f
            Constants.ArizonaConstantRates.THREE_POINT_SIX -> 0.036f
            Constants.ArizonaConstantRates.FOUR_POINT_TWO -> 0.042f
            Constants.ArizonaConstantRates.FIVE_POINT_ONE -> 0.051f
            else -> 0.027f
        }
    }
}
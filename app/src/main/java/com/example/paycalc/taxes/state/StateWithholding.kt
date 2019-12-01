package com.example.paycalc.taxes.state

import com.example.paycalc.Constants
import com.example.paycalc.Utils
import com.example.paycalc.taxes.Tax

/**
 * State Withholding
 * This class basically only being used to calculate flat rate states right now
 */
open class StateWithholding(private val state: String, private val frequency: String, regWages: Float, supWages: Float, deductions: Float) :
    Tax(regWages, supWages, deductions) {

    override fun calcRegularAmount(): Float {
        if (stateHasNoIncomeTax()) {
            return 0f
        }

        if (stateHasFlatRate()) {
            return regularTaxableWages * rateForState()
        }

        return 0f
    }

    // The great states of Colorado, Illinois, Indiana, Kentucky, Massachusetts, Michigan,
    // North Carolina, Pennsylvania, and Utah  make this easy and have a simple flat-rate income tax
    private fun stateHasFlatRate(): Boolean {
        return when (state) {
            Constants.States.COLORADO -> true
            Constants.States.ILLINOIS -> true
            Constants.States.INDIANA -> true
            Constants.States.KENTUCKY -> true
            Constants.States.MASSACHUSETTS -> true
            Constants.States.MICHIGAN -> true
            Constants.States.NORTH_CAROLINA -> true
            Constants.States.PENNSYLVANIA -> true
            Constants.States.UTAH -> true
            else -> false
        }
    }

     // The even greater states of Alaska, Florida, Nevada, New Hampshire, South Dakota, Tennessee,
     // Texas, Washington and Wyoming go the extra mile and have no income tax at all
    private fun stateHasNoIncomeTax(): Boolean {
        return when(state) {
            Constants.States.ALASKA -> true
            Constants.States.FLORIDA -> true
            Constants.States.NEVADA -> true
            Constants.States.NEW_HAMPSHIRE -> true
            Constants.States.SOUTH_DAKOTA -> true
            Constants.States.TENNESSEE -> true
            Constants.States.TEXAS -> true
            Constants.States.WASHINGTON -> true
            Constants.States.WYOMING -> true
            else -> false
        }
    }

    private fun rateForState(): Float {
        return when (state) {
            Constants.States.COLORADO -> 0.0463f
            Constants.States.ILLINOIS -> 0.0495f
            Constants.States.INDIANA -> 0.0323f
            Constants.States.KENTUCKY -> 0.05f
            Constants.States.MASSACHUSETTS -> 0.0505f
            Constants.States.MICHIGAN -> 0.0425f
            Constants.States.NORTH_CAROLINA -> 0.0525f
            Constants.States.PENNSYLVANIA -> 0.0307f
            Constants.States.UTAH -> 0.0495f
            else -> 0f
        }
    }
}
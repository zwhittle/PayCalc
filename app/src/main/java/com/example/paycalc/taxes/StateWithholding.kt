package com.example.paycalc.taxes

import com.example.paycalc.Constants

class StateWithholding(private val state: String, grossWages: Float, deductions: Float) :
    Tax(grossWages, deductions) {

    override fun calcVariableAmount(): Float {
        return if (stateHasFlatRate()) {
            taxableWages * rateForState()
        } else {
            0f
        }
    }

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
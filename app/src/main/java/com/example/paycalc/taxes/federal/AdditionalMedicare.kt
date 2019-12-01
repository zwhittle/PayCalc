package com.example.paycalc.taxes.federal

import com.example.paycalc.taxes.Tax

/**
 * Additional Medicare Tax
 * Flate rate of 0.9% on YTD FICA Wages > $200,000
 */
class AdditionalMedicare(regWages: Float, supWages: Float, deductions: Float) : Tax(regWages, supWages, deductions) {
    override var hasFlatRate = true
    override var flatRate = 0.009f
    override var hasWagesFloor = true
    override var wagesFloor = 200000f
}
package com.example.paycalc.taxes.federal

import com.example.paycalc.taxes.Tax

class AdditionalMedicare(regWages: Float, supWages: Float, deductions: Float) : Tax(regWages, supWages, deductions) {
    override var hasFlatRate = true
    override var flatRate = 0.009f
    override var hasWagesFloor = true
    override var wagesFloor = 200000f
}
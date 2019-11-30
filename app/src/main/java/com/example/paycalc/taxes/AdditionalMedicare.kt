package com.example.paycalc.taxes

class AdditionalMedicare(grossWages: Float, deductions: Float) : Tax(grossWages, deductions) {
    override var hasFlatRate = true
    override var flatRate = 0.009f
    override var hasWagesFloor = true
    override var wagesFloor = 200000f
}
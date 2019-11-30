package com.example.paycalc.taxes

class MedicareTax(grossWages: Float, deductions: Float) : Tax(grossWages, deductions) {

    override var hasFlatRate = true
    override var flatRate = 0.0145f
}
package com.example.paycalc.taxes

class Medicare(grossWages: Float, deductions: Float) : Tax(grossWages, deductions) {

    override var hasFlatRate = true
    override var flatRate = 0.0145f
}
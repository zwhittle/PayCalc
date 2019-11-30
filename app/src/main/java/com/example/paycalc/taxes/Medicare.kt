package com.example.paycalc.taxes

class Medicare(regWages: Float, supWages: Float, deductions: Float) : Tax(regWages, supWages, deductions) {

    override var hasFlatRate = true
    override var flatRate = 0.0145f
}
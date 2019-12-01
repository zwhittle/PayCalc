package com.example.paycalc.taxes.federal

import com.example.paycalc.taxes.Tax

/**
 * Medicare Tax
 * Flat rate of 1.45% on all FICA Wages
 */

class Medicare(regWages: Float, supWages: Float, deductions: Float) : Tax(regWages, supWages, deductions) {

    override var hasFlatRate = true
    override var flatRate = 0.0145f
}
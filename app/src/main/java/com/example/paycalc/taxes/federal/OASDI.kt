package com.example.paycalc.taxes.federal

import com.example.paycalc.taxes.Tax

/**
 * OASDI Tax (Social Security Tax)
 * Flat rate of 6.2% on all FICA Wages up to $132,900 YTD
 */

class OASDI(regWages: Float, supWages: Float, deductions: Float): Tax(regWages, supWages, deductions){

    override var hasFlatRate = true
    override var flatRate = 0.062f
    override var hasWagesLimit = true
    override var wagesLimit = 132900f
    override var hasAmountLimit = true
    override var amountLimit = wagesLimit * flatRate

}
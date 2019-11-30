package com.example.paycalc.taxes

class OASDITax(grossWages: Float, deductions: Float): Tax(grossWages, deductions){

    override var hasFlatRate = true
    override var flatRate = 0.062f
    override var hasWagesLimit = true
    override var wagesLimit = 132900f
    override var hasAmountLimit = true
    override var amountLimit = wagesLimit * flatRate

}
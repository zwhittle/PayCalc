package com.example.paycalc.taxes

//interface TaxInterface {
//    var amount: Float
//    var hasFlatRate: Boolean
//    var flatRate: Float
//    var hasWagesLimit: Boolean
//    var wagesLimit: Float
//    var hasAmountLimit: Boolean
//    var amountLimit: Float
//
//    fun calcWages()
//    fun calcAmount()
//    fun calcVariableAmount()
//    fun calcFlatRateAmount() : Float
//    fun amount() : Float
//}

abstract class Tax(private val grossWages: Float, private val deductions: Float) {

    private var subjectWages = 0f
    var taxableWages = 0f
    open var amount = 0f
    open var hasFlatRate = false
    open var flatRate = 0f
    open var hasWagesLimit = false
    open var wagesLimit = 0f
    open var hasWagesFloor = false
    open var wagesFloor = 0f
    open var hasAmountLimit = false
    open var amountLimit = 0f

    private fun calcWages() {
        subjectWages = grossWages - deductions

        if (hasWagesFloor && subjectWages < wagesFloor) {
            subjectWages = 0f
        }

        taxableWages = if (hasWagesLimit && subjectWages > wagesLimit) {
            wagesLimit
        } else {
            subjectWages
        }
    }

    private fun calcAmount() {
        calcWages()

        amount = if (hasFlatRate) {
            calcFlatRateAmount()
        } else {
            calcVariableAmount()
        }

        if (hasAmountLimit && amount > amountLimit) {
            amount = amountLimit
        }
    }

    open fun calcVariableAmount(): Float {

        return 0f
    }

    private fun calcFlatRateAmount(): Float {
        return taxableWages * flatRate
    }

    fun amount(): Float {
        calcAmount()
        return amount
    }
}
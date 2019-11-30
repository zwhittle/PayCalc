package com.example.paycalc

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction

/**
 * Fragment extensions
 */
inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
    beginTransaction().func().commit()
}

fun AppCompatActivity.addFragment(fragment: Fragment, frameId: Int, tag: String) {
    supportFragmentManager.inTransaction { add(frameId, fragment, tag) }
    Log.d(applicationContext.packageCodePath, "$fragment added to supportFragmentManager")
}

fun AppCompatActivity.replaceFragment(fragment: Fragment, frameId: Int, tag: String) {
    supportFragmentManager.inTransaction { replace(frameId, fragment, tag) }
    Log.d(applicationContext.packageCodePath, "$fragment added to supportFragmentManager")
}

fun AppCompatActivity.removeFragment(fragment: Fragment) {
    supportFragmentManager.inTransaction { remove(fragment) }
    Log.d(applicationContext.packageCodePath, "$fragment removed from supportFragmentManager")
}
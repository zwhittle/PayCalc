package com.example.paycalc

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.get
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.paycalc.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    companion object {
        private const val NUM_PAGES = 2
        private const val LOG_TAG = "MainActivity"
    }

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout

    private lateinit var navController: NavController


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = DataBindingUtil.setContentView<ActivityMainBinding>(this, R.layout.activity_main)

        navController = Navigation.findNavController(this, R.id.nav_host_fragment)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            destinationListener(controller, destination, arguments)
        }

        setSupportActionBar(toolbar)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_calculation -> showCalcFragment()
            R.id.action_election -> showElectionFragment()
        }

        return super.onOptionsItemSelected(item)
    }

    private fun showElectionFragment() {
        navController.navigate(CalcFragmentDirections.actionCalcFragmentToElectionFragment())
    }

    private fun showCalcFragment() {
        navController.navigate(ElectionFragmentDirections.actionElectionFragmentToCalcFragment())
    }

    private fun destinationListener(controller: NavController, destination: NavDestination,
                                    arguments: Bundle?) {

        val graph = navController.graph
        when (destination) {
            graph[R.id.calcFragment]    -> {}
            graph[R.id.electionFragment]     -> {}
        }
    }
}

package com.elevintech.motorbro.Dashboard

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.core.view.forEach
import androidx.fragment.app.FragmentTransaction
import com.elevintech.motorbro.Dashboard.Fragments.*
import com.elevintech.myapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_dashboard.*

class DashboardActivity : AppCompatActivity() {

    lateinit var homeFragment: HomeFragment
    lateinit var partsFragment: PartsFragment
    lateinit var shopFragment: ShopFragment
    lateinit var remindersFragment: RemindersFragment
    lateinit var historyFragment: HistoryFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        setUpBottomNav()
    }

    private fun setUpBottomNav() {

        homeFragment = HomeFragment()
        partsFragment = PartsFragment()
        shopFragment = ShopFragment()
        remindersFragment = RemindersFragment()
        historyFragment = HistoryFragment()

        val menuView = bottom_nav.getChildAt(0) as? ViewGroup ?: return
        menuView.forEach {
            it.findViewById<View>(R.id.largeLabel)?.setPadding(0, 0, 0, 0)
        }

        val bottomNavigation : BottomNavigationView = bottom_nav
        bottomNavigation.setOnNavigationItemSelectedListener { item ->

            when(item.itemId) {
                R.id.tabhome -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, homeFragment, "homeFragmentTag")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.tabparts -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, partsFragment, "partsFragmentTag")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.tabshop -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, shopFragment, "shopFragmentTag")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.tabreminders -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, remindersFragment, "remindersFragmentTag")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

                R.id.tabhistory -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, historyFragment, "historyFragmentTag")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()
                }

            }

            true
        }

        // default fragment
        bottomNavigation.selectedItemId = R.id.tabhome

    }
}

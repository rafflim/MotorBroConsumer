package com.elevintech.motorbro.Dashboard

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.forEach
import androidx.fragment.app.FragmentTransaction
import com.elevintech.motorbro.Achievements.AchievementsActivity
import com.elevintech.motorbro.BikeRegistration.BikeRegistrationActivity
import com.elevintech.motorbro.Dashboard.Fragments.*
import com.elevintech.motorbro.Garage.GarageActivity
import com.elevintech.motorbro.Glovebox.GloveboxActivity
import com.elevintech.motorbro.MyAccount.MyAccountActivity
import com.elevintech.motorbro.MainActivity
import com.elevintech.myapplication.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.drawer_dashboard.*

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    lateinit var homeFragment: HomeFragment
    lateinit var partsFragment: PartsFragment
    lateinit var shopFragment: ShopFragment
    lateinit var remindersFragment: RemindersFragment
    lateinit var historyFragment: HistoryFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_dashboard)

        buildNavigationDrawer()
        setUpBottomNav()

        shopImageView.setOnClickListener {
//            val intent = Intent(activity, EditGeneralInformationActivity::class.java)
//            startActivity(intent)
        }
    }

    // Navigation Drawer
    private fun buildNavigationDrawer(){

        // set toolbar as our created view
        setSupportActionBar(toolbar)

        // remove default title, so we can set it through XML
        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)

        val drawerLayout = drawer_layout
        println("is layout nil")
        println(drawerLayout)

        // create navigation drawer
        var toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

//        // get menu from navigationView
//        val nav_menu = nav_view.menu
//
//        // find MenuItem you want to change
//        val nav_profile_status = nav_menu.findItem(R.id.profile_status)
//
//        // set new title to the MenuItem
//        nav_profile_status.title = "NewTitleForCamera"
//
//        // do the same for other MenuItems
//        val nav_meeting = nav_menu.findItem(R.id.next_meeting)
//        nav_meeting.title = "NewTitleForGallery"
//
//        // get header from navigationView
//        val nav_header = nav_view.getHeaderView(0)
//
//        // find headerItem you want to change
//        val nav_user = nav_header.user_first_name
//
//        // set text to the headerItem
//        nav_user.setText("Gotcha")
//
//        val nav_user_image = nav_header.profileImage


//        updateUserImage(nav_user_image)

        nav_view.setNavigationItemSelectedListener(this)

    }



    // On select of Navigation Drawer Menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        println("${item.itemId}")

        when(item.itemId){


            R.id.my_account -> {
                val intent = Intent(applicationContext, MyAccountActivity::class.java)
                startActivity(intent)
            }

            R.id.garage -> {
                val intent = Intent(applicationContext, GarageActivity::class.java)
                startActivity(intent)
            }

            R.id.glovebox -> {
                val intent = Intent(applicationContext, GloveboxActivity::class.java)
                startActivity(intent)
            }

            R.id.achievements -> {
                val intent = Intent(applicationContext, AchievementsActivity::class.java)
                startActivity(intent)
            }

            R.id.sign_out -> {
                logOut()
            }

        }

        return true
    }

    private fun logOut() {

        FirebaseAuth.getInstance().signOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
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

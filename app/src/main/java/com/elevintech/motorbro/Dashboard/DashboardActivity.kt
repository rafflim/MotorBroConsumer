package com.elevintech.motorbro.Dashboard

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.forEach
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.elevintech.motorbro.Achievements.AchievementManager
import com.elevintech.motorbro.Achievements.AchievementsActivity
import com.elevintech.motorbro.AddOdometer.AddOdometerActivity
import com.elevintech.motorbro.AddParts.AddPartsActivity
import com.elevintech.motorbro.AddRefueling.AddRefuelingActivity
import com.elevintech.motorbro.Chat.ChatListActivity
import com.elevintech.motorbro.CloudFunctions.CloudFunctions
import com.elevintech.motorbro.Dashboard.Fragments.*
import com.elevintech.motorbro.Garage.GarageActivity
import com.elevintech.motorbro.Glovebox.GloveboxActivity
import com.elevintech.motorbro.MyAccount.MyAccountActivity
import com.elevintech.motorbro.MainActivity
import com.elevintech.motorbro.Model.Achievement
import com.elevintech.motorbro.Model.BikeInfo
import com.elevintech.motorbro.Model.User
import com.elevintech.motorbro.More.MoreActivity
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.ScheduledNotification.ScheduledNotification
import com.elevintech.motorbro.Shop.ShopActivity
import com.elevintech.motorbro.TypeOf.TypeOfBrandActivity
import com.elevintech.motorbro.TypeOf.TypeOfFuelActivity
import com.elevintech.motorbro.TypeOf.TypeOfPartsActivity
import com.elevintech.myapplication.R
import com.facebook.login.LoginManager
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FacebookAuthCredential
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_dashboard.*
import kotlinx.android.synthetic.main.drawer_dashboard.*
import kotlinx.android.synthetic.main.drawer_header.view.*
import kotlinx.android.synthetic.main.fragment_home.*

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var homeFragment: HomeFragment
    private lateinit var partsFragment: PartsFragment
    private lateinit var shopFragment: ShopFragment
    private lateinit var refuelFragment: RefuelFragment
    private lateinit var historyFragment: HistoryFragment

    private lateinit var bottomSheetDialog: BottomSheetDialog
    var lastActiveTab: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.drawer_dashboard)

//        buildNavigationDrawer()
        buildBottomSheetDialog()
        setUpFabClick()
        createNotificationChannel()
//        displayMessageBadge()
        showDashboardDialog()
//        shopImageView.setOnClickListener {
//            val intent = Intent(this, ShopActivity::class.java)
//            startActivity(intent)
//        }
//
//        chatImageView.setOnClickListener {
//            val intent = Intent(this, ChatListActivity::class.java)
//            startActivity(intent)
//        }

        AchievementManager().setAchievementAsAchieved( Achievement.Names.FIRST_MONITOR )
        ScheduledNotification().startAlarm(this)
        CloudFunctions().checkRegistrationToken()


    }

//    private fun displayMessageBadge(){
//
//        MotoroBroDatabase().getUnreadMessageCount{ unreadMessageCount ->
//            chatButton.setBadgeValue(unreadMessageCount)
//        }
//    }

    var channelId = "com.elevintech.motorbro"

    // Values displayed on phone's system settings
    val notificationName = "Daily Odometer Update"
    val notificationDescription = "Daily notification reminder for any odometer changes"

    // Create the NotificationChannel
    // because phones with API 26 and above requires it to show notifications
    private fun createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(channelId, notificationName, importance).apply {
                description = notificationDescription
            }

            channel.description = notificationDescription
            channel.setShowBadge(true)
            channel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC

            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun showDashboardDialog() {
        // DO AFTER 2 SECONDS
        val handler = Handler()
        handler.postDelayed({
            
            val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
            val prev: Fragment? = supportFragmentManager.findFragmentByTag("dialogTag")
            if (prev != null) {
                ft.remove(prev)
            }
            ft.addToBackStack(null)

            // Create and show the dialog.
            val newFragment: DialogFragment = DashboardDialog()
            newFragment.show(ft, "dialogTag")

        }, 2000 )
    }

    private fun buildBottomSheetDialog() {
        bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_dialog_dashboard, null)
        bottomSheetDialog.setContentView(view)
    }

    override fun onResume() {
        super.onResume()

        setUpBottomNav()

        if (bottomSheetDialog.isShowing)
            bottomSheetDialog.dismiss()

        // TODO: Load the user profile here
        val db = MotoroBroDatabase()

        db.getUser {
            setValuesNavHeader(it)
        }

    }

    private fun setUpFabClick() {
        floating_button.setOnClickListener {

            bottomSheetDialog.show()

            val parts = bottomSheetDialog.findViewById<LinearLayout>(R.id.layoutParts)
            val odometer = bottomSheetDialog.findViewById<LinearLayout>(R.id.layoutOdometer)
            val refueling = bottomSheetDialog.findViewById<LinearLayout>(R.id.layoutRefueling)

            parts?.setOnClickListener {
                val intent = Intent(this, AddPartsActivity::class.java)
                startActivity(intent)
            }

            odometer?.setOnClickListener {
                val intent = Intent(this, AddOdometerActivity::class.java)
                startActivity(intent)
            }

            refueling?.setOnClickListener {
                val intent = Intent(this, AddRefuelingActivity::class.java)
                startActivity(intent)
            }

        }
    }


    private fun setBikeValues(bike: BikeInfo) {

    }

    private fun setValuesNavHeader(user: User) {

        val navHeader = nav_view.getHeaderView(0)
        val navUserName = navHeader.usersNameText
        val navUserEmail = navHeader.userEmailText
        val imageView = navHeader.usersHeaderImage

        if (user.profileImage != "") {
            Glide.with(this).load(user.profileImage).into(imageView)
        } else {
            // Put an empty image here
            Glide.with(this).load(R.drawable.car_circle_icon).into(imageView)
        }
        navUserName.text = "${user.firstName} ${user.lastName}"
        navUserEmail.text = user.email
    }

    // Navigation Drawer
//    private fun buildNavigationDrawer(){
//
//        // set toolbar as our created view
//        setSupportActionBar(toolbar)
//
//        // remove default title, so we can set it through XML
//        getSupportActionBar()!!.setDisplayShowTitleEnabled(false)
//
//        val drawerLayout = drawer_layout
//
//        // create navigation drawer
//        var toggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_drawer, R.string.close_drawer)
//        drawerLayout.addDrawerListener(toggle)
//        toggle.syncState()
//
//        nav_view.setNavigationItemSelectedListener(this)
//
//    }


    // On select of Navigation Drawer Menu
    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        println("${item.itemId}")

        when(item.itemId){

            R.id.parts_menu-> {
                // TODO: There's a bug here make these unclickable when from here
                val intent = Intent(applicationContext, TypeOfPartsActivity::class.java)
                startActivity(intent)
            }

            R.id.brands -> {
                val intent = Intent(applicationContext, TypeOfBrandActivity::class.java)
                startActivity(intent)
            }

            R.id.fuels -> {
                val intent = Intent(applicationContext, TypeOfFuelActivity::class.java)
                startActivity(intent)
            }

//            R.id.schedule_menu-> {
//                // TODO: There's a bug here make these unclickable when from here
//                val intent = Intent(applicationContext, TypeOfHistoryActivity::class.java)
//                startActivity(intent)
//            }
//
//            R.id.service_menu-> {
//                // TODO: There's a bug here make these unclickable when from here
//                val intent = Intent(applicationContext, TypeOfReminderActivity::class.java)
//                startActivity(intent)
//            }

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

            R.id.settings -> {
                val intent = Intent(applicationContext, MoreActivity::class.java)
                startActivity(intent)
            }

//            R.id.qr_code -> {
//                val intent = Intent(applicationContext, QrCodeActivity::class.java)
//                startActivity(intent)
//            }

//            R.id.sign_out -> {
//                MotoroBroDatabase().deleteUserToken()
//                logOut()
//            }

        }

        return true
    }

    private fun logOut() {

        FirebaseAuth.getInstance().signOut()
        LoginManager.getInstance().logOut()
        val intent = Intent(this, MainActivity::class.java)
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(intent)
        finish()
    }

    private fun setUpBottomNav() {

        homeFragment = HomeFragment()
        partsFragment = PartsFragment()
        shopFragment = ShopFragment()
        refuelFragment = RefuelFragment()
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
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

                    lastActiveTab = R.id.tabhome
                }

                R.id.tabparts -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, partsFragment, "partsFragmentTag")
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

                    lastActiveTab = R.id.tabparts
                }

                R.id.tabshop -> {

                    floating_button.performClick()
//
//                    supportFragmentManager
//                        .beginTransaction()
//                        .replace(R.id.frame_layout, shopFragment, "shopFragmentTag")
//                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
//                        .commit()

                    lastActiveTab = null

                }

                R.id.tabreminders -> {
                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, refuelFragment, "remindersFragmentTag")
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

                    lastActiveTab = R.id.tabreminders
                }

                R.id.tabhistory -> {

                    supportFragmentManager
                        .beginTransaction()
                        .replace(R.id.frame_layout, historyFragment, "historyFragmentTag")
                        //.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit()

                    lastActiveTab = R.id.tabhistory
                }

            }

            true
        }

        if (lastActiveTab == null){
            // default fragment
            bottomNavigation.selectedItemId = R.id.tabhome
        } else {
            bottomNavigation.selectedItemId = lastActiveTab!!
        }


    }
}

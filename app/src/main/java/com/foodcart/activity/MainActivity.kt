package com.foodcart.activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.bumptech.glide.Glide
import com.example.foodcart.R
import com.firebase.ui.auth.AuthUI
import com.foodcart.fragment.FAQFragment
import com.foodcart.fragment.HomeFragment
import com.foodcart.modal.log
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*

class MainActivity : AppCompatActivity() {
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var toolbar: Toolbar
    lateinit var drawerlayout: DrawerLayout
    lateinit var frameLayout: FrameLayout
    lateinit var navigationView: NavigationView
    var previousmenu: MenuItem? = null
    var name:String="User Name"
    var email:String="User Mail"
    var image:String?=null
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
        setContentView(R.layout.activity_main)
        if(log().getLoggedIn(this) == false) {
            startActivity(Intent(this, LoginActivity::class.java))
        }
        drawerlayout = findViewById(R.id.drawerlayout)
        coordinatorLayout = findViewById(R.id.coordinatorlayout)
        toolbar = findViewById(R.id.toolbar)
        frameLayout = findViewById(R.id.frame)
        val sh=getSharedPreferences("user", MODE_PRIVATE)
        val user: FirebaseUser? = auth.currentUser
        if (user == null) {
            Toast.makeText(this, "Please Login!!!", Toast.LENGTH_SHORT).show()
        } else {
            name = sh?.getString("Name"," ").toString()
            email = sh?.getString("Email"," ").toString()
            image = sh?.getString("Image"," ").toString()
        }
            navigationView = findViewById(R.id.navigationlayout)
                val hview = navigationView.getHeaderView(0)
                val txtProfileName = hview.findViewById<TextView>(R.id.userName)
            txtProfileName.text = name
                val txtProfileEmail = hview.findViewById<TextView>(R.id.userEmail)
            txtProfileEmail.text = email
                val ProfileImage = hview.findViewById<ShapeableImageView>(R.id.userImage)
            Glide.with(this).load(image).into(ProfileImage)

        settoolbar()
        openHome()
        val drawerToggle = ActionBarDrawerToggle(
            this@MainActivity,
            drawerlayout, R.string.open_drawer, R.string.close_drawer
        )
        //hamburger icon colour
        drawerToggle.drawerArrowDrawable.color = resources.getColor(R.color.black)
        drawerlayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        navigationView.setNavigationItemSelectedListener {
            if (previousmenu != null) {
                previousmenu?.isChecked = false
            }
            it.isChecked = true
            it.isCheckable = true
            previousmenu = it
            when (it.itemId) {
                R.id.home -> {
                    openHome()
                    drawerlayout.closeDrawers()
                }
                R.id.faq -> {
                    supportFragmentManager.beginTransaction().addToBackStack("FAQ")
                        .replace(R.id.frame, FAQFragment()).commit()
                    supportActionBar?.title = "FAQ"
                    navigationView.setCheckedItem(R.id.faq)
                    drawerlayout.closeDrawers()

                }
                R.id.logout -> {
                    AuthUI.getInstance().signOut(this)
                        .addOnCompleteListener(this) { task ->
                            val dialogg = android.app.AlertDialog.Builder(this@MainActivity)
                            dialogg.setTitle("Logout")
                            dialogg.setMessage("Are you sure you want to logout? ")
                                .setPositiveButton(
                                    "LOGOUT",
                                    { dialog, id ->
                                        finish()
                                        if (task.isSuccessful) {
                                            log().setLoggedIn(this,false)
                                            Toast.makeText(this, "Logged OUT", Toast.LENGTH_SHORT).show()
                                            startActivity(Intent(this, LoginActivity::class.java))
                                            finish()
                                        } else {
                                            Log.d("OnComplete", task.exception.toString())
                                        }

                                    })
                                .setNegativeButton(
                                    "CANCEL",
                                    { dialog, id ->
                                        dialog.cancel()
                                    })
                            dialogg.create()
                            dialogg.show()
                        }
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }

    fun settoolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Toolbar Title"
        supportActionBar?.setHomeButtonEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }
   override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            drawerlayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)

    }

    fun openHome() {
        val fragment = HomeFragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame, fragment)
        transaction.commit()
        supportActionBar?.title = "All Restuarants"
        navigationView.setCheckedItem(R.id.home)
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame)
        when (frag) {
            !is HomeFragment -> openHome()
            else -> super.onBackPressed()

        }
    }
}
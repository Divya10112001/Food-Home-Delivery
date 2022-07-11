package com.foodcart.activity

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.foodcart.R
import com.foodcart.Connection
import kotlin.system.exitProcess

class Splash : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()
        if(!Connection().checkConnectivity(this)) {
            val dialog = AlertDialog.Builder(this)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection is not Found")
            dialog.create()
            dialog.show()
            exitProcess(1)
        }else {

            Handler(Looper.getMainLooper()).postDelayed(
                {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                },
                2000,
            )
        }
    }
}
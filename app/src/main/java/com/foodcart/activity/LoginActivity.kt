package com.foodcart.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.foodcart.R
import com.foodcart.modal.log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.*

class LoginActivity : AppCompatActivity() {
    lateinit var mAuth: FirebaseAuth
    lateinit var googleSignInClient: GoogleSignInClient
    var personName: String = " "
    var personGivenName: String = " "
    var personEmail: String = " "
    var personPhoto: String? = null
    val RC_SIGN_IN = 1002
    lateinit var progressBar: ProgressBar

    @SuppressLint("CommitPrefEdits")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val googlesignIn = findViewById<SignInButton>(R.id.googlebtn)
        mAuth= FirebaseAuth.getInstance()
        progressBar = findViewById(R.id.pb)
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        progressBar.visibility = View.GONE
        googleSignInClient = GoogleSignIn.getClient(this, gso)

        googlesignIn.setOnClickListener {
            log().setLoggedIn(this@LoginActivity, true)
            val signInIntent = googleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            progressBar.visibility = View.VISIBLE
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            if (task.isSuccessful) {
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    val credential = GoogleAuthProvider.getCredential(account.idToken!!, null)
                    mAuth.signInWithCredential(credential)
                        .addOnCompleteListener(this) { task ->
                            Toast.makeText(this, "SignIn Successful!!", Toast.LENGTH_SHORT)
                                .show()
                            details()
                        }
                } catch (e: ApiException) {
                    Toast.makeText(this, "Google Sign In Failed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "SignIn Failed!!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private fun details() {
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                personName = acct.displayName.toString()
                personGivenName = acct.givenName.toString()
                personEmail = acct.email.toString()
                personPhoto = acct.photoUrl.toString()
            }
        log().setLoggedIn(this@LoginActivity, true)
        val sh=getSharedPreferences("user", MODE_PRIVATE)
        val editor : SharedPreferences.Editor=sh.edit()
        editor.putString("Name",personName)
        editor.putString("GivenName",personGivenName)
        editor.putString("Email",personEmail)
        editor.putString("Image",personPhoto)
        editor.apply()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}

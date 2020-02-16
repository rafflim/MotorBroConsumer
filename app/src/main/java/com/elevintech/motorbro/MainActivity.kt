package com.elevintech.motorbro

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.elevintech.motorbro.CreateAccount.CreateAccountActivity
import com.elevintech.motorbro.Dashboard.DashboardActivity
import com.elevintech.myapplication.R
import com.google.firebase.auth.FirebaseAuth

import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var maleGenderLayoutIsClicked = false
    var femaleGenderLayoutIsClicked = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        createAccountText.setOnClickListener {
            val intent = Intent(applicationContext, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            doLogin()
        }


    }

    fun doLogin(){

        if (userNameEditText.text.toString() == "" || passwordEditText.text.toString() == ""){
            Toast.makeText(baseContext, "Please Fill up the username and password field", Toast.LENGTH_SHORT).show()
        } else {

            var progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Logging in....")
            progressDialog.setCancelable(false)
            progressDialog.show()

            FirebaseAuth.getInstance().signInWithEmailAndPassword("${userNameEditText.text}", "${passwordEditText.text}")
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        progressDialog.hide()
                        goToDashBoardActivity()
                    } else {
                        progressDialog.hide()
                        Toast.makeText(baseContext, "Authentication failed: ${task.exception?.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }

        }
    }

    fun goToDashBoardActivity(){
        val intent = Intent(applicationContext, DashboardActivity::class.java)
        startActivity(intent)
    }
}

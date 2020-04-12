package com.elevintech.motorbro

import com.elevintech.myapplication.R
import android.app.AlertDialog
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.elevintech.motorbro.BikeRegistration.BikeRegistrationActivity
import com.elevintech.motorbro.CreateAccount.CreateAccountActivity
import com.elevintech.motorbro.Dashboard.DashboardActivity
import com.elevintech.motorbro.Model.User
import com.elevintech.motorbro.Model.UserType
import com.elevintech.motorbro.MotorBroDatabase.MotoroBroDatabase
import com.elevintech.motorbro.Utils.Utils
import com.facebook.*
import com.facebook.AccessToken
import com.facebook.login.LoginResult
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.dialog_forgot_password.view.*


class MainActivity : AppCompatActivity() {

    private val callbackManager: CallbackManager = CallbackManager.Factory.create()
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //FacebookSdk.sdkInitialize(getApplicationContext());
//        AppEventsLogger.activateApp(this);
        auth = FirebaseAuth.getInstance()

        // facebook access token
        val accessToken = AccessToken.getCurrentAccessToken()
        val isLoggedIn = accessToken != null && !accessToken.isExpired
        println(isLoggedIn)

        createAccountText.setOnClickListener {
            val intent = Intent(applicationContext, CreateAccountActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            doLogin()
        }

        forgotPasswordButton.setOnClickListener {
            createAlertDialog()
        }

        setupFacebookLogin()

    }

    private fun setupFacebookLogin() {

        val loginButton = facebookLoginButton
        val userFacebookPermissions =  listOf("email", "public_profile")
        // Initialize Facebook Login button

        //loginButton.setReadPermissions("email", "public_profile")
        loginButton.setPermissions(userFacebookPermissions)
        loginButton.registerCallback(callbackManager, object : FacebookCallback<LoginResult> {
            override fun onSuccess(loginResult: LoginResult) {
                Log.d("SUCCESS", "facebook:onSuccess:$loginResult")
                handleFacebookAccessToken(loginResult.accessToken)
                loginResult.accessToken.permissions.forEach {
                    print(it)
                }
            }

            override fun onCancel() {
                Log.d("CANCELLED", "facebook:onCancel")
                // ...
            }

            override fun onError(error: FacebookException) {
                Log.d("ERROR", "facebook:onError", error)
                print(error.localizedMessage)
                //Toast.makeText(this, error.localizedMessage, Toast.LENGTH_LONG)
                // ...
            }
        })
    }

    private fun doLogin(){

        if (userNameEditText.text.toString() == "" || passwordEditText.text.toString() == ""){
            Toast.makeText(baseContext, "Please Fill up the username and password field", Toast.LENGTH_SHORT).show()
        } else {

            val progressDialog = Utils().easyProgressDialog(this, "Logging in....")
            progressDialog.show()

            FirebaseAuth.getInstance().signInWithEmailAndPassword("${userNameEditText.text}", "${passwordEditText.text}")
                .addOnSuccessListener {
                    verifyDeviceToken()
                    checkUserType(progressDialog)

                }
                .addOnFailureListener { e ->
                    progressDialog.dismiss()
                    Toast.makeText(baseContext, "Authentication failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                }

        }
    }

    private fun checkUserType(progressDialog: ProgressDialog) {
        val db = MotoroBroDatabase()
        db.getUserType{ userType ->

            if (userType == UserType.Type.EMPLOYEE || userType == UserType.Type.OWNER){
                progressDialog.dismiss()

                Toast.makeText(baseContext, "Please use the MotorBroShop app", Toast.LENGTH_SHORT).show()
                FirebaseAuth.getInstance().signOut()
            } else {
                progressDialog.dismiss()
                goToDashBoardActivity()
            }

        }
    }

    private fun verifyDeviceToken() {
        FirebaseInstanceId.getInstance().instanceId
            .addOnCompleteListener(OnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Get new Instance ID token
                    val token = task.result?.token!!

                    // Log and toast
                    MotoroBroDatabase().getUserToken{ tokenInDatabase ->
                        if (tokenInDatabase != token){
                            MotoroBroDatabase().updateFcmToken(token)
                        }
                    }
                } else {
                    println("getInstanceId failed" + task.exception)
                }
            })
    }

    private fun goToDashBoardActivity(){
        val intent = Intent(applicationContext, DashboardActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun createAlertDialog(){
        // instantiate dialog
        val builder = AlertDialog.Builder(this)
        val optionDialog = AlertDialog.Builder(this).create()
        // instantiate the view for the dialog
        val viewInflated = layoutInflater.inflate(R.layout.dialog_forgot_password, null)
        // inflate the view in the dialog
        builder.setView(viewInflated)
        // set the dialog title
        builder.setTitle("Forgot Password")
        builder.setCancelable(true) // can be set to false, to make the dialog undismissable
        builder.setPositiveButton("Send Email",
            DialogInterface.OnClickListener { dialog, which ->
                println("works")
                val email = viewInflated.emailEditDialogText.text.toString()
                //val someEmail = emailEditDialogText.
                println(email)
                auth.sendPasswordResetEmail(email)

                Toast.makeText(this, "If your email was in our database, you would have received an email. Please check your email.", Toast.LENGTH_SHORT).show()

                // dismiss dialog after
                dialog.dismiss()
            })
        builder.setNegativeButton("Cancel",
            DialogInterface.OnClickListener { dialog, which ->
                dialog.cancel()
            })

        builder.show()
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        val progressDialog = Utils().easyProgressDialog(this, "Logging in....")
        progressDialog.show()

        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("SUCCESS", "signInWithCredential:success")
                    val user = auth.currentUser
                    //updateUI(user)
                    print(user?.email)
                    print(user?.photoUrl)
                    //var photoUrl = user?.photoUrl  + "?height=500" //High quality

                    // if user exist and has an account then go to the main dashboard..
                    val db = MotoroBroDatabase()
                    db.doesUserExist(user?.uid!!) {
                        val doesUserExist = it
                        if (doesUserExist) {
                            // check the user registration process
                            // act accordingly
                            verifyDeviceToken()
                            checkUserType(progressDialog)
                        } else {
                            val request = GraphRequest.newMeRequest(
                                token
                            ) { requestObject, response ->
                                // Application code

                                print(response)
                                print(response.jsonObject)

                                val firstName = requestObject.getString("first_name")
                                val lastName = requestObject.getString("last_name")
                                val photoUrl = user.photoUrl.toString() + "?height=250" //High quality
                                val uid = user.uid
                                val email = user.email

                                if (email != null && uid != null) {
                                    saveFacebookData(email, photoUrl, firstName, lastName, uid, progressDialog)
                                }
                                // Transform response.jsonObject to an actual facebook user object
                                // Then transform that to a user object
                                // Save it
                            }
                            val parameters = Bundle()
                            parameters.putString("fields", "first_name,gender,last_name, picture.type(large), location,birthday,age_range")
                            // TODO: Add picture.type(large)
                            request.parameters = parameters
                            request.executeAsync()
                        }
                    }


                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("ERROR", "signInWithCredential:failure", task.exception)
                    progressDialog.hide()
                    Toast.makeText(baseContext, "Authentication failed.",
                        Toast.LENGTH_SHORT).show()
                    //updateUI(null)
                }
            }
    }

    private fun saveFacebookData(email: String, photoUrl: String, firstName: String, lastName: String, uid: String, progressDialog: ProgressDialog) {

            val user = User()
            user.uid = uid
            user.firstName = firstName
            user.lastName = lastName
            //user.gender = gender
            user.email = email
            user.profileImage = photoUrl
            user.usersRegistrationProgress = 1
            val db = MotoroBroDatabase()
            db.createUserType {
                db.createUser(user){
                        progressDialog.dismiss()

                        val intent = Intent(applicationContext, BikeRegistrationActivity::class.java)
                        intent.putExtra("previousActivity", "createAccount")
                        startActivity(intent)
                        finish()
                    }
            }

    }

    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

}

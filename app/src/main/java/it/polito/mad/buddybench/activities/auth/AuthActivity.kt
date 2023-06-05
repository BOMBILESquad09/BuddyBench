package it.polito.mad.buddybench.activities.auth

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.android.material.button.MaterialButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import it.polito.mad.buddybench.R
import it.polito.mad.buddybench.activities.HomeActivity

class AuthActivity : AppCompatActivity() {
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var signUpRequest: BeginSignInRequest
    private lateinit var signInButton: MaterialButton
    private lateinit var progressBar: ProgressBar

    private val REQ_ONE_TAP = 2

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

        setContentView(R.layout.activity_auth)

        signInButton = findViewById<MaterialButton>(R.id.sign_in_button)
        progressBar = findViewById<ProgressBar>(R.id.progress_bar)
//        signInButton.setSize(SignInButton.SIZE_WIDE)

        signInButton.setOnClickListener {
            signIn()
        }

        auth = FirebaseAuth.getInstance()

        if (auth.currentUser == null && !intent.getBooleanExtra("fromLogout", false))
            signIn()
        else if (auth.currentUser != null) {
            // User already logged-in, close this activity
            signInButton.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
            val i = Intent(applicationContext, HomeActivity::class.java)
            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(i)
            signInButton.visibility = View.VISIBLE
            progressBar.visibility = View.GONE
        }
    }

    private fun signIn() {
        /* Building sign-up request code */

        oneTapClient = Identity.getSignInClient(this)
        signUpRequest = BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.google_oauth_client_id))
                    // Show all accounts on the device.
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .build()

        /* Building Google Sign-in code */
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(
                BeginSignInRequest.PasswordRequestOptions.builder()
                    .setSupported(true)
                    .build()
            )
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.google_oauth_client_id))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(true)
                    .build()
            )
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()

        /* Starting sign-in request */
        oneTapClient.beginSignIn(signInRequest)
            .addOnSuccessListener(this) { result ->
                try {
                    startIntentSenderForResult(
                        result.pendingIntent.intentSender, REQ_ONE_TAP,
                        null, 0, Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK, 0, null
                    )

                } catch (e: IntentSender.SendIntentException) {
                    Log.e(ContentValues.TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                }
            }
            .addOnFailureListener(this) { e ->
                // No saved credentials found. Launch the One Tap sign-up flow, or
                // do nothing and continue presenting the signed-out UI.

                /* Starting sign-up flow */
                Log.d(ContentValues.TAG, e.localizedMessage ?: "Starting sign-up flow")
                oneTapClient.beginSignIn(signUpRequest)
                    .addOnSuccessListener(this) { result ->
                        try {
                            Log.d(ContentValues.TAG, "Starting sign-up request")
                            startIntentSenderForResult(
                                result.pendingIntent.intentSender, REQ_ONE_TAP,
                                null, 0, 0, 0
                            )
                        } catch (e: IntentSender.SendIntentException) {
                            Log.e(
                                ContentValues.TAG,
                                "Couldn't start One Tap UI: ${e.localizedMessage}"
                            )
                        }
                    }
                    .addOnFailureListener(this) { _ ->
                        // No Google Accounts found. Just continue presenting the signed-out UI.
                        // Signed-out UI is unwanted so the app is closed
                        Toast.makeText(
                            this,
                            "Google account is necessary to use the app. Add at least one to your device",
                            Toast.LENGTH_SHORT
                        ).show()
                        Log.d(ContentValues.TAG, e.localizedMessage ?: "No google accounts found")
                    }
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    signInButton.visibility = View.GONE
                    progressBar.visibility = View.VISIBLE
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with firebase

                            val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                            auth.signInWithCredential(firebaseCredential)
                                .addOnCompleteListener(this) { task ->
                                    if (task.isSuccessful) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(ContentValues.TAG, "signInWithCredential:success")
                                        val user = auth.currentUser
                                        val i = Intent(applicationContext, HomeActivity::class.java)
                                        i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                        startActivity(i)
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        signInButton.visibility = View.VISIBLE
                                        progressBar.visibility = View.GONE
                                        Log.w(
                                            ContentValues.TAG,
                                            "signInWithCredential:failure",
                                            task.exception
                                        )
                                        //updateUI(null)
                                    }
                                }
                            Log.d(ContentValues.TAG, "Got ID token.")
                        }

                        else -> {
                            // Shouldn't happen.
                            Log.d(ContentValues.TAG, "No ID token or password!")
                            signInButton.visibility = View.VISIBLE
                            progressBar.visibility = View.GONE
                        }
                    }
                } catch (e: ApiException) {
                    Toast.makeText(
                        this,
                        "Google account is necessary to use the app.",
                        Toast.LENGTH_LONG
                    ).show()
                    Log.d(
                        ContentValues.TAG,
                        "Api exception thrown from activityResult: ${e.message}"
                    )
                    signInButton.visibility = View.VISIBLE
                    progressBar.visibility = View.GONE
                }
            }
        }
    }
}
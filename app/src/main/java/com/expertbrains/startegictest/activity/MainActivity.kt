package com.expertbrains.startegictest.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import com.expertbrains.startegictest.R
import com.expertbrains.startegictest.base.BaseActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.lay_new_sign_in.*
import kotlinx.android.synthetic.main.lay_old_sign_in.*

class MainActivity : BaseActivity() {

    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var fbAuth: FirebaseAuth

    companion object {
        const val REQUEST_RESULT_CODE = 1001
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        btnSignIn.setOnClickListener {
            startActivityForResult(googleSignInClient.signInIntent, REQUEST_RESULT_CODE)
        }

        btnContinue.setOnClickListener {
            startActivity(Intent(this, DashboardActivity::class.java))
        }
        btnSignOut.visibility = View.GONE
        btnSignOut.setOnClickListener { signOut() }
    }

    private fun init() {
        bindToolbar()
        fbAuth = Firebase.auth
        changeUI(fbAuth.currentUser)
        val googleSignInOption = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOption)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_RESULT_CODE -> {
                val task = GoogleSignIn.getSignedInAccountFromIntent(data)
                try {
                    val account = task.getResult(ApiException::class.java)!!
                    firebaseAuthWithGoogle(account.idToken!!)
                } catch (e: ApiException) {
                }
            }
        }
    }

    private fun firebaseAuthWithGoogle(token: String) {
        fbAuth.signInWithCredential(GoogleAuthProvider.getCredential(token, null))
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful)
                    changeUI(fbAuth.currentUser)
                else
                    changeUI(null)
            }
    }

    private fun changeUI(user: FirebaseUser?) {
        if (user != null) {
            vfSignIn.displayedChild = 1
            tvPersonName.text = "Sign in with ${user.displayName}"
        } else {
            vfSignIn.displayedChild = 0
            tvPersonName.text = ""
        }
    }

    private fun signOut() {
        fbAuth.signOut()
        googleSignInClient.signOut().addOnCompleteListener(this) {
            changeUI(null)
        }
    }
}
package com.project.major.alumniapp.activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.mikepenz.iconics.Iconics
import com.project.major.alumniapp.R
import com.project.major.alumniapp.utils.ConnectionService
import com.project.major.alumniapp.utils.SessionManager
import kotlinx.android.synthetic.main.activity_splash_screen.*

class SplashScreen : AppCompatActivity() {
    var sessionManager: SessionManager? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        Iconics.init(this)
        sessionManager = SessionManager(applicationContext)
        intent = Intent(this, ConnectionService::class.java)
        startService(intent)
        Handler().postDelayed({ sessionManager!!.checkLogin() }, 2000)
    }

    override fun onPause() {
        super.onPause()
        finish()
    }
}
package com.example.group3_starry

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp

class StartScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 初始化 Firebase
        FirebaseApp.initializeApp(this)

        setContentView(R.layout.activity_start_screen)

        // 获取按钮
        val signUpButton = findViewById<Button>(R.id.signUpButton)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // 点击事件：跳转到 Sign Up 页面
        signUpButton.setOnClickListener {
            val intent = Intent(this, UsernameActivity::class.java) // Sign Up 的第一个页面
            startActivity(intent)
        }

        // 点击事件：跳转到 Log In 页面
        loginButton.setOnClickListener {
            val intent = Intent(this, LogInActivity::class.java) // Log In 页面
            startActivity(intent)
        }
    }
}

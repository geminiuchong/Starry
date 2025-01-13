package com.example.group3_starry

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class UsernameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_username)

        val emailInput = findViewById<EditText>(R.id.emailInput)
        val nextButton = findViewById<ImageView>(R.id.nextButton)

        // 点击右下角箭头
        nextButton.setOnClickListener {
            val email = emailInput.text.toString().trim()

            // 校验邮箱格式是否有效
            if (email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                // 跳转到 Password Screen
                val intent = Intent(this, PasswordActivity::class.java)
                intent.putExtra("email", email) // 将邮箱传递给下一步
                startActivity(intent)
            } else {
                // 显示错误提示
                Toast.makeText(this, "Invalid email address. Please enter a valid email.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

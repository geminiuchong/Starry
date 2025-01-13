package com.example.group3_starry

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.AutocompleteActivity
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import java.util.*

class SignUpActivity : AppCompatActivity() {

    private lateinit var birthPlaceInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        // 初始化 Google Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, "AIzaSyCNIWM0uM3jLqOG-eP8_ouuV54I76gn_1I")
        }

        val birthDateInput = findViewById<EditText>(R.id.birthDateInput)
        val birthDateIcon = findViewById<ImageView>(R.id.birthDateIcon)
        val birthTimeInput = findViewById<EditText>(R.id.birthTimeInput)
        val birthTimeIcon = findViewById<ImageView>(R.id.birthTimeIcon)
        birthPlaceInput = findViewById(R.id.birthPlaceInput)
        val birthPlaceIcon = findViewById<ImageView>(R.id.birthPlaceIcon)
        val nextButton = findViewById<ImageView>(R.id.nextButton)

        // 日期选择逻辑
        birthDateIcon.setOnClickListener {
            val calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                birthDateInput.setText("$selectedDay - ${selectedMonth + 1} - $selectedYear")
            }, year, month, day)
            datePicker.show()
        }

        // 时间选择逻辑
        birthTimeIcon.setOnClickListener {
            val calendar = Calendar.getInstance()
            val hour = calendar.get(Calendar.HOUR_OF_DAY)
            val minute = calendar.get(Calendar.MINUTE)

            val timePicker = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
                birthTimeInput.setText(String.format("%02d:%02d", selectedHour, selectedMinute))
            }, hour, minute, true)
            timePicker.show()
        }

        // 地点选择逻辑（使用 Google Places API）
        birthPlaceIcon.setOnClickListener {
            val fields = listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS
            )
            val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.FULLSCREEN, fields)
                .build(this)
            startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE)
        }

        // 点击右下角箭头跳转到主页面
        nextButton.setOnClickListener {
            if (birthDateInput.text.isNullOrEmpty() ||
                birthTimeInput.text.isNullOrEmpty() ||
                birthPlaceInput.text.isNullOrEmpty()
            ) {
                Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // 接收 Google Places API 返回的结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            when (resultCode) {
                RESULT_OK -> {
                    val place = Autocomplete.getPlaceFromIntent(data!!)
                    birthPlaceInput.setText(place.address) // 将选中的地址填入输入框
                }
                AutocompleteActivity.RESULT_ERROR -> {
                    val status = Autocomplete.getStatusFromIntent(data!!)
                    Toast.makeText(this, "Error: ${status.statusMessage}", Toast.LENGTH_SHORT).show()
                }
                RESULT_CANCELED -> {
                    Toast.makeText(this, "Location selection canceled.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    companion object {
        private const val AUTOCOMPLETE_REQUEST_CODE = 1
    }
}

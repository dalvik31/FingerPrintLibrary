package com.dalvik.fingerprintauth

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.dalvik.fingerprintlibrary.ManagerFingerPrint

class MainActivity : AppCompatActivity() {


    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val button = findViewById<Button>(R.id.btn_authenticate)
        val managerFingerPrint: ManagerFingerPrint = ManagerFingerPrint.from(this)
        button.setOnClickListener {
            managerFingerPrint.initAuthentication { isAuthSuccess, msgError ->
                Toast.makeText(this, msgError, Toast.LENGTH_SHORT).show()
            }
        }
    }

}
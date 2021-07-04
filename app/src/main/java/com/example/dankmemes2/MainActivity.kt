package com.example.dankmemes2

import android.os.Bundle
import android.os.Environment
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.File


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
    }

    override fun onDestroy() {
        super.onDestroy()
        val dir = File(Environment.getExternalStorageDirectory().toString() + "/SharedDankMemes")
        if (dir.isDirectory()) {
            dir.deleteRecursively()
        }
        Log.i("TAGG", "onDestroy Called")
    }
}


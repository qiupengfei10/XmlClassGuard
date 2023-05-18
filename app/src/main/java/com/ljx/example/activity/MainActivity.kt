package com.ljx.example.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.ljx.example.R
import com.ljx.example.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private val dataBinding: ActivityMainBinding by lazy(LazyThreadSafetyMode.NONE) {
        DataBindingUtil.setContentView(this, R.layout.activity_main)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        dataBinding.lifecycleOwner = this
    }

    fun getController(): NavController {
        return findNavController(R.id.global_container)
    }
}
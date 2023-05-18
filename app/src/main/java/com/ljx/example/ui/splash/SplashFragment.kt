package com.ljx.example.ui.splash

import android.os.Bundle
import android.view.View
import androidx.lifecycle.lifecycleScope
import com.ljx.example.R
import com.ljx.example.base.BaseFragment
import com.ljx.example.databinding.FragmentSplashBinding
import com.ljx.example.test.Test
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SplashFragment:BaseFragment<FragmentSplashBinding>(R.layout.fragment_splash){
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.test = Test().apply {
            text = "启动页"
        }
        lifecycleScope.launch {
            delay(3000)
            withContext(Dispatchers.Main){
                safeNavigation(R.id.splashFragment,SplashFragmentDirections.gotoMain(test = Test().apply {
                    text = "主页"
                }))
            }
        }
    }
}
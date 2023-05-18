package com.ljx.example.ui.splash

import android.os.Bundle
import android.view.View
import androidx.navigation.fragment.navArgs
import com.ljx.example.R
import com.ljx.example.base.BaseFragment
import com.ljx.example.databinding.FragmentMainBinding

class MainFragment : BaseFragment<FragmentMainBinding>(R.layout.fragment_main) {

    private val mainArgs by navArgs<MainFragmentArgs>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dataBinding.test = mainArgs.test
    }
}
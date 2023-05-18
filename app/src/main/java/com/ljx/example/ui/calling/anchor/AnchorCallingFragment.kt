package com.ljx.example.ui.calling.anchor

import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ljx.example.R
import com.ljx.example.base.BaseFragment
import com.ljx.example.databinding.FragmentAnchorCallingBinding

class AnchorCallingFragment :
    BaseFragment<FragmentAnchorCallingBinding>(R.layout.fragment_anchor_calling) {
    private val viewModel by viewModels<AnchorCallingViewModel>()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
    }
}
package com.ljx.example.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import com.ljx.example.activity.MainActivity

abstract class BaseFragment<T : ViewDataBinding>(
    @LayoutRes private val layoutId: Int,
    private val isDisableBack: Boolean = false
) : Fragment(), View.OnClickListener {

    lateinit var dataBinding: T
        private set

    private var permissionAction: (() -> Unit)? = null


    override fun onAttach(context: Context) {
        super.onAttach(context)
        // todo 用于替换掉 MainActivity 中的 onHackBackListener
        if (isDisableBack) {
            val callback = object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    handleBackEvent()
                }
            }
            requireActivity().onBackPressedDispatcher.addCallback(this, callback)
        }
        // 手动触发回退
        // callback.isEnabled = false
        // dispatcher.onBackPressed()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dataBinding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        dataBinding.lifecycleOwner = viewLifecycleOwner
        return dataBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onStart() {
        super.onStart()
    }

    override fun onResume() {
        super.onResume()
    }

    override fun onPause() {
        super.onPause()
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onDestroyView() {
        permissionAction = null
        super.onDestroyView()
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    override fun onClick(v: View?) = Unit


    open fun handleBackEvent() = Unit


    fun getMainActivity(): MainActivity? {
        return activity as? MainActivity?
    }

    fun safeNavigation(
        destination: Int,
        directions: NavDirections,
        isLeft2Right: Boolean = false,
        isBottom2Top: Boolean = false,
        isShowAnimation: Boolean = true
    ) {
        val controller = getMainActivity()?.getController() ?: return
        if (controller.currentDestination?.id != destination) return
        val navOptions =
            controller.graph.findNode(destination)?.getAction(directions.actionId)?.navOptions
        if (!isShowAnimation) {
            val newNavOptionsBuilder = NavOptions.Builder()
                .setLaunchSingleTop(true)
            if (navOptions?.isPopUpToInclusive() == true) {
                newNavOptionsBuilder.setPopUpTo(destination, true)
            }
            controller.navigate(directions, newNavOptionsBuilder.build())
            return
        }
        val newNavOptionsBuilder = NavOptions.Builder()
            .setLaunchSingleTop(true)
        if (navOptions?.isPopUpToInclusive() == true) {
            newNavOptionsBuilder.setPopUpTo(destination, true)
        }
        controller.navigate(directions, newNavOptionsBuilder.build())
    }

    private fun getAnimation(isShowAnimation: Boolean, animationId: Int?, defaultId: Int): Int {
        if (!isShowAnimation) return -1
        return if (animationId != null && animationId != -1) animationId else defaultId
    }

    fun navigateUp(destination: Int) {
        if (getMainActivity()?.getController()?.currentDestination?.id == destination) {
            getMainActivity()?.getController()?.navigateUp()
        }
    }

    fun getController(): NavController? = getMainActivity()?.getController()


    fun dataBindingIsInitialized(): Boolean {
        return this::dataBinding.isInitialized
    }

}
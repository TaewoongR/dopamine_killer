package com.example.dopamine_killer.foregroundService

import android.accessibilityservice.AccessibilityService
import android.animation.Animator
import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.dopamine_killer.R
import kotlin.math.abs

class CustomPopupView(private val context: Context) {
    private val handler = Handler(Looper.getMainLooper())
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val popupView: View = LayoutInflater.from(context).inflate(R.layout.view_custom_popup, null)
    private var isShowing = false
    private var initialX = 0f
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private val clickThreshold = 10

    init {
        // Setup touch listener for popup view
        popupView.setOnTouchListener{ _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = popupView.x
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    true
                }
                MotionEvent.ACTION_MOVE -> {
                    val offsetX = event.rawX - initialTouchX
                    popupView.x = initialX + offsetX
                    true
                }
                MotionEvent.ACTION_UP -> {
                    val offsetX = event.rawX - initialTouchX
                    val offsetY = event.rawY - initialTouchY
                    if (abs(offsetX) > clickThreshold || abs(offsetY) > clickThreshold) {
                        if (abs(offsetX) > popupView.width / 4) {
                            animateAndHidePopup(offsetX > 0)
                        } else {
                            animateToOriginalPosition()
                        }
                    } else {
                        performAction()
                    }
                    true
                }
                else -> false
            }
        }
    }

    fun showMessage(message: String, duration: Long = 10000) {
        if (isShowing || !Settings.canDrawOverlays(context)) return

        val messageTextView: TextView = popupView.findViewById(R.id.popup_message)
        messageTextView.text = message

        val iconImageView: ImageView = popupView.findViewById(R.id.popup_icon)
        iconImageView.setImageResource(R.drawable.dkapplogo) // Set the icon resource

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )

        layoutParams.gravity = android.view.Gravity.TOP or android.view.Gravity.CENTER_HORIZONTAL
        layoutParams.windowAnimations = android.R.style.Animation_Dialog

        if (popupView.parent == null) {
            windowManager.addView(popupView, layoutParams)
        }

        popupView.visibility = View.VISIBLE
        isShowing = true

        handler.postDelayed({
            hidePopup()
        }, duration)
    }

    private fun hidePopup() {
        if (isShowing && popupView.visibility == View.VISIBLE) {
            try {
                windowManager.removeView(popupView)
            } catch (e: IllegalArgumentException) {
                // View가 이미 제거된 경우 예외 처리
                e.printStackTrace()
            } finally {
                isShowing = false
            }
        }
    }

    private fun performAction() {
        if (isAccessibilityServiceEnabled(context, YourAccessibilityService::class.java)) {
            val foregroundAppChecker = ForegroundAppChecker(context)
            val foregroundApp = foregroundAppChecker.getForegroundApp()
            if (foregroundApp != null) {
                val intent = Intent("com.example.dopamine_killer.FORCE_STOP_APP")
                intent.putExtra("EXTRA_APP_PACKAGE", foregroundApp)
                context.sendBroadcast(intent)

                launchApp("com.example.dopamine_killer")
                hidePopup() // 팝업 종료
            } else {
                Toast.makeText(context, "Unable to determine foreground app", Toast.LENGTH_SHORT).show()
                hidePopup() // 팝업 종료
            }
        } else {
            Toast.makeText(context, "Accessibility Service is not enabled", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
            hidePopup()
        }
    }

    private fun animateAndHidePopup(toRight: Boolean) {
        val translationX = if (toRight) popupView.width.toFloat() else -popupView.width.toFloat()
        val animator = ObjectAnimator.ofFloat(popupView, "translationX", translationX)
        animator.duration = 300
        animator.start()
        animator.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                hidePopup()
            }
            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
    }

    private fun animateToOriginalPosition() {
        val animator = ObjectAnimator.ofFloat(popupView, "translationX", 0f)
        animator.duration = 300
        animator.start()
    }

    private fun launchApp(packageName: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        if (intent != null) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } else {
            Toast.makeText(context, "App not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService>): Boolean {
        val expectedComponentName = ComponentName(context, service)
        val enabledServices = Settings.Secure.getString(context.contentResolver, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES)
        val colonSplitter = TextUtils.SimpleStringSplitter(':')
        colonSplitter.setString(enabledServices)
        while (colonSplitter.hasNext()) {
            val componentNameString = colonSplitter.next()
            val enabledComponentName = ComponentName.unflattenFromString(componentNameString)
            if (enabledComponentName != null && enabledComponentName == expectedComponentName) {
                return true
            }
        }
        return false
    }
}

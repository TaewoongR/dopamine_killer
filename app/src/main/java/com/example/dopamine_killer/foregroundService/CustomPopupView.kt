package com.example.dopamine_killer.foregroundService

import YourAccessibilityService
import android.accessibilityservice.AccessibilityService
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.dopamine_killer.R

class CustomPopupView(private val context: Context) {
    private val handler = Handler(Looper.getMainLooper())
    private val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    private val popupView: View = LayoutInflater.from(context).inflate(R.layout.view_custom_popup, null)
    private var isShowing = false

    init {
        // Setup expand button
        val expandButton: Button = popupView.findViewById(R.id.expand_button)
        val expandedLayout: LinearLayout = popupView.findViewById(R.id.expanded_layout)
        expandButton.setOnClickListener {
            if (expandedLayout.visibility == View.VISIBLE) {
                expandedLayout.visibility = View.GONE
                expandButton.text = "Expand"
            } else {
                expandedLayout.visibility = View.VISIBLE
                expandButton.text = "Collapse"
            }
        }

        // Setup action button
        val actionButton: Button = popupView.findViewById(R.id.action_button)
        actionButton.setOnClickListener {
            performAction()
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
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
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
            popupView.visibility = View.GONE
            windowManager.removeView(popupView)
            isShowing = false
        }, duration)
    }

    private fun performAction() {
        if (isAccessibilityServiceEnabled(context, YourAccessibilityService::class.java)) {
            val foregroundAppChecker = ForegroundAppChecker(context)
            val foregroundApp = foregroundAppChecker.getForegroundApp()
            if (foregroundApp != null) {
                val intent = Intent("com.example.dopamine_killer.FORCE_STOP_APP")
                intent.putExtra("EXTRA_APP_PACKAGE", foregroundApp)
                context.sendBroadcast(intent)
            } else {
                Toast.makeText(context, "Unable to determine foreground app", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(context, "Accessibility Service is not enabled", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)  // FLAG_ACTIVITY_NEW_TASK 플래그 추가
            context.startActivity(intent)
        }
    }

    fun isAccessibilityServiceEnabled(context: Context, service: Class<out AccessibilityService>): Boolean {
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

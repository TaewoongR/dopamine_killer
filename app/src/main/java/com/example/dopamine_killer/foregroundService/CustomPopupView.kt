package com.example.dopamine_killer.foregroundService

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
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

    fun showMessage(message: String, duration: Long = 3000) {
        if (isShowing || !Settings.canDrawOverlays(context)) return

        val messageTextView: TextView = popupView.findViewById(R.id.popup_message)
        messageTextView.text = message

        val layoutParams = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else
                WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            WindowManager.LayoutParams.TYPE_STATUS_BAR
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
        // Perform the desired action here
    }
}

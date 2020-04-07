package com.project.major.alumniapp.utils

import android.app.Activity
import android.app.Dialog
import android.view.Window
import com.project.major.alumniapp.R

class LoadingDialog(private val activity: Activity) {
    private var dialog: Dialog? = null
    fun showLoading() {
        dialog = Dialog(activity)
        dialog!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog!!.setCancelable(false)
        dialog!!.setContentView(R.layout.activity_loading_dialog)
        dialog!!.show()
    }

    fun hideLoading() {
        dialog!!.dismiss()
    }

}
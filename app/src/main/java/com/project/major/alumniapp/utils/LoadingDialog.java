package com.project.major.alumniapp.utils;

import android.app.Activity;
import android.app.Dialog;
import android.view.Window;

import com.project.major.alumniapp.R;

public class LoadingDialog {
    private Activity activity;
    private Dialog dialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void showLoading() {
        dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.activity_loading_dialog);
        dialog.show();
    }

    public void hideLoading() {
        dialog.dismiss();
    }
}

package com.project.major.alumniapp.utils;

import android.app.Activity;
import android.content.Context;

import com.project.major.alumniapp.R;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class AlertDialogManager {
    public void showDialog(Activity activity, String title, String message, Boolean status){
        MaterialDialog materialDialog = new MaterialDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", R.drawable.ic_ok, (dialogInterface, which) -> dialogInterface.dismiss())
                .setAnimation("sucess-anim.json")
                .build();
        materialDialog.show();

    }
}

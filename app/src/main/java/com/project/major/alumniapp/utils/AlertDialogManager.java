package com.project.major.alumniapp.utils;

import android.app.Activity;

import com.project.major.alumniapp.R;
import com.shreyaspatil.MaterialDialog.MaterialDialog;

public class AlertDialogManager {
    private MaterialDialog materialDialog;
    public void showDialog(Activity activity, String title, String message, Boolean status){
        int file ;
        if (status){
            file= R.raw.sucess_anim;
        }else {
            file= R.raw.sucess_anim;
        }
        materialDialog = new MaterialDialog.Builder(activity)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(false)
                .setPositiveButton("OK", R.drawable.ic_ok, (dialogInterface, which) -> dialogInterface.dismiss())
                .setAnimation(file)
                .build();
        materialDialog.show();
    }
    public void hidedialog() {
        materialDialog.dismiss();
    }
}

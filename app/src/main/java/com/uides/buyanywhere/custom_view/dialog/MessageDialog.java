package com.uides.buyanywhere.custom_view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * Created by Admin on 7/8/2017.
 */

public class MessageDialog {
    private AlertDialog alertDialog;
    private DialogInterface.OnClickListener onClickListener;

    public MessageDialog(Context context, String title, String message, DialogInterface.OnClickListener onClickListener){
        alertDialog = new AlertDialog.Builder(context).create();
        alertDialog.setTitle(title);
        alertDialog.setMessage(message);
        if(onClickListener != null) {
            alertDialog.setCanceledOnTouchOutside(false);
        }else {
            alertDialog.setCanceledOnTouchOutside(true);
        }
        this.onClickListener = onClickListener;
        initButton();
    }

    public AlertDialog getDialog() {
        return alertDialog;
    }

    protected void initButton(){
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", onClickListener);
    }

    public void show(){
        alertDialog.show();
    }
}

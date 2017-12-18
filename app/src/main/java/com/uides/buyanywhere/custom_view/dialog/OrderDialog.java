package com.uides.buyanywhere.custom_view.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.uides.buyanywhere.R;
import com.uides.buyanywhere.custom_view.ClearableEditText;
import com.uides.buyanywhere.model.Order;
import com.uides.buyanywhere.model.UserProfile;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by TranThanhTung on 08/12/2017.
 */

public class OrderDialog implements View.OnClickListener, DialogInterface.OnShowListener {
    @BindView(R.id.txt_input_name)
    ClearableEditText textName;
    @BindView(R.id.txt_input_address)
    ClearableEditText textAddress;
    @BindView(R.id.txt_input_phone)
    ClearableEditText textPhone;
    @BindView(R.id.txt_quantity)
    TextView textQuantity;
    @BindView(R.id.btn_minus)
    ImageButton buttonMinus;
    @BindView(R.id.btn_plus)
    ImageButton buttonPlus;
    @BindView(R.id.progress_bar)
    ProgressBar progressBar;
    @BindView(R.id.rl_content)
    RelativeLayout contentLayout;

    private int currentQuantity = 1;
    private int maxQuantity;

    private AlertDialog alertDialog;
    private OnSubmitSuccessListener onSubmitSuccessListener;


    public OrderDialog(Context context, int maxQuantity) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.dialog_ship, null, false);
        ButterKnife.bind(this, rootView);

        this.maxQuantity = maxQuantity;

        buttonMinus.setOnClickListener(this);
        buttonPlus.setOnClickListener(this);

        alertDialog = new AlertDialog.Builder(context)
                .setTitle(R.string.order_dialog)
                .setView(rootView)
                .setPositiveButton(R.string.ship, null)
                .setNegativeButton(R.string.cancel, null)
                .setCancelable(true)
                .create();
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public void setCurrentQuantity(int currentQuantity) {
        this.currentQuantity = currentQuantity;
    }

    public void setMaxQuantity(int maxQuantity) {
        this.maxQuantity = maxQuantity;
    }

    public void show() {
        alertDialog.setOnShowListener(this);
        alertDialog.show();
    }

    public void setOnSubmitSuccessListener(OnSubmitSuccessListener onSubmitSuccessListener) {
        this.onSubmitSuccessListener = onSubmitSuccessListener;
    }

    @Override
    public void onShow(DialogInterface dialog) {
        Button buttonPositive = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
        buttonPositive.setOnClickListener(v -> {
            if (onSubmitSuccessListener != null) {
                if (!textName.validate() || !textAddress.validate() || !textPhone.validate()) {
                    return;
                }
                Order order = new Order(textName.getText(), textAddress.getText(), textPhone.getText(), currentQuantity);
                onSubmitSuccessListener.onSubmitSuccess(OrderDialog.this, order);
            }
        });
    }

    public void showProgressBar(boolean isShow) {
        alertDialog.setCancelable(!isShow);
//        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setEnabled(!isShow);
//        alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setEnabled(!isShow);
        progressBar.setVisibility(isShow ? View.VISIBLE : View.INVISIBLE);
        contentLayout.setVisibility(isShow ? View.INVISIBLE : View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_minus: {
                if (currentQuantity > 1) {
                    currentQuantity -= 1;
                    textQuantity.setText("" + currentQuantity);
                }
            }
            break;

            case R.id.btn_plus: {
                if (currentQuantity < maxQuantity) {
                    currentQuantity += 1;
                    textQuantity.setText("" + currentQuantity);
                }
            }
            break;

            default: {
                break;
            }
        }
    }

    public void dismiss() {
        alertDialog.dismiss();
    }

    public void showData(UserProfile userProfile) {
        textName.setText(userProfile.getName());
        textAddress.setText(userProfile.getAddress());
        textPhone.setText(userProfile.getPhone());
    }

    public interface OnSubmitSuccessListener {
        void onSubmitSuccess(OrderDialog orderDialog, Order order);
    }
}

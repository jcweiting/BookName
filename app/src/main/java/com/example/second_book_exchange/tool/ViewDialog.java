package com.example.second_book_exchange.tool;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.example.second_book_exchange.AddBookActivity;
import com.example.second_book_exchange.R;

import org.w3c.dom.Text;

public class ViewDialog {

    private OnAlertDialogClickListener onAlertDialogClickListener;

    public void setOnAlertDialogClickListener(OnAlertDialogClickListener onAlertDialogClickListener){
        this.onAlertDialogClickListener = onAlertDialogClickListener;
    }


    public void showDialog(Activity activity, String msg){

        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_dialog_warning);

        TextView info = dialog.findViewById(R.id.info);
        info.setText(msg);

        TextView confirm = dialog.findViewById(R.id.confirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onAlertDialogClickListener.onConfirm();
            }
        });

        TextView cancel = dialog.findViewById(R.id.cancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onAlertDialogClickListener.onCancel();
            }
        });

        dialog.show();

    }


    public interface OnAlertDialogClickListener{
        void onConfirm();

        void onCancel();
    }
}

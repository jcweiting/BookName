package com.example.second_book_exchange.tool;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.example.second_book_exchange.R;

public class OrderStatusDialog extends DialogFragment {

    private ConstraintLayout optionUnpaid, optionConfirmPaid, optionShipped, optionComplete;
    private TextView tvUnpaid, tvConfirmPaid, tvShipped, tvComplete;


    public static OrderStatusDialog newInstance() {

        Bundle args = new Bundle();

        OrderStatusDialog fragment = new OrderStatusDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public OnOrderStatusDialogClickListener listener;

    public void setListener(OnOrderStatusDialogClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() == null){
            return;
        }
    }

    //初始化VIEW
    private void initView(View view) {

        optionUnpaid = view.findViewById(R.id.opt_family);
        optionConfirmPaid = view.findViewById(R.id.opt_seven);
        optionShipped = view.findViewById(R.id.opt_delivery);
        optionComplete = view.findViewById(R.id.opt_face);

        tvUnpaid = view.findViewById(R.id.unpaid);
        tvConfirmPaid = view.findViewById(R.id.confirmpaid);
        tvShipped = view.findViewById(R.id.shipped);
        tvComplete = view.findViewById(R.id.complete);

        optionUnpaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickUnpaid(tvUnpaid.getText().toString());
                dismiss();
            }
        });

        optionConfirmPaid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickConfirmPaid(tvConfirmPaid.getText().toString());
                dismiss();
            }
        });

        optionShipped.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickShipped(tvShipped.getText().toString());
                dismiss();
            }
        });

        optionComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickComplete(tvComplete.getText().toString());
                dismiss();
            }
        });
    }

    //這邊要綁定LAYOUT 需要改的地方只有R.layout.xxxxx 還有 wlp.width 寬度 其他不用動
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_order_status, null);
        Dialog dialog = new Dialog(getActivity());
        // 关闭标题栏，setContentView() 之前调用
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(view);
        initView(view);
        dialog.setCancelable(false);
        Window window = dialog.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.width = DpConvertTool.getInstance().getDb(300);
        wlp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(wlp);
        return dialog;
    }

    //這裡跟recyclerView的點擊事件一樣的用法
    public interface OnOrderStatusDialogClickListener {
        void onClickUnpaid(String status);
        void onClickConfirmPaid(String status);
        void onClickShipped(String status);
        void onClickComplete(String status);
    }

}

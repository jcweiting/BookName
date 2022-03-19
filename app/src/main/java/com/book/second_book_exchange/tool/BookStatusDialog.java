package com.book.second_book_exchange.tool;

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
import com.book.second_book_exchange.R;

public class BookStatusDialog extends DialogFragment {

    private ConstraintLayout option1, option2, option3, option4;
    private TextView tvStatus1,tvStatus2,tvStatus3,tvStatus4;


    public static BookStatusDialog newInstance() {

        Bundle args = new Bundle();

        BookStatusDialog fragment = new BookStatusDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public OnBookStatusDialogClickListener listener;

    public BookStatusDialog setOnBookStatusDialogClickListener(OnBookStatusDialogClickListener listener){
        this.listener = listener;
        return this;
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

        option1 = view.findViewById(R.id.opt_family);
        option2 = view.findViewById(R.id.opt_seven);
        option3 = view.findViewById(R.id.opt_delivery);
        option4 = view.findViewById(R.id.opt_face);

        tvStatus1 = view.findViewById(R.id.unpaid);
        tvStatus2 = view.findViewById(R.id.confirmpaid);
        tvStatus3 = view.findViewById(R.id.shipped);
        tvStatus4 = view.findViewById(R.id.complete);

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLevel1Click(tvStatus1.getText().toString());
                dismiss();
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLevel2Click(tvStatus2.getText().toString());
                dismiss();
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLevel3Click(tvStatus3.getText().toString());
                dismiss();
            }
        });

        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onLevel4Click(tvStatus4.getText().toString());
                dismiss();
            }
        });
    }

    //這邊要綁定LAYOUT 需要改的地方只有R.layout.xxxxx 還有 wlp.width 寬度 其他不用動
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_bookstatus, null);
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
    public interface OnBookStatusDialogClickListener{
        void onLevel1Click(String level);
        void onLevel2Click(String level);
        void onLevel3Click(String level);
        void onLevel4Click(String level);
    }

}

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

public class ClassifyDialog extends DialogFragment {

    private ConstraintLayout option1, option2, option3, option4, option5, option6, option7, option8, option9, option10;
    private TextView tv000, tv100, tv200, tv300, tv400, tv500, tv600, tv700, tv800, tv900;


    public static ClassifyDialog newInstance() {

        Bundle args = new Bundle();

        ClassifyDialog fragment = new ClassifyDialog();
        fragment.setArguments(args);
        return fragment;
    }

    public OnClassifyDialogClickListener listener;

    public void setListener(OnClassifyDialogClickListener listener) {
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

        option1 = view.findViewById(R.id.opt_family);
        option2 = view.findViewById(R.id.opt_seven);
        option3 = view.findViewById(R.id.opt_delivery);
        option4 = view.findViewById(R.id.opt_face);
        option5 = view.findViewById(R.id.option5);
        option6 = view.findViewById(R.id.option6);
        option7 = view.findViewById(R.id.option7);
        option8 = view.findViewById(R.id.option8);
        option9 = view.findViewById(R.id.option9);
        option10 = view.findViewById(R.id.option10);


        tv000 = view.findViewById(R.id.book_classify_000);
        tv100 = view.findViewById(R.id.book_classify_100);
        tv200 = view.findViewById(R.id.book_classify_200);
        tv300 = view.findViewById(R.id.book_classify_300);
        tv400 = view.findViewById(R.id.book_classify_400);
        tv500 = view.findViewById(R.id.book_classify_500);
        tv600 = view.findViewById(R.id.book_classify_600);
        tv700 = view.findViewById(R.id.book_classify_700);
        tv800 = view.findViewById(R.id.book_classify_800);
        tv900 = view.findViewById(R.id.book_classify_900);

        option1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClassifyClick(tv000.getText().toString());
                dismiss();
            }
        });

        option2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClassifyClick(tv100.getText().toString());
                dismiss();
            }
        });

        option3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClassifyClick(tv200.getText().toString());
                dismiss();
            }
        });

        option4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClassifyClick(tv300.getText().toString());
                dismiss();
            }
        });

        option5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClassifyClick(tv400.getText().toString());
                dismiss();
            }
        });

        option6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClassifyClick(tv500.getText().toString());
                dismiss();
            }
        });

        option7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClassifyClick(tv600.getText().toString());
                dismiss();
            }
        });

        option8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClassifyClick(tv700.getText().toString());
                dismiss();
            }
        });

        option9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClassifyClick(tv800.getText().toString());
                dismiss();
            }
        });

        option10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClassifyClick(tv900.getText().toString());
                dismiss();
            }
        });
    }

    //這邊要綁定LAYOUT 需要改的地方只有R.layout.xxxxx 還有 wlp.width 寬度 其他不用動
    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_classify, null);
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
    public interface OnClassifyDialogClickListener{
        void onClassifyClick(String classify);

    }
}

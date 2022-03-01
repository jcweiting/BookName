package com.example.second_book_exchange;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

public class OrderStatusSelect {

    private TextView tvCode0, tvCode1;
    private OnClickStatus listener;

    public void setListener(OnClickStatus listener) {
        this.listener = listener;
    }

    public void showView(View mask, ConstraintLayout root, FragmentActivity activity){

        final View bottomView = View.inflate(activity,R.layout.my_order_status_select,null);

        tvCode0 = bottomView.findViewById(R.id.code0);
        tvCode1 = bottomView.findViewById(R.id.code1);

        mask.setVisibility(View.VISIBLE);
        root.addView(bottomView);

        bottomView.setVisibility(View.VISIBLE);
        bottomView.setY(root.getBottom());
        bottomView.setX(0);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bottomView.getLayoutParams();
        params.height = root.getBottom() / 2;
        params.width = root.getRight();
        bottomView.setLayoutParams(params);

        bottomView.post(new Runnable() {
            @Override
            public void run() {
                int height = bottomView.getBottom() - bottomView.getTop();
                int rootBottom = root.getBottom();
                float finalY = rootBottom - height;

                bottomView.setVisibility(View.VISIBLE);
                bottomView.animate().y(finalY).start();
            }
        });

        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maskGone(bottomView, root, mask);
            }
        });

        tvCode0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maskGone(bottomView, root, mask);
                listener.onClickStatus(0);
            }
        });

        tvCode1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maskGone(bottomView, root, mask);
                listener.onClickStatus(1);
            }
        });
    }

    private void maskGone(View bottomView, ConstraintLayout root, View mask) {
        bottomView.animate().y(root.getBottom()).start();
        mask.setVisibility(View.GONE);
    }

    public interface OnClickStatus{
        void onClickStatus(int code);
    }
}

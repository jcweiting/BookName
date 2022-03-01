package com.example.second_book_exchange;

import static com.example.second_book_exchange.AddBookActivity.BOOK_INFO;
import static com.example.second_book_exchange.AddBookActivity.BOOK_LIST;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import com.example.second_book_exchange.recyclerview.BookInfoAdapter;
import com.example.second_book_exchange.tool.ImageLoaderProvider;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

public class CustomCartSelect {

    private ImageView ivCover;
    private TextView tvName, tvUnitPrice, tvMinus, tvQty, tvAdd, tvAddToCart;
    private OnButtonClickListener listener;

    public void setListener(OnButtonClickListener listener) {
        this.listener = listener;
    }

    public void showView(View mask, ConstraintLayout root, FragmentActivity activity){

        final View bottomView = View.inflate(activity, R.layout.custom_cart_select,null);   //綁定layout ID

        ivCover = bottomView.findViewById(R.id.cover);
        tvName = bottomView.findViewById(R.id.name);
        tvUnitPrice = bottomView.findViewById(R.id.unit_price);
        tvMinus = bottomView.findViewById(R.id.minus);
        tvQty = bottomView.findViewById(R.id.qty);
        tvAdd = bottomView.findViewById(R.id.add);
        tvAddToCart = bottomView.findViewById(R.id.add_to_cart);

        mask.setVisibility(View.VISIBLE);
        root.addView(bottomView);

        bottomView.setVisibility(View.VISIBLE);
        bottomView.setY(root.getBottom());  //先將視窗固定在底部
        bottomView.setX(0);                 //X軸最左邊是0, 讓視窗靠左


        //設定寬高=====================================
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bottomView.getLayoutParams();
        params.height = root.getBottom() / 3;
        params.width = root.getRight();
        bottomView.setLayoutParams(params);

        //顯示在1/3的位置
        //params.height = root.getBottom() -  root.getBottom() / 3;
        //============================================


        bottomView.post(new Runnable() {
            @Override
            public void run() {

                int height = bottomView.getBottom() - bottomView.getTop();  //算出視窗高度    //dialog長度
                int weight = bottomView.getRight() - bottomView.getLeft();  //算出視窗寬度

                int rootBottom = root.getBottom();  //取root的最底部的座標  //視窗總長度
                float finalY = rootBottom - height; //讓dialog要上升多少的座標   //視窗總長度-dialog長度

                bottomView.setVisibility(View.VISIBLE);
                bottomView.animate().y(finalY).start();
            }
        });

        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomView.animate().y(root.getBottom()).start();
                mask.setVisibility(View.GONE);
            }
        });

        tvMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickMinus();
            }
        });

        tvAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickAdd();
            }
        });

        tvAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickToCart(tvQty.getText().toString());

                bottomView.animate().y(root.getBottom()).start();
                mask.setVisibility(View.GONE);
            }
        });

    }

    public void setQty(int qty) {

        tvQty.setText(qty+"");
    }

    public void setTvName(String name){
        tvName.setText(name);
    }

    public void setIvCover(String url){
        ImageLoaderProvider.getInstance().setImage(url, ivCover);
    }

    public void setTvUnitPrice(String unitPrice){
        tvUnitPrice.setText("NTD "+unitPrice);
    }

    public interface OnButtonClickListener{
        void onClickToCart(String qty);
        void onClickMinus();
        void onClickAdd();
    }
}

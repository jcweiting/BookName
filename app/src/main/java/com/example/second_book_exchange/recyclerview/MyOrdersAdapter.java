package com.example.second_book_exchange.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.second_book_exchange.BookInsideData;
import com.example.second_book_exchange.BookOuterData;
import com.example.second_book_exchange.MyOrders;
import com.example.second_book_exchange.R;
import com.example.second_book_exchange.UserBasicData;
import com.example.second_book_exchange.api.OrderData;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.tool.OrderStatusDialog;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.gson.Gson;
import com.nostra13.universalimageloader.utils.L;

import java.util.ArrayList;
import java.util.logging.Level;

import kotlin.jvm.internal.PropertyReference0Impl;

public class MyOrdersAdapter extends RecyclerView.Adapter<MyOrdersAdapter.ViewHolder> {

    private OnClickListener listener;
    private ArrayList<OrderData> orderDataArr;
    private Context context;

    public void setOrderDataArr(ArrayList<OrderData> orderDataArr) {
        this.orderDataArr = orderDataArr;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_myorders,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        OrderData orderData = orderDataArr.get(position);

        if (orderData.getStatus() == 1){
            holder.tvStatus.setText("完成交易");
            holder.tvStatus.setEnabled(false);
        } else {
            holder.tvStatus.setText("交易中");
        }

        JoyceLog.i("MyOrderAdapter | orderData.getCheckOutList: "+new Gson().toJson(orderData.getCheckOutList()));

        //購物車內層資料
        MyOrdersInnerAdapter innerAdapter = new MyOrdersInnerAdapter();
        innerAdapter.setOuterDataArr(orderData.getCheckOutList());
        innerAdapter.setListener(new MyOrdersInnerAdapter.OnClickMoreDetails() {
            @Override
            public void onClickDetail(ArrayList<BookInsideData> insideDataArr) {
                listener.onClickDetails(insideDataArr);
            }
        });

        int sum = 0;

        for (BookOuterData outerData : orderData.getCheckOutList()){

            for (BookInsideData insideData: outerData.getProductLists()){
                sum += Integer.parseInt(insideData.getUnitPrice().trim()) * Integer.parseInt(insideData.getQty().trim());
            }

            sum = sum + outerData.getShipmentFee();
        }

        holder.tvSum.setText("總金額: NTD "+sum);

        checkEmail(holder,orderData, innerAdapter);

        if (orderData.getRole() == 666){

            holder.tvStatus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickStatus(orderData);
                }
            });
        }

        holder.recyclerView.setAdapter(innerAdapter);
    }

    private void checkEmail(ViewHolder holder, OrderData orderData, MyOrdersInnerAdapter innerAdapter) {

        //555 使用者是買家
        if (orderData.getRole() == 555){
            holder.ivRole.setImageResource(R.drawable.buyer);
            innerAdapter.setRoleTitle("賣家為: ");

            //666 使用者是賣家
        } else if (orderData.getRole() == 666){
            holder.ivRole.setImageResource(R.drawable.seller);
            innerAdapter.setRoleTitle("買家為: ");
        }
    }

    @Override
    public int getItemCount() {
        return orderDataArr == null ? 0 : orderDataArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvStatus, tvSum;
        private RecyclerView recyclerView;
        private ImageView ivRole;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            recyclerView = itemView.findViewById(R.id.recyclerview_myorders);
            tvStatus = itemView.findViewById(R.id.status);
            tvSum = itemView.findViewById(R.id.sum);
            ivRole = itemView.findViewById(R.id.role);

            recyclerView.setLayoutManager(new LinearLayoutManager(context));
        }
    }

    public interface OnClickListener{
        void onClickStatus(OrderData orderData);
        void onClickDetails(ArrayList<BookInsideData> insideDataArr);
    }
}

package com.example.second_book_exchange.recyclerview;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.second_book_exchange.BookInsideData;
import com.example.second_book_exchange.R;
import com.example.second_book_exchange.ShipmentSelect;
import com.example.second_book_exchange.SpinnerList;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.tool.ImageLoaderProvider;

import java.util.ArrayList;

public class CartAdapter extends RecyclerView.Adapter {

    public static final int CART_TOP = 0;
    public static final int CART_BOTTOM = 1;

    private ArrayList<BookInsideData> bookInsideData;
    private int fee;
    private String way;
    private OnClickChangeQty listener;

    public void setAllInfo(int fee ,String way){
        this.fee =fee;
        this.way = way;
    }

    public void setBookInsideData(ArrayList<BookInsideData> bookInsideData) {
        this.bookInsideData = bookInsideData;
    }

    public void setListener(OnClickChangeQty listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {

        if (position < bookInsideData.size()){
            return CART_TOP;
        }
        return CART_BOTTOM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == CART_TOP){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_cart_top,parent,false);
            return new CartTopViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_cart_bottom,parent,false);
        return new CartBottomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (holder instanceof CartTopViewHolder){

            BookInsideData bookInsideObject = bookInsideData.get(position);

            ((CartTopViewHolder) holder).setCartListView(bookInsideObject);

            //以true或false為資料,來設定CheckBox是否為打勾狀態
            ((CartTopViewHolder) holder).checkBox.setChecked(bookInsideObject.isSelectedProduct());

            ((CartTopViewHolder) holder).checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    bookInsideObject.setSelectedProduct(!bookInsideObject.isSelectedProduct());
                    listener.onSelectSingleItem(bookInsideObject);
                }
            });

            ((CartTopViewHolder) holder).tvAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickAdd(Integer.parseInt(bookInsideObject.getUnitPrice()),bookInsideObject);
                }
            });

            ((CartTopViewHolder) holder).tvMinus.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickMinus(Integer.parseInt(bookInsideObject.getUnitPrice()),bookInsideObject);
                }
            });

            ((CartTopViewHolder) holder).ivBookCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickCover(bookInsideData.get(position));
                }
            });

            ((CartTopViewHolder) holder).ivDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickDelete(bookInsideData.get(position));
                }
            });
        }

        if (holder instanceof CartBottomViewHolder){

            ((CartBottomViewHolder) holder).setShipment(way,fee,bookInsideData);

            //點擊後, 跳出視窗讓使用者選擇運送方式
            ((CartBottomViewHolder) holder).tvWay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //所以只需要把點擊事件導到Fragment即可,不需要傳送任何值出去
                    listener.onClickShipmentWay();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
                                                        // +1 means 加上bottom的長度
        return bookInsideData == null ? 0 : bookInsideData.size() + 1;
    }

    public static class CartTopViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private ImageView ivBookCover, ivDelete;
        private TextView tvBookName, tvMinus, tvAdd, tvQty, tvAmount;
        private int qty, qtyMax, unitPrice;

        public CartTopViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.check_box);
            ivBookCover = itemView.findViewById(R.id.book_cover);
            tvBookName = itemView.findViewById(R.id.book_name);
            tvAmount = itemView.findViewById(R.id.amount);
            tvQty = itemView.findViewById(R.id.qty);
            tvMinus = itemView.findViewById(R.id.minus);
            tvAdd = itemView.findViewById(R.id.add);
            ivDelete = itemView.findViewById(R.id.trash);
        }

        public void setCartListView(BookInsideData bookInsideData){
            tvBookName.setText(bookInsideData.getBookName());
            tvQty.setText(bookInsideData.getQty());
            ImageLoaderProvider.getInstance().setImage(bookInsideData.getPhotoUrl(),ivBookCover);

            qtyMax = Integer.parseInt(bookInsideData.getQty().trim());
            qty = Integer.parseInt(tvQty.getText().toString().trim());
            unitPrice = Integer.parseInt(bookInsideData.getUnitPrice());
            tvAmount.setText("NTD "+(qty*unitPrice));
            checkBox.setChecked(bookInsideData.isSelectedProduct());
        }
    }

    public static class CartBottomViewHolder extends  RecyclerView.ViewHolder {

        private TextView tvShipment, tvSum, tvAmount, tvWay;

        public CartBottomViewHolder(@NonNull View itemView) {
            super(itemView);

            tvWay = itemView.findViewById(R.id.way);
            tvShipment = itemView.findViewById(R.id.shipment);
            tvSum = itemView.findViewById(R.id.sum);
            tvAmount = itemView.findViewById(R.id.amount);
        }

        public void setShipment(String way, int fee, ArrayList<BookInsideData> insideData){

            int sum = 0;
            for (BookInsideData data : insideData) {

                if (data.isSelectedProduct()) {
                    sum += Integer.parseInt(data.getUnitPrice().trim())*Integer.parseInt(data.getQty().trim());
                }
            }

            //算出勾選起來的金額(小計)
            tvWay.setText(way);
            tvAmount.setText("小計: NTD "+sum);
            tvShipment.setText("運費: NTD "+fee);
            tvSum.setText("總計(含運費): NTD "+(sum+fee));
        }
    }

    public interface OnClickChangeQty{

        void onSelectSingleItem(BookInsideData insideData);
        void onClickShipmentWay();
        void onClickAdd(int unitPrice,BookInsideData data);
        void onClickMinus(int unitPrice, BookInsideData data);
        void onClickCover(BookInsideData bookInsideData);
        void onClickDelete(BookInsideData bookInsideData);
    }
}



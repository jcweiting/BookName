package com.book.second_book_exchange.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.book.second_book_exchange.BookInsideData;
import com.book.second_book_exchange.BookOuterData;
import com.book.second_book_exchange.R;

import java.util.ArrayList;

public class CartOuterAdapter extends RecyclerView.Adapter<CartOuterAdapter.ViewHolder> {

    private Context context;
    private ArrayList<BookOuterData> bookDataArr;
    private OnInsideAdapterListener listener;

    public void setListener(OnInsideAdapterListener listener) {
        this.listener = listener;
    }

    public void setBookDataArr(ArrayList<BookOuterData> bookDataArr) {
        this.bookDataArr = bookDataArr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        this.context = parent.getContext();

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_cart_content,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BookOuterData bookOuterData = bookDataArr.get(position);

        holder.tvUploader.setText(bookOuterData.getUserEmail());

        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //bookData.isAllSelected() --> 預設是false，點擊之後會變成相反(true)
                bookOuterData.setAllSelected(!bookOuterData.isAllSelected());
                listener.onClickAllCheckBoxListener(bookOuterData);
            }
        });

        //以true或false為資料,來設定CheckBox是否為打勾狀態
        holder.checkBox.setChecked(bookOuterData.isAllSelected());

        //FOR CART ADAPTER =================================
        holder.recyclerViewOuter.setLayoutManager(new LinearLayoutManager(context));    //設定內部recyclerview為直向

        CartAdapter cartAdapter = new CartAdapter();
        cartAdapter.setAllInfo(bookOuterData.getShipmentFee(),bookOuterData.getShipmentWay());
        cartAdapter.setBookInsideData(bookOuterData.getProductLists());

        holder.recyclerViewOuter.setAdapter(cartAdapter);

        //把內部點擊事件傳到外部
        cartAdapter.setListener(new CartAdapter.OnClickChangeQty() {
            @Override
            public void onSelectSingleItem(BookInsideData insideData) {
                listener.onSingleItemSelect(insideData,bookOuterData);
            }

            @Override
            public void onClickShipmentWay() {
                listener.onClickShipmentWay(bookOuterData);
            }

            @Override
            public void onClickAdd(int unitPrice,BookInsideData data) {
                listener.onClickAdd(unitPrice,data);
            }

            @Override
            public void onClickMinus(int unitPrice, BookInsideData data) {
                listener.onClickMinus(unitPrice, data);
            }

            @Override
            public void onClickCover(BookInsideData bookInsideData) {
                listener.onClickCover(bookInsideData);
            }

            @Override
            public void onClickDelete(BookInsideData bookInsideData) {
                listener.onClickDelete(bookInsideData);
            }
        });
        //=====================================================

    }

    @Override
    public int getItemCount() {
        return bookDataArr == null ? 0 : bookDataArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private CheckBox checkBox;
        private TextView tvUploader;
        private RecyclerView recyclerViewOuter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.check_box);
            tvUploader = itemView.findViewById(R.id.uploader);
            recyclerViewOuter = itemView.findViewById(R.id.recyclerview_cart_content);
        }
    }

    public interface OnInsideAdapterListener{

        void onClickAllCheckBoxListener(BookOuterData bookOuterData);

        void onSingleItemSelect(BookInsideData bookInsideData, BookOuterData bookOuterData);

        void onClickShipmentWay(BookOuterData bookOuterData);

        //內部Adapter接口
        void onClickAdd(int unitPrice,BookInsideData data);
        void onClickMinus(int unitPrice, BookInsideData data);
        void onClickCover(BookInsideData bookInsideData);
        void onClickDelete(BookInsideData bookInsideData);
    }
}

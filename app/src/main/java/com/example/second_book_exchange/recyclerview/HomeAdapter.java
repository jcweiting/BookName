package com.example.second_book_exchange.recyclerview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.second_book_exchange.AddBookBasicData;
import com.example.second_book_exchange.R;
import com.example.second_book_exchange.tool.ImageLoaderProvider;

import java.util.ArrayList;

/**
 * Step 1: 建立常數、arrayList
 */
public class HomeAdapter extends RecyclerView.Adapter {

    public static final int TYPE_A = 0;
    public static final int TYPE_B = 1;
    private Context context;  //activity繼承的其中一個物件
    private OnBookInfoClickListener listener;
    private OnButtonClickListener listenerBtn;
    private ArrayList<AddBookBasicData> bookArray;
    private ArrayList<AddBookBasicData> newBookArray;

    public void setListener(OnBookInfoClickListener listener) {
        this.listener = listener;
    }

    public void setListenerBtn(OnButtonClickListener listenerBtn) {
        this.listenerBtn = listenerBtn;
    }

    public void setInfo(ArrayList<AddBookBasicData> bookArray, ArrayList<AddBookBasicData> newBookArray) {
        this.bookArray = bookArray;
        this.newBookArray = newBookArray;
    }

    @Override
    public int getItemViewType(int position) {

        //用position判斷要顯示哪一個view
        if (position == 0){
            return TYPE_A;
        }
        return TYPE_B;
    }

    /**
     * Step 3
     */
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //拿activity的東西
        this.context = parent.getContext();

        //Type A ViewHolder
        if (viewType == TYPE_A){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_home,parent,false);
            return new TypeAViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_home_straight,parent,false);
        return new TypeBViewHolder(view);
    }

    /**
     * Step 4
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        //position 0 | Type A
        if (holder instanceof TypeAViewHolder){
            ((TypeAViewHolder) holder).setInfo(newBookArray,context);
            ((TypeAViewHolder) holder).setListener(listenerBtn);
        }

        //position 1 | Type B
        if (holder instanceof TypeBViewHolder){
                                                            //要從第0個位置開始取資料,所以要-1
            ((TypeBViewHolder) holder).setInfo(bookArray.get(position-1));

            //點擊事件
            ((TypeBViewHolder) holder).ivBookCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                                     //丟物件近來
                    listener.onClick(bookArray.get(position-1));
                }
            });

            ((TypeBViewHolder) holder).ivCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listenerBtn.onClickCart(bookArray.get(position-1));
                }
            });

            ((TypeBViewHolder) holder).ivLove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listenerBtn.onClickLove(bookArray.get(position-1));
                }
            });
        }
    }

    /**
     * Step 5
     */
    @Override
    public int getItemCount() {
        return 1 + bookArray.size();
    }

    /**
     * Step 2: 建立ViewHolder
     */

    public static class TypeAViewHolder extends RecyclerView.ViewHolder {

        private RecyclerView recyclerView;

        private OnButtonClickListener listener;

        public void setListener(OnButtonClickListener listener) {
            this.listener = listener;
        }

        public TypeAViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerView = itemView.findViewById(R.id.recyclerview_home);
        }

            public void setInfo(ArrayList<AddBookBasicData> newBookArray, Context context){

            //設定橫向layout
            LinearLayoutManager manager = new LinearLayoutManager(context);
            manager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(manager);

            //綁定Adapter、綁定recyclerView
            HomeNewBookRecyclerView homeNewBookRecyclerView = new HomeNewBookRecyclerView();
            homeNewBookRecyclerView.setBookArrayList(newBookArray);

            homeNewBookRecyclerView.setListener(new HomeNewBookRecyclerView.OnClickListener() {
                @Override
                public void onClickChangePage(AddBookBasicData addBookBasicData) {
                    listener.onClickChangePage(addBookBasicData);
                }
            });

            recyclerView.setAdapter(homeNewBookRecyclerView);
        }
    }

    public static class TypeBViewHolder extends RecyclerView.ViewHolder {

        private TextView tvBookName, tvBookPrice;
        private ImageView ivBook, ivBookCover, ivLove, ivCart;

        public TypeBViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookName = itemView.findViewById(R.id.book_name);
            tvBookPrice = itemView.findViewById(R.id.book_price);
            ivBook = itemView.findViewById(R.id.book);
            ivBookCover = itemView.findViewById(R.id.book_cover);
            ivLove = itemView.findViewById(R.id.love);
            ivCart = itemView.findViewById(R.id.shopcart);
        }

        public void setInfo(AddBookBasicData data) {
            tvBookName.setText(data.getBookName());
            tvBookPrice.setText("NTD "+data.getUnitPrice());
            ImageLoaderProvider.getInstance().setImage(data.getPhotoUrl(),ivBookCover);     //show picture

            if (data.isSelectHeart()){
                ivLove.setImageResource(R.drawable.love_click);
            } else {
                ivLove.setImageResource(R.drawable.love1);
            }
        }
    }

    public interface OnBookInfoClickListener{
        void onClick(AddBookBasicData addBookBasicData);
    }

    public interface OnButtonClickListener{
        void onClickCart(AddBookBasicData addBookBasicData);
        void onClickLove(AddBookBasicData addBookBasicData);
        void onClickChangePage(AddBookBasicData addBookBasicData);
    }
}

package com.book.second_book_exchange.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.book.second_book_exchange.AddBookBasicData;
import com.book.second_book_exchange.R;
import com.book.second_book_exchange.tool.ImageLoaderProvider;

import java.util.ArrayList;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {

    private ArrayList<AddBookBasicData> searchList;
    private OnClickListener listener;

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setSearchList(ArrayList<AddBookBasicData> searchList) {
        this.searchList = searchList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_home_straight,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AddBookBasicData addBookBasicData = searchList.get(position);
        holder.tvBookName.setText(addBookBasicData.getBookName());
        holder.tvBookPrice.setText("NTD "+addBookBasicData.getUnitPrice());
        ImageLoaderProvider.getInstance().setImage(addBookBasicData.getPhotoUrl(),holder.ivBookCover);  //顯示書的圖片

        if (addBookBasicData.isSelectHeart()){
            holder.ivLove.setImageResource(R.drawable.love_click);
        } else {
            holder.ivLove.setImageResource(R.drawable.love1);
        }

        holder.ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickCart(addBookBasicData);
            }
        });

        holder.ivLove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickLove(addBookBasicData);
            }
        });

        holder.ivBookCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickChangePage(addBookBasicData);
            }
        });
    }

    @Override
    public int getItemCount() {
        return searchList == null ? 0 : searchList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvBookName, tvBookPrice;
        private ImageView ivBook, ivBookCover, ivLove, ivCart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookName = itemView.findViewById(R.id.book_name);
            tvBookPrice = itemView.findViewById(R.id.book_price);
            ivBook = itemView.findViewById(R.id.book);
            ivBookCover = itemView.findViewById(R.id.book_cover);
            ivLove = itemView.findViewById(R.id.love);
            ivCart = itemView.findViewById(R.id.shopcart);
        }
    }

    public interface OnClickListener{
        void onClickCart(AddBookBasicData addBookBasicData);
        void onClickLove(AddBookBasicData addBookBasicData);
        void onClickChangePage(AddBookBasicData addBookBasicData);
    }
}

package com.book.second_book_exchange.recyclerview;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.book.second_book_exchange.AddBookBasicData;
import com.book.second_book_exchange.R;
import com.book.second_book_exchange.tool.ImageLoaderProvider;

import java.util.ArrayList;

public class HeartAdapter extends RecyclerView.Adapter<HeartAdapter.ViewHolder> {

    private ArrayList<AddBookBasicData> heartList;
    private OnClickListener listener;

    public void setHeartList(ArrayList<AddBookBasicData> heartList) {
        this.heartList = heartList;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_heart,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        AddBookBasicData addBookBasicData = heartList.get(position);
        holder.tvName.setText(addBookBasicData.getBookName());
        holder.tvUnitPrice.setText("NTD "+addBookBasicData.getUnitPrice());
        holder.ivCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickToCart(heartList.get(position));
            }
        });

        holder.ivCover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickPicture(heartList.get(position));
            }
        });

        holder.ivDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onDelete(heartList.get(position));
            }
        });

        ImageLoaderProvider.getInstance().setImage(addBookBasicData.getPhotoUrl(), holder.ivCover);
    }

    @Override
    public int getItemCount() {
        return heartList == null ? 0 : heartList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox checkBox;
        private ImageView ivCover, ivCart, ivDelete;
        private TextView tvName, tvUnitPrice;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            checkBox = itemView.findViewById(R.id.check_box);
            ivCover = itemView.findViewById(R.id.book_cover);
            tvName = itemView.findViewById(R.id.book_name);
            tvUnitPrice = itemView.findViewById(R.id.amount);
            ivCart = itemView.findViewById(R.id.cart);
            ivDelete = itemView.findViewById(R.id.trash);
        }
    }

    public interface OnClickListener{
        void onClickToCart(AddBookBasicData addBookBasicData);
        void onClickPicture(AddBookBasicData addBookBasicData);
        void onDelete(AddBookBasicData addBookBasicData);
    }
}

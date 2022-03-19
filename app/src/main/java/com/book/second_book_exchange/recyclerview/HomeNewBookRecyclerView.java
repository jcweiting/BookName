package com.book.second_book_exchange.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.book.second_book_exchange.AddBookBasicData;
import com.book.second_book_exchange.R;
import com.book.second_book_exchange.tool.ImageLoaderProvider;

import java.util.ArrayList;

public class HomeNewBookRecyclerView extends RecyclerView.Adapter<HomeNewBookRecyclerView.ViewHolder> {

    private ArrayList<AddBookBasicData> bookArrayList;
    private OnClickListener listener;

    public void setBookArrayList(ArrayList<AddBookBasicData> bookArrayList) {
        this.bookArrayList = bookArrayList;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_home_horizontal,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AddBookBasicData bookNumber = bookArrayList.get(position);

        holder.ivNewBook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickChangePage(bookNumber);
            }
        });

        //show picture
        ImageLoaderProvider.getInstance().setImage(bookNumber.getPhotoUrl(), holder.ivNewBook);

    }

    @Override
    public int getItemCount() {
        return bookArrayList == null ? 0 : bookArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivNewBook;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivNewBook = itemView.findViewById(R.id.new_book);
        }
    }

    public interface OnClickListener{
        void onClickChangePage(AddBookBasicData addBookBasicData);
    }
}

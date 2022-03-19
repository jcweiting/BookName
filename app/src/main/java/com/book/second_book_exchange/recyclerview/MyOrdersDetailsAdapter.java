package com.book.second_book_exchange.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.book.second_book_exchange.BookInsideData;
import com.book.second_book_exchange.R;
import com.book.second_book_exchange.tool.ImageLoaderProvider;

import java.util.ArrayList;

public class MyOrdersDetailsAdapter extends RecyclerView.Adapter<MyOrdersDetailsAdapter.ViewHolder> {

    private ArrayList<BookInsideData> insideDataArr;

    public void setInsideDataArr(ArrayList<BookInsideData> insideDataArr) {
        this.insideDataArr = insideDataArr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_check_out_content,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BookInsideData insideData = insideDataArr.get(position);

        ImageLoaderProvider.getInstance().setImage(insideData.getPhotoUrl(), holder.ivBookCover);
        holder.tvBookName.setText(insideData.getBookName());
        holder.tvBookQty.setText("數量: "+insideData.getQty());

        String strQty = insideData.getQty();
        String strUnitPrice = insideData.getUnitPrice();
        holder.tvAmount.setText("NTD "+Integer.parseInt(strQty.trim())*Integer.parseInt(strUnitPrice.trim()));

    }

    @Override
    public int getItemCount() {
        return insideDataArr == null ? 0 : insideDataArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivBookCover;
        private TextView tvBookName, tvBookQty, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivBookCover = itemView.findViewById(R.id.book_cover);
            tvBookName = itemView.findViewById(R.id.book_name);
            tvBookQty = itemView.findViewById(R.id.qty);
            tvAmount = itemView.findViewById(R.id.amount);
        }
    }
}

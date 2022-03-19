package com.book.second_book_exchange.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.book.second_book_exchange.BookInsideData;
import com.book.second_book_exchange.R;
import com.book.second_book_exchange.log.JoyceLog;
import com.book.second_book_exchange.tool.ImageLoaderProvider;

import java.util.ArrayList;

public class CheckOutInnerAdapter extends RecyclerView.Adapter<CheckOutInnerAdapter.ViewHolder> {

    private ArrayList<BookInsideData> bookInsideData;
    private Context context;

    public void setBookInsideData(ArrayList<BookInsideData> bookInsideData) {
        this.bookInsideData = bookInsideData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_check_out_content,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BookInsideData insideDataObject = bookInsideData.get(position);

        String strQty = insideDataObject.getQty().trim();
        String strUnitPrice = insideDataObject.getUnitPrice();
        JoyceLog.i("CheckOutInnerAdapter | strQty: "+strQty);
        JoyceLog.i("CheckOutInnerAdapter | strUnitPrice: "+strUnitPrice);

        if (strUnitPrice.equals("") || strUnitPrice.isEmpty() || strQty.equals("") || strQty.isEmpty()){
            JoyceLog.i("CartFragment | 金額是空的");
            showHint("請選取欲結帳書籍");
            return;
        }

        holder.tvBookName.setText(insideDataObject.getBookName());
        holder.tvBookAmount.setText("NTD "+(Integer.parseInt(strQty) *Integer.parseInt(strUnitPrice)));
        holder.tvBookQty.setText("數量: "+insideDataObject.getQty());
        ImageLoaderProvider.getInstance().setImage(insideDataObject.getPhotoUrl(), holder.ivBookCover);
    }

    @Override
    public int getItemCount() {
        return bookInsideData == null ? 0 : bookInsideData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivBookCover;
        private TextView tvBookName, tvBookAmount, tvBookQty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivBookCover = itemView.findViewById(R.id.book_cover);
            tvBookName = itemView.findViewById(R.id.book_name);
            tvBookAmount = itemView.findViewById(R.id.amount);
            tvBookQty = itemView.findViewById(R.id.qty);

        }
    }

    private void showHint(String content) {
        Toast.makeText(context,content,Toast.LENGTH_SHORT).show();
    }

}

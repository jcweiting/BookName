package com.book.second_book_exchange.recyclerview;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.book.second_book_exchange.AddBookBasicData;
import com.book.second_book_exchange.BookInfoList;
import com.book.second_book_exchange.R;
import com.book.second_book_exchange.tool.ImageLoaderProvider;

import java.util.ArrayList;

public class BookInfoAdapter extends RecyclerView.Adapter {

    public static final int BOOK_INFO = 0;
    public static final int BOOK_INFO_LIST = 1;

    private AddBookBasicData addBookBasicData;
    private ArrayList<BookInfoList> bookList;
    private OnClickListener listener;
    private String user;

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setAddBookBasicData(AddBookBasicData addBookBasicData) {
        this.addBookBasicData = addBookBasicData;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public void setBookList(ArrayList<BookInfoList> bookList) {
        this.bookList = bookList;
    }

    @Override
    public int getItemViewType(int position) {

        //會把值傳到onCreateViewHolder的function裡,去顯示view
        if (position == 0 ){
            return BOOK_INFO;
        }

        return BOOK_INFO_LIST;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == BOOK_INFO){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_book_info,parent,false);
            return new BookInfoViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_book_info_list,parent,false);
        return new BookInfoListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof BookInfoViewHolder){

            ((BookInfoViewHolder) holder).setAddBookBasicData(addBookBasicData,user);

            ((BookInfoViewHolder) holder).tvFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickFavorite();
                }
            });

            ((BookInfoViewHolder) holder).tvCart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickCart();
                }
            });

            ((BookInfoViewHolder) holder).ivEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickEdit();
                }
            });

            //顯示照片
            ImageLoaderProvider.getInstance().setImage(addBookBasicData.getPhotoUrl(), ((BookInfoViewHolder) holder).ivBookCover);

        }

        if (holder instanceof BookInfoListViewHolder){
            ((BookInfoListViewHolder) holder).setBookList(bookList.get(position-1));

            if (position != 1){
                return;
            }

            ((BookInfoListViewHolder) holder).tvListContent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickSendProfile();
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookList == null ? 0 : 1 + bookList.size();
    }

    public static class BookInfoViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivBookCover, ivEdit;
        private TextView tvBookName, tvFavorite, tvCart;

        public BookInfoViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookName = itemView.findViewById(R.id.book_name);
            ivBookCover = itemView.findViewById(R.id.book_cover);
            tvFavorite = itemView.findViewById(R.id.add_to_favorite);
            tvCart = itemView.findViewById(R.id.add_to_cart);
            ivEdit = itemView.findViewById(R.id.edit);
        }

        public void setAddBookBasicData(AddBookBasicData addBookBasicData, String user) {
            tvBookName.setText(addBookBasicData.getBookName());

            if (user.equals(addBookBasicData.getUploaderUid())){
                ivEdit.setVisibility(View.VISIBLE);
            }else{
                ivEdit.setVisibility(View.GONE);
            }

            Log.i("Joyce","user: "+user);
            Log.i("Joyce","addBookBasicData.getUid(): "+addBookBasicData.getUploaderUid());
        }
    }

    public static class BookInfoListViewHolder extends RecyclerView.ViewHolder {

        private TextView tvListTitle, tvListContent;

        public BookInfoListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvListTitle = itemView.findViewById(R.id.list_title);
            tvListContent = itemView.findViewById(R.id.list_content);
        }

        public void setBookList(BookInfoList bookList){
            tvListTitle.setText(bookList.getTitle());
            tvListContent.setText(bookList.getContent());
        }
    }

    public interface OnClickListener{
        void onClickCart();
        void onClickFavorite();
        void onClickEdit();
        void onClickSendProfile();
    }
}

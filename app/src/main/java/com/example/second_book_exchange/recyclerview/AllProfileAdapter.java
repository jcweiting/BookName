package com.example.second_book_exchange.recyclerview;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.second_book_exchange.AddBookBasicData;
import com.example.second_book_exchange.R;
import com.example.second_book_exchange.api.UserAllInformation;
import com.example.second_book_exchange.tool.ImageLoaderProvider;
import com.google.android.gms.dynamic.IFragmentWrapper;

import java.util.ArrayList;

public class AllProfileAdapter extends RecyclerView.Adapter {

    private static final int ALL_PROFILE_TOP = 0 ;
    private static final int ALL_PROFILE_BOTTOM = 1;

    private UserAllInformation userAllInformation;
    private ArrayList<AddBookBasicData> bookInfoArray;
    private OnClickListener listener;
    private String followBtnText;


    public void setBtnText(String followBtnText) {
        this.followBtnText = followBtnText;
    }

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setUserAllInformation(UserAllInformation userAllInformation) {
        this.userAllInformation = userAllInformation;
    }

    public void setBookInfoArray(ArrayList<AddBookBasicData> bookInfoArray) {
        this.bookInfoArray = bookInfoArray;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0){
            return ALL_PROFILE_TOP;
        }

        return ALL_PROFILE_BOTTOM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == ALL_PROFILE_TOP){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_profile_other_top,parent,false);
            return new AllProfileTopView(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_profile_straight,parent,false);
        return new AllProfileBottomView(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (holder instanceof AllProfileTopView){
            ((AllProfileTopView) holder).setProfileData(userAllInformation,followBtnText);
            ((AllProfileTopView) holder).tvPost.setText(userAllInformation.getBookList().size()+"");

            ((AllProfileTopView) holder).tvSetFollow.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickFollow(bookInfoArray.get(position));
                }
            });

            ((AllProfileTopView) holder).tvSetMessage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickChat(bookInfoArray.get(position));
                }
            });
        }

        if (holder instanceof AllProfileBottomView){

            ((AllProfileBottomView) holder).setBookData(bookInfoArray.get(position - 1), userAllInformation);

            ((AllProfileBottomView) holder).tvMsgCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickMsgCount(bookInfoArray.get(position-1));
                }
            });

            ((AllProfileBottomView) holder).ivChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickIvChat(bookInfoArray.get(position-1));
                }
            });

            ((AllProfileBottomView) holder).ivLove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickLove(bookInfoArray.get(position-1));
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return bookInfoArray == null ? 0 : bookInfoArray.size() +1 ;
    }



    public static class AllProfileTopView extends RecyclerView.ViewHolder{

        private ImageView ivProfilePic;
        private TextView tvPost, tvFollower, tvFollow, tvSetFollow, tvSetMessage;

        public AllProfileTopView(@NonNull View itemView) {
            super(itemView);

            ivProfilePic = itemView.findViewById(R.id.profile_picture);
            tvPost = itemView.findViewById(R.id.post_count);
            tvFollower = itemView.findViewById(R.id.follower_count);
            tvFollow = itemView.findViewById(R.id.following_count);
            tvSetFollow = itemView.findViewById(R.id.set_follow);
            tvSetMessage = itemView.findViewById(R.id.set_message);
        }

        @SuppressLint("ResourceAsColor")
        public void setProfileData(UserAllInformation userAllInformation, String followBtnText){

            ImageLoaderProvider.getInstance().setImage(userAllInformation.getUserPhotoUrl(),ivProfilePic);
            tvFollow.setText(userAllInformation.getFollow()+"");
            tvFollower.setText(userAllInformation.getFollower()+"");

            tvSetFollow.setText(followBtnText);

            if (tvSetFollow.getText().toString().equals("追蹤中")){
                tvSetFollow.setBackgroundResource(R.drawable.edit_profile_bg_click);
                tvSetFollow.setPadding(0,25,0,25);
                tvSetFollow.setTextColor(R.color.black);
                tvSetFollow.setEnabled(false);
            }
        }
    }

    public static class AllProfileBottomView extends RecyclerView.ViewHolder {

        private ImageView ivProfilePicture, ivBookCover, ivLove, ivChat;
        private TextView tvUserEmail, tvMsgCount;

        public AllProfileBottomView(@NonNull View itemView) {
            super(itemView);

            ivProfilePicture = itemView.findViewById(R.id.profile_picture_b);
            ivBookCover = itemView.findViewById(R.id.book_cover);
            ivLove = itemView.findViewById(R.id.love);
            ivChat = itemView.findViewById(R.id.chat);
            tvUserEmail = itemView.findViewById(R.id.user_account);
            tvMsgCount = itemView.findViewById(R.id.message_count);
        }

        public void setBookData(AddBookBasicData addBookBasicData, UserAllInformation userAllInformation){
            ImageLoaderProvider.getInstance().setImage(addBookBasicData.getPhotoUrl(), ivBookCover);
            ImageLoaderProvider.getInstance().setImage(userAllInformation.getUserPhotoUrl(),ivProfilePicture);
            tvUserEmail.setText(userAllInformation.getEmail());
            tvMsgCount.setText("共有 "+addBookBasicData.getMsgCount()+" 則留言");

            if (addBookBasicData.isSelectHeart()){
                ivLove.setImageResource(R.drawable.love_click);
            } else {
                ivLove.setImageResource(R.drawable.love);
            }

            if (addBookBasicData.getMsgCount() != 0){
                ivChat.setImageResource(R.drawable.chat_click);
            }
        }
    }

    public interface OnClickListener{
        void onClickMsgCount(AddBookBasicData addBookBasicData);
        void onClickIvChat(AddBookBasicData addBookBasicData);
        void onClickFollow(AddBookBasicData addBookBasicData);
        void onClickChat(AddBookBasicData addBookBasicData);
        void onClickLove(AddBookBasicData addBookBasicData);
    }
}

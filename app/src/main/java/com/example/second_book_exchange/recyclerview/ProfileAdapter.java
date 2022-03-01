package com.example.second_book_exchange.recyclerview;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.second_book_exchange.AddBookBasicData;
import com.example.second_book_exchange.R;
import com.example.second_book_exchange.api.UserAllInformation;
import com.example.second_book_exchange.api.UserData;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.tool.ImageLoaderProvider;

import java.util.ArrayList;

public class ProfileAdapter extends RecyclerView.Adapter {

    private static final int PROFILE_TOP = 0;
    private static final int PROFILE_BOTTOM = 1;

    private UserAllInformation userAllInformation;
    private ArrayList<AddBookBasicData> addBookArray;
    private OnClickEditProfile listener;
    private UserData userData;
    private boolean isMenuOpen = false;

    public void setUserAllInformation(UserAllInformation userAllInformation) {
        this.userAllInformation = userAllInformation;
    }

    public void setAddBookBasicData(ArrayList<AddBookBasicData> addBookArray) {
        this.addBookArray = addBookArray;
    }

    public void setListener(OnClickEditProfile listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {

        if (position == 0){
            JoyceLog.i("ProfileAdapter | TypeA getItemViewType ");
            return PROFILE_TOP;
        }

        JoyceLog.i("ProfileAdapter | TypeB getItemViewType ");
        return PROFILE_BOTTOM;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == PROFILE_TOP){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_profile,parent,false);
            return new ProfileTopViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_profile_straight,parent,false);
        return new ProfileBottomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, @SuppressLint("RecyclerView") int position) {

        if (holder instanceof ProfileTopViewHolder){

            ((ProfileTopViewHolder) holder).setUserAllInformation(userAllInformation);

            ((ProfileTopViewHolder) holder).tvMenu .setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    isMenuOpen = !isMenuOpen;
                    ((ProfileTopViewHolder) holder).expandView.setVisibility(isMenuOpen ? View.VISIBLE : View.GONE);
                }
            });

            ((ProfileTopViewHolder) holder).tvPostCount.setText(addBookArray.size()+"");

            ((ProfileTopViewHolder) holder).tvEditProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    listener.onClickEdit();

                }
            });

            ((ProfileTopViewHolder) holder).ivMoney.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickMoney();
                }
            });

            ((ProfileTopViewHolder) holder).ivOrders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickOrder();
                }
            });

            ((ProfileTopViewHolder) holder).ivChatList.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickChatList();
                }
            });


        }

        if (holder instanceof ProfileBottomViewHolder){

            ((ProfileBottomViewHolder) holder).setAddBookBasicData(addBookArray, addBookArray.get(position-1) ,userAllInformation);

            ((ProfileBottomViewHolder) holder).ivBookCover.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickChangePage(addBookArray.get(position-1));
                }
            });

            ((ProfileBottomViewHolder) holder).tvMsgCount.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickToMessage(addBookArray.get(position-1));
                }
            });

            ((ProfileBottomViewHolder) holder).ivChat.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onClickIvChat(addBookArray.get(position-1));
                }
            });

            ((ProfileBottomViewHolder) holder).ivLove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddBookBasicData addBookBasicData = addBookArray.get(position-1);

                    listener.onClickFavorite(addBookBasicData);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return addBookArray == null ? 0 : 1 + addBookArray.size();
    }

    public static class ProfileTopViewHolder extends RecyclerView.ViewHolder {

        private ImageView ivProfilePic, ivMoney, ivOrders, ivChatList;
        private TextView tvPostCount, tvFollower, tvFollowing, tvEditProfile, tvMenu;
        private ConstraintLayout expandView;

        public ProfileTopViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePic = itemView.findViewById(R.id.profile_picture);
            tvPostCount = itemView.findViewById(R.id.post_count);
            tvFollower = itemView.findViewById(R.id.follower_count);
            tvFollowing = itemView.findViewById(R.id.following_count);
            tvEditProfile = itemView.findViewById(R.id.edit_profile);
            ivMoney = itemView.findViewById(R.id.money);
            ivOrders = itemView.findViewById(R.id.order);
            ivChatList = itemView.findViewById(R.id.chat);
            tvMenu = itemView.findViewById(R.id.menu);
            expandView = itemView.findViewById(R.id.expand_space);
        }

        public void setUserAllInformation (UserAllInformation userAllInformation){

            ImageLoaderProvider.getInstance().setImage(userAllInformation.getUserPhotoUrl(),ivProfilePic);
            tvFollower.setText(userAllInformation.getFollower()+"");
            tvFollowing.setText(userAllInformation.getFollow()+"");
        }
    }

    public static class ProfileBottomViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout consBookLayout, consNoData;
        private ImageView ivProfilePicture, ivBookCover, ivLove, ivChat;
        private TextView tvUserEmail, tvMsgCount;

        public ProfileBottomViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePicture = itemView.findViewById(R.id.profile_picture_b);
            ivBookCover = itemView.findViewById(R.id.book_cover);
            ivLove = itemView.findViewById(R.id.love);
            ivChat = itemView.findViewById(R.id.chat);
            tvUserEmail = itemView.findViewById(R.id.user_account);
            tvMsgCount = itemView.findViewById(R.id.message_count);
            consBookLayout = itemView.findViewById(R.id.book_lay_out);
            consNoData = itemView.findViewById(R.id.no_data);
        }

        public void setAddBookBasicData(ArrayList<AddBookBasicData> addBookArray, AddBookBasicData addBookBasicData, UserAllInformation userAllInformation){

            ImageLoaderProvider.getInstance().setImage(addBookBasicData.getPhotoUrl(), ivBookCover);
            ImageLoaderProvider.getInstance().setImage(userAllInformation.getUserPhotoUrl(),ivProfilePicture);
            tvUserEmail.setText(userAllInformation.getEmail());
            tvMsgCount.setText("共有 "+addBookBasicData.getMsgCount()+" 則留言");

            if (addBookBasicData.isSelectHeart()){
                ivLove.setImageResource(R.drawable.love_click);
            } else {
                ivLove.setImageResource(R.drawable.love);
            }

            if (addBookArray.size() - 10 == 0){
                consNoData.setVisibility(View.VISIBLE);
                consBookLayout.setVisibility(View.GONE);

            } else {
                consNoData.setVisibility(View.GONE);
                consBookLayout.setVisibility(View.VISIBLE);
            }

            if (addBookBasicData.getMsgCount() != 0){
                ivChat.setImageResource(R.drawable.chat_click);
            }
        }
    }

    public interface OnClickEditProfile{
        void onClickEdit();
        void onClickOrder();
        void onClickMoney();
        void onClickChatList();
        void onClickFavorite(AddBookBasicData addBookBasicData);
        void onClickIvChat(AddBookBasicData addBookBasicData);
        void onClickToMessage(AddBookBasicData addBookBasicData);
        void onClickChangePage(AddBookBasicData addBookBasicData);
    }
}

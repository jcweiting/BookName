package com.book.second_book_exchange.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.book.second_book_exchange.AddBookBasicData;
import com.book.second_book_exchange.MessageData;
import com.book.second_book_exchange.R;
import com.book.second_book_exchange.UserBasicData;
import com.book.second_book_exchange.api.ApiTool;
import com.book.second_book_exchange.log.JoyceLog;
import com.book.second_book_exchange.tool.ImageLoaderProvider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatRoomAdapter extends RecyclerView.Adapter {

    public static final int MINE = 0;
    public static final int SENDER = 1;
    private ArrayList<MessageData> messageDataArr;
    private String myUid;
    private UserBasicData userBasicData;
    private String otherUid;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private String otherPhotoUrl;

    public void setOtherUid(String otherUid) {
        this.otherUid = otherUid;
    }

    public void setMyUid(String myUid) {
        this.myUid = myUid;
    }

    public void setMessageDataArr(ArrayList<MessageData> messageDataArr) {
        this.messageDataArr = messageDataArr;
    }

    @Override
    public int getItemViewType(int position) {

        MessageData messageData = messageDataArr.get(position);

        if (messageData.getSenderUid().equals(myUid)){
            return MINE;
        }

        return SENDER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view;

        if (viewType == MINE){
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_right,parent,false);
            return new ChatRightViewHolder(view);
        }

        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_left,parent,false);
        return new ChatLeftViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof ChatRightViewHolder){
            ((ChatRightViewHolder) holder).setMessage(messageDataArr.get(position));

        }

        if (holder instanceof ChatLeftViewHolder){

            userBasicData = new UserBasicData();
            userBasicData.setUserUid(otherUid);
            JoyceLog.i("ChatRoomAdapter | otherUid: "+otherUid);

            ApiTool.getRequestApi()
                    .checkUserData(userBasicData)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<UserBasicData>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(@NonNull UserBasicData userBasicData) {
                            otherPhotoUrl = userBasicData.getUserPhotoUrl();
                            JoyceLog.i("ChatRoomAdapter | checkUserData | otherPhotoUrl: "+otherPhotoUrl);

                            ((ChatLeftViewHolder) holder).setMessage(messageDataArr.get(position),otherPhotoUrl);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            JoyceLog.i("ChatRoomAdapter | checkUserData | Error: "+e);
                        }

                        @Override
                        public void onComplete() {
                            JoyceLog.i("ChatRoomAdapter | checkUserData | onComplete");
                        }
                    });
        }
    }


    @Override
    public int getItemCount() {
        return messageDataArr == null ? 0 : messageDataArr.size();
    }

    //自己的聊天內容
    public class ChatRightViewHolder extends RecyclerView.ViewHolder {

        private TextView chatMsg, time;

        public ChatRightViewHolder(@NonNull View itemView) {
            super(itemView);

            chatMsg = itemView.findViewById(R.id.chat_msg);
            time = itemView.findViewById(R.id.time);
        }

        public void setMessage(MessageData messageData){
            chatMsg.setText(messageData.getMsg());
            time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(messageData.getTime())));
        }
    }

    //對方的聊天內容
    public class ChatLeftViewHolder extends RecyclerView.ViewHolder {

        private TextView chatMsg, time;
        private ImageView ivChatPic;

        public ChatLeftViewHolder(@NonNull View itemView) {
            super(itemView);

            chatMsg = itemView.findViewById(R.id.chat_msg);
            time = itemView.findViewById(R.id.time);
            ivChatPic = itemView.findViewById(R.id.profile_picture);
        }

        public void setMessage(MessageData messageData, String otherPhotoUrl){
            chatMsg.setText(messageData.getMsg());
            time.setText(new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(messageData.getTime())));
            ImageLoaderProvider.getInstance().setImage(otherPhotoUrl,ivChatPic);
            JoyceLog.i("ChatRoomAdapter | setMessage | otherPhotoUrl: "+ otherPhotoUrl);
        }
    }
}

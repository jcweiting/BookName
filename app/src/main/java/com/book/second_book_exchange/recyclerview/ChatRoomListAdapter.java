package com.book.second_book_exchange.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.book.second_book_exchange.R;
import com.book.second_book_exchange.UserBasicData;
import com.book.second_book_exchange.api.ApiTool;
import com.book.second_book_exchange.log.JoyceLog;
import com.book.second_book_exchange.tool.ImageLoaderProvider;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatRoomListAdapter extends RecyclerView.Adapter<ChatRoomListAdapter.ViewHolder> {

    private ArrayList<String> otherIdArr,lastMsgList;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();
    private UserBasicData userData;
    private OnClickListener listener;

    public void setListener(OnClickListener listener) {
        this.listener = listener;
    }

    public void setLastMsgList(ArrayList<String> lastMsgList) {
        this.lastMsgList = lastMsgList;
    }

    public void setOtherIdArr(ArrayList<String> otherIdArr) {
        this.otherIdArr = otherIdArr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message_overview_list_recyclerview,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String otherId = otherIdArr.get(position);
        String lastMsg = lastMsgList.get(position);
        checkOtherUserEmail(holder, otherId,lastMsg);
        JoyceLog.i("ChatRoomOverviewAdapter | position : "+position);

        holder.constraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickChatRoom(otherId);
            }
        });
    }

    private void checkOtherUserEmail(ViewHolder holder, String otherId, String lastMsg) {
//        JoyceLog.i("otherId : "+otherId);
        userData = new UserBasicData();
        userData.setUserUid(otherId);
        holder.tvMessage.setText(lastMsg);

        ApiTool.getRequestApi()
                .checkUserData(userData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull UserBasicData userBasicData) {
                        holder.tvEmail.setText(userBasicData.getEmail());
                        ImageLoaderProvider.getInstance().setImage(userBasicData.getUserPhotoUrl(), holder.ivProfilePic);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("ChatRoomOverviewAdapter | checkUserData | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("ChatRoomOverviewAdapter | checkUserData | onComplete");
                    }
                });
    }

    @Override
    public int getItemCount() {

        return otherIdArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivProfilePic;
        private TextView tvEmail, tvMessage;
        private ConstraintLayout constraint;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePic = itemView.findViewById(R.id.profile_picture);
            tvEmail = itemView.findViewById(R.id.nickname);
            tvMessage = itemView.findViewById(R.id.message);
            constraint = itemView.findViewById(R.id.chat_space);
        }
    }

    public interface OnClickListener{
        void onClickChatRoom(String otherId);
    }
}

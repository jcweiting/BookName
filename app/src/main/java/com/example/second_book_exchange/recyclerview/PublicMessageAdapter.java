package com.example.second_book_exchange.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.second_book_exchange.PublicMessage;
import com.example.second_book_exchange.PublicMessageData;
import com.example.second_book_exchange.R;
import com.example.second_book_exchange.UserBasicData;
import com.example.second_book_exchange.api.ApiTool;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.tool.ImageLoaderProvider;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class PublicMessageAdapter extends RecyclerView.Adapter<PublicMessageAdapter.ViewHolder> {

    private ArrayList<PublicMessageData> messageDataArr;
    private UserBasicData userBasicData;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void setMessageDataArr(ArrayList<PublicMessageData> messageDataArr) {
        this.messageDataArr = messageDataArr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_public_message,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        PublicMessageData messageDataObject = messageDataArr.get(position);

        showIvProfilePic(messageDataObject,holder);

        holder.tvMessage.setText(messageDataObject.getMsg());

    }

    private void showIvProfilePic(PublicMessageData messageDataObject, ViewHolder holder) {
        userBasicData = new UserBasicData();
        userBasicData.setUserUid(messageDataObject.getUidForLeftMsg());

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
                        ImageLoaderProvider.getInstance().setImage(userBasicData.getUserPhotoUrl(), holder.ivProfilePic);
                        holder.tvId.setText(userBasicData.getEmail()+"");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("PublicMessage | checkUserData | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("PublicMessage | checkUserData | onComplete");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return messageDataArr == null ? 0 : messageDataArr.size();
    }

    public void onDestroy(){
        compositeDisposable.clear();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView ivProfilePic;
        private TextView tvId, tvMessage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProfilePic = itemView.findViewById(R.id.profile_picture);
            tvId = itemView.findViewById(R.id.public_message_id);
            tvMessage = itemView.findViewById(R.id.public_message_content);
        }
    }
}

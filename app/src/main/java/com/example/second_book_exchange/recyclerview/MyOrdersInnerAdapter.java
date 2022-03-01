package com.example.second_book_exchange.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.second_book_exchange.BookInsideData;
import com.example.second_book_exchange.BookOuterData;
import com.example.second_book_exchange.PublicMessage;
import com.example.second_book_exchange.R;
import com.example.second_book_exchange.UserBasicData;
import com.example.second_book_exchange.api.ApiTool;
import com.example.second_book_exchange.log.JoyceLog;
import com.google.gson.Gson;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyOrdersInnerAdapter extends RecyclerView.Adapter<MyOrdersInnerAdapter.ViewHolder> {

    private ArrayList<BookOuterData> outerDataArr;
    private String roleTitle;
    private OnClickMoreDetails listener;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public void setListener(OnClickMoreDetails listener) {
        this.listener = listener;
    }

    public void setRoleTitle(String roleTitle) {
        this.roleTitle = roleTitle;
    }

    public void setOuterDataArr(ArrayList<BookOuterData> outerDataArr) {
        this.outerDataArr = outerDataArr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_myorders_content,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        BookOuterData outerData = outerDataArr.get(position);
        JoyceLog.i("MyOrderInnerAdapter | outerData: "+new Gson().toJson(outerData));

        String bookName = "";

        //用for迴圈找出bookName
        for (BookInsideData insideData : outerData.getProductLists()){
            bookName += " | "+insideData.getBookName();
        }

        holder.tvBookName.setText("書名:"+bookName+" | ");
        JoyceLog.i("MyOrderInnerAdapter | bookName: "+bookName);

        checkRoleEmail(holder,outerData);

        holder.tvShipment.setText("運送方式: "+outerData.getShipmentWay());

        holder.tvMoreDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onClickDetail(outerData.getProductLists());
            }
        });
    }

    private void checkRoleEmail(ViewHolder holder, BookOuterData outerData) {

        UserBasicData userBasicData = new UserBasicData();

        if (roleTitle.equals("買家為: ")){

            userBasicData.setUserUid(outerData.getMyUid());

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

                            //設定角色 & Email
                            if (roleTitle.equals("買家為: ")){
                                holder.tvRole.setText(roleTitle+userBasicData.getEmail());
                            }
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            JoyceLog.i("MyOrdersInnerAdapter | checkUserData | Error: "+e);
                        }

                        @Override
                        public void onComplete() {
                            JoyceLog.i("MyOrdersInnerAdapter | checkUserData | onComplete");
                        }
                    });

        } else {

            userBasicData.setUserUid(outerData.getUploaderUid());

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
                            holder.tvRole.setText(roleTitle+userBasicData.getEmail());
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            JoyceLog.i("MyOrdersInnerAdapter | checkUserData | Error: "+e);
                        }

                        @Override
                        public void onComplete() {
                            JoyceLog.i("MyOrdersInnerAdapter | checkUserData | onComplete");
                        }
                    });
        }
    }

    @Override
    public int getItemCount() {
        return outerDataArr == null ? 0 : outerDataArr.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvBookName, tvMoreDetails, tvShipment, tvRole ;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvBookName = itemView.findViewById(R.id.book_name);
            tvRole = itemView.findViewById(R.id.show_role);
            tvShipment = itemView.findViewById(R.id.shipment);
            tvMoreDetails = itemView.findViewById(R.id.more_details);
        }
    }

    public interface OnClickMoreDetails{
        void onClickDetail(ArrayList<BookInsideData> insideDataArr);
    }
}

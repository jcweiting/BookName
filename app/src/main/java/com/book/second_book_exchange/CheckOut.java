package com.book.second_book_exchange;

import static com.book.second_book_exchange.fragment.CartFragment.OUTER_DATA_ARR;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.book.second_book_exchange.api.ApiTool;
import com.book.second_book_exchange.api.ResponseData;
import com.book.second_book_exchange.log.JoyceLog;
import com.book.second_book_exchange.recyclerview.CheckOutAdapter;
import com.google.gson.Gson;
import com.book.second_book_exchange.R;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class CheckOut extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvPlaceOrder;
    private ImageView ivBack;
    private ArrayList<BookOuterData> outerDataList;
    private UserBasicData userDataObject;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);

        compositeDisposable = new CompositeDisposable();
        initView();

        //接收值 ==================================================
        Bundle bundle = getIntent().getExtras();

        if (bundle == null){
            showHint("查無購物車資料");
            return;
        }

        outerDataList = (ArrayList<BookOuterData>) bundle.getSerializable(OUTER_DATA_ARR);
        //========================================================


        ArrayList<UserBasicData> userDataArr = new ArrayList<>();

        for (BookOuterData outerData : outerDataList){

            userDataObject = new UserBasicData();
            userDataObject.setUserUid(outerData.getUploaderUid());
            userDataArr.add(userDataObject);

        }

        ApiTool.getRequestApi()
                .getAllSellerData(userDataArr)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<UserBasicData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<UserBasicData> userBasicDataArr) {

                        JoyceLog.i("CheckOut | getAllSellerData | userBasicDataArr: "+new Gson().toJson(userBasicDataArr));
                        CheckOutAdapter checkOutAdapter = new CheckOutAdapter();
                        checkOutAdapter.setBookOuterData(outerDataList);
                        checkOutAdapter.setUserDataArr(userBasicDataArr);
                        recyclerView.setAdapter(checkOutAdapter);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("CheckOut | getAllSellerData | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("CheckOut | getAllSellerData | onComplete");
                    }
                });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvPlaceOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                progressCheckOut(outerDataList);
            }
        });
    }

    private void progressCheckOut(ArrayList<BookOuterData> outerDataList) {

        ApiTool.getRequestApi()
                .checkOut(outerDataList)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ResponseData responseData) {

                        JoyceLog.i("CheckOut | checkOut | responseData: "+new Gson().toJson(responseData));

                        if (responseData.getResult() == 200){

                            Intent intent = new Intent(CheckOut.this,MyOrders.class);
                            startActivity(intent);

                            finish();

                        } else {
                            showHint("結帳發生錯誤");
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("CheckOut | checkOut | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("CheckOut | checkOut | onComplete");
                    }
                });
    }

    private void showHint(String content) {
        Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerview_check_out);
        tvPlaceOrder = findViewById(R.id.enter_order);
        ivBack = findViewById(R.id.back);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
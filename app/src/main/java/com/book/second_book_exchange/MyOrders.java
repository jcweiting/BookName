package com.book.second_book_exchange;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.book.second_book_exchange.api.ApiTool;
import com.book.second_book_exchange.api.OrderData;
import com.book.second_book_exchange.api.ResponseData;
import com.book.second_book_exchange.api.UserData;
import com.book.second_book_exchange.log.JoyceLog;
import com.book.second_book_exchange.recyclerview.MyOrdersAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.gson.Gson;
import com.book.second_book_exchange.R;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MyOrders extends AppCompatActivity {

    private ImageView ivBack;
    private RecyclerView recyclerView;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private CompositeDisposable compositeDisposable;
    private UserData userData;
    private ConstraintLayout root;
    private View mask;
    private MyOrdersAdapter myOrdersAdapter = new MyOrdersAdapter();
    private TextView tv0, tv1, tvAll;
    private ArrayList<OrderData> orderNewDataArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        compositeDisposable = new CompositeDisposable();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        initView();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        showMy0Orders();

        tv0.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv0.setTextColor(Color.WHITE);
                tv1.setTextColor(Color.parseColor("#9A9A9A"));
                tvAll.setTextColor(Color.parseColor("#9A9A9A"));
                showMy0Orders();
            }
        });

        tv1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv0.setTextColor(Color.parseColor("#9A9A9A"));
                tv1.setTextColor(Color.WHITE);
                tvAll.setTextColor(Color.parseColor("#9A9A9A"));
                showMy1Orders();
            }
        });

        tvAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                tv0.setTextColor(Color.parseColor("#9A9A9A"));
                tv1.setTextColor(Color.parseColor("#9A9A9A"));
                tvAll.setTextColor(Color.WHITE);
                showAllOrders();
            }
        });
    }

    private void showMy1Orders() {
        userData = new UserData();
        userData.setUid(user.getUid());

        ApiTool.getRequestApi()
                .getAllOrderList(userData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<OrderData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<OrderData> orderDataArr) {

                        orderNewDataArr = new ArrayList<>();

                        for (OrderData orderDataObject : orderDataArr){

                            if (orderDataObject.getStatus() == 1){
                                orderNewDataArr.add(orderDataObject);
                            }
                        }

                        myOrdersAdapter.setOrderDataArr(orderNewDataArr);

                        myOrdersAdapter.setListener(new MyOrdersAdapter.OnClickListener() {
                            @Override
                            public void onClickStatus(OrderData orderDataObject) {
                                OrderStatusSelect orderStatusSelect = new OrderStatusSelect();
                                orderStatusSelect.showView(mask,root,MyOrders.this);

                                orderStatusSelect.setListener(new OrderStatusSelect.OnClickStatus() {
                                    @Override
                                    public void onClickStatus(int code) {

                                        orderDataObject.setStatus(code);

                                        ApiTool.getRequestApi()
                                                .changeOrderStatus(orderDataObject)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Observer<ResponseData>() {
                                                    @Override
                                                    public void onSubscribe(@NonNull Disposable d) {
                                                        compositeDisposable.add(d);
                                                    }

                                                    @Override
                                                    public void onNext(@NonNull ResponseData responseData) {
                                                        JoyceLog.i("MyOrders | changeOrderStatus1 | responseData: "+responseData.getResult()+" ;" +responseData.getMessage());
                                                        showMy1Orders();
                                                    }

                                                    @Override
                                                    public void onError(@NonNull Throwable e) {
                                                        JoyceLog.i("MyOrders | changeOrderStatus1 | Error: "+e);
                                                    }

                                                    @Override
                                                    public void onComplete() {
                                                        JoyceLog.i("MyOrders | changeOrderStatus1 | onComplete");
                                                    }
                                                });
                                    }
                                });
                            }

                            @Override
                            public void onClickDetails(ArrayList<BookInsideData> insideDataArr) {
                                MyOrdersDetails myOrdersDetails = new MyOrdersDetails();
                                myOrdersDetails.setInsideDataArr(insideDataArr);
                                myOrdersDetails.showView(mask,root,MyOrders.this);
                            }
                        });

                        recyclerView.setAdapter(myOrdersAdapter);
                        myOrdersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("MyOrders | getAllOrderList(0) | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("MyOrders | getAllOrderList(0) | onComplete");
                    }
                });
    }

    private void showMy0Orders() {

        tv0.setTextColor(Color.WHITE);

        userData = new UserData();
        userData.setUid(user.getUid());

        ApiTool.getRequestApi()
                .getAllOrderList(userData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<OrderData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<OrderData> orderDataArr) {

                        orderNewDataArr = new ArrayList<>();

                        for (OrderData orderDataObject : orderDataArr){

                            if (orderDataObject.getStatus() == 0){
                                orderNewDataArr.add(orderDataObject);
                            }
                        }

                        myOrdersAdapter.setOrderDataArr(orderNewDataArr);

                        myOrdersAdapter.setListener(new MyOrdersAdapter.OnClickListener() {
                            @Override
                            public void onClickStatus(OrderData orderDataObject) {

                                OrderStatusSelect orderStatusSelect = new OrderStatusSelect();
                                orderStatusSelect.showView(mask,root,MyOrders.this);

                                orderStatusSelect.setListener(new OrderStatusSelect.OnClickStatus() {
                                    @Override
                                    public void onClickStatus(int code) {

                                        orderDataObject.setStatus(code);

                                        ApiTool.getRequestApi()
                                                .changeOrderStatus(orderDataObject)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Observer<ResponseData>() {
                                                    @Override
                                                    public void onSubscribe(@NonNull Disposable d) {
                                                        compositeDisposable.add(d);
                                                    }

                                                    @Override
                                                    public void onNext(@NonNull ResponseData responseData) {
                                                        JoyceLog.i("MyOrders | changeOrderStatus | responseData: "+responseData.getResult()+" ;" +responseData.getMessage());
                                                        showMy0Orders();
                                                    }

                                                    @Override
                                                    public void onError(@NonNull Throwable e) {
                                                        JoyceLog.i("MyOrders | changeOrderStatus | Error: "+e);
                                                    }

                                                    @Override
                                                    public void onComplete() {
                                                        JoyceLog.i("MyOrders | changeOrderStatus | onComplete");
                                                    }
                                                });
                                    }
                                });
                            }

                            @Override
                            public void onClickDetails(ArrayList<BookInsideData> insideDataArr) {
                                MyOrdersDetails myOrdersDetails = new MyOrdersDetails();
                                myOrdersDetails.setInsideDataArr(insideDataArr);
                                myOrdersDetails.showView(mask,root,MyOrders.this);
                            }
                        });

                        recyclerView.setAdapter(myOrdersAdapter);
                        myOrdersAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("MyOrders | getAllOrderList(0) | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("MyOrders | getAllOrderList(0) | onComplete");
                    }
                });
    }

    private void showAllOrders() {

        userData = new UserData();
        userData.setUid(user.getUid());

        ApiTool.getRequestApi()
                .getAllOrderList(userData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<OrderData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<OrderData> orderData) {
                        JoyceLog.i("MyOrders | getAllOrderList | orderData: "+new Gson().toJson(orderData));

                        myOrdersAdapter.setOrderDataArr(orderData);
                        recyclerView.setAdapter(myOrdersAdapter);

                        myOrdersAdapter.setListener(new MyOrdersAdapter.OnClickListener() {
                            @Override
                            public void onClickStatus(OrderData orderDataObject) {

                                OrderStatusSelect orderStatusSelect = new OrderStatusSelect();
                                orderStatusSelect.showView(mask,root,MyOrders.this);

                                orderStatusSelect.setListener(new OrderStatusSelect.OnClickStatus() {
                                    @Override
                                    public void onClickStatus(int code) {

                                        orderDataObject.setStatus(code);

                                        ApiTool.getRequestApi()
                                                .changeOrderStatus(orderDataObject)
                                                .subscribeOn(Schedulers.io())
                                                .observeOn(AndroidSchedulers.mainThread())
                                                .subscribe(new Observer<ResponseData>() {
                                                    @Override
                                                    public void onSubscribe(@NonNull Disposable d) {
                                                        compositeDisposable.add(d);
                                                    }

                                                    @Override
                                                    public void onNext(@NonNull ResponseData responseData) {
                                                        JoyceLog.i("MyOrders | changeOrderStatus | responseData: "+responseData.getResult()+" ;" +responseData.getMessage());
                                                        showAllOrders();

                                                    }

                                                    @Override
                                                    public void onError(@NonNull Throwable e) {
                                                        JoyceLog.i("MyOrders | changeOrderStatus | Error: "+e);
                                                    }

                                                    @Override
                                                    public void onComplete() {
                                                        JoyceLog.i("MyOrders | changeOrderStatus | onComplete");
                                                    }
                                                });
                                    }
                                });
                            }

                            @Override
                            public void onClickDetails(ArrayList<BookInsideData> insideDataArr) {

                                MyOrdersDetails myOrdersDetails = new MyOrdersDetails();
                                myOrdersDetails.setInsideDataArr(insideDataArr);
                                myOrdersDetails.showView(mask,root,MyOrders.this);
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("MyOrders | getAllOrderList | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("MyOrders | getAllOrderList | onComplete");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void initView() {
        ivBack = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyclerview_orders);
        root = findViewById(R.id.root);
        mask = findViewById(R.id.mask);
        tv0 = findViewById(R.id.order_status_0);
        tv1 = findViewById(R.id.order_status_1);
        tvAll = findViewById(R.id.order_status_all);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
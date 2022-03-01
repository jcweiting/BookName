package com.example.second_book_exchange.fragment;

import static com.example.second_book_exchange.fragment.HomeFragment.ADD_BOOK_BASIC_DATA;
import static com.example.second_book_exchange.fragment.HomeFragment.CART;
import static com.example.second_book_exchange.fragment.HomeFragment.LOVE;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.second_book_exchange.AddBookBasicData;
import com.example.second_book_exchange.BookInfoActivity;
import com.example.second_book_exchange.CustomCartSelect;
import com.example.second_book_exchange.R;
import com.example.second_book_exchange.api.ApiTool;
import com.example.second_book_exchange.api.UserData;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.recyclerview.HeartAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class HeartFragment extends Fragment {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private OnChangeHeartListener onHeartListener;
    private RecyclerView recyclerView;
    private TextView tvNoData;
    private int defaultQty = 1;
    private int qtyMax = 0;
    private ConstraintLayout root;
    private View mask;
    private CompositeDisposable compositeDisposable;
    private HeartAdapter heartAdapter;
    private  ArrayList<AddBookBasicData> loveDataArray;

    public void setOnHeartListener(OnChangeHeartListener onHeartListener) {
        this.onHeartListener = onHeartListener;
    }

    public static HeartFragment newInstance() {
        HeartFragment fragment = new HeartFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compositeDisposable = new CompositeDisposable();
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_heart, container, false);
        initView(view);

        if (user == null){
            onHeartListener.onChangeHeartIcon();
            showHint("請先登入會員");

        } else{

            JoyceLog.i("HeartFragment | uid: "+user.getUid());
            ApiTool.getRequestApi()
                    .allFavoriteList(new UserData(user.getUid()))
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<ArrayList<AddBookBasicData>>() {
                        @Override
                        public void onSubscribe(@NonNull Disposable d) {
                            compositeDisposable.add(d);
                        }

                        @Override
                        public void onNext(@NonNull ArrayList<AddBookBasicData> loveListArray) {

                            if (loveListArray.isEmpty()){
                                tvNoData.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                return;
                            }

                            loveDataArray = loveListArray;

                            JoyceLog.i("HeartFragment | loveListArray: "+new Gson().toJson(loveListArray));
                            heartAdapter = new HeartAdapter();

                            //add to cart
                            heartAdapter.setListener(new HeartAdapter.OnClickListener() {
                                @Override
                                public void onClickToCart(AddBookBasicData addBookObject) {

                                    showCartView(loveListArray,addBookObject);
                                }

                                @Override
                                public void onClickPicture(AddBookBasicData addBookBasicData) {

                                    Intent intent = new Intent(getContext(), BookInfoActivity.class);
                                    intent.putExtra(ADD_BOOK_BASIC_DATA,addBookBasicData);
                                    startActivity(intent);
                                }

                                @Override
                                public void onDelete(AddBookBasicData bookDataObject) {

                                    ApiTool.getRequestApi()
                                            .deleteFavorite(bookDataObject)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(new Observer<AddBookBasicData>() {
                                                @Override
                                                public void onSubscribe(@NonNull Disposable d) {
                                                    compositeDisposable.add(d);
                                                }

                                                @Override
                                                public void onNext(@NonNull AddBookBasicData addBookBasicData) {
                                                    JoyceLog.i("HeartFragment | onClickDelete | 已移除addBookBasicData: "+new Gson().toJson(addBookBasicData));
                                                    showHint("已從我的最愛清單中刪除");

                                                    //資料本地更新======================================================

                                                    //這邊做資料刪除
                                                    for (AddBookBasicData data : loveDataArray){
                                                        if (data.getId() == addBookBasicData.getId()){
                                                            loveDataArray.remove(data);
                                                            break;
                                                        }
                                                    }

                                                    //資料更新,重新顯示UI
                                                    heartAdapter.setHeartList(loveDataArray);
                                                    heartAdapter.notifyDataSetChanged();

                                                    //=================================================================

                                                }

                                                @Override
                                                public void onError(@NonNull Throwable e) {
                                                    JoyceLog.i("HeartFragment | onClickDelete | Error: "+e);
                                                }

                                                @Override
                                                public void onComplete() {
                                                    JoyceLog.i("HeartFragment | onClickDelete | onComplete");
                                                }
                                            });
                                }
                            });

                            heartAdapter.setHeartList(loveListArray);
                            recyclerView.setAdapter(heartAdapter);
                        }

                        @Override
                        public void onError(@NonNull Throwable e) {
                            JoyceLog.i("HeartFragment | Error: "+e);
                        }

                        @Override
                        public void onComplete() {
                            JoyceLog.i("HeartFragment | onComplete");
                        }
                    });
        }
        return view;
    }

    private void showCartView(ArrayList<AddBookBasicData> loveListArray, AddBookBasicData addBookObject) {
        CustomCartSelect customCartSelect = new CustomCartSelect();
        customCartSelect.showView(mask,root,getActivity());
        customCartSelect.setTvName(addBookObject.getBookName());
        customCartSelect.setTvUnitPrice(addBookObject.getUnitPrice());
        customCartSelect.setIvCover(addBookObject.getPhotoUrl());

        customCartSelect.setListener(new CustomCartSelect.OnButtonClickListener() {
            @Override
            public void onClickToCart(String tvQty) {
                for (int i = 0 ; i < loveListArray.size() ; i++){
                    AddBookBasicData bookDataObject = loveListArray.get(i);

                    if (bookDataObject.getBookName().equals(addBookObject.getBookName())){

                        addBookObject.setMyUid(user.getUid());
                        addBookObject.setQty(tvQty);   //把書本的數量蓋掉
                        addToCart(bookDataObject);
                    }
                }
            }

            @Override
            public void onClickMinus() {
                defaultQty = defaultQty - 1 ;

                if (defaultQty <= 0){
                    showHint("數量不應小於0");

                    defaultQty = 1;
                    customCartSelect.setQty(defaultQty);
                    return;
                }

                customCartSelect.setQty(defaultQty);
            }

            @Override
            public void onClickAdd() {
                defaultQty = defaultQty + 1 ;

                for (int i = 0 ; i < loveListArray.size() ; i++){
                    AddBookBasicData bookBasicData = loveListArray.get(i);

                    if (bookBasicData.getBookName().equals(addBookObject.getBookName())){
                        qtyMax = Integer.parseInt(loveListArray.get(i).getQty());
                    }
                }

                if (defaultQty > qtyMax){
                    showHint("已超過庫存數量");
                    defaultQty = qtyMax;
                    return;
                }

                customCartSelect.setQty(defaultQty);
            }
        });

    }

    private void addToCart(AddBookBasicData bookDataObject) {
        ApiTool.getRequestApi()
                .addToCart(bookDataObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddBookBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull AddBookBasicData addBookBasicData) {
                        showHint("已新增至購物車");
                        defaultQty = 1 ;
                        JoyceLog.i("HeartFragment | addToCart | 已新增至-購物車");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("HeartFragment | addToCart | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("HeartFragment | addToCart | onComplete");
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerview_heart);
        mask = view.findViewById(R.id.mask);
        root = view.findViewById(R.id.root);
        tvNoData = view.findViewById(R.id.no_data);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void showHint(String content) {
        Toast.makeText(getContext(),content, Toast.LENGTH_SHORT).show();
    }

    public interface OnChangeHeartListener{
         void onChangeHeartIcon();
    }
}
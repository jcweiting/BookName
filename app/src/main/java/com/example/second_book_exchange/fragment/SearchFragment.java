package com.example.second_book_exchange.fragment;

import static com.example.second_book_exchange.fragment.HomeFragment.ADD_BOOK_BASIC_DATA;
import static com.example.second_book_exchange.fragment.HomeFragment.LOVE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.second_book_exchange.AddBookBasicData;
import com.example.second_book_exchange.BookInfoActivity;
import com.example.second_book_exchange.CustomCartSelect;
import com.example.second_book_exchange.R;
import com.example.second_book_exchange.api.ApiTool;
import com.example.second_book_exchange.api.SearchData;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.recyclerview.SearchAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.protobuf.Api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SearchFragment extends Fragment {

    private TextView tv000, tv100, tv200, tv300, tv400, tv500, tv600, tv700, tv800, tv900, tvChoose;
    private EditText edSearch;
    private ImageView ivSearch, ivExpand;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String searchViaClassify;
    private RecyclerView recyclerView;
    private ConstraintLayout noData, searchResult, root;
    private View mask;
    private int qtyMax = 0;
    private int defaultQty = 1;
    private CompositeDisposable compositeDisposable;
    private ArrayList<AddBookBasicData> bookArray = new ArrayList<>();
    private SearchAdapter searchAdapter;
    private boolean isClassifyOpen = false;
    private ConstraintLayout searchClassify;

    public static SearchFragment newInstance() {
        SearchFragment fragment = new SearchFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        compositeDisposable = new CompositeDisposable();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_search, container, false);
        initView(view);

        //由分類搜尋
        searchClassify();

        //由書名關鍵字搜尋
        ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvChoose.setText("");
                searchBookKeyWord();
                hideKeyboard(edSearch.getWindowToken());
            }
        });

        return view;
    }

    private void hideKeyboard(IBinder windowToken) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void searchBookKeyWord() {

        String search = edSearch.getText().toString();

        if (search.isEmpty()) {
            showHint("請輸入搜尋關鍵字");
            return;
        }

        ApiTool.getRequestApi()
                .searchBooks(new SearchData(search, user.getUid()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<AddBookBasicData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<AddBookBasicData> bookDataArray) {

                        bookArray = bookDataArray;
                        updateUI(bookDataArray);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("Error: " + e);        //因為 i 這個方法是static 所以可以直接呼叫
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("onComplete");
                    }
                });
    }

    private void updateUI(ArrayList<AddBookBasicData> arr) {

        if (arr.isEmpty()) {
            noData.setVisibility(View.VISIBLE);
            searchResult.setVisibility(View.GONE);

        } else {
            noData.setVisibility(View.GONE);
            searchResult.setVisibility(View.VISIBLE);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            searchAdapter = new SearchAdapter();
            searchAdapter.setSearchList(arr);

            searchAdapter.setListener(new SearchAdapter.OnClickListener() {
                @Override
                public void onClickCart(AddBookBasicData addBookBasicData) {

                    if (mAuth.getCurrentUser() == null) {
                        showHint("請先登入會員");
                        return;
                    }

                    addToCart(arr, addBookBasicData);
                }

                @Override
                public void onClickLove(AddBookBasicData addBookBasicData) {

                    if (mAuth.getCurrentUser() == null) {
                        showHint("請先登入會員");
                        return;
                    }

                    for (AddBookBasicData bookDataObject : bookArray) {

                        if (bookDataObject.getBookName().equals(addBookBasicData.getBookName())) {

                            addBookBasicData.setMyUid(user.getUid());   //設置my uid

                            if (!addBookBasicData.isSelectHeart()) {
                                addToFavorite(addBookBasicData);
                            } else {
                                deleteFavorite(addBookBasicData);
                            }
                        }
                    }
                }

                @Override
                public void onClickChangePage(AddBookBasicData addBookBasicData) {

                    Intent intent = new Intent(getContext(), BookInfoActivity.class);
                    intent.putExtra(ADD_BOOK_BASIC_DATA, addBookBasicData);
                    startActivity(intent);
                }
            });
            recyclerView.setAdapter(searchAdapter);
        }
    }

    private void deleteFavorite(AddBookBasicData addBookBasicData) {

        addBookBasicData.setMyUid(user.getUid());

        ApiTool.getRequestApi()
                .deleteFavorite(addBookBasicData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddBookBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull AddBookBasicData addBookBasicData) {

                        for (AddBookBasicData bookDataObject : bookArray) {

//                            JoyceLog.i("MemberFragment | bookDataObject: "+new Gson().toJson(bookDataObject));

                            if (bookDataObject.getBookName().equals(addBookBasicData.getBookName())) {
                                bookDataObject.setSelectHeart(false);
                            }
                        }

                        showHint("已從我的最愛中移除");
                        searchAdapter.setSearchList(bookArray);
                        searchAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("SearchFragment | deleteFavorite | Error: " + e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("SearchFragment | deleteFavorite | onComplete");
                    }
                });
    }

    private void addToFavorite(AddBookBasicData addBookBasicData) {

        JoyceLog.i("SearchFragment | uid: " + user.getUid());

        ApiTool.getRequestApi()
                .addFavorite(addBookBasicData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddBookBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull AddBookBasicData addBookBasicData) {

                        showHint("已儲存至我的最愛");

                        for (AddBookBasicData bookDataObject : bookArray) {

                            if (bookDataObject.getBookName().equals(addBookBasicData.getBookName())) {
                                bookDataObject.setSelectHeart(true);
                            }
                        }

                        searchAdapter.setSearchList(bookArray);
                        searchAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("SearchFragment | addToFavorite | Error: " + e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("SearchFragment | addToFavorite | onComplete");
                    }
                });
    }

    private void addToCart(ArrayList<AddBookBasicData> arr, AddBookBasicData addBookBasicData) {

        CustomCartSelect customCartSelect = new CustomCartSelect();
        customCartSelect.showView(mask, root, getActivity());
        customCartSelect.setTvName(addBookBasicData.getBookName());
        customCartSelect.setTvUnitPrice(addBookBasicData.getUnitPrice());
        customCartSelect.setIvCover(addBookBasicData.getPhotoUrl());

        customCartSelect.setListener(new CustomCartSelect.OnButtonClickListener() {
            @Override
            public void onClickToCart(String tvQty) {
                for (int i = 0; i < arr.size(); i++) {
                    AddBookBasicData cartViewObject = arr.get(i);

                    if (cartViewObject.getBookName().equals(addBookBasicData.getBookName())) {
                        qtyMax = Integer.parseInt(arr.get(i).getQty());
                        cartViewObject.setQty(tvQty);   //把書本的數量蓋掉
                        addToCartList(cartViewObject);
                        break;
                    }
                }
            }

            @Override
            public void onClickMinus() {

                defaultQty = defaultQty - 1;

                if (defaultQty <= 0) {
                    showHint("數量不應小於0");

                    defaultQty = 1;
                    customCartSelect.setQty(defaultQty);
                    return;
                }

                customCartSelect.setQty(defaultQty);
            }

            @Override
            public void onClickAdd() {
                defaultQty = defaultQty + 1;

                for (int i = 0; i < arr.size(); i++) {
                    AddBookBasicData bookBasicData = arr.get(i);

                    if (bookBasicData.getBookName().equals(addBookBasicData.getBookName())) {
                        qtyMax = Integer.parseInt(arr.get(i).getQty());
                    }
                }

                if (defaultQty > qtyMax) {
                    showHint("已超過庫存數量");
                    defaultQty = qtyMax;
                    return;
                }

                customCartSelect.setQty(defaultQty);
            }
        });
    }

    private void addToCartList(AddBookBasicData cartViewObject) {

        cartViewObject.setMyUid(user.getUid());

        ApiTool.getRequestApi()
                .addToCart(cartViewObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddBookBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull AddBookBasicData cartListObject) {
                        showHint("已將書本儲存在購物車");
                        defaultQty = 1;
                        JoyceLog.i("SearchFragment | addToCartList() | 已將書本儲存在購物車");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("SearchFragment | addToCartList() | Error: " + e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("SearchFragment | addToCartList() | onComplete");
                    }
                });
    }

    private void searchClassify() {

        tv000.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViaClassify = getKeyWord(tv000);
                afterClick(tv000);
                clearEdSearch();
                startSearchViaClassify();
            }
        });

        tv100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViaClassify = getKeyWord(tv100);
                afterClick(tv100);
                clearEdSearch();
                startSearchViaClassify();
            }
        });

        tv200.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViaClassify = getKeyWord(tv200);
                afterClick(tv200);
                clearEdSearch();
                startSearchViaClassify();
            }
        });

        tv300.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViaClassify = getKeyWord(tv300);
                afterClick(tv300);
                clearEdSearch();
                startSearchViaClassify();
            }
        });

        tv400.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViaClassify = getKeyWord(tv400);
                afterClick(tv400);
                clearEdSearch();
                startSearchViaClassify();
            }
        });

        tv500.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViaClassify = getKeyWord(tv500);
                afterClick(tv500);
                clearEdSearch();
                startSearchViaClassify();
            }
        });

        tv600.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViaClassify = getKeyWord(tv600);
                afterClick(tv600);
                clearEdSearch();
                startSearchViaClassify();
            }
        });

        tv700.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViaClassify = getKeyWord(tv700);
                afterClick(tv700);
                clearEdSearch();
                startSearchViaClassify();
            }
        });

        tv800.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViaClassify = getKeyWord(tv800);
                afterClick(tv800);
                clearEdSearch();
                startSearchViaClassify();
            }
        });

        tv900.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchViaClassify = getKeyWord(tv900);
                afterClick(tv900);
                clearEdSearch();
                startSearchViaClassify();
            }
        });
    }

    private void afterClick(TextView textItem) {
        tvChoose.setVisibility(View.VISIBLE);
        String chosen = textItem.getText().toString();
        tvChoose.setText(" : " + chosen);
    }

    private void clearEdSearch() {
        edSearch.setText("");
    }

    private void startSearchViaClassify() {

        ApiTool.getRequestApi()
                .searchBooks(new SearchData(searchViaClassify, user.getUid()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<AddBookBasicData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<AddBookBasicData> bookListArray) {

                        bookArray = bookListArray;
                        updateUI(bookListArray);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("Error: " + e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("onComplete");
                    }
                });
    }

    private String getKeyWord(TextView textContent) {
        searchViaClassify = textContent.getText().toString();
        return searchViaClassify;
    }

    private void showHint(String content) {
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
    }

    private void initView(View view) {
        recyclerView = view.findViewById(R.id.recyclerview_search);
        edSearch = view.findViewById(R.id.search_box);
        ivSearch = view.findViewById(R.id.search_icon);
        noData = view.findViewById(R.id.no_data);
        searchResult = view.findViewById(R.id.search_info);
        tvChoose = view.findViewById(R.id.choose);
        ivExpand = view.findViewById(R.id.expand);
        tv000 = view.findViewById(R.id.cl000);
        tv100 = view.findViewById(R.id.cl100);
        tv200 = view.findViewById(R.id.cl200);
        tv300 = view.findViewById(R.id.cl300);
        tv400 = view.findViewById(R.id.cl400);
        tv500 = view.findViewById(R.id.cl500);
        tv600 = view.findViewById(R.id.cl600);
        tv700 = view.findViewById(R.id.cl700);
        tv800 = view.findViewById(R.id.cl800);
        tv900 = view.findViewById(R.id.cl900);
        mask = view.findViewById(R.id.mask);
        root = view.findViewById(R.id.root);
        searchClassify = view.findViewById(R.id.search_classify);

        ivExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isClassifyOpen = !isClassifyOpen;   //每點擊一次就改變布林值
                searchClassify.setVisibility(isClassifyOpen ? View.VISIBLE : View.GONE);
            }
        });
    }
}
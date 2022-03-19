package com.book.second_book_exchange;

import static com.book.second_book_exchange.fragment.HomeFragment.ADD_BOOK_BASIC_DATA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.book.second_book_exchange.api.ApiTool;
import com.book.second_book_exchange.log.JoyceLog;
import com.book.second_book_exchange.recyclerview.BookInfoAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.book.second_book_exchange.R;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BookInfoActivity extends AppCompatActivity {

    public static final String BOOK_UPLOADER_UID = "BOOK UPLOADER UID";
    private RecyclerView recyclerViewBookInfo;
    private ImageView ivBack, ivEdit, ivMessage;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private View mask;
    private ConstraintLayout root;
    private int defaultQty = 1;
    private int qtyMax = 0;
    private AddBookBasicData addBookBasicData;
    private String userUid;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_info);

        compositeDisposable = new CompositeDisposable();

        initFirebase();
        initView();

        //接收值=======================================
        Bundle bundle = getIntent().getExtras();

        if (bundle == null){
            showHint("查無書本資料");
            return;
        }

        addBookBasicData = (AddBookBasicData) bundle.getSerializable(ADD_BOOK_BASIC_DATA);
        Log.i("Joyce","BookInfoActivity | Book Name: "+addBookBasicData.getBookName());
        //=============================================


        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                finish();
            }
        });

        ivMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (user == null){
                    showHint("請先登入會員");
                    return;
                }

                if (addBookBasicData.getUploaderUid().equals(user.getUid())){
                    return;
                }

                Intent intent = new Intent(BookInfoActivity.this,ChatRoomActivity.class);
                intent.putExtra("myUid",user.getUid());
                intent.putExtra("otherUid",addBookBasicData.getUploaderUid());
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        showData(addBookBasicData);
    }

    private void showData(AddBookBasicData addBookBasicData) {
        BookInfoAdapter bookInfoAdapter = new BookInfoAdapter();

        ArrayList<BookInfoList> bookList = new ArrayList<>();
        bookList.add(new BookInfoList("Upload By", addBookBasicData.getUserEmail()));
        bookList.add(new BookInfoList(getString(R.string.description), addBookBasicData.getDescription()));
        bookList.add(new BookInfoList(getString(R.string.classify), addBookBasicData.getClassify()));
        bookList.add(new BookInfoList(getString(R.string.qty), addBookBasicData.getQty()+" 本"));
        bookList.add(new BookInfoList(getString(R.string.unitPrice), "NTD "+ addBookBasicData.getUnitPrice()));
        bookList.add(new BookInfoList(getString(R.string.totalPrice), "NTD "+ addBookBasicData.getTotalPrice()));
        bookList.add(new BookInfoList(getString(R.string.status), addBookBasicData.getStatus()));
        bookList.add(new BookInfoList(getString(R.string.remark), addBookBasicData.getRemark()));

        if (user == null){
            userUid = "0";

        } else {
            userUid = user.getUid();
        }

        bookInfoAdapter.setUser(userUid); //把UID傳進adapter

        bookInfoAdapter.setListener(new BookInfoAdapter.OnClickListener() {
            @Override
            public void onClickCart() {

                if (mAuth.getCurrentUser() == null){
                    showHint("請先登入會員");
                    return;
                }

                showCartView(addBookBasicData);
            }

            @Override
            public void onClickFavorite() {

                if (mAuth.getCurrentUser() == null){
                    showHint("請先登入會員");
                    return;
                }

                addToFavorite(addBookBasicData);
            }

            @Override
            public void onClickEdit() {

                //換頁至編輯頁面
                Intent intent = new Intent(BookInfoActivity.this,EditBookInfo.class);
                intent.putExtra(ADD_BOOK_BASIC_DATA, addBookBasicData);
                startActivity(intent);

                finish();
            }

            @Override
            public void onClickSendProfile() {

                if (!addBookBasicData.getUploaderUid().equals(user.getUid())){
                    Log.i("Joyce","BookInfo other uid : "+addBookBasicData.getUploaderUid());

                    Intent intent = new Intent(BookInfoActivity.this, AllProfileActivity.class);
                    intent.putExtra(BOOK_UPLOADER_UID,addBookBasicData.getUploaderUid());
                    startActivity(intent);

                } else {
                    showHint("此為使用者本人帳號");
                   return;
                }
            }
        });

        bookInfoAdapter.setAddBookBasicData(addBookBasicData);
        bookInfoAdapter.setBookList(bookList);
        recyclerViewBookInfo.setAdapter(bookInfoAdapter);
    }

    private void addToFavorite(AddBookBasicData bookDataObject) {

        bookDataObject.setMyUid(user.getUid());

        ApiTool.getRequestApi()
                .addFavorite(bookDataObject)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddBookBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull AddBookBasicData addBookBasicData) {
                        showHint("已新增至我的最愛");
                        JoyceLog.i("BookInfoActivity | addToFavorite | 已新增至我的最愛");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("BookInfoActivity | addToFavorite | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("BookInfoActivity | addToFavorite | onComplete");
                    }
                });
    }

    private void showCartView(AddBookBasicData bookDataObject) {

        defaultQty = 1;

        CustomCartSelect customCartSelect = new CustomCartSelect();
        customCartSelect.showView(mask,root,this);
        customCartSelect.setTvName(bookDataObject.getBookName());
        customCartSelect.setTvUnitPrice(bookDataObject.getUnitPrice());
        customCartSelect.setIvCover(bookDataObject.getPhotoUrl());

        customCartSelect.setListener(new CustomCartSelect.OnButtonClickListener() {
            @Override
            public void onClickToCart(String qty) {

                qtyMax = Integer.parseInt(bookDataObject.getQty());
                bookDataObject.setMyUid(user.getUid());     //先設置myUid
                bookDataObject.setQty(qty);     //把書本的數量蓋掉
                addToCart(bookDataObject);
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

                qtyMax = Integer.parseInt(bookDataObject.getQty());

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
                        JoyceLog.i("BookInfoActivity | addToCart | 已新增至購物車");
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("BookInfoActivity | addToCart | Error: " + e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("BookInfoActivity | addToCart | onComplete");
                    }
                });
    }

    private void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void showHint(String content) {
        Toast.makeText(BookInfoActivity.this,content,Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        recyclerViewBookInfo = findViewById(R.id.recyclerview_bookInfo);
        recyclerViewBookInfo.setLayoutManager(new LinearLayoutManager(this));
        mask = findViewById(R.id.mask);
        root = findViewById(R.id.root);
        ivBack = findViewById(R.id.back);
        ivEdit = findViewById(R.id.edit);
        ivMessage = findViewById(R.id.message);
    }
}
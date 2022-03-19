package com.book.second_book_exchange;

import static com.book.second_book_exchange.BookInfoActivity.BOOK_UPLOADER_UID;
import static com.book.second_book_exchange.fragment.HomeFragment.ADD_BOOK_BASIC_DATA;

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
import com.book.second_book_exchange.api.FollowData;
import com.book.second_book_exchange.api.ResponseData;
import com.book.second_book_exchange.api.UserAllInformation;
import com.book.second_book_exchange.log.JoyceLog;
import com.book.second_book_exchange.recyclerview.AllProfileAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.book.second_book_exchange.R;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AllProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ImageView ivBack;
    private TextView tvUserID;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ArrayList<AddBookBasicData> bookDataArr;
    private ArrayList<AddBookBasicData> bookListArray;
    private String bookUploadUid;
    private UserAllInformation userDataObject;
    private UserBasicData userData;
    private CompositeDisposable compositeDisposable;
    private AllProfileAdapter profileAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        compositeDisposable = new CompositeDisposable();
        initView();

        //接收值: uid ============================================
        Bundle bundle = getIntent().getExtras();

        if (bundle == null) {
            showHint("查無書本資料");
            return;
        }

        bookUploadUid = bundle.getString(BOOK_UPLOADER_UID,"");
        //=======================================================

        profileAdapter = new AllProfileAdapter();
        profileAdapter.setBtnText("追蹤");

        searchUserData(bookUploadUid);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        searchUserData(bookUploadUid);
    }

    private void searchUserData(String bookUploadUid) {

        userData = new UserBasicData();
        userData.setUserUid(bookUploadUid);
        userData.setEmail(user.getUid());   //放入user uid

        ApiTool.getRequestApi()
                .searchOtherUserAll(userData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserAllInformation>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull UserAllInformation userAllInformation) {

                        JoyceLog.i("AllProfileActivity | searchUserAll | userAllInformation.getFollow: "+userAllInformation.getFollow());

                        tvUserID.setText(userAllInformation.getEmail());

                        bookDataArr = userAllInformation.getBookList();

                        profileAdapter.setUserAllInformation(userAllInformation);
                        profileAdapter.setBookInfoArray(bookDataArr);
                        recyclerView.setAdapter(profileAdapter);

                        checkFollowStatus();

                        profileAdapter.setListener(new AllProfileAdapter.OnClickListener() {
                            @Override
                            public void onClickMsgCount(AddBookBasicData addBookBasicData) {
                                Intent intent = new Intent(AllProfileActivity.this,PublicMessage.class);
                                intent.putExtra(ADD_BOOK_BASIC_DATA,addBookBasicData);
                                startActivity(intent);
                            }

                            @Override
                            public void onClickIvChat(AddBookBasicData addBookBasicData) {
                                Intent intent = new Intent(AllProfileActivity.this,PublicMessage.class);
                                intent.putExtra(ADD_BOOK_BASIC_DATA,addBookBasicData);
                                startActivity(intent);
                            }

                            @Override
                            public void onClickFollow(AddBookBasicData addBookBasicData) {

                                profileAdapter.setBtnText("追蹤中");
                                profileAdapter.notifyDataSetChanged();

                                FollowData followData = new FollowData();
                                followData.setMyUid(user.getUid());
                                followData.setTargetUid(addBookBasicData.getUploaderUid());

                                ApiTool.getRequestApi()
                                        .sendFollow(followData)
                                        .subscribeOn(Schedulers.io())
                                        .observeOn(AndroidSchedulers.mainThread())
                                        .subscribe(new Observer<ResponseData>() {
                                            @Override
                                            public void onSubscribe(@NonNull Disposable d) {
                                                compositeDisposable.add(d);
                                            }

                                            @Override
                                            public void onNext(@NonNull ResponseData responseData) {

                                                if(responseData.getResult() == 404){
                                                    showHint("追蹤失敗");
                                                    return;
                                                }

                                                profileAdapter.setBtnText("追蹤中");
                                                searchUserData(addBookBasicData.getUploaderUid());    //重抓資料
                                            }

                                            @Override
                                            public void onError(@NonNull Throwable e) {
                                                JoyceLog.i("AllProfileActivity | sendFollow | Error: "+e);
                                            }

                                            @Override
                                            public void onComplete() {
                                                JoyceLog.i("AllProfileActivity | sendFollow | onComplete");
                                            }
                                        });
                            }

                            @Override
                            public void onClickChat(AddBookBasicData addBookBasicData) {

                                Intent intent = new Intent(AllProfileActivity.this,ChatRoomActivity.class);
                                intent.putExtra("myUid",user.getUid());
                                intent.putExtra("otherUid",addBookBasicData.getUploaderUid());
                                startActivity(intent);
                            }

                            @Override
                            public void onClickLove(AddBookBasicData addBookBasicData) {

                                for (AddBookBasicData bookData : bookDataArr){

                                    if(bookData.getBookName().equals(addBookBasicData.getBookName())){

                                        bookData.setMyUid(user.getUid());

                                        addBookBasicData.setMyUid(user.getUid());
                                        if (!addBookBasicData.isSelectHeart()){
                                            JoyceLog.i("addFavorite");
                                            addToFavorite(bookData);

                                        }else{
                                            JoyceLog.i("deleteFavorite");
                                            deleteFavorite(bookData);
                                        }
                                    }
                                }
                            }
                        });
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("AllProfileActivity | searchUserAll | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("AllProfileActivity | searchUserAll | onComplete");
                    }
                });
    }

    private void deleteFavorite(AddBookBasicData bookData) {

        bookData.setMyUid(user.getUid());
        ApiTool.getRequestApi()
                .deleteFavorite(bookData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddBookBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull AddBookBasicData addBookBasicData) {

                        for (AddBookBasicData bookDataObject : bookDataArr){

                            if (bookDataObject.getBookName().equals(addBookBasicData.getBookName())){
                                bookDataObject.setSelectHeart(false);
                            }
                        }

                        showHint("已從我的最愛中移除");
                        profileAdapter.setBookInfoArray(bookDataArr);
                        profileAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("MemberFragment | deleteFavorite | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("MemberFragment | deleteFavorite | onComplete");
                    }
                });
    }

    private void addToFavorite(AddBookBasicData bookData) {

        bookData.setMyUid(user.getUid());
        ApiTool.getRequestApi()
                .addFavorite(bookData)
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

                        for (AddBookBasicData bookDataObject : bookDataArr){

                            if (bookDataObject.getBookName().equals(addBookBasicData.getBookName())){
                                bookDataObject.setSelectHeart(true);
                            }
                        }

                        profileAdapter.setBookInfoArray(bookDataArr);
                        profileAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("MemberFragment | addFavorite | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("MemberFragment | addFavorite | onComplete");
                    }
                });
    }

    //確認追蹤關係
    private void checkFollowStatus() {

        FollowData followData = new FollowData();
        followData.setMyUid(user.getUid());
        followData.setTargetUid(bookUploadUid);

        ApiTool.getRequestApi()
                .isFriend(followData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ResponseData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ResponseData responseData) {

                        if (responseData.getResult() == 404){
                            showHint("發生錯誤");
                            return;

                        } else{
                            if (responseData.getMessage().equals("Is friend")){
                                profileAdapter.setBtnText("追蹤中");
                                profileAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("AllProfileActivity | isFriend | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("AllProfileActivity | isFriend | onComplete");
                    }
                });
    }

    private void showHint(String content) {
        Toast.makeText(AllProfileActivity.this, content, Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        recyclerView = findViewById(R.id.recyclerview_profile);
        ivBack = findViewById(R.id.back);
        tvUserID = findViewById(R.id.user_id);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
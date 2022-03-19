package com.book.second_book_exchange;

import static com.book.second_book_exchange.fragment.HomeFragment.ADD_BOOK_BASIC_DATA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.book.second_book_exchange.api.ApiTool;
import com.book.second_book_exchange.api.ResponseData;
import com.book.second_book_exchange.log.JoyceLog;
import com.book.second_book_exchange.recyclerview.PublicMessageAdapter;
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

public class PublicMessage extends AppCompatActivity {

    private ImageView ivBack;
    private EditText edMessage;
    private TextView tvPost, tvPageBookName;
    private RecyclerView recyclerView;
    private PublicMessageAdapter msgAdapter;
    private AddBookBasicData addBookBasicData;
    private CompositeDisposable compositeDisposable;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_message);

        compositeDisposable = new CompositeDisposable();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        initView();


        //================================================
        Bundle bundle = getIntent().getExtras();

        if (bundle == null){
            showHint("查無資料");
            return;
        }

        addBookBasicData = (AddBookBasicData) bundle.getSerializable(ADD_BOOK_BASIC_DATA);
        //================================================

        showAllMsg(addBookBasicData);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = edMessage.getText().toString();

                if (msg.isEmpty()){
                    showHint("查無留言內容");
                    return;
                }

                //先設置msgData的資料
                PublicMessageData msgData = new PublicMessageData();
                msgData.setUidForLeftMsg(user.getUid());
                msgData.setMsg(msg);
                msgData.setBookName(addBookBasicData.getBookName());
                msgData.setUploaderUid(addBookBasicData.getUploaderUid());
                msgData.setBookId(addBookBasicData.getId());

                ApiTool.getRequestApi()
                        .addMessage(msgData)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<ResponseData>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {
                                compositeDisposable.add(d);
                            }

                            @Override
                            public void onNext(@NonNull ResponseData responseData) {

                                edMessage.setText("");

                                JoyceLog.i("PublicMessage | addMessage | responseData: "+new Gson().toJson(responseData));

                                if (responseData.getResult() == 200){
                                    showAllMsg(addBookBasicData);

                                } else {
                                    showHint("Error");
                                }
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                JoyceLog.i("PublicMessage | addMessage | Error: "+e);
                            }

                            @Override
                            public void onComplete() {
                                JoyceLog.i("PublicMessage | addMessage | onComplete");
                            }
                        });
            }
        });
    }

    private void showAllMsg(AddBookBasicData addBookBasicData) {

        tvPageBookName.setText(addBookBasicData.getBookName());

        ApiTool.getRequestApi()
                .getAllMsgList(addBookBasicData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<PublicMessageData>>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull ArrayList<PublicMessageData> publicMessageData) {
                        msgAdapter = new PublicMessageAdapter();
                        msgAdapter.setMessageDataArr(publicMessageData);
                        recyclerView.setAdapter(msgAdapter);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("PublicMessage | getAllMsgList | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("PublicMessage | getAllMsgList | onComplete");
                    }
                });
    }

    private void showHint(String content) {
        Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        ivBack = findViewById(R.id.back);
        edMessage = findViewById(R.id.message);
        tvPost = findViewById(R.id.post);
        recyclerView = findViewById(R.id.recyclerview_public_message);
        tvPageBookName = findViewById(R.id.page_name);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        compositeDisposable.clear();
        msgAdapter.onDestroy();
    }
}
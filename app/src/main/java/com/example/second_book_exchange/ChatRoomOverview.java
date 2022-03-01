package com.example.second_book_exchange;

import static com.example.second_book_exchange.ChatRoomActivity.CHAT;
import static com.example.second_book_exchange.ChatRoomActivity.CHAT_MSG;
import static com.example.second_book_exchange.ChatRoomActivity.CHAT_ROOM;

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

import com.example.second_book_exchange.api.ApiTool;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.recyclerview.ChatRoomListAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatRoomOverview extends AppCompatActivity {

    private TextView tvMyNickName;
    private ImageView ivBack;
    private RecyclerView recyclerView;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private ArrayList<ChatRoom> chatRoomArr;
    private UserBasicData userBasicData;
    private CompositeDisposable compositeDisposable;
    private ArrayList<String> othersIdArr;
    private ArrayList<String> chatRoomIdList;
    private ArrayList<String> lastMsgList = new ArrayList<>();
    private int msgIndex = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room_overview);

        compositeDisposable = new CompositeDisposable();
        initFirebase();
        initView();

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

        //每次回來都要先把lastMsgList清除, 否則會壘加
        lastMsgList.clear();

        checkNickName();
        checkMyChatRoom();
    }

    private void checkNickName() {

        userBasicData = new UserBasicData();
        userBasicData.setUserUid(user.getUid());

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
                        tvMyNickName.setText(userBasicData.getNickName());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("ChatRoomOverview | checkUserData | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("ChatRoomOverview | checkUserData | onComplete");
                    }
                });
    }

    private void checkMyChatRoom() {

        db.collection(CHAT_ROOM).document(CHAT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if (!task.isSuccessful()){
                            JoyceLog.i("搜尋聊天紀錄失敗");
                            return;
                        }

                        DocumentSnapshot snapshot = task.getResult();

                        if (snapshot == null || snapshot.getData() == null){
                            showHint("查無聊天紀錄");
                            return;
                        }

                        String json = (String) snapshot.getData().get("json");

                        chatRoomArr = new Gson().fromJson(json,new TypeToken<ArrayList<ChatRoom>>(){}.getType());

                        String chatRoomId = "";
                        String otherId = "";
                        String myUid = user.getUid();
                        othersIdArr = new ArrayList<>();
                        chatRoomIdList = new ArrayList<>();

                        for (ChatRoom chatRoomObject : chatRoomArr){

                            //抓出有關自己的uid的聊天室
                            if (chatRoomObject.getChatRoomId().contains(myUid)){

                                chatRoomId = chatRoomObject.getChatRoomId();
                                chatRoomIdList.add(chatRoomId);
                                String[] idArr = chatRoomId.split(",");
                                if (idArr[0].equals(myUid)){
                                    otherId = idArr[1];

                                } else {
                                    otherId = idArr[0];
                                }

                                //這行打在外面會造成, 連不是使用者的聊天室的UID都會抓到,所以導致長度不一致
                                othersIdArr.add(otherId);
                            }

                        }

                        startToSearchLastMessage();
                    }
                });
    }

    private void startToSearchLastMessage() {

        //遞迴取出每個聊天室最後一筆的聊天內容
        if (msgIndex < chatRoomIdList.size()){

            String chatRoomId = chatRoomIdList.get(msgIndex);
            JoyceLog.i("chatRoomId : "+chatRoomId);
            db.collection(CHAT_MSG).document(chatRoomId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (!task.isSuccessful()){
                                JoyceLog.i("chat isNotSuccessful");
                                return;
                            }

                            if (task.getResult() == null || task.getResult().getData() == null){
                                JoyceLog.i("無法取得聊天資訊 task.getResult == null ");
                                return;
                            }

                            DocumentSnapshot snapshot = task.getResult();

                            String json = (String) snapshot.getData().get("json");

                            ArrayList<MessageData> msgList = new Gson().fromJson(json,new TypeToken<ArrayList<MessageData>>(){}.getType());

                            JoyceLog.i("last msg : "+msgList.get(msgList.size() - 1).getMsg());
                                               //取出最後一個位置的值
                            MessageData data = msgList.get(msgList.size() - 1);

                            //放入lastMsgArrayList
                            lastMsgList.add(data.getMsg());



                            msgIndex ++;
                            startToSearchLastMessage();
                        }
                    });

        }else {

            msgIndex = 0;

            JoyceLog.i("id List size : "+othersIdArr.size() +" msgList size : "+lastMsgList.size());
            //按了

            ChatRoomListAdapter roomListAdapter = new ChatRoomListAdapter();
            roomListAdapter.setOtherIdArr(othersIdArr);
            roomListAdapter.setLastMsgList(lastMsgList);
            roomListAdapter.setListener(new ChatRoomListAdapter.OnClickListener() {
                @Override
                public void onClickChatRoom(String otherId) {
                    Intent intent = new Intent(ChatRoomOverview.this,ChatRoomActivity.class);
                    intent.putExtra("myUid",user.getUid());
                    intent.putExtra("otherUid",otherId);
                    startActivity(intent);
                }
            });

            recyclerView.setAdapter(roomListAdapter);
        }
    }

    private void showHint(String content) {
        Toast.makeText(ChatRoomOverview.this,content,Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        tvMyNickName = findViewById(R.id.user_id);
        ivBack = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyclerview_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
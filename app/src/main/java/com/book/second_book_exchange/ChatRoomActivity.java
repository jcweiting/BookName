package com.book.second_book_exchange;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.book.second_book_exchange.api.ApiTool;
import com.book.second_book_exchange.log.JoyceLog;
import com.book.second_book_exchange.recyclerview.ChatRoomAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.book.second_book_exchange.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class ChatRoomActivity extends AppCompatActivity {

    public static final String CHAT_ROOM = "CHAT_ROOM";
    public static final String CHAT = "CHAT";
    public static final String CHAT_MSG = "CHAT_MSG";
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private TextView tvOtherEmail, tvSendMsg;
    private ImageView ivBack;
    private RecyclerView recyclerView;
    private EditText edEnterMsg;
    private String myUid, otherUid, chatRoomId;
    private ArrayList<MessageData> messageList;
    private ChatRoomAdapter chatAdapter;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        Bundle bundle = getIntent().getExtras();

        if (bundle == null){
            JoyceLog.i("bundle是null");
            return;
        }

        myUid = bundle.getString("myUid","");
        otherUid = bundle.getString("otherUid","");
        JoyceLog.i("ChatRoomActivity | myUid: "+myUid + " / otherUid: "+otherUid);

        initFirebase();
        initView();
        checkSenderEmail(otherUid);
    }

    @Override
    protected void onResume() {
        super.onResume();

        //從這邊開始
        showMessageList();
    }

    private void showMessageList() {

        db.collection(CHAT_ROOM).document(CHAT)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (!task.isSuccessful()){
                            showHint("聊天紀錄取得失敗");
                            return;
                        }

                        DocumentSnapshot snapshot = task.getResult();

                        //搜尋結果是空的(沒有路徑的存在),就創立一個新的聊天室
                        if (snapshot == null || snapshot.getData() == null){
                            JoyceLog.i("ChatRoomActivity | showMessageList | 沒有路徑的存在,創立一個新的聊天室");

                            //創立新的聊天室的roomId
                            String chatRoomId = myUid+","+ otherUid;
                            createNewChatRoom(chatRoomId);
                            return;
                        }

                        String json = (String) snapshot.getData().get("json");

                        //把所有人的聊天室id集中管理在ChatRoom資料夾
                        ArrayList<ChatRoom> chatRoomArr = new Gson().fromJson(json,new TypeToken<ArrayList<ChatRoom>>(){}.getType());

                        chatRoomId = "";

                        for (ChatRoom chatRoom : chatRoomArr){

                            if (chatRoom.getChatRoomId().equals(myUid+","+otherUid) || chatRoom.getChatRoomId().equals(otherUid+","+myUid)){
                                chatRoomId = chatRoom.getChatRoomId();
                                break;
                            }
                        }

                        //有路徑資料夾,但是沒有chatRoomId,新創一個
                        if (chatRoomId.isEmpty()){
                            String roomId = myUid+","+otherUid;
                            JoyceLog.i("ChatRoomActivity | showMessageList | otherUid: "+otherUid);

                            //如果資料夾內沒有聊天紀錄的id,就創一個新的
                            createOurChatRoom(chatRoomArr,roomId);
                            return;
                        }

                        JoyceLog.i("ChatRoomActivity | showMessageList | 成功找到聊天室: "+chatRoomId);

                        //開始尋找舊有的聊天紀錄
                        searchMessageHistory(chatRoomId);
                    }
                });
    }

    private void searchMessageHistory(String chatRoomId) {

        //雙向回傳
        DocumentReference document = db.collection(CHAT_MSG).document(chatRoomId);
        document.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null){
                    JoyceLog.i("ChatRoomActivity | searchMessageHistory | Error: "+ error.toString());
                    return;
                }

                //如果沒資料
                if (value == null || !value.exists()){
                    JoyceLog.i("ChatRoomActivity | searchMessageHistory | 沒資料");
                    return;
                }

                String json = (String) value.get("json");

                messageList = new Gson().fromJson(json, new TypeToken<ArrayList<MessageData>>(){}.getType());

                chatAdapter = new ChatRoomAdapter();
                chatAdapter.setMyUid(myUid);
                chatAdapter.setOtherUid(otherUid);
                chatAdapter.setMessageDataArr(messageList);
                recyclerView.setAdapter(chatAdapter);
            }
        });
    }

    private void createOurChatRoom(ArrayList<ChatRoom> chatRoomArr, String roomId) {

        this.chatRoomId = roomId;

        ChatRoom chatRoom = new ChatRoom();
        chatRoom.setChatRoomId(roomId);

        chatRoomArr.add(chatRoom);

        Map<String,String> map = new HashMap<>();
        map.put("json",new Gson().toJson(chatRoomArr));
        db.collection(CHAT_ROOM).document(CHAT).set(map,SetOptions.merge());

        //開始尋找舊有的聊天紀錄
        searchMessageHistory(chatRoomId);
    }

    private void createNewChatRoom(String chatRoomId) {

        //因為全域變數跟區域變數有相同的變數名稱,所以要打"this",來辨別它是全域變數(紫色)
        this.chatRoomId = chatRoomId;

        ArrayList<ChatRoom> chatRoomArr = new ArrayList<>();

        ChatRoom chatRoom = new ChatRoom(chatRoomId);     //把id set至ChatRoom
        chatRoomArr.add(chatRoom);      //把物件加入arrayList

        //上傳聊天紀錄
        Map<String,String> map = new HashMap<>();
        map.put("json",new Gson().toJson(chatRoomArr));
        db.collection(CHAT_ROOM).document(CHAT).set(map, SetOptions.merge());

        //監聽聊天紀錄
        searchMessageHistory(chatRoomId);
    }

    private void checkSenderEmail(String otherUid) {

        UserBasicData userBasicData = new UserBasicData();
        userBasicData.setUserUid(otherUid);

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
                        tvOtherEmail.setText(userBasicData.getEmail());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("ChatRoomActivity | checkUserData | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("ChatRoomActivity | checkUserData | onComplete");
                    }
                });
    }

    private void showHint(String content) {
        Toast.makeText(ChatRoomActivity.this,content,Toast.LENGTH_SHORT).show();
    }

    private void initView() {
        tvOtherEmail = findViewById(R.id.user_id);
        tvSendMsg = findViewById(R.id.send);
        ivBack = findViewById(R.id.back);
        recyclerView = findViewById(R.id.recyclerview_chat);
        edEnterMsg = findViewById(R.id.enter_message);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvSendMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String msg = edEnterMsg.getText().toString();

                if (msg.isEmpty()){
                    return;
                }

                sendMessage(msg);
                edEnterMsg.setText("");
                hideKeyboard(edEnterMsg.getWindowToken());
            }
        });
    }

    private void hideKeyboard(IBinder windowToken) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void sendMessage(String msg) {

        //如果沒有資料的話new出一個新的
        if (messageList == null){
            messageList = new ArrayList<>();
        }

        //把msg設定到物件裡
        MessageData msgData = new MessageData();
        msgData.setSenderUid(myUid);
        msgData.setMsg(msg);
        msgData.setTime(System.currentTimeMillis());

        //把聊天內容(物件) 新增到總聊天
        messageList.add(msgData);

        //上傳聊天紀錄到firebase
        Map<String,String> map = new HashMap<>();
        map.put("json",new Gson().toJson(messageList));
        db.collection(CHAT_MSG).document(chatRoomId).set(map , SetOptions.merge());
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
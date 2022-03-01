package com.example.second_book_exchange.fragment;

import static com.example.second_book_exchange.fragment.HomeFragment.ADD_BOOK_BASIC_DATA;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.IBinder;
import android.os.Parcelable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.second_book_exchange.AccountInfo;
import com.example.second_book_exchange.AddBookBasicData;
import com.example.second_book_exchange.BookInfoActivity;
import com.example.second_book_exchange.ChatRoomActivity;
import com.example.second_book_exchange.ChatRoomOverview;
import com.example.second_book_exchange.EditProfileActivity;
import com.example.second_book_exchange.MyOrders;
import com.example.second_book_exchange.PublicMessage;
import com.example.second_book_exchange.R;
import com.example.second_book_exchange.RegisterActivity;
import com.example.second_book_exchange.UserBasicData;
import com.example.second_book_exchange.api.ApiTool;
import com.example.second_book_exchange.api.UserAllInformation;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.recyclerview.ProfileAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.util.ArrayList;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class MemberFragment extends Fragment {

    public static final int RC_SIGN_IN = 9001;
    public static final String TAG = "Joyce";
    public static final String USER = "user";
    public static final String USER_BASIC_DATA = "UserBasicData";

    private GoogleSignInButton googleSignIn;
    private FirebaseFirestore db;
    private ConstraintLayout loginPage, afterLogin;
    private UserBasicData data;

    private RecyclerView recyclerViewProfile;
    private ImageView ivSubmit, ivClear, ivLogout, ivProfilePic;
    private TextView tvRegister, tvUserid, tvEditProfile, tvLogin;
    private EditText edAccount, edPassword;
    private TextInputLayout tilAccount, tilPassword;
    private UserBasicData userData;
    private ArrayList<AddBookBasicData> bookArray;
    private ProfileAdapter profileAdapter;

    private CompositeDisposable compositeDisposable;

    /**
     * Step 3 初始化
     */
    //Google Authorization================================
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    //====================================================

    public static MemberFragment newInstance() {

        MemberFragment fragment = new MemberFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        compositeDisposable = new CompositeDisposable();
        googleSignInInitView();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        ivLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Firebase登出
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getContext(), "成功登出", Toast.LENGTH_SHORT).show();

                //Google sign out
                mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if (task.isSuccessful()) {
                            Log.i("Joyce", "登出成功");
                            user = mAuth.getCurrentUser();
                            updateUI(user);
                            return;
                        }

                        Log.i("Joyce", "登出失敗");
                    }
                });

                //確認這個使用者是否存在
                FirebaseUser user = mAuth.getCurrentUser();
                //呼叫更新畫面的方法
                updateUI(user);

            }
        });
    }

    /**
     * Step 4 初始化
     */
    private void googleSignInInitView() {

        //Google Authorization
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    /**
     * Step 1 綁定 UI
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //綁定View, fragment綁定layout
        View view = inflater.inflate(R.layout.fragment_member, container, false);
        initView(view);
        initHandler();
        return view;
    }

    //判斷是否有登入過
    @Override
    public void onResume() {
        super.onResume();

        user = mAuth.getCurrentUser();
        updateUI(user);
    }

    /**
     * Step 2
     */
    private void initHandler() {

        //GOOGLE登入鈕點擊事件
        googleSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                signIn();
            }
        });

        tvLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String account = edAccount.getText().toString();
                String password = edPassword.getText().toString();

                boolean isEmpty = false;

                if (TextUtils.isEmpty(account) && TextUtils.isEmpty(password)) {
                    tilAccount.setError("請輸入帳號");
                    tilPassword.setError("請輸入密碼");
                    isEmpty = true;

                }

                if (TextUtils.isEmpty(account)) {
                    tilAccount.setError("請輸入帳號");
                    isEmpty = true;

                } else {
                    tilAccount.setError(null);
                }

                if (TextUtils.isEmpty(password)) {
                    tilPassword.setError("請輸入密碼");
                    isEmpty = true;

                } else {
                    tilPassword.setError(null);
                }

                if (isEmpty) {
                    return;
                }

                mAuth.signInWithEmailAndPassword(account, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                if (task.isSuccessful()) {
                                    Log.i("Joyce", "成功登錄");
                                    updateUI(user = mAuth.getCurrentUser());

                                } else {
                                    Log.i("Joyce", "登錄失敗" + task.getException());
                                    Toast.makeText(getContext(), "帳號或密碼不正確", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                hideKeyboard(edAccount.getWindowToken());
                hideKeyboard(edPassword.getWindowToken());
            }
        });
    }

    private void hideKeyboard(IBinder windowToken) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(windowToken, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * Step 5 點擊後換頁登入, 跳出Google帳號選擇頁面
     */
    private void signIn() {

        Intent singInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(singInIntent, RC_SIGN_IN);
    }

    /**
     * Step 6 開始比對Google帳號
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);

            try {

                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());

                //用google帳號,跟firebase註冊
                firebaseAuthWithGoogle(account.getIdToken());

            } catch (ApiException e) {

                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e);
            }
        }

    }

    /**
     * Step 7 Google登入成功後, Google在資料庫中建立一個帳號(尚未有使用者個人資料)
     */
    private void firebaseAuthWithGoogle(String idToken) {

        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);

        //把登入的gmail丟入firebase的專案裡, 去註冊
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        //成功登入後, 資料庫會新增此使用者
                        if (task.isSuccessful()) {

                            Log.d(TAG, "signInWithCredential: success");

                            //取得資料庫的帳號
                            user = mAuth.getCurrentUser();

                            //在資料庫創建帳號後,要判斷資料庫是否有此使用者資料
                            startToCheckUserDataInDataBse(user);

                        } else {

                            //登入失敗, 不顯示這個
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void startToCheckUserDataInDataBse(FirebaseUser user) {

        data = new UserBasicData();

        //一剛開始只會取得uid & Email,所以先從uid開始做
        data.setUserUid(user.getUid());
        data.setEmail(user.getEmail());

        ApiTool.getRequestApi()
                .checkUserData(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull UserBasicData userBasicData) {

                        userData = userBasicData;
                        updateUI(user);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("MemberFragment | checkUserData | Error: " + e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("MemberFragment | checkUserData | onComplete");
                    }
                });
    }

    // 登入成功後, 畫面導向
    private void updateUI(FirebaseUser user) {

        if (user == null) {
            //沒有登入的話走這
            loginPage.setVisibility(View.VISIBLE);
            afterLogin.setVisibility(View.GONE);
            tvUserid.setText("");
            return;
        }

        //有登入的話走這
        loginPage.setVisibility(View.GONE);
        afterLogin.setVisibility(View.VISIBLE);

        searchUserData();
    }

    private void searchUserData() {

        data = new UserBasicData();

        //一剛開始只會取得uid & Email,所以先從uid開始做
        //後台會根據uid去找會員資料
        data.setUserUid(user.getUid());
        data.setEmail(user.getEmail());

        ApiTool.getRequestApi()
                .searchUserAll(data)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserAllInformation>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull UserAllInformation userAllInformation) {
                        JoyceLog.i("MemberFragment | searchUserAll | userAllInformation: " + new Gson().toJson(userAllInformation));
                        bookArray = userAllInformation.getBookList();

                        recyclerViewProfile.setLayoutManager(new LinearLayoutManager(getContext()));

                        profileAdapter = new ProfileAdapter();
                        profileAdapter.setUserAllInformation(userAllInformation);
                        profileAdapter.setAddBookBasicData(bookArray);

                        profileAdapter.setListener(new ProfileAdapter.OnClickEditProfile() {
                            @Override
                            public void onClickEdit() {

                                Intent intent = new Intent(getContext(), EditProfileActivity.class);
                                intent.putExtra("userDataObject", user.getUid());
                                startActivity(intent);
                            }

                            @Override
                            public void onClickOrder() {

                                Intent intent = new Intent(getContext(),MyOrders.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onClickMoney() {

                                //先把值丟進userData再換頁=============================================
                                userData = new UserBasicData();

                                userData.setUserUid(userAllInformation.getUserUid());
                                userData.setNickName(userAllInformation.getNickName());
                                userData.setAccount(userAllInformation.getAccount());
                                userData.setTel(userAllInformation.getTel());
                                userData.setEmail(userAllInformation.getEmail());
                                userData.setUserPhotoUrl(userAllInformation.getUserPhotoUrl());
                                userData.setFollow(userAllInformation.getFollow());
                                userData.setFollower(userAllInformation.getFollower());
                                userData.setBookCount(userAllInformation.getBookCount());
                                userData.setBankCode(userAllInformation.getBankCode());
                                userData.setBankAccount(userAllInformation.getBankAccount());
                                userData.setBankName(userAllInformation.getBankName());

                                Intent intent = new Intent(getContext(), AccountInfo.class);
                                intent.putExtra(USER_BASIC_DATA, userData);
                                startActivity(intent);

                                //===================================================================
                            }

                            @Override
                            public void onClickChatList() {

                                Intent intent = new Intent(getContext(), ChatRoomOverview.class);
                                startActivity(intent);
                            }

                            @Override
                            public void onClickFavorite(AddBookBasicData addBookBasicData) {

                                for (AddBookBasicData bookBasicData : bookArray){

                                    if (addBookBasicData.getBookName().equals(bookBasicData.getBookName())){
                                        JoyceLog.i("MemberFragment | bookBasicData.getBookName(): "+bookBasicData.getBookName());
                                        JoyceLog.i("MemberFragment | addBookBasicData.getBookName(): "+addBookBasicData.getBookName());

                                        bookBasicData.setMyUid(user.getUid());
                                        if (!addBookBasicData.isSelectHeart()){
                                            JoyceLog.i("addFavorite");
                                            addToFavorite(bookBasicData);

                                        }else{
                                            JoyceLog.i("deleteFavorite");
                                            deleteFavorite(bookBasicData);
                                        }
                                    }
                                }
                            }

                            @Override
                            public void onClickIvChat(AddBookBasicData addBookBasicData) {
                                Intent intent = new Intent(getContext(), PublicMessage.class);
                                intent.putExtra(ADD_BOOK_BASIC_DATA, addBookBasicData);
                                startActivity(intent);
                            }

                            @Override
                            public void onClickToMessage(AddBookBasicData addBookBasicData) {

                                Intent intent = new Intent(getContext(), PublicMessage.class);
                                intent.putExtra(ADD_BOOK_BASIC_DATA, addBookBasicData);
                                startActivity(intent);
                            }

                            @Override
                            public void onClickChangePage(AddBookBasicData addBookBasicData) {
                                Intent intent = new Intent(getContext(), BookInfoActivity.class);
                                intent.putExtra(ADD_BOOK_BASIC_DATA, addBookBasicData);
                                startActivity(intent);
                            }
                        });

                        recyclerViewProfile.setAdapter(profileAdapter);
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("MemberFragment | searchUserAll | Error: " + e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("MemberFragment | searchUserAll | onComplete");
                    }
                });
    }

    private void deleteFavorite(AddBookBasicData bookBasicData) {

        bookBasicData.setMyUid(user.getUid());
        ApiTool.getRequestApi()
                .deleteFavorite(bookBasicData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddBookBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull AddBookBasicData addBookBasicData) {

                        for (AddBookBasicData bookDataObject : bookArray){

//                            JoyceLog.i("MemberFragment | bookDataObject: "+new Gson().toJson(bookDataObject));

                            if (bookDataObject.getBookName().equals(addBookBasicData.getBookName())){
                                bookDataObject.setSelectHeart(false);
                            }
                        }

                        showHint("已從我的最愛中移除");
                        profileAdapter.setAddBookBasicData(bookArray);
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

    private void addToFavorite(AddBookBasicData bookBasicData) {

        bookBasicData.setMyUid(user.getUid());
        ApiTool.getRequestApi()
                .addFavorite(bookBasicData)
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

                        for (AddBookBasicData bookDataObject : bookArray){

                            if (bookDataObject.getBookName().equals(addBookBasicData.getBookName())){
                                bookDataObject.setSelectHeart(true);
                            }
                        }

                        profileAdapter.setAddBookBasicData(bookArray);
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

    private void showHint(String content) {
        Toast.makeText(getContext(),content,Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void initView(View view) {
        googleSignIn = view.findViewById(R.id.google_signin);
        loginPage = view.findViewById(R.id.login_page);
        afterLogin = view.findViewById(R.id.after_login_page);
        ivSubmit = view.findViewById(R.id.submit);
        ivClear = view.findViewById(R.id.clear);
        ivLogout = view.findViewById(R.id.logout);
        ivProfilePic = view.findViewById(R.id.profile_picture);
        tvRegister = view.findViewById(R.id.register_text);
        tvUserid = view.findViewById(R.id.user_id);
        tvEditProfile = view.findViewById(R.id.edit_profile);
        tvLogin = view.findViewById(R.id.login);
        edAccount = view.findViewById(R.id.account_input);
        edPassword = view.findViewById(R.id.password_input);
        tilAccount = view.findViewById(R.id.account_layout);
        tilPassword = view.findViewById(R.id.password_layout);
        recyclerViewProfile = view.findViewById(R.id.recyclerview_profile);

        recyclerViewProfile.setLayoutManager(new LinearLayoutManager(getContext()));

        tvRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });
    }
}

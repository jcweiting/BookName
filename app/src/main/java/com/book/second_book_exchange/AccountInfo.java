package com.book.second_book_exchange;

import static com.book.second_book_exchange.fragment.MemberFragment.USER_BASIC_DATA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.book.second_book_exchange.api.ApiTool;
import com.book.second_book_exchange.log.JoyceLog;
import com.book.second_book_exchange.tool.ViewDialog;
import com.google.gson.Gson;
import com.book.second_book_exchange.R;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class AccountInfo extends AppCompatActivity {

    private EditText edBankCode, edBankAccount, edBandName;
    private ImageView ivClear, ivSubmit, ivBack;
    private CompositeDisposable compositeDisposable;
    private UserBasicData userBasicData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_info);

        compositeDisposable = new CompositeDisposable();
        initView();


        //接收值=============================================================
        Bundle bundle = getIntent().getExtras();

        if (bundle == null){
            showHint("查無使用者相關資料");
            return;
        }

        userBasicData = (UserBasicData) bundle.getSerializable(USER_BASIC_DATA);
        //===================================================================

        showUserData(userBasicData);

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog("是否確認要清除所有資料");
            }
        });

        ivSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                checkData();
            }
        });
    }

    private void showUserData(UserBasicData userBasicData) {
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
                        edBankCode.setText(userBasicData.getBankCode() == null ? "" : userBasicData.getBankCode());
                        edBankAccount.setText(userBasicData.getBankAccount() == null ? "" : userBasicData.getBankAccount());
                        edBandName.setText(userBasicData.getBankName() == null ? "" : userBasicData.getBankName());
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("AccountInfo | checkUserData | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("AccountInfo | checkUserData | onComplete");
                    }
                });
    }

    private void checkData() {

        String code = edBankCode.getText().toString();
        String name = edBandName.getText().toString();
        String account = edBankAccount.getText().toString();

        if (code.isEmpty() || name.isEmpty() || account.isEmpty()){
            showHint("請輸入相關資訊");
            return;
        }

        userBasicData.setBankCode(code);
        userBasicData.setBankAccount(account);
        userBasicData.setBankName(name);

        uploadPersonalInfo(userBasicData);
    }

    private void uploadPersonalInfo(UserBasicData userBasicData) {

        ApiTool.getRequestApi()
                .editUserData(userBasicData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UserBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull UserBasicData userBasicData) {
                        JoyceLog.i(userBasicData.getBankCode());
                        JoyceLog.i(userBasicData.getBankAccount());
                        JoyceLog.i("AccountInfo | userBasicData: "+new Gson().toJson(userBasicData));
                        finish();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        JoyceLog.i("AccountInfo | editUserData | Error: "+e);
                    }

                    @Override
                    public void onComplete() {
                        JoyceLog.i("AccountInfo | editUserData | onComplete");
                    }
                });
    }

    private void showHint(String content) {
        Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
    }

    private void showAlertDialog(String content) {

        ViewDialog alert = new ViewDialog();
        alert.showDialog(AccountInfo.this,content);
        alert.setOnAlertDialogClickListener(new ViewDialog.OnAlertDialogClickListener() {
            @Override
            public void onConfirm() {
                clearEd();
            }

            @Override
            public void onCancel() {

            }
        });
    }

    private void clearEd() {
        edBankCode.setText("");
        edBankAccount.setText("");
        edBandName.setText("");
    }

    private void initView() {
        edBankCode = findViewById(R.id.code);
        edBankAccount = findViewById(R.id.account);
        edBandName = findViewById(R.id.name);
        ivClear = findViewById(R.id.clear);
        ivSubmit = findViewById(R.id.submit);
        ivBack = findViewById(R.id.back);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }
}
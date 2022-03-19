package com.book.second_book_exchange;

import static com.book.second_book_exchange.fragment.HomeFragment.ADD_BOOK_BASIC_DATA;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.book.second_book_exchange.api.ApiTool;
import com.book.second_book_exchange.log.JoyceLog;
import com.book.second_book_exchange.tool.BookStatusDialog;
import com.book.second_book_exchange.tool.ClassifyDialog;
import com.book.second_book_exchange.tool.ImageLoaderProvider;
import com.book.second_book_exchange.tool.ViewDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.book.second_book_exchange.R;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class EditBookInfo extends AppCompatActivity implements View.OnFocusChangeListener {

    private FirebaseFirestore db;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private ConstraintLayout abNew, abEdit;
    private ImageView ivBack, ivSave, ivClear, ivAddPicture, ivAddBtn;
    private ImageView ivArrowName, ivArrowClassify, ivArrowDescription, ivArrowQty, ivArrowStatus, ivArrowRemark, ivArrowUnitP, ivArrowTotalP;
    private EditText edName, edClassify, edDescription, edQty, edStatus, edRemark, edUnitPrice, edTotalPrice;
    private AddBookBasicData addBookBasicData, newAddBookBasicData;
    private int qty, unitPrice;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        compositeDisposable = new CompositeDisposable();

        initFirebase();
        initView();

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //接收值(物件)===============================
        Bundle bundle = getIntent().getExtras();

        if (bundle == null){
            showHint("查無編輯資料");
            return;
        }

        addBookBasicData = (AddBookBasicData) bundle.getSerializable(ADD_BOOK_BASIC_DATA);
        //=========================================

        searchOriginalData(addBookBasicData);

        edName.setOnFocusChangeListener(this);
        edDescription.setOnFocusChangeListener(this);
        edQty.setOnFocusChangeListener(this);
        edUnitPrice.setOnFocusChangeListener(this);
        edTotalPrice.setOnFocusChangeListener(this);
        edRemark.setOnFocusChangeListener(this);

        edClassify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivArrowClassify.startAnimation(AnimationUtils.loadAnimation(EditBookInfo.this,R.anim.animation));
                showAlertDialogClassify();
            }
        });

        edStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivArrowStatus.startAnimation(AnimationUtils.loadAnimation(EditBookInfo.this,R.anim.animation));
                showAlertDialogBookStatus();
            }
        });

        edTotalPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivArrowTotalP.startAnimation(AnimationUtils.loadAnimation(EditBookInfo.this,R.anim.animation));
            }
        });

        edName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivArrowName.startAnimation(AnimationUtils.loadAnimation(EditBookInfo.this,R.anim.animation));
            }
        });

        ivClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAlertDialog("是否確認要刪除所有資料");
            }
        });

        ivSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadData();
            }
        });

        totalPriceData(edUnitPrice,edQty);
    }

    private void uploadData() {
        String bookName = edName.getText().toString();
        String bookClassify = edClassify.getText().toString();
        String bookDescription = edDescription.getText().toString();
        String bookQty = edQty.getText().toString();
        String bookUnitPrice = edUnitPrice.getText().toString();
        String bookStatus = edStatus.getText().toString();
        String bookRemark = edRemark.getText().toString();
        String uid = user.getUid();
        long time = System.currentTimeMillis(); //取得手機目前時間

        if (bookName.isEmpty()||bookClassify.isEmpty()||bookQty.isEmpty()||bookStatus.isEmpty()||bookUnitPrice.isEmpty()){
            showHint("請輸入必填欄位");
            return;
        }

        if (bookQty.equals("0")){
            showHint("數量不應為0");
            return;
        }

        String bookTotalPrice = Integer.parseInt(bookQty) * Integer.parseInt(bookUnitPrice)+"";
        String userEmail = user.getEmail();

        if (userEmail == null){
            Toast.makeText(this, "無法取得使用者Email", Toast.LENGTH_SHORT).show();
            return;
        }

        addBookBasicData.setClassify(bookClassify);
        addBookBasicData.setDescription(bookDescription);
        addBookBasicData.setQty(bookQty);
        addBookBasicData.setStatus(bookStatus);
        addBookBasicData.setRemark(bookRemark);
        addBookBasicData.setUnitPrice(bookUnitPrice);
        addBookBasicData.setTotalPrice(bookTotalPrice);
        addBookBasicData.setUploaderUid(uid);
        addBookBasicData.setTime(time);
        addBookBasicData.setUserEmail(userEmail);

        ApiTool.getRequestApi()
                .editBook(addBookBasicData)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<AddBookBasicData>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {
                        compositeDisposable.add(d);
                    }

                    @Override
                    public void onNext(@NonNull AddBookBasicData dataObject) {
                        Log.i("Joyce","新增成功: "+dataObject.getQty());
                        newAddBookBasicData = dataObject;
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.i("Joyce","e: "+e);
                        showHint("書本上傳失敗");
                    }

                    @Override
                    public void onComplete() {
                        Log.i("Joyce","onComplete");

                        Intent intent = new Intent(EditBookInfo.this,BookInfoActivity.class);
                        intent.putExtra(ADD_BOOK_BASIC_DATA,newAddBookBasicData);
                        startActivity(intent);

                        finish();
                    }
                });
    }

    private void totalPriceData(EditText edUnitPrice, EditText edQty) {

        edQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String strQty = edQty.getText().toString();

                if (edQty.getText().toString().isEmpty()){
                    return;
                }

                qty = Integer.parseInt(strQty);

                if (edUnitPrice.getText().toString().isEmpty()){
                    return;
                }
                JoyceLog.i("price : "+unitPrice);

                edTotalPrice.setText("NTD "+qty*unitPrice);

            }
        });

        edUnitPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String strUnitPrice = edUnitPrice.getText().toString();

                if (edUnitPrice.getText().toString().isEmpty()){

                    JoyceLog.i("price is empty ");
                    edTotalPrice.setText("NTD "+0);
                    return;
                }

                unitPrice = Integer.parseInt(strUnitPrice);

                if (edQty.getText().toString().isEmpty()){
                    return;
                }
                JoyceLog.i("price : "+unitPrice);
                edTotalPrice.setText("NTD "+qty*unitPrice);


            }
        });
    }

    private void showAlertDialogBookStatus() {
        BookStatusDialog bookStatusDialog = BookStatusDialog.newInstance();
        bookStatusDialog.show(getSupportFragmentManager(),"dialog");
        bookStatusDialog.setOnBookStatusDialogClickListener(new BookStatusDialog.OnBookStatusDialogClickListener() {
            @Override
            public void onLevel1Click(String level) {
                edStatus.setText(level);
            }

            @Override
            public void onLevel2Click(String level) {
                edStatus.setText(level);
            }

            @Override
            public void onLevel3Click(String level) {
                edStatus.setText(level);
            }

            @Override
            public void onLevel4Click(String level) {
                edStatus.setText(level);
            }
        });
    }

    private void showAlertDialogClassify() {
        ClassifyDialog classifyDialog = ClassifyDialog.newInstance();
        classifyDialog.show(getSupportFragmentManager(),"dialog");
        classifyDialog.setListener(new ClassifyDialog.OnClassifyDialogClickListener() {
            @Override
            public void onClassifyClick(String classify) {
                edClassify.setText(classify);
            }
        });
    }

    private void showAlertDialog(String content) {
        ViewDialog alert = new ViewDialog();
        alert.showDialog(EditBookInfo.this,content);
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
        edClassify.setText("");
        edDescription.setText("");
        edQty.setText("");
        edStatus.setText("");
        edRemark.setText("");
        edUnitPrice.setText("");
        edTotalPrice.setText("");
    }

    private void searchOriginalData(AddBookBasicData addBookBasicData) {

        ImageLoaderProvider.getInstance().setImage(addBookBasicData.getPhotoUrl(),ivAddPicture);    //put book cover

        String name = addBookBasicData.getBookName();
        String classify = addBookBasicData.getClassify();
        String qty = addBookBasicData.getQty();
        String unitPrice = addBookBasicData.getUnitPrice();
        String totalPrice = addBookBasicData.getTotalPrice();
        String status = addBookBasicData.getStatus();
        String description = addBookBasicData.getDescription();
        String remark = addBookBasicData.getRemark();

        if (description.isEmpty()){
            edDescription.setText("");
        }

        if (remark.isEmpty()){
            edRemark.setText("");
        }

        //先把直給全域變數
        this.unitPrice = Integer.parseInt(unitPrice);
        this.qty = Integer.parseInt(qty);

        edName.setText(name);
        edClassify.setText(classify);
        edDescription.setText(description);
        edQty.setText(qty);
        edUnitPrice.setText(unitPrice);
        edTotalPrice.setText(totalPrice);
        edStatus.setText(status);
        edRemark.setText(remark);
    }

    private void showHint(String content) {
        Toast.makeText(this,content,Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        abNew = findViewById(R.id.action_bar);
        abEdit = findViewById(R.id.action_bar1);
        edName = findViewById(R.id.book_name);
        edClassify = findViewById(R.id.book_classify);
        edDescription = findViewById(R.id.book_description);
        edQty = findViewById(R.id.book_qty);
        edStatus = findViewById(R.id.book_status);
        edRemark = findViewById(R.id.book_remark);
        edUnitPrice = findViewById(R.id.book_unit_price);
        edTotalPrice = findViewById(R.id.book_total_price);

        ivAddPicture = findViewById(R.id.book_picture);
        ivAddBtn = findViewById(R.id.book_add);
        ivBack = findViewById(R.id.back1);
        ivSave = findViewById(R.id.submit);
        ivClear = findViewById(R.id.clear);
        ivArrowName = findViewById(R.id.arrow);
        ivArrowClassify = findViewById(R.id.arrow1);
        ivArrowDescription = findViewById(R.id.arrow2);
        ivArrowQty = findViewById(R.id.arrow3);
        ivArrowUnitP = findViewById(R.id.arrow4);
        ivArrowTotalP = findViewById(R.id.arrow5);
        ivArrowStatus = findViewById(R.id.arrow6);
        ivArrowRemark = findViewById(R.id.arrow8);


        abNew.setVisibility(View.GONE);
        abEdit.setVisibility(View.VISIBLE);
        ivAddBtn.setVisibility(View.GONE);
        edName.setFocusable(false);
        edName.setTextAppearance(this,R.style.TextLabel);
//        edName.setTypeface(TypeFaceHelper.getInstance().getTypeFace(this,"togalite_regular.otf"));
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
    }

    //要先implements View.OnFocusChangeListener --> 可以集中管理
    //黃色箭頭跳動效果,因為是EditText,所以另外設了一個實作的方法
    //若是EditText的focusable是關閉的,就可以直接把動畫效果打在點擊事件裡
    //isFocus --> 點擊就是true (點其他按鈕就會變false)
    @Override
    public void onFocusChange(View view, boolean isFocus) {

        if (view.getId() == R.id.book_description && isFocus){
            ivArrowDescription.startAnimation(AnimationUtils.loadAnimation(EditBookInfo.this,R.anim.animation));
            return;
        }
        if (view.getId() == R.id.book_qty && isFocus){
            ivArrowQty.startAnimation(AnimationUtils.loadAnimation(EditBookInfo.this,R.anim.animation));
            return;
        }
        if (view.getId() == R.id.book_unit_price && isFocus){
            ivArrowUnitP.startAnimation(AnimationUtils.loadAnimation(EditBookInfo.this,R.anim.animation));
            return;
        }
        if (view.getId() == R.id.book_total_price && isFocus){
            ivArrowTotalP.startAnimation(AnimationUtils.loadAnimation(EditBookInfo.this,R.anim.animation));
            return;
        }
        if (view.getId() == R.id.book_remark && isFocus){
            ivArrowRemark.startAnimation(AnimationUtils.loadAnimation(EditBookInfo.this,R.anim.animation));
            return;
        }
    }

}
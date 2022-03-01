package com.example.second_book_exchange.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.second_book_exchange.tool.TypeFaceHelper;
import com.google.android.material.textfield.TextInputEditText;

public class BookTreeInputEditText extends TextInputEditText {
    public BookTreeInputEditText(@NonNull Context context) {
        super(context);
        changeFontType(context);

    }

    public BookTreeInputEditText(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        changeFontType(context);

    }

    public BookTreeInputEditText(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        changeFontType(context);

    }

    private void changeFontType(Context context) {
        setTypeface( TypeFaceHelper.getInstance().getTypeFace(context,"yozai.ttf"));

    }
}


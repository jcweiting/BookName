package com.example.second_book_exchange.widget;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.second_book_exchange.tool.TypeFaceHelper;

public class BookTreeTextView extends androidx.appcompat.widget.AppCompatTextView {

    public BookTreeTextView(@NonNull Context context) {
        super(context);
        changeFontType(context);
    }

    public BookTreeTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        changeFontType(context);
    }

    public BookTreeTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        changeFontType(context);
    }

    private void changeFontType(Context context) {
        setTypeface( TypeFaceHelper.getInstance().getTypeFace(context,"yozai.ttf"));
    }
}

package com.book.second_book_exchange;

import android.view.View;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.book.second_book_exchange.recyclerview.MyOrdersDetailsAdapter;
import com.book.second_book_exchange.R;

import java.util.ArrayList;

public class MyOrdersDetails {

    private RecyclerView recyclerView;
    private ArrayList<BookInsideData> insideDataArr;

    public void setInsideDataArr(ArrayList<BookInsideData> insideDataArr) {
        this.insideDataArr = insideDataArr;
    }

    public void showView(View mask, ConstraintLayout root, FragmentActivity activity){

        final View bottomView = View.inflate(activity, R.layout.recyclerview_myorders_details,null);
        recyclerView = bottomView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(activity));

        mask.setVisibility(View.VISIBLE);
        root.addView(bottomView);

        bottomView.setVisibility(View.VISIBLE);
        bottomView.setY(root.getBottom());
        bottomView.setX(0);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bottomView.getLayoutParams();
        params.height = root.getBottom() -  root.getBottom() / 3 ;
        params.width = root.getRight();
        bottomView.setLayoutParams(params);

        bottomView.post(new Runnable() {
            @Override
            public void run() {
                int height = bottomView.getBottom() - bottomView.getTop();

                int rootBottom = root.getBottom();

                float finalY = rootBottom - height;

                bottomView.setVisibility(View.VISIBLE);
                bottomView.animate().y(finalY).start();
            }
        });

        MyOrdersDetailsAdapter detailsAdapter = new MyOrdersDetailsAdapter();
        detailsAdapter.setInsideDataArr(insideDataArr);
        recyclerView.setAdapter(detailsAdapter);

        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                maskGone(bottomView, root, mask);
            }
        });
    }

    private void maskGone(View bottomView, ConstraintLayout root, View mask) {
        bottomView.animate().y(root.getBottom()).start();
        mask.setVisibility(View.GONE);
    }


}

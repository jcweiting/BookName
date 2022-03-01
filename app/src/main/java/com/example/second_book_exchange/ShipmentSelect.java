package com.example.second_book_exchange;

import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;

public class ShipmentSelect {

    private ConstraintLayout optFamily, optSeven, optDelivery, optFace;
    private OnClickShipment onClickShipment;

    public void setOnClickShipment(OnClickShipment onClickShipment){
        this.onClickShipment = onClickShipment;
    }

    private static final int FAMILY = 0;
    private static final int SEVEN = 1;
    private static final int DELIVERY = 2;
    private static final int FACE = 3;

    private ArrayList<SpinnerList> getDataList(){
        ArrayList<SpinnerList> mySpinner = new ArrayList<>();
        mySpinner.add(new SpinnerList("全家店到店",60));
        mySpinner.add(new SpinnerList("7-11店到店",60));
        mySpinner.add(new SpinnerList("宅配",80));
        mySpinner.add(new SpinnerList("面交",0));
        return mySpinner;
    }

    public void showView(View mask, ConstraintLayout root, FragmentActivity activity){

        final View bottomView = View.inflate(activity,R.layout.shipment_select,null);

        optFamily = bottomView.findViewById(R.id.opt_family);
        optSeven = bottomView.findViewById(R.id.opt_seven);
        optDelivery = bottomView.findViewById(R.id.opt_delivery);
        optFace = bottomView.findViewById(R.id.opt_face);

        mask.setVisibility(View.VISIBLE);
        root.addView(bottomView);

        bottomView.setVisibility(View.VISIBLE);
        bottomView.setY(root.getBottom());
        bottomView.setX(0);

        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) bottomView.getLayoutParams();
        params.height = root.getBottom() / 2 ;
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

        mask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                maskGone(bottomView, root, mask);
            }
        });
        //這邊傳出去fragment
        optFamily.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maskGone(bottomView, root, mask);
                onClickShipment.onClickShipment(getDataList().get(FAMILY));
            }
        });

        optSeven.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maskGone(bottomView, root, mask);
                onClickShipment.onClickShipment(getDataList().get(SEVEN));
            }
        });

        optDelivery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maskGone(bottomView, root, mask);
                onClickShipment.onClickShipment(getDataList().get(DELIVERY));
            }
        });

        optFace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                maskGone(bottomView, root, mask);
                onClickShipment.onClickShipment(getDataList().get(FACE));
            }
        });
    }

    private void maskGone(View bottomView, ConstraintLayout root, View mask) {
        bottomView.animate().y(root.getBottom()).start();
        mask.setVisibility(View.GONE);
    }

    public interface OnClickShipment{
        void onClickShipment(SpinnerList data);
    }
}

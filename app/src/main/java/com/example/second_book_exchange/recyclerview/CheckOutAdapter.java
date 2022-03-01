package com.example.second_book_exchange.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.second_book_exchange.BookOuterData;
import com.example.second_book_exchange.CheckOut;
import com.example.second_book_exchange.R;
import com.example.second_book_exchange.UserBasicData;
import com.example.second_book_exchange.log.JoyceLog;
import com.example.second_book_exchange.tool.ImageLoaderProvider;
import com.google.gson.Gson;

import java.util.ArrayList;

public class CheckOutAdapter extends RecyclerView.Adapter<CheckOutAdapter.ViewHolder> {

    private ArrayList<BookOuterData> bookOuterData;
    private Context context;
    private ArrayList<UserBasicData> userDataArr;

    public void setBookOuterData(ArrayList<BookOuterData> bookOuterData) {
        this.bookOuterData = bookOuterData;
    }

    public void setUserDataArr(ArrayList<UserBasicData> userDataArr) {
        this.userDataArr = userDataArr;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        this.context = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_check_out,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        JoyceLog.i("CheckOutAdapter | position: "+position);

        BookOuterData outerDataObject = bookOuterData.get(position);
        UserBasicData userBasicData = userDataArr.get(position);
        JoyceLog.i("CheckOutAdapter | userBasicData: "+new Gson().toJson(userBasicData));

        holder.tvEmail.setText(outerDataObject.getUserEmail());
        holder.tvShipment.setText("運送方式: "+outerDataObject.getShipmentWay());
        holder.tvSum.setText("總金額: NTD "+outerDataObject.getSum());
        holder.tvBankCode.setText("銀行代碼: "+userBasicData.getBankCode());
        holder.tvBankAccount.setText("銀行帳號: "+userBasicData.getBankAccount());
        holder.tvBankName.setText("銀行戶名: "+userBasicData.getBankName());

        CheckOutInnerAdapter innerAdapter = new CheckOutInnerAdapter();
        innerAdapter.setBookInsideData(bookOuterData.get(position).getProductLists());
        holder.recyclerViewOuter.setAdapter(innerAdapter);

    }

    @Override
    public int getItemCount() {
        return bookOuterData == null ? 0 : bookOuterData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private TextView tvEmail, tvBankCode, tvBankAccount, tvBankName, tvShipment, tvSum;
        private RecyclerView recyclerViewOuter;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvEmail = itemView.findViewById(R.id.email);
            tvBankCode = itemView.findViewById(R.id.bank_code);
            tvBankAccount = itemView.findViewById(R.id.bank_account);
            tvBankName = itemView.findViewById(R.id.bank_name);
            tvShipment = itemView.findViewById(R.id.shipment);
            tvSum = itemView.findViewById(R.id.sum);
            recyclerViewOuter = itemView.findViewById(R.id.recyclerview_content);

            recyclerViewOuter.setLayoutManager(new LinearLayoutManager(context));
        }
    }
}

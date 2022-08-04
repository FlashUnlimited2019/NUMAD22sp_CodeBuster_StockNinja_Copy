package edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers;

import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.*;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.R;

public class TransactionRecyclerAdapter extends RecyclerView.Adapter<TransactionRecyclerAdapter.TransactionViewHolder> {

    private ArrayList<Transaction> transactionList;

    public TransactionRecyclerAdapter(ArrayList<Transaction> transactionList) {
        this.transactionList = transactionList;
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        private TextView transactionDate;
        private TextView transactionAction;
        private TextView transactionPrice;
        private TextView transactionQty;
        private TextView transactionValue;


        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            transactionDate = itemView.findViewById(R.id.t_date);
            transactionAction = itemView.findViewById(R.id.t_action);
            transactionPrice = itemView.findViewById(R.id.t_price);
            transactionQty = itemView.findViewById(R.id.t_quality);
            transactionValue = itemView.findViewById(R.id.t_value);
        }
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.transaction_card_item, parent, false);
        return new TransactionViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {
        MyDateTime date =transactionList.get(position).getTimestamp();
        String action = transactionList.get(position).getAction();
        String price = String.format("$%.2f", transactionList.get(position).getPrice());
        String qty = String.format("%d", transactionList.get(position).getShares());
        String value = String.format("$%.2f", transactionList.get(position).getPrice() * transactionList.get(position).getShares());
        int background = action.equals("Buy") ? R.drawable.normal_item : R.drawable.red_item;
        String hour = date.getHour() /10 < 1 ? "0"+date.getHour() : String.valueOf(date.getHour());
        String minus = date.getMinus() / 10 < 1 ? "0"+date.getMinus() : String.valueOf(date.getMinus());
        holder.transactionDate.setText(date.getYear() + "/" + date.getMonth() + "/" + date.getDay() +" "+hour +":"+minus);
        holder.transactionAction.setText(action);
        holder.transactionPrice.setText(price);
        holder.transactionQty.setText(qty);
        holder.transactionValue.setText(value);
        holder.itemView.findViewById(R.id.cardLayout2).setBackgroundResource(background);

    }

    @Override
    public int getItemCount() {
        return transactionList.size();
    }

}

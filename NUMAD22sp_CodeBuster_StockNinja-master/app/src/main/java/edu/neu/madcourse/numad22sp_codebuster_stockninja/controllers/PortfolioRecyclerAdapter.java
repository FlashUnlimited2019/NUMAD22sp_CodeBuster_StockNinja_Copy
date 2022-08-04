package edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;

import edu.neu.madcourse.numad22sp_codebuster_stockninja.R;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.Stock;

public class PortfolioRecyclerAdapter extends RecyclerView.Adapter<PortfolioRecyclerAdapter.PortfolioViewHolder> {

    private ArrayList<Stock> portfolioList;
    private RecyclerViewClickerListener listener;

    public PortfolioRecyclerAdapter(ArrayList<Stock> portfolioList, RecyclerViewClickerListener listener) {
        this.portfolioList = portfolioList;
        this.listener = listener;
    }


    public class PortfolioViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView stockSymbol;
        private TextView stockShares;
        private TextView stockPrice;
        private TextView stockChange;
        private TextView stockValue;
        private TextView stockGainLoss;


        public PortfolioViewHolder(@NonNull View itemView) {
            super(itemView);
            stockSymbol = itemView.findViewById(R.id.stock_symbol);
            stockShares = itemView.findViewById(R.id.stock_shares);
            stockPrice = itemView.findViewById(R.id.stock_price);
            stockChange = itemView.findViewById(R.id.change_percent);
            stockValue = itemView.findViewById(R.id.stock_value);
            stockGainLoss = itemView.findViewById(R.id.gain_loss);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }


    @NonNull
    @Override
    public PortfolioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.portfolio_card_item, parent, false);
        return new PortfolioRecyclerAdapter.PortfolioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull PortfolioViewHolder holder, int position) {
        String symbol = portfolioList.get(position).getStockSymbol();
        double avePrice = portfolioList.get(position).getAveragePrice();
        double currPrice = portfolioList.get(position).getCurrPrice();; // fetch curr price from api based on stockSymbol, for now hardcoded, new thread works here???
        double changePercent = (currPrice - avePrice) / avePrice;
        int shares = portfolioList.get(position).getShares();
        double value = currPrice * shares;
        double gainLoss = (currPrice - avePrice) * shares;
        int background = gainLoss > 0 ? R.drawable.green_item : R.drawable.red_item;

        holder.stockSymbol.setText(symbol);
        holder.stockShares.setText(String.valueOf(shares));
        holder.stockPrice.setText(String.format("$%.2f", currPrice));
        holder.stockChange.setText(String.format("%.2f%%", changePercent*100));
        holder.stockValue.setText(String.format("$%.2f", value));
        holder.stockGainLoss.setText(String.format("$%.2f", gainLoss));
        holder.itemView.findViewById(R.id.cardLayout1).setBackgroundResource(background);

    }

    @Override
    public int getItemCount() {
        return portfolioList.size();
    }

    public interface RecyclerViewClickerListener {
        void onClick(View v, int position);
    }

}


package edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.R;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers.SearchResultAdapter.SearchResultHolder;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.SearchResult;
import java.util.List;

public class SearchResultAdapter extends Adapter<SearchResultHolder> {

  private List<SearchResult> searchResults;
  private OnClickListener onClickListener;

  public SearchResultAdapter(List<SearchResult> searchResults) {
    this.searchResults = searchResults;
  }

  public void setOnClickListener(OnClickListener onClickListener) {
    this.onClickListener = onClickListener;
  }

  @NonNull
  @Override
  public SearchResultHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result, parent, false);
    return new SearchResultHolder(view);
  }

  @Override
  public void onBindViewHolder(@NonNull SearchResultHolder holder, int position) {
    SearchResult searchResult = this.searchResults.get(position);
    holder.txtStockSymbol.setText(searchResult.getStockSymbol());
    holder.txtCompanyName.setText(searchResult.getCompanyName());
  }

  @Override
  public int getItemCount() {
    return this.searchResults.size();
  }

  class SearchResultHolder extends ViewHolder {

    TextView txtStockSymbol;
    TextView txtCompanyName;

    public SearchResultHolder(@NonNull View view) {
      super(view);
      this.txtStockSymbol = view.findViewById(R.id.txtStockSymbol);
      this.txtCompanyName = view.findViewById(R.id.txtCompanyName);

      view.setOnClickListener(v -> {
            int position = this.getAdapterPosition();
            if (SearchResultAdapter.this.onClickListener == null || position == RecyclerView.NO_POSITION) {
              return;
            }
            SearchResultAdapter.this.onClickListener.onClick(position);
          }
      );
    }
  }
}

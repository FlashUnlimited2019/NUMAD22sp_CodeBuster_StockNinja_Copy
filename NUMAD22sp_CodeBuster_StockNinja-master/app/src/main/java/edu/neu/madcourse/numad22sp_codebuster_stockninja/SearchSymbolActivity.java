package edu.neu.madcourse.numad22sp_codebuster_stockninja;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers.OnClickListener;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers.SearchResultAdapter;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.SearchResult;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class SearchSymbolActivity extends AppCompatActivity {

  private static final String KEY_OF_SYMBOL_INPUT = "KEY_OF_SYMBOL_INPUT";
  private static final String KEY_OF_ERROR_MSG = "KEY_OF_ERROR_MSG";
  private static final String KEY_OF_SEARCH_RESULT = "KEY_OF_SEARCH_RESULT";
  private EditText inputStockSymbol;
  private ProgressBar vSearchStatus;
  private TextView txtErrorMsg;
  private RecyclerView rvSearchResult;
  private final Handler handler = new Handler(Looper.getMainLooper());
  private SearchResultAdapter searchResultAdapter;
  private List<SearchResult> searchResults = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setContentView(R.layout.activity_search_symbol);

    this.inputStockSymbol = super.findViewById(R.id.inputStockSymbol);
    ImageButton btnSearchSymbol = super.findViewById(R.id.btnSearchSymbol);
    this.vSearchStatus = super.findViewById(R.id.vSearchStatus);
    this.txtErrorMsg = super.findViewById(R.id.txtSSErrorMsg);
    this.rvSearchResult = super.findViewById(R.id.rvSearchResult);

    this.init(savedInstanceState);
    this.createRecyclerView();

    btnSearchSymbol.setOnClickListener(v -> {
      SearchSymbolActivity.this.txtErrorMsg.setVisibility(View.GONE);
      SearchSymbolActivity.this.vSearchStatus.setVisibility(View.VISIBLE);
      SearchSymbolTask task = new SearchSymbolTask();
      new Thread(task).start();
    });
  }

  private void init(Bundle savedInstanceState) {
    if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_OF_SYMBOL_INPUT) || this.searchResults.size() > 0) {
      return;
    }
    this.inputStockSymbol.setText(savedInstanceState.getString(KEY_OF_SYMBOL_INPUT));
    this.searchResults.addAll(savedInstanceState.getParcelableArrayList(KEY_OF_SEARCH_RESULT));
    if (this.searchResults.size() == 0) {
      this.txtErrorMsg.setText(savedInstanceState.getString(KEY_OF_ERROR_MSG));
      this.txtErrorMsg.setVisibility(View.VISIBLE);
    }
  }

  private void createRecyclerView() {
    LayoutManager layoutManger = new LinearLayoutManager(this);
    OnClickListener onClickListener = (position) -> {
      Bundle extras = super.getIntent().getExtras();
      Intent intent = new Intent(this, FetchTimeSeriesActivity.class);
      intent.putExtras(extras);
      intent.putExtra("searchResult", this.searchResults.get(position));
      super.startActivity(intent);
      this.searchResultAdapter.notifyItemChanged(position);
    };
    this.searchResultAdapter = new SearchResultAdapter(this.searchResults);
    this.searchResultAdapter.setOnClickListener(onClickListener);
    this.rvSearchResult.setHasFixedSize(true);
    this.rvSearchResult.setLayoutManager(layoutManger);
    this.rvSearchResult.setAdapter(this.searchResultAdapter);
  }

  private void addSearchResult(SearchResult newSearchResult) {
    this.searchResults.add(0, newSearchResult);
    this.searchResultAdapter.notifyItemInserted(0);
  }

  private void clearSearchResults() {
    int numSearchResults = this.searchResults.size();
    this.searchResults.clear();
    this.searchResultAdapter.notifyItemRangeRemoved(0, numSearchResults);
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_OF_SYMBOL_INPUT, this.inputStockSymbol.getText().toString());
    outState.putParcelableArrayList(KEY_OF_SEARCH_RESULT, new ArrayList<>(this.searchResults));
    outState.putString(KEY_OF_ERROR_MSG, this.txtErrorMsg.getText().toString());
  }

  class SearchSymbolTask implements Runnable {

    static final String QUERY_URL_PREFIX =
        "https://www.alphavantage.co/query?function=SYMBOL_SEARCH&apikey=89RX1CEV53MN7GVU&keywords=";
    static final String ERROR_MSG = "Stock symbol cannot be found. Please try again.";

    @Override
    public void run() {
      try {
        SearchSymbolActivity.this.clearSearchResults();

        URL url = new URL(this.generateQueryUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        InputStream inStream = connection.getInputStream();
        String response = this.convertStreamToString(inStream);

        JSONArray arrResults = new JSONObject(response).getJSONArray("bestMatches");

        for (int i = arrResults.length() - 1; i >= 0; i--) {
          JSONObject objResult = arrResults.getJSONObject(i);
          String region = objResult.getString("4. region");
          if (!region.equals("United States")) {
            continue;
          }
          String symbol = objResult.getString("1. symbol");
          String companyName = objResult.getString("2. name");
          SearchSymbolActivity.this.addSearchResult(new SearchResult(symbol, companyName));
        }
      } catch (Exception ignored) {
      }
      SearchSymbolActivity.this.handler.post(()-> {
        SearchSymbolActivity.this.vSearchStatus.setVisibility(View.GONE);
        if (SearchSymbolActivity.this.searchResults.size() == 0) {
          SearchSymbolActivity.this.txtErrorMsg.setText(ERROR_MSG);
          SearchSymbolActivity.this.txtErrorMsg.setVisibility(View.VISIBLE);
        }
      });
    }

    private String generateQueryUrl() {
      return QUERY_URL_PREFIX + SearchSymbolActivity.this.inputStockSymbol.getText().toString();
    }

    private String convertStreamToString(InputStream is) {
      Scanner scanner = new Scanner(is).useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    }
  }
}
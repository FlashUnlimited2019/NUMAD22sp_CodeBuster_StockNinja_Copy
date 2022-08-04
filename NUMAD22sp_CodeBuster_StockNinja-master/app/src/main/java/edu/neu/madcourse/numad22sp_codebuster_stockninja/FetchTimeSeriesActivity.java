package edu.neu.madcourse.numad22sp_codebuster_stockninja;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.LayoutManager;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers.TimeSeriesAdapter;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.SearchResult;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.TimeSeries;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;

public class FetchTimeSeriesActivity extends AppCompatActivity {

  private static final String KEY_OF_STOCK_SYMBOL = "KEY_OF_STOCK_SYMBOL";
  private static final String KEY_OF_COMPANY_NAME = "KEY_OF_COMPANY_NAME";
  private static final String KEY_OF_ERROR_MSG = "KEY_OF_ERROR_MSG";
  private static final String KEY_OF_TIME_SERIES = "KEY_OF_TIME_SERIES";
  private TextView txtStockSymbol;
  private TextView txtCompanyName;
  private Button btnViewChart;
  private Button btnStartTrading;
  private ProgressBar vFetchStatus;
  private TextView txtErrorMsg;
  private final Handler handler = new Handler(Looper.getMainLooper());
  private TimeSeriesAdapter timeSeriesAdapter;
  private ArrayList<TimeSeries> timeSeries = new ArrayList<>();

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    super.setContentView(R.layout.activity_fetch_time_series);

    Bundle extras = super.getIntent().getExtras();
    if (extras == null || extras.getParcelable("searchResult") == null) {
      Intent intent = new Intent(this, SearchSymbolActivity.class);
      super.startActivity(intent);
      return;
    }

    SearchResult searchResult = extras.getParcelable("searchResult");
    this.txtStockSymbol = super.findViewById(R.id.txtStockSymbol2);
    this.txtCompanyName = super.findViewById(R.id.txtCompanyName2);
    this.txtStockSymbol.setText(searchResult.getStockSymbol());
    this.txtCompanyName.setText(searchResult.getCompanyName());

    this.btnViewChart = super.findViewById(R.id.btnViewChart);
    this.btnViewChart.setOnClickListener(view -> {
      Intent intent = new Intent(this, TimeSeriesChartActivity.class);
      intent.putExtra("stockSymbol", this.txtStockSymbol.getText().toString());
      intent.putParcelableArrayListExtra("timeSeries", this.timeSeries);
      super.startActivity(intent);
    });

    this.btnStartTrading = super.findViewById(R.id.btnStartTrading);
    this.btnStartTrading.setOnClickListener(view -> {
      Intent intent = new Intent(this, TransactionHistoryActivity.class);
      intent.putExtra("username", extras.getString("username"));
      intent.putExtra("stockSymbol", this.txtStockSymbol.getText().toString());
      intent.putExtra("companyName", this.txtCompanyName.getText().toString());
      intent.putExtra("currPrice", this.timeSeries.get(0).getOpen());
      super.startActivity(intent);
    });

    this.vFetchStatus = super.findViewById(R.id.vFetchStatus);
    this.txtErrorMsg = super.findViewById(R.id.txtTSErrorMsg);

    this.init(savedInstanceState);
    this.createRecyclerView();
  }

  private void init(Bundle savedInstanceState) {
    if (savedInstanceState == null || !savedInstanceState.containsKey(KEY_OF_STOCK_SYMBOL)) {
      FetchTimeSeriesTask task = new FetchTimeSeriesTask();
      new Thread(task).start();
      return;
    }
    this.txtStockSymbol.setText(savedInstanceState.getString(KEY_OF_STOCK_SYMBOL));
    this.txtCompanyName.setText(savedInstanceState.getString(KEY_OF_COMPANY_NAME));
    this.timeSeries.addAll(savedInstanceState.getParcelableArrayList(KEY_OF_TIME_SERIES));
    if (this.timeSeries.size() > 0) {
      this.btnViewChart.setEnabled(true);
      this.btnStartTrading.setEnabled(true);
    } else {
      this.txtErrorMsg.setText(savedInstanceState.getString(KEY_OF_ERROR_MSG));
      this.txtErrorMsg.setVisibility(View.VISIBLE);
    }
    this.vFetchStatus.setVisibility(View.GONE);
  }

  private void createRecyclerView() {
    LayoutManager layoutManger = new LinearLayoutManager(this);
    this.timeSeriesAdapter = new TimeSeriesAdapter(this.timeSeries);
    RecyclerView rvTimeSeries = super.findViewById(R.id.rvTimeSeries);
    rvTimeSeries.setHasFixedSize(true);
    rvTimeSeries.setLayoutManager(layoutManger);
    rvTimeSeries.setAdapter(this.timeSeriesAdapter);
  }

  private void addTimeSeries(ArrayList<TimeSeries> newTimeSeries) {
    int preSize = this.timeSeriesAdapter.getItemCount();
    this.timeSeries.addAll(newTimeSeries);
    this.timeSeriesAdapter.notifyItemRangeInserted(preSize, newTimeSeries.size());
  }

  @Override
  protected void onSaveInstanceState(@NonNull Bundle outState) {
    super.onSaveInstanceState(outState);
    outState.putString(KEY_OF_STOCK_SYMBOL, this.txtStockSymbol.getText().toString());
    outState.putString(KEY_OF_COMPANY_NAME, this.txtCompanyName.getText().toString());
    outState.putParcelableArrayList(KEY_OF_TIME_SERIES, this.timeSeries);
    outState.putString(KEY_OF_ERROR_MSG, this.txtErrorMsg.getText().toString());
  }

  class FetchTimeSeriesTask implements Runnable {

    static final String QUERY_URL_PREFIX =
        "https://api.stockdata.org/v1/data/intraday?api_token=Hwn4qRX2NdR1L7eHgzYkw8I6valUIV6YUErPjso9&key_by_date=true";
    static final String STOCK_SYMBOL_PARAM = "&symbols=";
    static final String DATE_FROM_PARAM = "&date_from=";
    static final String ERROR_MSG = "Intraday data is currently unavailable for this stock.";

    @Override
    public void run() {
      try {
        URL url = new URL(this.generateQueryUrl());
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setDoInput(true);
        connection.connect();

        InputStream inStream = connection.getInputStream();
        String response = this.convertStreamToString(inStream);

        JSONObject objectResult = new JSONObject(response).getJSONObject("data");
        Iterator<String> keys = objectResult.keys();
        ArrayList<TimeSeries> tempTimeSeries = new ArrayList<>();

        while (keys.hasNext()) {
          String key = keys.next();
          JSONArray arrResults = (JSONArray) objectResult.get(Objects.requireNonNull(key));
          JSONObject data = arrResults.getJSONObject(0).getJSONObject("data");
          String timestamp = this.generateTimeStamp(key);
          double open = data.getDouble("open");
          double high = data.getDouble("high");
          double low = data.getDouble("low");
          double close = data.getDouble("close");
          tempTimeSeries.add(new TimeSeries(timestamp, open, high, low, close));
        }
        Collections.sort(tempTimeSeries);
        FetchTimeSeriesActivity.this.addTimeSeries(tempTimeSeries);
      } catch (Exception ignored) {
      }
      FetchTimeSeriesActivity.this.handler
          .post(() -> {
            FetchTimeSeriesActivity.this.vFetchStatus.setVisibility(View.GONE);
            if (FetchTimeSeriesActivity.this.timeSeries.size() == 0) {
              FetchTimeSeriesActivity.this.txtErrorMsg.setText(ERROR_MSG);
              FetchTimeSeriesActivity.this.txtErrorMsg.setVisibility(View.VISIBLE);
            } else {
              FetchTimeSeriesActivity.this.btnViewChart.setEnabled(true);
              FetchTimeSeriesActivity.this.btnStartTrading.setEnabled(true);
            }
          });
    }

    private String generateQueryUrl() {
      String stockSymbol = FetchTimeSeriesActivity.this.txtStockSymbol.getText().toString();
      DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
      String fromDate = LocalDateTime.now().minusDays(7).format(formatter);
      return QUERY_URL_PREFIX + STOCK_SYMBOL_PARAM + stockSymbol + DATE_FROM_PARAM + fromDate;
    }

    private String convertStreamToString(InputStream is) {
      Scanner scanner = new Scanner(is).useDelimiter("\\A");
      return scanner.hasNext() ? scanner.next() : "";
    }

    private String generateTimeStamp(String str) {
      LocalDateTime dateTime = LocalDateTime
          .parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
      return dateTime.toLocalDate() + " " + dateTime.toLocalTime();
    }
  }
}

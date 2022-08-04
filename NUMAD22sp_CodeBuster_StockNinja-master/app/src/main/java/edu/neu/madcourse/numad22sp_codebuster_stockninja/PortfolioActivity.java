package edu.neu.madcourse.numad22sp_codebuster_stockninja;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers.PortfolioRecyclerAdapter;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.Stock;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.TimeSeries;

public class PortfolioActivity extends AppCompatActivity {
    private String currentUser = null;
    private ArrayList<Stock> portfolioList;
    private RecyclerView recyclerView;
    private PortfolioRecyclerAdapter adapter;
    private PortfolioRecyclerAdapter.RecyclerViewClickerListener listener;

    private final Handler handler = new Handler(Looper.getMainLooper());
    final String QUERY_URL_PREFIX =
            "https://api.stockdata.org/v1/data/intraday?api_token=Hwn4qRX2NdR1L7eHgzYkw8I6valUIV6YUErPjso9&key_by_date=true";
    final String STOCK_SYMBOL_PARAM = "&symbols=";
    final String DATE_FROM_PARAM = "&date_from=";
    final String ERROR_MSG = "Intraday data is currently unavailable for this stock.";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference userReference;
    private DatabaseReference portfolioReference;

    private TextView cashRemaining;
    private TextView accountValue;
    private TextView changeToday;
    private TextView overallReturn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_portfolio);
        TextView todayDate = findViewById(R.id.date);
        String timeNow = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM dd"));
        todayDate.setText(timeNow);
        cashRemaining = findViewById(R.id.cash_remaining);
        accountValue = findViewById(R.id.account_value);
        changeToday = findViewById(R.id.change_today);
        overallReturn = findViewById(R.id.overall_return);
        progressBar = findViewById(R.id.progress_bar);


        // get db references
        firebaseDatabase = FirebaseDatabase.getInstance();
        userReference = firebaseDatabase.getReference("users");
        portfolioReference = firebaseDatabase.getReference("Portfolio");

        portfolioList = new ArrayList<>();
        recyclerView = findViewById(R.id.portfolio_list);
        recyclerView.setNestedScrollingEnabled(false);
        getIntentData();
        setAdapter();
        fetchDataFromDB(currentUser);
        updateBalance();
//        portfolioList.add(new Stock("GOOG", "Alphabet, Inc.", 25, 60, 20));
//        portfolioList.add(new Stock("AMAZON","Amazon Inc.", 15, 60, 20));
//        portfolioList.add(new Stock("NIN","SAMPLE", 12, 60,30));
    }

    private void getIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString("username");
        }
    }

    public void updateBalance() {
        // update cash remaining ui data whenever changed
        userReference.child(currentUser).child("cash").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                double currBalance = snapshot.getValue(Double.class);
                cashRemaining.setText("Cash Remaining: $" + String.format("%.2f", currBalance));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    public void updateUIData() {
        double todayValue = 0;
        double totalCost = 0;
        double todayChange = 0;
        double todayReturn = 0;
        for (Stock stock : portfolioList) {
            todayValue += stock.getCurrPrice() * stock.getShares();
            totalCost += stock.getAveragePrice() * stock.getShares();
        }
        todayChange = todayValue - totalCost;
        todayReturn = todayChange / totalCost;
        accountValue.setText("Account Value: $"+String.format("%.2f", todayValue));
        changeToday.setText("Change Today: $" + String.format("%.2f", todayChange));
        overallReturn.setText("Overall Return: " + String.format("%.2f%%", todayReturn * 100));
    }

    public void fetchCurrPrice(String stockSymbol, Stock newStock) {
        progressBar.setVisibility(View.VISIBLE);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String fromDate = LocalDateTime.now().minusDays(2).format(formatter);
        String queryUrl = QUERY_URL_PREFIX + STOCK_SYMBOL_PARAM + stockSymbol + DATE_FROM_PARAM + fromDate;
        ExecutorService executor = Executors.newSingleThreadExecutor();
        List<TimeSeries> tempTimeSeries = new ArrayList<>();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL(queryUrl);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.connect();

                    InputStream inStream = connection.getInputStream();
                    String response = convertStreamToString(inStream);

                    JSONObject objectResult = new JSONObject(response).getJSONObject("data");
                    Iterator<String> keys = objectResult.keys();

                    while (keys.hasNext()) {
                        String key = keys.next();
                        JSONArray arrResults = (JSONArray) objectResult.get(Objects.requireNonNull(key));
                        JSONObject data = arrResults.getJSONObject(0).getJSONObject("data");
                        String timestamp = generateTimeStamp(key);
                        double open = data.getDouble("open");
                        double high = data.getDouble("high");
                        double low = data.getDouble("low");
                        double close = data.getDouble("close");
                        tempTimeSeries.add(new TimeSeries(timestamp, open, high, low, close));
                    }
                    Log.i("timeseries", tempTimeSeries.toString());

                } catch (Exception ignored) {
                    Log.i("exception", ignored.getMessage());
                }

                //connect with main thread
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!tempTimeSeries.isEmpty()) {
                            // if curr price available, update currPrice
                            double newCurrPrice = tempTimeSeries.get(0).getOpen();
                            newStock.setCurrPrice(newCurrPrice);
                            portfolioReference.child(currentUser).child(newStock.getStockSymbol()).child("currPrice").setValue(newCurrPrice);
                        }
                            portfolioList.add(0, newStock);
                            adapter.notifyDataSetChanged();
                            //update ui value
                            updateUIData();
                            progressBar.setVisibility(View.GONE);
                    }
                });
            }
        });
    };

    private String convertStreamToString(InputStream is) {
        Scanner scanner = new Scanner(is).useDelimiter("\\A");
        return scanner.hasNext() ? scanner.next() : "";
    }

    private String generateTimeStamp(String str) {
        LocalDateTime dateTime = LocalDateTime.parse(str, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"));
        return dateTime.toLocalDate() + " " + dateTime.toLocalTime();
    }

    private void fetchDataFromDB(String currentUser) {
        //update recyclerView when data changes in portfolio table
        portfolioReference.child(currentUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Stock newStock = snapshot.getValue(Stock.class);
                //if (stock.getShares()!=0) { //only add stock with shares?
                //update currPrice for each stock
                fetchCurrPrice(newStock.getStockSymbol(), newStock);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Stock changedStock = snapshot.getValue(Stock.class);
                for (int i=0; i < portfolioList.size();i++) {
                    if (portfolioList.get(i).getStockSymbol().equals(changedStock.getStockSymbol())) {
                        portfolioList.set(i, changedStock);
                        //if stock.getShares()==0, remove?
                        adapter.notifyDataSetChanged();
                        updateUIData();
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }


    private void setAdapter() {
        setOnClickListener();
        adapter = new PortfolioRecyclerAdapter(portfolioList, listener);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        listener = new PortfolioRecyclerAdapter.RecyclerViewClickerListener() {
            @Override
            public void onClick(View v, int position) {
                Intent intent = new Intent(getApplicationContext(), TransactionHistoryActivity.class);
                intent.putExtra("username", currentUser);
                intent.putExtra("stockSymbol", portfolioList.get(position).getStockSymbol());
                intent.putExtra("companyName", portfolioList.get(position).getCompanyName());
                intent.putExtra("currPrice", portfolioList.get(position).getCurrPrice());
                startActivity(intent);
            }
        };
    }
}
package edu.neu.madcourse.numad22sp_codebuster_stockninja;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;

import edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers.BuyDialog;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers.DialogListener;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers.SellDialog;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers.TransactionRecyclerAdapter;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.MyDateTime;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.Stock;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.Transaction;

public class TransactionHistoryActivity extends AppCompatActivity implements DialogListener {
    private String stockSymbol = null;
    private String currentUser = null;
    private String companyName = null;
    private double currPrice = 0;
    private ArrayList<Transaction> transactionList;
    private RecyclerView recyclerView;
    private TransactionRecyclerAdapter adapter;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference transactionReference;
    private DatabaseReference userReference;
    private DatabaseReference portfolioReference;

    private Button buttonBuy;
    private Button buttonSell;
    TextView stockPrice;
    TextView stockValue;
    TextView stockQty;
    TextView gainLoss;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        getIntentData();
        TextView comSymbol = findViewById(R.id.com_symbol);
        TextView comName = findViewById(R.id.com_name);
        comSymbol.setText(stockSymbol);
        comName.setText(companyName);
        stockPrice = findViewById(R.id.s_price);
        stockValue = findViewById(R.id.s_value);
        stockQty = findViewById(R.id.s_quality);
        gainLoss = findViewById(R.id.s_gain_loss);

        // get db references
        firebaseDatabase = FirebaseDatabase.getInstance();
        transactionReference = firebaseDatabase.getReference("Transactions");
        userReference = firebaseDatabase.getReference("users");
        portfolioReference = firebaseDatabase.getReference("Portfolio");

        // set up recyclerview
        transactionList = new ArrayList<>();
        recyclerView = findViewById(R.id.transaction_list);
        recyclerView.setNestedScrollingEnabled(false);
        setAdapter();
        fetchDataFromDB(currentUser, stockSymbol);
        updateUIData();

        // buy and sell stock
        buttonBuy = findViewById(R.id.button_buy);
        buttonSell = findViewById(R.id.button_sell);
        checkStockMarketTime(); // comment out for testing
        buttonBuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBuyDialog();
            }
        });

        buttonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSellDialog();
            }
        });
    }


    private void getIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString("username");
            Log.i("username", currentUser);
            stockSymbol = extras.getString("stockSymbol");
            Log.i("SYMBOL", stockSymbol);
            companyName = extras.getString("companyName");
            Log.i("companyName", companyName);
            currPrice = extras.getDouble("currPrice"); // passed from portfolio or SearchTimeSeries activity
        }
    }

    // check if stock market open (9:30am - 4pm ET weekday), unable trading if closed
    private void checkStockMarketTime() {
        ZonedDateTime currentISTime = ZonedDateTime.now();
        ZonedDateTime currentETime = currentISTime
                .withZoneSameInstant(ZoneId.of("America/New_York")); //ET Time
        Log.i("time", ""+ currentETime.getHour()+":"+currentETime.getMinute()+ "  " + currentETime.getDayOfWeek().getValue());
        if (currentETime.getDayOfWeek().getValue()==6 || currentETime.getDayOfWeek().getValue()==7) {
            Toast.makeText(TransactionHistoryActivity.this,"Stock market closed on weekend.\nNo trading available.", Toast.LENGTH_SHORT).show();
            buttonBuy.setEnabled(false);
            buttonSell.setEnabled(false);
        }

        int hour = currentETime.getHour();
        int minute = currentETime.getMinute();
        if ((hour == 9 && minute <32) || (hour == 15 && minute > 58) || (hour<9 || hour >= 16)) {
            Toast.makeText(TransactionHistoryActivity.this,"Stock market closed today.\nNo trading available.", Toast.LENGTH_SHORT).show();
            buttonBuy.setEnabled(false);
            buttonSell.setEnabled(false);
        }
    }


//    public void fetchPriceFromAPI(String stockSymbol){}//set price and value gain/loss on the ui
    public void updateUIData() {
        stockPrice.setText("Price: $" + currPrice);
        portfolioReference.child(currentUser).orderByKey().equalTo(stockSymbol).
                addValueEventListener(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot) {
               if (!snapshot.exists() || snapshot.getValue()==null) {
                   stockValue.setText("Value: $0");
                   stockQty.setText("QTY: 0");
                   gainLoss.setText("Gain/Loss: $0");
               } else {
                   // stock already exists, update stock under the portfolio
                   for (DataSnapshot childSnapshot: snapshot.getChildren()) {
                       Stock stock = childSnapshot.getValue(Stock.class);
                       stockValue.setText("Value: $" + String.format("%.2f", stock.getShares() * currPrice));
                       stockQty.setText("QTY: " + stock.getShares());
                       gainLoss.setText("Gain/Loss: $" + String.format("%.2f", (currPrice - stock.getAveragePrice()) * stock.getShares()));
                   }
               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {
           }
        });

    }

    private void fetchDataFromDB(String currentUser, String stockSymbol) {
        //update recyclerView when data changes in transaction table
        transactionReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                Transaction newTransaction = snapshot.getValue(Transaction.class);
                if (newTransaction.getUsername().equalsIgnoreCase(currentUser) && newTransaction.getSymbol().equals(stockSymbol)) {
                    transactionList.add(0, newTransaction);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

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
        adapter = new TransactionRecyclerAdapter(transactionList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


    private void openBuyDialog() {
        BuyDialog buyDialog = new BuyDialog();
        buyDialog.show(getSupportFragmentManager(), "buyDialog");
    }
    private void openSellDialog() {
        SellDialog sellDialog = new SellDialog();
        sellDialog.show(getSupportFragmentManager(), "sellDialog");
    }

    @Override
    public void buyStock(String quantity) {
        String qty = quantity.trim();
        if (qty.isEmpty() || qty.trim().isEmpty()) {
            Snackbar.make(findViewById(R.id.transaction_history), "Quantity must be entered", Snackbar.LENGTH_LONG)
                    .setAction("Re-enter", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openBuyDialog();
                        }
                    }).show();
            return;
        }

        if (!qty.matches("\\d+")) {
            Snackbar.make(findViewById(R.id.transaction_history), "Invalid quantity number", Snackbar.LENGTH_LONG)
                    .setAction("Re-enter", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openBuyDialog();
                        }
                    }).show();
            return;
        }

//        Log.i("test", "BUY");
        int buyQty = Integer.parseInt(qty);

        // check if there is enough balance in user's account to buy
        userReference.child(currentUser).child("cash").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
//                    Log.i("BALANCE", String.valueOf(task.getResult().getValue()));
                    double currBalance = Double.parseDouble(task.getResult().getValue().toString());
                    if (currBalance < currPrice * buyQty) {
                        Snackbar.make(findViewById(R.id.transaction_history), "Not enough balance in your account.", Snackbar.LENGTH_LONG)
                                .setAction("Re-enter", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        openBuyDialog();
                                    }
                                }).show();
                        return;
                    } else {
                        addTransactionToDb(buyQty, "Buy", currPrice);
                    }
                }
            }
        });
    }



    @Override
    public void sellStock(String quantity) {
        String qty = quantity.trim();
        if (qty.isEmpty() || qty.trim().isEmpty()) {
            Snackbar.make(findViewById(R.id.transaction_history), "Quantity must be entered", Snackbar.LENGTH_LONG)
                    .setAction("Re-enter", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openSellDialog();
                        }
                    }).show();
            return;
        }

        if (!qty.matches("\\d+")) {
            Snackbar.make(findViewById(R.id.transaction_history), "Invalid quantity number", Snackbar.LENGTH_LONG)
                    .setAction("Re-enter", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            openSellDialog();
                        }
                    }).show();
            return;
        }

//        Log.i("test", "SELL");
        int sellQty = Integer.parseInt(qty);

        // check if there are enough shares in user's account to sell
        portfolioReference.child(currentUser).child(stockSymbol).child("shares").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful() || task.getResult().getValue()==null) {
//                    Log.e("firebase", "Error getting data", task.getException());
                    Snackbar.make(findViewById(R.id.transaction_history), "No stock available to sell.", Snackbar.LENGTH_LONG)
                            .setAction("Re-enter", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    openSellDialog();
                                }
                            }).show();
                    return;
                } else {
                    int shares = Integer.parseInt(task.getResult().getValue().toString());
                    if (sellQty > shares) {
                        Snackbar.make(findViewById(R.id.transaction_history), "Not enough shares to sell.", Snackbar.LENGTH_LONG)
                                .setAction("Re-enter", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        openSellDialog();
                                    }
                                }).show();
                        return;
                    } else {
                        addTransactionToDb(sellQty, "Sell", currPrice);
                    }
                }
            }
        });
    }


    // process transaction in database: insert to database transaction table, update user balance and user portfolio table)
    private void addTransactionToDb(int quantity, String action, double currPrice) {
        LocalDateTime localDateTimeNow= LocalDateTime.now();
        MyDateTime timeNow = new MyDateTime(localDateTimeNow.getYear(), localDateTimeNow.getMonthValue(), localDateTimeNow.getDayOfMonth(),
                localDateTimeNow.getHour(), localDateTimeNow.getMinute(), localDateTimeNow.getSecond());
        Transaction newTransaction = new Transaction(currentUser, stockSymbol, action, currPrice, quantity, timeNow);
        int finalQuantity = action.equals("Buy") ? quantity : -quantity;

        // add new transaction record to database
        String transactionId = transactionReference.push().getKey();
        transactionReference.child(transactionId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                transactionReference.child(transactionId).setValue(newTransaction);
                Toast.makeText(TransactionHistoryActivity.this, action +" " + quantity + " shares successfully!", Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TransactionHistoryActivity.this, "Action Failed" + error, Toast.LENGTH_SHORT).show();
            }
        });

        // update user balance in user table
        userReference.child(currentUser).child("cash").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                } else {
//                    Log.i("BALANCE", String.valueOf(task.getResult().getValue()));
                    double currBalance = Double.parseDouble(task.getResult().getValue().toString());
                    double balanceChange = action.equals("Buy") ? -quantity * currPrice : quantity * currPrice;
                    userReference.child(currentUser).child("cash").setValue(currBalance + balanceChange);
                }
            }
        });

        //update portfolio table
        portfolioReference.child(currentUser).orderByKey().equalTo(stockSymbol).
                addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    // stock doesn't exist, add new stock under the portfolio
                    portfolioReference.child(currentUser).child(stockSymbol).setValue(new Stock(stockSymbol, companyName, currPrice, currPrice, quantity));
                } else {
                    // stock already exists, update stock under the portfolio
                    for (DataSnapshot childSnapshot: snapshot.getChildren()) {
//                        Log.i("stock", childSnapshot.getValue().toString());
                        Stock stock = childSnapshot.getValue(Stock.class);
//                        Log.i("stock", stock.getStockSymbol()+stock.getAveragePrice()+stock.getShares());
                        int newShares = stock.getShares() + finalQuantity;
                        //only update average price when buying stock, average price is unchanged when selling
                        if (action.equals("Buy")) {
                            double newAveragePrice = (stock.getAveragePrice() * stock.getShares() + currPrice * finalQuantity) / newShares;
                            stock.setAveragePrice(newAveragePrice);
                        }
                        stock.setShares(newShares);
                        portfolioReference.child(currentUser).child(stockSymbol).setValue(stock);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

    }
}


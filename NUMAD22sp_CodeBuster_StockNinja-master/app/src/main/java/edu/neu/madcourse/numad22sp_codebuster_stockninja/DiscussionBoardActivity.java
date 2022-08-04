package edu.neu.madcourse.numad22sp_codebuster_stockninja;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import edu.neu.madcourse.numad22sp_codebuster_stockninja.controllers.DiscussionRecyclerAdapter;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.Discussion;

public class DiscussionBoardActivity extends AppCompatActivity {
    private ArrayList<Discussion> discussionList;
    private RecyclerView recyclerView;
    private DiscussionRecyclerAdapter discussionRecyclerAdapter;
    private String currentUser = null;
    private String currentCity = null;
    private String currentState = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //https://blog.csdn.net/weixin_28835583/article/details/117519697
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_discussion_board);
        getIntentData();

        recyclerView = findViewById(R.id.discussion_list_view);
        discussionList = new ArrayList<>();

        FloatingActionButton addDiscussionBtn = super.findViewById(R.id.add_discussion_btn);
        addDiscussionBtn.setOnClickListener(view -> {
            Intent intent = new Intent(DiscussionBoardActivity.this, DiscussionInputActivity.class);
            intent.putExtra("username", currentUser);
            intent.putExtra("city", currentCity);
            intent.putExtra("state", currentState);
            this.startActivity(intent);
        });
        setDiscussionAdapter();
//        setDiscussionData();
        fetchDataFromDB();
    }


    private void getIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString("username");
            currentCity = extras.getString("city");
            currentState = extras.getString("state");
//            System.out.println("current User"+currentUser);
        }
    }


    private void setDiscussionAdapter() {
        discussionRecyclerAdapter = new DiscussionRecyclerAdapter(discussionList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(discussionRecyclerAdapter);
    }


    private void fetchDataFromDB() {
        DatabaseReference discussionRef = FirebaseDatabase.getInstance().getReference().child("Discussion");
//        discussionRef.getDatabase()

        discussionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                System.out.println("snapshot data"+ snapshot.getValue());
                Iterable<DataSnapshot> dataList = snapshot.getChildren();
//                System.out.println("snapshot data = "+discussionList);
                discussionList.clear();
                for(DataSnapshot item: dataList){
//                    item.getValue()
                    discussionList.add(item.getValue(Discussion.class));
//                    System.out.println("snapshot data2 = "+item.getValue(Discussion.class));
//                    System.out.println("snapshot data3 = "+item.toString());

                }
                discussionRecyclerAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }




//    private void setDiscussionData() {
//        discussionList.add(new Discussion(
//                "001",
//                "Welcome to StockNinja",
//                "Welcome! Feel free to post anything you would like to share with group!",
//                "timestamp",
//                "management",
//                "City, State"
//        ));
//    }
}
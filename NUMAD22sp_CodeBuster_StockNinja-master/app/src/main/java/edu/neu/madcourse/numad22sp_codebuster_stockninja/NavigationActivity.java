package edu.neu.madcourse.numad22sp_codebuster_stockninja;

import static android.app.Notification.DEFAULT_SOUND;
import static android.app.Notification.DEFAULT_VIBRATE;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;

import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.Discussion;

public class NavigationActivity extends AppCompatActivity {

  private Timestamp currTime;
  private String currentUsername = ""; // store username from LoginActivity

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_navigation);

    currTime = new Timestamp((System.currentTimeMillis()));

    Button btnToSearchSymbolActivity = super.findViewById(R.id.btnToSearchSymbolActivity);
    btnToSearchSymbolActivity.setOnClickListener(view -> {
      Bundle extras = super.getIntent().getExtras();
      Intent intent = new Intent(this, SearchSymbolActivity.class);
      intent.putExtras(extras);
      this.startActivity(intent);
    });

    Button btnToPortfolioActivity = super.findViewById(R.id.btnToPortfolioActivity);
    btnToPortfolioActivity.setOnClickListener(view -> {
      Bundle bundle = getIntent().getExtras();
      Intent intent = new Intent(NavigationActivity.this, PortfolioActivity.class);
      intent.putExtras(bundle);
      this.startActivity(intent);
    });

    Button btnToDiscussionActivity = super.findViewById(R.id.btnToDiscussionActivity);
    btnToDiscussionActivity.setOnClickListener(view -> {
      Bundle bundle = getIntent().getExtras();
      Intent intent = new Intent(NavigationActivity.this, DiscussionBoardActivity.class);
      intent.putExtras(bundle);
      this.startActivity(intent);
    });

    getUsername();
    notificationMessage(currentUsername);

  }

  /**
   * get username from MainActivity
   */
  private void getUsername(){
    Bundle extras = getIntent().getExtras();
    if (extras != null) {
      currentUsername = extras.getString("username");
    }
  }

  private DatabaseReference databaseRef;
  private DatabaseReference msgRef;
  private ChildEventListener alistener;

  public void notificationMessage(String currUser){

    databaseRef = FirebaseDatabase.getInstance().getReference();
    msgRef = databaseRef.child("Discussion");

    alistener = new ChildEventListener() {
      @Override
      public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
        Discussion discussion = snapshot.getValue(Discussion.class);
        if((!currUser.equals(discussion.getUsername())) && discussion.getTimestamp().compareTo(String.valueOf(currTime)) > 0){
          System.out.println("message---------------- "+discussion.getUsername());
          currTime = new Timestamp((System.currentTimeMillis()));
          sendNotificationMessage(discussion.getUsername());
        }


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

    };

    msgRef.addChildEventListener(alistener);

  }

  /**
   * end listener with go back
   */
  @Override
  public void onBackPressed() {
    super.onBackPressed();
    System.out.println("message---------------- STOP!!");
    msgRef.removeEventListener(alistener);
    this.finish();
  }

  /**
   * push notifications
   * @param sender
   */
  public void sendNotificationMessage(String sender){

    NotificationCompat.Builder builder = new NotificationCompat.Builder(NavigationActivity.this, "n")
            .setContentTitle("Notification")
            .setContentText("New Post from " + sender)
            .setSmallIcon(R.drawable.search_icon)
            .setDefaults(DEFAULT_SOUND | DEFAULT_VIBRATE)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true);

    NotificationManagerCompat notificationManager = NotificationManagerCompat.from(NavigationActivity.this);
    notificationManager.notify(1, builder.build());

    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
      NotificationChannel channel = new NotificationChannel("n", "notification", NotificationManager.IMPORTANCE_HIGH);
      NotificationManager manager = getSystemService(NotificationManager.class);
      manager.createNotificationChannel(channel);
    }


  }

}

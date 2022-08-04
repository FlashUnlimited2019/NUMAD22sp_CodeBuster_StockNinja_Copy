package edu.neu.madcourse.numad22sp_codebuster_stockninja;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.Discussion;

public class DiscussionInputActivity extends AppCompatActivity {
    private EditText titleEditText;
    private EditText contentEditText;
    private String currentUser = null;
    private String currentCity = null;
    private String currentState = null;
    private static final SimpleDateFormat timeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discussion_input);
        getIntentData();

        Button submitPostBtn = findViewById(R.id.submit_post_btn);
        submitPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                titleEditText = findViewById(R.id.input_title_edit);
                contentEditText = findViewById(R.id.input_content_edit);
                String titleText = titleEditText.getText().toString().trim();
                String contentText = contentEditText.getText().toString().trim();
                if (titleText.isEmpty() || contentText.isEmpty()) {
                    Snackbar.make(view, "Title and content could not be empty. Please try again.\n",
                            Snackbar.LENGTH_LONG)
                            .setAction("action", null).show();
                    return;
                }
                Toast.makeText(DiscussionInputActivity.this, "Your post has been submitted to forum successfully!\n", Toast.LENGTH_LONG).show();
                submitNewDiscussion(titleText,contentText);
                finish();
            }
        });
    }

    private void getIntentData() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            currentUser = extras.getString("username");
            currentCity = extras.getString("city");
            currentState = extras.getString("state");
        }
    }

    public void submitNewDiscussion(String title,String content) {
//        String randomId = UUID.randomUUID().toString();
        DatabaseReference discussionRef = FirebaseDatabase.getInstance().getReference().child("Discussion");
        Discussion discussion = new Discussion();
        discussion.setTitle(title);
        discussion.setContent(content);
        Timestamp ts = new Timestamp((System.currentTimeMillis()));
        discussion.setTimestamp(String.valueOf(timeFormat.format(ts)));
        discussion.setPlace(currentCity+ ", "+currentState);
        discussion.setUsername(currentUser);
        discussionRef.push().setValue(discussion);
    }

}
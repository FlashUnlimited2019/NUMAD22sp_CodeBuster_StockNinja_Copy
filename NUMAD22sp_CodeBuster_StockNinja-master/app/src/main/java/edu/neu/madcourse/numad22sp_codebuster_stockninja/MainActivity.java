package edu.neu.madcourse.numad22sp_codebuster_stockninja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.Place;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.User;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void goToRegister(View view) {
        Intent intent = new Intent(this, RegisterActivity.class);
        startActivity(intent);
    }


    public void btnLogin(View view) {
        username = findViewById(R.id.textUsername_input);
        password = findViewById(R.id.textPassword_input);
        String usernameS = username.getText().toString().trim();
        String passwordS = password.getText().toString().trim();

        if(usernameS.length() != 0) {
            System.out.println("message: input.........."+usernameS+"---"+passwordS);
        }

        if(usernameS.length() == 0 || passwordS.length() ==0) {
            Toast.makeText(MainActivity.this, "Please enter a valid username and password", Toast.LENGTH_SHORT).show();
            return;
        }


        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");

        userRef.orderByChild("username").equalTo(usernameS)
            .addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (!dataSnapshot.exists()) {
                        Toast.makeText(MainActivity.this, "Username or password incorrect.", Toast.LENGTH_SHORT).show();
                    } else {
                        for (DataSnapshot childSnapshot: dataSnapshot.getChildren()) {
                            User user = childSnapshot.getValue(User.class);
                            if(user.getPassword().equals(passwordS)){
                                Toast.makeText(MainActivity.this, "Successfully Login.", Toast.LENGTH_SHORT).show();
                                handleLogin(user);
                            }else{
                                Toast.makeText(MainActivity.this, "Username or password incorrect.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                }
            });

    }

    public void handleLogin(User user){
        Intent intent = new Intent(this, NavigationActivity.class);
        intent.putExtra("username", user.getUsername());
        intent.putExtra("cash", user.getCash());
        intent.putExtra("city", user.getPlace().getCity());
        intent.putExtra("state", user.getPlace().getState());
        intent.putExtra("country", user.getPlace().getCountry());
        startActivity(intent);
    }




}
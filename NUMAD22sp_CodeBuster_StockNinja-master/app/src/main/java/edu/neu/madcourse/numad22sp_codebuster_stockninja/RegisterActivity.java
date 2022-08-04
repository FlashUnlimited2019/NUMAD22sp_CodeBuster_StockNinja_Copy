package edu.neu.madcourse.numad22sp_codebuster_stockninja;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.Place;
import edu.neu.madcourse.numad22sp_codebuster_stockninja.models.User;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import java.io.IOException;
import java.lang.Object;
import java.util.List;
import java.util.Locale;

import android.location.Geocoder;

public class RegisterActivity extends AppCompatActivity implements LocationListener{

    EditText usernameR;
    EditText passwordR;
    TextView tv_location;

    LocationManager locationManager;
    Place place = new Place("Unknown", "Unknown", "Unknown");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        tv_location =findViewById(R.id.textLocationR_result);

        if(ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(RegisterActivity.this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION
            }, 100);
        }
        getLocation();
    }

    public void registerUser(View view) {
        usernameR = findViewById(R.id.textUsernameR_input);
        passwordR = findViewById(R.id.textPasswordR_input);
        String usernameRS = usernameR.getText().toString().trim();
        String passwordRS = passwordR.getText().toString().trim();

        if(usernameRS.length() == 0 || passwordRS.length() ==0) {
            Toast.makeText(RegisterActivity.this, "Please enter a valid username and password", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users");

        userRef.orderByChild("username").equalTo(usernameRS)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            userRef.child(usernameRS).setValue(new User(usernameRS, passwordRS, place));
                            Toast.makeText(RegisterActivity.this, "Register successfully.", Toast.LENGTH_SHORT).show();
                            backToLogin();
                        } else {
                            Toast.makeText(RegisterActivity.this, "This username already exist.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    public void backToLogin(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }


    /////
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (ContextCompat.checkSelfPermission(RegisterActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "No Permission!", Toast.LENGTH_SHORT).show();
            } else {
                getLocation();
            }
        }
    }

    @SuppressLint("MissingPermission")
    private void getLocation() {
        try{
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, RegisterActivity.this);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        System.out.println("Longitude:"+location.getLongitude());
        System.out.println("Longitude:"+location.getLatitude());

        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(), 5);
            System.out.println("address------------------- "+addresses.toString());
            System.out.println("address------------------- "+addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " +addresses.get(0).getCountryName());

            place.setCity(addresses.get(0).getLocality());
            place.setState(addresses.get(0).getAdminArea());
            place.setCountry(addresses.get(0).getCountryName());
            tv_location.setText(addresses.get(0).getLocality() +", " + addresses.get(0).getAdminArea() + ", " +addresses.get(0).getCountryName());
        } catch (Exception e) {
            Toast.makeText(this, "Unable to get precise location.", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

    }
}
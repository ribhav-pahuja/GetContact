package com.ribhav.getcontact;

import android.Manifest;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    EditText password;
    Button button;
    TextView tv;
    String pass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SharedPreferences sharedPref = MainActivity.this.getSharedPreferences("myPrefs",Context.MODE_PRIVATE);
        password = findViewById(R.id.ET);
        button = findViewById(R.id.button);
        tv = findViewById(R.id.tv);
        //this.getSharedPreferences("myPrefs", Context.MODE_PRIVATE).edit().clear().apply();
        tv.setText("Your current password is : " + sharedPref.getString("Password","1234"));
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            pass = password.getText().toString();
            if(pass.equals("")){
                Toast.makeText(getApplicationContext(), "Cannot set an empty password.", Toast.LENGTH_SHORT).show();
            }else {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("Password", pass);
                editor.apply();
                Toast.makeText(getApplicationContext(), "Your password now is :- " + pass, Toast.LENGTH_SHORT).show();
                tv.setText("Your current password is : " + sharedPref.getString("Password", "1234"));
            }
            }
        });
        boolean bool = checkAndRequestPermissions();
    }

    private  boolean checkAndRequestPermissions() {
        int permissionSendMessage = ContextCompat.checkSelfPermission(this,
                Manifest.permission.SEND_SMS);

        int receiveSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECEIVE_SMS);

        int readC = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_CONTACTS);

        int readSMS = ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_SMS);
        List<String> listPermissionsNeeded = new ArrayList<>();

        if (receiveSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECEIVE_MMS);
        }
        if (readSMS != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (readC != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_CONTACTS);
        }
        if (permissionSendMessage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.SEND_SMS);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this,
                    listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),
                    REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
}
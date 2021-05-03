package com.daon.onorder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonObject;
import com.sam4s.printer.Sam4sPrint;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    LinearLayout loginBtn;
    LinearLayout loginBtn2;
    EditText id;
    EditText pass;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Sam4sPrint printer;
    Sam4sPrint printer2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        editor = pref.edit();

        id = findViewById(R.id.edit_id);
        pass = findViewById(R.id.edit_pw);

        loginBtn = findViewById(R.id.loginactivity_btn_login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_id = "";
                String str_pass = "1234";
                editor.putString("id", str_id);
                editor.putString("table", "4");
                editor.putString("fcm", "fcm");
                editor.putString("storecode", "hdmg_test");
                editor.putString("storename", "daon_showroom");
                editor.putString("addr", "경기도 부천시");
                editor.putString("storename", "다온시스템");
                editor.commit();
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }



        });

        loginBtn2 = findViewById(R.id.loginactivity_btn_login2);
        loginBtn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str_id = "";
                String str_pass = "1234";
                editor.putString("id", str_id);
                editor.putString("table", "4");
                editor.putString("fcm", "fcm");
                editor.putString("storecode", "ots_test");
                editor.putString("storename", "daon_showroom");
                editor.putString("addr", "경기도 부천시");
                editor.putString("storename", "다온시스템");
                editor.commit();
                Intent intent = new Intent(LoginActivity.this, MenuActivity.class);
                startActivity(intent);
                finish();
            }


        });
    }


    public void setPrint() {
        try {
            printer = new Sam4sPrint();
            printer2 = new Sam4sPrint();
            try {

                printer.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "172.30.1.45", 9100);
                printer.resetPrinter();
                printer2.openPrinter(Sam4sPrint.DEVTYPE_ETHERNET, "172.30.1.54", 9100);
                printer2.resetPrinter();

                AdminApplication.setPrinter(printer, printer2);


            } catch (Exception e) {
                e.printStackTrace();
                Log.d("daon", "print error1 = " + e.getMessage());
            }

            if (!printer.IsConnected(Sam4sPrint.DEVTYPE_ETHERNET)) {
                try {

                    Log.d("daon", "print error1 = " + printer.getPrinterStatus());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
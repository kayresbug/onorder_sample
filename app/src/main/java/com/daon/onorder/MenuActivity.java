package com.daon.onorder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.daon.onorder.Model.MenuModel;
import com.daon.onorder.Model.OrderModel;
import com.daon.onorder.Model.PrintOrderModel;
import com.daon.onorder.Model.SampleCategoryModel;
import com.daon.onorder.Model.SampleMenuModel;
import com.daon.onorder.fragment.DetailFragment;
import com.daon.onorder.fragment.menu1Fragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuActivity extends AppCompatActivity {
    static TextView order_price;
    static int all_price = 0;
    RecyclerView.LayoutManager layoutManager;
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    DatabaseReference myRef = database.getReference("order");

    TextView menu1;
    TextView menu2;
    TextView menu3;
    TextView menu4;
    TextView menu5;
    TextView menu6;
    TextView menu7;
    TextView menu8;
    TextView menu9;
    TextView table;
    ImageView call;
    ImageView call_driver;
    ImageView cart;
    RecyclerView recyclerView;
    RecyclerView order_recycler;
    ArrayList<TextView> menuBtn = new ArrayList<>();
    ArrayList<Object> menu_list = new ArrayList<>();
    ArrayList<Object> menu_listsize = new ArrayList<>();
    MenuAdapter adapter;
    OrderAdapter orderAdapter;
    LinearLayoutManager layoutManager2;
    RelativeLayout payment_layout;
    LinearLayout bodyLayout;
    String removePrice = "";
    FragmentManager fragmentManager;
    SharedPreferences pref;
    TaskTimer taskTimer = new TaskTimer(); // extends AsyncTask
    int basic = 300;
    int[] array_count1;
    /*
     * ??????????????? ??????
     * ????????? ????????????????????? ?????? ????????? ??????????????? ??????????????? ?????????????????? ???????????? ????????????
     */
    String prevAuthNum = ""; //?????? ????????? ????????????
    String prevAuthDate = "";//?????? ????????? ????????????

    String prevClassfication = "";

    String vanTr = "";          //VanTr?????????(????????? ??????) ?????????????????? ??????
    String prevCardNo = "";     //VanTr?????????(????????? ??????) ????????????????????? ??????

    ArrayList<OrderModel> order_list = new ArrayList<>();
    ArrayList<OrderModel> cart_list = new ArrayList<>();
    JsonArray categoryArray = new JsonArray();
    JsonArray menuArray;
    menu1Fragment menuFragment;
    Context context;
    boolean isOrder = false;
    ArrayList<SampleCategoryModel> sampleCategoryModels = new ArrayList<>();
    ArrayList<SampleMenuModel> sampleMenuModels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        context = this;
        pref = getSharedPreferences("pref", MODE_PRIVATE);
        table = findViewById(R.id.menuactivity_text_table);
        table.setText(pref.getString("table", ""));
        fragmentManager = getSupportFragmentManager();
        menuFragment = new menu1Fragment();

        menu1 = findViewById(R.id.menuactivity_text_btn1);
        menu2 = findViewById(R.id.menuactivity_text_btn2);
        menu3 = findViewById(R.id.menuactivity_text_btn3);
        menu4 = findViewById(R.id.menuactivity_text_btn4);
        menu5 = findViewById(R.id.menuactivity_text_btn5);
        menu6 = findViewById(R.id.menuactivity_text_btn6);
        menu7 = findViewById(R.id.menuactivity_text_btn7);
        menu8 = findViewById(R.id.menuactivity_text_btn8);
        menu9 = findViewById(R.id.menuactivity_text_btn9);
        call = findViewById(R.id.menuactivity_img_call);
        call_driver = findViewById(R.id.menuactivity_img_calldriver);
        JSONArray jsonArray = new JSONArray();
        // ????????????????????? ????????? ????????? ????????? ??????.
        ArrayList<TextView> menuBtn = new ArrayList<TextView>();
        menuBtn.add(menu1);
        menuBtn.add(menu2);
        menuBtn.add(menu3);
        menuBtn.add(menu4);
        menuBtn.add(menu5);
        menuBtn.add(menu6);
        menuBtn.add(menu7);
        menuBtn.add(menu8);
        menuBtn.add(menu9);

        bodyLayout = findViewById(R.id.menuactivity_layout_menu);

        setCategory(pref.getString("storecode", ""));

        for (int y = 0; y < 9; y++) {
            for (int i = 0; i < sampleCategoryModels.size(); i++) {
                menuBtn.get(i).setText(sampleCategoryModels.get(i).getName());
                menuBtn.get(i).setVisibility(View.VISIBLE);
            }
        }
        setLiset2(pref.getString("storecode", ""));

        cart = findViewById(R.id.cart);
        cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        order_price = findViewById(R.id.menuactivity_text_price);
        payment_layout = findViewById(R.id.menuactivity_layout_payment);
        payment_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);
//                setPayment(String.valueOf(all_price), "credit");
                Log.d("daon_test", "isorder = " + isOrder);
                if (!isOrder) {
                    if (order_list.size() > 0) {
                        sendData();
//                        isOrder = true;
//                        setPayment(String.valueOf(all_price), "credit");

                    }
                }

            }
        });

        // ????????????????????? LinearLayoutManager ?????? ??????.
//        recyclerView = findViewById(R.id.recycler1);
//        recyclerView.setHasFixedSize(true);

        order_recycler = findViewById(R.id.order_recycler);
        order_recycler.setHasFixedSize(true);

//        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)) ;
//        layoutManager = new LoopingLayoutManager(
//                this,                           // Pass the context.
//                LoopingLayoutManager.HORIZONTAL,  // Pass the orientation. Vertical by default.
//                false                           // Pass whether the views are laid out in reverse.
//                // False by default.
//        );


//        for (int i=0; i<10; i++) {
//            list.add(String.format("TEXT %d", i)) ;
//        }


        findViewById(R.id.menuactivity_layout_btn1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffcf5d"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));

                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                bundle.putInt("position", 0);
                menuFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });

        findViewById(R.id.menuactivity_layout_btn2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(1));
                menuFragment.setArguments(bundle);
                if (pref.getString("storecode", "").equals("hdmg_test")){
                    bundle.putInt("position", 3);
                }else if (pref.getString("storecode", "").equals("ots_test")) {
                    bundle.putInt("position", 19);
                }


                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();

                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffcf5d"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
//                adapter.notifyDataSetChanged();
            }
        });
        findViewById(R.id.menuactivity_layout_btn3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffcf5d"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                if (pref.getString("storecode", "").equals("hdmg_test")){
                    bundle.putInt("position", 7);
                }else if (pref.getString("storecode", "").equals("ots_test")) {
                    bundle.putInt("position", 43);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });
        findViewById(R.id.menuactivity_layout_btn4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffcf5d"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                if (pref.getString("storecode", "").equals("hdmg_test")){
                    bundle.putInt("position", 15);
                }else if (pref.getString("storecode", "").equals("ots_test")) {
                    bundle.putInt("position", 58);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();


            }
        });
        findViewById(R.id.menuactivity_layout_btn5).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffcf5d"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                if (pref.getString("storecode", "").equals("hdmg_test")){
                    bundle.putInt("position", 19);
                }else if (pref.getString("storecode", "").equals("ots_test")) {
                    bundle.putInt("position", 64);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });
        findViewById(R.id.menuactivity_layout_btn6).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffcf5d"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                if (pref.getString("storecode", "").equals("hdmg_test")){
                    bundle.putInt("position", 22);
                }else if (pref.getString("storecode", "").equals("ots_test")) {
                    bundle.putInt("position", 73);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });
        findViewById(R.id.menuactivity_layout_btn7).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffcf5d"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                if (pref.getString("storecode", "").equals("hdmg_test")){
                    bundle.putInt("position", 3);
                }else if (pref.getString("storecode", "").equals("ots_test")) {
                    bundle.putInt("position", 77);
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });
        findViewById(R.id.menuactivity_layout_btn8).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffcf5d"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(0));
                menuFragment.setArguments(bundle);
                bundle.putInt("position", array_count1[1]);
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });
        findViewById(R.id.menuactivity_layout_btn9).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);

                menu1Fragment menuFragment = new menu1Fragment();
                Bundle bundle = new Bundle();
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffcf5d"));
                //put your ArrayList data in bundle
                bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(8));
                menuFragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);
                Intent intent = new Intent(MenuActivity.this, CallActivity.class);
                startActivity(intent);
            }
        });
        call_driver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                taskTimer.setTime(basic);
                Intent intent = new Intent(MenuActivity.this, CallDriverActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        // put your code here...
        Log.d("daon_test", "on resume!!!" + taskTimer.getStatus());
//        taskTimer.cancel(true);
        if (taskTimer.getStatus() != AsyncTask.Status.RUNNING) {
            taskTimer = new TaskTimer();
            taskTimer.setTime(10, context);
            taskTimer.execute("");
        }

    }

    public void setList() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://15.164.232.164:5000/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);
        interfaceApi.getMenu(pref.getString("storecode", "")).enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                if (response.isSuccessful()) {
                    JsonArray jsonArray1 = response.body();
                    ArrayList<MenuModel> list = new ArrayList<>();
                    array_count1 = new int[categoryArray.size()];
                    for (int z = 0; z < categoryArray.size(); z++) {
                        String aa = categoryArray.get(z).toString();
                        aa = aa.replace("\\", "");
                        aa = aa.substring(2, aa.length() - 2);
                        JSONObject jsonObject = null;
                        try {
                            jsonObject = new JSONObject(aa);
                            int checkcount = 0;
                            for (int i = 0; i < jsonArray1.size(); i++) {

                                MenuModel menuModel = new MenuModel();

                                String bb = jsonArray1.get(i).toString();
                                bb = bb.replace("\\", "");
                                bb = bb.substring(2, bb.length() - 2);
                                Log.d("daon_Test", bb);
                                JSONObject menuObject = new JSONObject(bb);
                                if (jsonObject.get("code").equals(menuObject.get("ctgcode"))) {
                                    checkcount++;
                                    menuModel.setName(menuObject.get("name").toString().replace("\"", ""));
                                    menuModel.setPicurl(menuObject.get("picurl").toString().replace("\"", ""));
                                    menuModel.setPrice(menuObject.get("price").toString().replace("\"", ""));
                                    menuModel.setCode(menuObject.get("code").toString().replace("\"", ""));
                                    menuModel.setInfo(menuObject.get("info").toString().replace("\"", ""));
                                    Log.d("daon", menuObject.get("name").toString());
                                    list.add(menuModel);
                                }
                                menu_listsize.add(list);
                            }
                            if (z == 0) {
                                array_count1[z] = checkcount;
                            } else {
                                array_count1[z] = array_count1[z - 1] + checkcount;
                            }
                            menu_list.add(list);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    for (int i = 0; i < array_count1.length; i++) {
                        Log.d("daon_test", "array size = " + array_count1[i]);
                    }
                    menu1Fragment menuFragment = new menu1Fragment();
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("list", list);
                    menuFragment.setArguments(bundle);
                    fragmentManager.beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                t.printStackTrace();
                Log.d("daon", "error = " + t.getMessage());
            }
        });
    }

    public void setPosition(int count) {
        ArrayList<MenuModel> list = new ArrayList<>();
        int menu_count = 0;
        if (pref.getString("storecode", "").equals("hdmg_test")) {

            if (count == 3) {
                menu1.setTextColor(Color.parseColor("#ffcf5d"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > 3 && count < 7) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffcf5d"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > 7 && count < 15) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffcf5d"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > 15 && count < 18) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffcf5d"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > 18 && count < 21) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffcf5d"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > 22) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffcf5d"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > array_count1[5] && count < array_count1[6]) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffcf5d"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            }
        }else if (pref.getString("storecode", "").equals("ots_test")){
            if (count < 18) {
                menu1.setTextColor(Color.parseColor("#ffcf5d"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > 18 && count < 42) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffcf5d"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > 42 && count < 57) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffcf5d"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > 57 && count < 63) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffcf5d"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > 63 && count < 72) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffcf5d"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > 72 && count < 76) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffcf5d"));
                menu7.setTextColor(Color.parseColor("#ffffff"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            } else if (count > 76) {
                menu1.setTextColor(Color.parseColor("#ffffff"));
                menu2.setTextColor(Color.parseColor("#ffffff"));
                menu3.setTextColor(Color.parseColor("#ffffff"));
                menu4.setTextColor(Color.parseColor("#ffffff"));
                menu5.setTextColor(Color.parseColor("#ffffff"));
                menu6.setTextColor(Color.parseColor("#ffffff"));
                menu7.setTextColor(Color.parseColor("#ffcf5d"));
                menu8.setTextColor(Color.parseColor("#ffffff"));
                menu9.setTextColor(Color.parseColor("#ffffff"));
            }
        }

    }

    public void callCart(ArrayList<OrderModel> array) {
        orderAdapter = new OrderAdapter(MenuActivity.this, array);
        order_recycler.setAdapter(orderAdapter);
        layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        order_recycler.setLayoutManager(layoutManager2);
        Log.d("daon", "remove_item = " + all_price);

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        int price = 0;
        for (int i = 0; i < array.size(); i++) {
            price = price + (Integer.parseInt(array.get(i).getPrice()) * Integer.parseInt(array.get(i).getCount()));
        }
        all_price = price;
        String formattedStringPrice = myFormatter.format(all_price);

        order_price.setText("??? " + formattedStringPrice + "??? ????????????");
    }

    public void closeCart() {
        menu1Fragment menuFragment = new menu1Fragment();
        Bundle bundle = new Bundle();

        //put your ArrayList data in bundle
        bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(1));
        menuFragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
    }

    public void callDetail(int i) {
        Log.d("daon_test", "Call = " + i);
        DetailFragment detailFragment = new DetailFragment();
        Bundle bundle = new Bundle();
        //put your ArrayList data in bundle
        bundle.putSerializable("list", (Serializable) (ArrayList<MenuModel>) menu_listsize.get(i));
        bundle.putInt("position", i);
        detailFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.menuactivity_layout_menu, detailFragment).commit();
    }

    public void closeDetail(int position) {
        menu1Fragment menuFragment = new menu1Fragment();
        Bundle bundle = new Bundle();

        //put your ArrayList data in bundle
        bundle.putSerializable("list", (ArrayList<MenuModel>) menu_list.get(1));
        menuFragment.setArguments(bundle);
        bundle.putInt("position", position);
        getSupportFragmentManager().beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();
    }

    public void callItem(String menu, String price, String url, String code, String count) {

        Log.d("daon", "call = " + menu);
        OrderModel order = new OrderModel();
        boolean isCount = false;
        if (order_list.size() > 0) {
            for (int i = 0; i < order_list.size(); i++) {
                Log.d("daon_test", "name = " + order_list.get(i).getName());
                if (order_list.get(i).getName().equals(menu)) {
                    order.setCount(String.valueOf(Integer.parseInt(order_list.get(i).getCount()) + Integer.parseInt(count)));
                    order.setName(order_list.get(i).getName());
                    order.setPrice(price);
                    order.setUrl(url);
                    order.setMenuno(code);
                    order_list.set(i, order);
                    isCount = true;
                    break;
                }
            }
            if (!isCount) {
                order.setName(menu);
                order.setCount(count);
                order.setPrice(price);
                order.setUrl(url);
                order.setMenuno(code);
                order_list.add(order);
            }
        } else {
            order.setName(menu);
            order.setCount(count);
            order.setPrice(price);
            order.setUrl(url);
            order.setMenuno(code);
            order_list.add(order);
        }
        cart_list.clear();
        cart_list.addAll(order_list);
        orderAdapter = new OrderAdapter(MenuActivity.this, order_list);
        order_recycler.setAdapter(orderAdapter);
        layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        order_recycler.setLayoutManager(layoutManager2);
        all_price = all_price + Integer.parseInt(price);
        Log.d("daon", "remove_item = " + all_price);

        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(all_price);

        order_price.setText("??? " + formattedStringPrice + "??? ????????????");

    }

    public void removeMenu(int price) {
        all_price = all_price - price;

        if (all_price > 0) {
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            String formattedStringPrice = myFormatter.format(all_price);
            order_price.setText("??? " + formattedStringPrice + "??? ????????????");
        } else {
            all_price = 0;
            order_price.setText("??? 0??? ????????????");
            isOrder = false;
        }


    }

    public void sendData() {
        isOrder = false;

        String order = "";
        for (int i = 0; i < order_list.size(); i++) {
            String order_name = order_list.get(i).getName();
            order_name = order_name.replace("?????????", "");
            order = order + order_name + " " + order_list.get(i).getCount() + "???" + "\n\n";
        }
        if (order_list.size() > 0) {
//            sendFCM(order);
//            sendFirebaseOrder(order);
            removePrice = String.valueOf(all_price);
            orderAdapter.removeData();
            all_price = 0;
            order_price.setText("??? 0??? ????????????");
            isOrder = false;
            Toast toast = Toast.makeText(MenuActivity.this, "????????? ?????????????????????.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            isOrder = false;
        }

    }

    public void sendFirebaseOrder(String order) {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String time = format.format(calendar.getTime());
        String time2 = format2.format(calendar.getTime());
        PrintOrderModel printOrderModel = new PrintOrderModel(pref.getString("table", "") + "??? ??????", order, time, "x", "order");

        myRef.child(pref.getString("storename", "")).child(time2).push().setValue(printOrderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    removePrice = String.valueOf(all_price);
                    orderAdapter.removeData();
                    all_price = 0;
                    order_price.setText("??? 0??? ????????????");
                    isOrder = false;
                    Toast toast = Toast.makeText(MenuActivity.this, "????????? ?????????????????????.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(MenuActivity.this, "?????? ????????? ?????????.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                }
            }
        });
    }

    public void sendOrder() {
        Long tsLong = System.currentTimeMillis();
        String ts = tsLong.toString();
        String order = "10";
        for (int i = 0; i < order_list.size(); i++) {

            order = order + order_list.get(i).getName() + " " + order_list.get(i).getCount() + "???" + "\n";
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://15.164.232.164:5000/")
                    .addConverterFactory(new NullOnEmptyConverterFactory())
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
            InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);

            interfaceApi.postOrder(pref.getString("storecode", ""), pref.getString("table", ""), order_list.get(i).getMenuno(), order_list.get(i).getPrice(), order_list.get(i).getCount(),
                    String.valueOf((Integer.parseInt(order_list.get(i).getPrice())) * (Integer.parseInt(order_list.get(i).getCount()))), "Card", order_list.get(i).getName(), ts,
                    prevAuthNum, prevAuthDate, vanTr, prevCardNo).enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        Log.d("daon", "success = ");

                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    t.printStackTrace();
                    Log.d("daon", "error = " + t.getMessage());
                }
            });
        }
//        sendFCM(order);
    }


    public void setPayment(String amount, String type) {
        Log.d("daon", "payment = " + amount);
        amount = "1004";
//        prevAuthNum = "71525617    ";
//        prevAuthDate = "210126";
        int i_amount = Integer.parseInt(amount);
        int tax = (i_amount / 100) * 10;
        int aamount = (i_amount / 100) * 90;
        HashMap<String, byte[]> m_hash = new HashMap<String, byte[]>();
        /*?????? ????????????*/
        m_hash.put("TelegramType", "0200".getBytes());                                    // ?????? ?????? ,  ??????(0200) ??????(0420)
        m_hash.put("DPTID", "DPT0TEST03".getBytes());                                     // ??????????????? , ????????????????????? DPT0TEST03
        m_hash.put("PosEntry", "S".getBytes());                                           // Pos Entry Mode , ??????????????? ?????? ??? ?????????????????? 'K'??????
        m_hash.put("PayType", "00".getBytes());                                           // [??????]???????????????(default '00') [??????]???????????????
        m_hash.put("TotalAmount", getStrMoneytoTgAmount(amount)); // ?????????
        m_hash.put("Amount", getStrMoneytoTgAmount(String.valueOf(amount)));      // ???????????? = ????????? - ????????? - ?????????
        m_hash.put("ServicAmount", getStrMoneytoTgAmount("0"));                           // ?????????
        m_hash.put("TaxAmount", getStrMoneytoTgAmount("0"));                              // ?????????
        m_hash.put("FreeAmount", getStrMoneytoTgAmount("0"));                             // ?????? 0??????  / ?????? 1004?????? ?????? ????????? 1004??? ?????????(ServiceAmount),?????????(TaxAmount) 0??? ???????????? 1004???/ ??????(FreeAmount)  1004???
        m_hash.put("AuthNum", "".getBytes());                                            //????????? ???????????? , ??????????????? ??????
        m_hash.put("Authdate", "".getBytes());                                           //????????? ???????????? , ??????????????? ??????
        m_hash.put("Filler", "".getBytes());                                              // ???????????? - ????????? ??????????????? ????????????
        m_hash.put("SignTrans", "N".getBytes());                                          // ???????????? ??????, ?????????(N) 50000??? ????????? ?????? "N" => "S"?????? ??????
        if (Long.parseLong(amount) > 50000)
            m_hash.put("SignTrans", "S".getBytes());                                          // ???????????? ??????, ?????????(N) 50000??? ????????? ?????? "N" => "S"?????? ??????
        m_hash.put("PlayType", "D".getBytes());                                           // ????????????,  ??????????????? ?????????(D)
        m_hash.put("CardType", "".getBytes());                                            // ???????????? ???????????? (?????? ????????????), "" ??????
        m_hash.put("BranchNM", "".getBytes());                                            // ???????????? ,?????? ?????? ?????????????????? ?????? , ????????? "" ??????
        m_hash.put("BIZNO", "".getBytes());                                               // ??????????????? ,KSNET ?????? ????????? ????????????????????? ??????, ?????? ???"" ??????
        m_hash.put("TransType", "".getBytes());                                           // "" ??????
        m_hash.put("AutoClose_Time", "30".getBytes());                                    // ????????? ?????? ?????? ??? ?????? ?????? ex)30??? ??? ??????
        /*?????? ????????????*/
        //m_hash.put("SubBIZNO","".getBytes());                                            // ?????? ??????????????? ,??????????????? ??????????????? ?????? ??? ????????? ??????
        //m_hash.put("Device_PortName","/dev/bus/usb/001/002".getBytes());                 //????????? ?????? ?????? ?????? ??? UsbDevice ??????????????? getDeviceName() ??????????????? , ?????????????????? ????????????
        //m_hash.put("EncryptSign","A!B@C#D4".getBytes());                                 // SignTrans "T"????????? KSCIC?????? ?????? ???????????? ?????? ?????????????????? ????????????, ??????????????????

        ComponentName compName = new ComponentName("ks.kscic_ksr01", "ks.kscic_ksr01.PaymentDlg");

        Intent intent = new Intent(Intent.ACTION_MAIN);

        if (type.equals("credit")) {
            m_hash.put("ReceiptNo", "X".getBytes());  // ??????????????? ????????????, ???????????? ??? "X", ??????????????? ??????????????? "", Key-In????????? "??????????????? ??? ??????" -> Pos Entry Mode 'K;
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        } else if (type.equals("cancle")) {

            //???????????? ?????? ???
            m_hash.put("TelegramType", "0420".getBytes());  // ?????? ?????? ,  ??????(0200) ??????(0420)
            m_hash.put("ReceiptNo", "X".getBytes());        // ??????????????? ????????????, ???????????? ??? "X", ??????????????? ??????????????? "", Key-In????????? "??????????????? ??? ??????" -> Pos Entry Mode 'K;
            m_hash.put("AuthNum", prevAuthNum.getBytes());
            m_hash.put("Authdate", prevAuthDate.getBytes());
        } else if (type.equals("cancleNocard")) {
            //?????? ????????? ?????? ?????????
            m_hash.put("TelegramType", "0420".getBytes()); // ?????? ?????? ,  ??????(0200) ??????(0420)
            m_hash.put("ReceiptNo", "X".getBytes());      // ??????????????? ????????????, ???????????? ??? "X", ??????????????? ??????????????? "", Key-In????????? "??????????????? ??? ??????" -> Pos Entry Mode 'K;
            m_hash.put("VanTr", vanTr.getBytes());        // ?????????????????? , ????????? ????????? ?????? ?????? ??????
            m_hash.put("Cardbin", prevCardNo.getBytes());
            m_hash.put("AuthNum", prevAuthNum.getBytes());
            m_hash.put("Authdate", prevAuthDate.getBytes());
        }

        intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(compName);
        intent.putExtra("AdminInfo_Hash", m_hash);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {
            HashMap<String, String> m_hash = (HashMap<String, String>) data.getSerializableExtra("result");
            try {
                Thread.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (m_hash != null) {
                prevAuthNum = m_hash.get("AuthNum");
                prevAuthDate = m_hash.get("Authdate");
                prevClassfication = m_hash.get("Classification");

                vanTr = m_hash.get("VanTr");
                prevCardNo = m_hash.get("CardNo");

                //KTC ????????? ??????
                Log.d("payment", "recv [Classification]:: " + (m_hash.get("Classification")));
                System.out.println("recv [TelegramType]:: " + (m_hash.get("TelegramType")));
                System.out.println("recv [Dpt_Id]:: " + (m_hash.get("Dpt_Id")));
                System.out.println("recv [Enterprise_Info]:: " + (m_hash.get("Enterprise_Info")));
                System.out.println("recv [Full_Text_Num]:: " + (m_hash.get("Full_Text_Num")));
                System.out.println("recv [Status]:: " + (m_hash.get("Status")));
                System.out.println("recv [CardType]:: " + (m_hash.get("CardType")));              //'N':???????????? 'G':??????????????? 'C':???????????? 'P'???????????? 'P'????????? ?????????
                System.out.println("recv [Authdate]:: " + (m_hash.get("Authdate")));
                System.out.println("recv [Message1]:: " + (m_hash.get("Message1")));
                System.out.println("recv [Message2]:: " + (m_hash.get("Message2")));
                System.out.println("recv [VanTr]:: " + (m_hash.get("VanTr")));
                System.out.println("recv [AuthNum]:: " + (m_hash.get("AuthNum")));
                System.out.println("recv [FranchiseID]:: " + (m_hash.get("FranchiseID")));
                System.out.println("recv [IssueCode]:: " + (m_hash.get("IssueCode")));
                System.out.println("recv [CardName]:: " + (m_hash.get("CardName")));
                System.out.println("recv [PurchaseCode]:: " + (m_hash.get("PurchaseCode")));
                System.out.println("recv [PurchaseName]:: " + (m_hash.get("PurchaseName")));
                System.out.println("recv [Remain]:: " + (m_hash.get("Remain")));
                System.out.println("recv [point1]:: " + (m_hash.get("point1")));
                System.out.println("recv [point2]:: " + (m_hash.get("point2")));
                System.out.println("recv [point3]:: " + (m_hash.get("point3")));
                System.out.println("recv [notice1]:: " + (m_hash.get("notice1")));
                System.out.println("recv [notice2]:: " + (m_hash.get("notice2")));
                System.out.println("recv [CardNo]:: " + (m_hash.get("CardNo")));
            }

//            Toast.makeText(this, "??????" + (m_hash.get("AuthNum")), Toast.LENGTH_LONG).show();
//
//            Retrofit retrofit = new Retrofit.Builder()
//                    .baseUrl("http://15.164.232.164:5000/")
//                    .addConverterFactory(new NullOnEmptyConverterFactory())
//                    .addConverterFactory(GsonConverterFactory.create())
//                    .build();
//            InterfaceApi interfaceApi = retrofit.create(InterfaceApi.class);
//            interfaceApi.payment(pref.getString("storecode", ""), m_hash.get("Classification"), m_hash.get("TelegramType"), m_hash.get("Dpt_Id"), m_hash.get("Enterprise_Info"), m_hash.get("Full_Text_Num"),
//                    m_hash.get("Status"), m_hash.get("Authdate"), m_hash.get("Message1"), m_hash.get("Message2"), m_hash.get("AuthNum"), m_hash.get("FranchiseID"),
//                    m_hash.get("IssueCode"), m_hash.get("CardName"), m_hash.get("PurchaseCode"), m_hash.get("PurchaseName"), m_hash.get("Remain"),
//                    m_hash.get("point1"), m_hash.get("point2"), m_hash.get("point3"), m_hash.get("notice1"), m_hash.get("notice2"), m_hash.get("CardType"),
//                    m_hash.get("CardNo"), m_hash.get("SWModelNum"), m_hash.get("ReaderModelNum"), m_hash.get("VanTr"), m_hash.get("Cardbin")).enqueue(new Callback<JsonObject>() {
//                @Override
//                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                    Log.d("daon", "isSuccessful = " + response.isSuccessful());
//                    if (response.isSuccessful()) {
//                        sendOrder();
//                        prevAuthNum = m_hash.get("AuthNum");
//                        prevAuthDate = m_hash.get("Authdate");
//                        removePrice = String.valueOf(all_price);
//                        orderAdapter.removeData();
//                        all_price = 0;
//                        order_price.setText("??? 0??? ????????????");
//                        isOrder = false;
//
//
//                    }
//
//                }
//
//                @Override
//                public void onFailure(Call<JsonObject> call, Throwable t) {
//                    t.printStackTrace();
//                }
//            });

            sendData();
        } else if (resultCode == RESULT_FIRST_USER && data != null) {
            //??????????????????IC ???????????? ?????? ????????? ???????????? ?????? ?????? ??????
            //Toast.makeText(this, "??????????????????IC ?????? ????????? ???????????? ??? ??????????????? ????????????", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(this, "????????? ?????? ??????", Toast.LENGTH_LONG).show();
        }
        // ????????? ????????? ?????? ?????? ??????
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "??? ?????? ??????", Toast.LENGTH_LONG).show();
        }

    }

    public byte[] getStrMoneytoTgAmount(String Money) {
        byte[] TgAmount = null;
        if (Money.length() == 0) {
//            Toast.makeText(MainActivity.this, "????????? ???????????? ????????????", Toast.LENGTH_SHORT).show();
            return "000000001004".getBytes();
        } else {
            Long longMoney = Long.parseLong(Money.replace(",", ""));
            Money = String.format("%012d", longMoney);

            TgAmount = Money.getBytes();
            return TgAmount;
        }
    }

    public void setCategory(String storecode) {
        if (storecode.equals("hdmg_test")) {
            sampleCategoryModels.add(new SampleCategoryModel("120", storecode, "???????????????"));
            sampleCategoryModels.add(new SampleCategoryModel("121", storecode, "???????????????"));
            sampleCategoryModels.add(new SampleCategoryModel("122", storecode, "???????????????"));
            sampleCategoryModels.add(new SampleCategoryModel("123", storecode, "??????"));
            sampleCategoryModels.add(new SampleCategoryModel("124", storecode, "?????????"));
            sampleCategoryModels.add(new SampleCategoryModel("125", storecode, "????????????"));
        }else if (storecode.equals("ots_test")){
            sampleCategoryModels.add(new SampleCategoryModel("11", storecode, "???????????????"));
            sampleCategoryModels.add(new SampleCategoryModel("12", storecode, "??????"));
            sampleCategoryModels.add(new SampleCategoryModel("13", storecode, "????????????"));
            sampleCategoryModels.add(new SampleCategoryModel("14", storecode, "??????"));
            sampleCategoryModels.add(new SampleCategoryModel("15", storecode, "?????????"));
            sampleCategoryModels.add(new SampleCategoryModel("16", storecode, "??????"));
            sampleCategoryModels.add(new SampleCategoryModel("17", storecode, "??????"));
//            sampleCategoryModels.add(new SampleCategoryModel("19", storecode, "?????????"));

        }
    }

    public void setLiset2(String storecode) {
        if (storecode.equals("hdmg_test")) {
            sampleMenuModels.add(new SampleMenuModel("???????????????????????????", "b_1", "3000", "9000", "", "120"));
            sampleMenuModels.add(new SampleMenuModel("????????????????????????", "b_2", "3002", "10000", "", "120"));
            sampleMenuModels.add(new SampleMenuModel("??????????????????????????????", "b_3", "3003", "13000", "", "121"));
            sampleMenuModels.add(new SampleMenuModel("?????????????????????????????????", "b_4", "3004", "12000", "", "121"));
            sampleMenuModels.add(new SampleMenuModel("?????????????????????????????????", "b_5", "3005", "10000", "", "121"));
            sampleMenuModels.add(new SampleMenuModel("??????????????????????????????(???)", "b_6", "3006", "18000", "", "121"));
            sampleMenuModels.add(new SampleMenuModel("??????????????????????????????(???)", "b_6", "3007", "26000", "", "121"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "b_7", "3008", "1000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "b_8", "3009", "1000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "b_9", "3010", "2000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "b_10", "3011", "2000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "b_11", "3012", "2000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "b_12", "3013", "2000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "b_13", "3014", "1000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "b_14", "3015", "1000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "b_15", "3016", "1000", "", "123"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "b_16", "3017", "2000", "", "123"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "b_17", "3018", "2000", "", "123"));
            sampleMenuModels.add(new SampleMenuModel("?????? ???????????????", "b_18", "3019", "3000", "", "123"));
            sampleMenuModels.add(new SampleMenuModel("?????? ?????????", "b_19", "3020", "4000", "", "124"));
            sampleMenuModels.add(new SampleMenuModel("?????? ??????", "b_20", "3021", "1000", "", "124"));
            sampleMenuModels.add(new SampleMenuModel("????????? ??????", "b_21", "3022", "4000", "", "124"));
            sampleMenuModels.add(new SampleMenuModel("?????????17???", "b_22", "3023", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("?????????21???", "b_23", "3024", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("??????", "b_24", "3025", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "b_25", "3026", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("??????", "b_27", "3027", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("??????", "b_28", "3028", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "b_29", "3029", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("??????", "b_30", "3030", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "b_31", "3031", "4000", "", "125"));

        }else if (storecode.equals("ots_test")) {
            sampleMenuModels.add(new SampleMenuModel("????????????????????????", "a_1", "100", "27900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("????????????????????????(??????)", "a_1", "101", "27900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "a_1", "102", "12000", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("?????????????????????", "a_2", "100", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("?????????????????????(??????)", "a_2", "101", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "a_2", "102", "12000", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "a_3", "100", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("???????????????(??????)", "a_3", "101", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "a_3", "102", "12000", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("???????????? ?????????", "a_4", "100", "9900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("???????????? ?????????", "a_4", "101", "18900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("???????????? ?????????+????????????", "a_4", "102", "18900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "a_5", "102", "9900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("?????? ???????????? ????????? ??????", "a_6", "102", "14900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("????????? ????????? ??????", "a_7", "102", "14900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("????????????+??????+??????", "a_8", "102", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("????????????+??????+??????(??????)", "a_8", "102", "23900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("????????????+?????????+??????", "a_9", "102", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("????????????+?????????+??????(??????)", "a_9", "102", "23900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("????????? ???????????? ??????", "a_10", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("????????? ???????????? ??????(??????)", "a_10", "102", "19900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("????????? ???????????? ??????(??????)", "a_10", "102", "16900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("????????? ????????????", "a_11", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("????????? ????????????(??????)", "a_11", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("????????? ????????????(??????)", "a_11", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????????????????????????????????", "a_12", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????????????????????????????????(??????)", "a_12", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????????????????????????????????(??????)", "a_12", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("???????????? ????????????", "a_13", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("???????????? ????????????(??????)", "a_13", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("???????????? ????????????(??????)", "a_13", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("????????? ???????????????", "a_14", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("????????? ???????????????(??????)", "a_14", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("????????? ???????????????(??????)", "a_14", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????? ????????????", "a_15", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????? ????????????(??????)", "a_15", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????? ????????????(??????)", "a_15", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????? ???????????????", "a_16", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????? ???????????????(??????)", "a_16", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????? ???????????????(??????)", "a_16", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????? ??????????????????", "a_14", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????? ??????????????????(??????)", "a_14", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("?????? ??????????????????(??????)", "a_14", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("??????+????????? ???????????????", "a_17", "102", "18900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("?????????+????????? ???????????????", "a_18", "102", "20900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("??????+????????? ???????????????", "a_18", "102", "18900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("??????+????????? ???????????????", "a_18", "102", "18900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "a_20", "102", "17900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("??????????????????", "a_18", "102", "17900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("?????????????????????", "a_19", "102", "17900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("??????????????????", "a_21", "102", "18900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("??????????????????", "a_22", "102", "18900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("??????????????????", "a_23", "102", "15900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "a_24", "102", "15900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "a_25", "102", "15900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "a_26", "102", "5000", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("??????&??????", "a_27", "102", "12900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "a_28", "102", "9900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_29", "102", "16900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "a_30", "102", "16900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("?????? ??????????????????", "a_31", "102", "15900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "a_32", "102", "18900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "a_33", "102", "20900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_34", "102", "15900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("??????????????????", "a_35", "102", "14900", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("??????????????????", "a_36", "102", "8000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_37", "102", "9900", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("????????????????????????", "a_38", "102", "10900", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("????????????+????????????", "a_39", "102", "8000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "a_40", "102", "5000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "a_41", "102", "5000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("???????????????", "a_42", "102", "5000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_43", "102", "5000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("??????", "a_73", "102", "2000", "", "16"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_71", "102", "2000", "", "16"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_72", "102", "2000", "", "16"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_74", "102", "2000", "", "16"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_50", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_51", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_52", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("????????? 17???", "a_53", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_54", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("????????????????????????", "a_55", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("??????", "a_56", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("??????(???)", "a_57", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("??????(???)", "a_58", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("????????????", "a_59", "102", "7500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_60", "102", "7500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_61", "102", "7500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("?????????", "a_62", "102", "8500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("?????? ????????????", "a_66", "102", "7900", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("?????? ????????? ?????????", "a_67", "102", "8500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("?????? ????????? ?????????", "a_68", "102", "8500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("?????? ????????? ???????????????", "a_69", "102", "8500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("?????? ????????? IPA???", "a_70", "102", "9000", "", "17"));

        }
        ArrayList<MenuModel> list = new ArrayList<>();
        array_count1 = new int[sampleMenuModels.size()];

        for (int z = 0; z < sampleCategoryModels.size(); z++) {
            SampleCategoryModel sampleCategoryModel = sampleCategoryModels.get(z);
            int checkcount = 0;
            for (int i = 0; i < sampleMenuModels.size(); i++) {
                MenuModel menuModel = new MenuModel();
                if (sampleCategoryModel.getCode().equals(sampleMenuModels.get(i).getCtgcode())) {
                    checkcount++;
                    menuModel.setName(sampleMenuModels.get(i).getName());
                    menuModel.setPicurl(sampleMenuModels.get(i).getPicurl());
                    menuModel.setPrice(sampleMenuModels.get(i).getPrice());
                    menuModel.setCode(sampleMenuModels.get(i).getCode());
                    menuModel.setInfo(sampleMenuModels.get(i).getInfo());
                    list.add(menuModel);
                }
                menu_listsize.add(list);
            }
//            if (z == 0) {
//                array_count1[z] = checkcount;
//            } else {
//                array_count1[z] = array_count1[z - 1] + checkcount;
//            }
            menu_list.add(list);
        }

        menu1Fragment menuFragment = new menu1Fragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", list);
        menuFragment.setArguments(bundle);
        fragmentManager.beginTransaction().replace(R.id.menuactivity_layout_menu, menuFragment).commit();


    }

    public void timerReset() {
        taskTimer.setTime(basic);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(MenuActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}
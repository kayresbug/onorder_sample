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
     * 취소거래용 변수
     * 예제는 직전취소이지만 특정 거래의 승인번호와 승인일자를 알고있는경우 이전거래 취소가능
     */
    String prevAuthNum = ""; //취소 거래용 승인번호
    String prevAuthDate = "";//취소 거래용 승인일자

    String prevClassfication = "";

    String vanTr = "";          //VanTr취소용(무카드 취소) 거래거유번호 저장
    String prevCardNo = "";     //VanTr취소용(무카드 취소) 마스킹카드번호 저장

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
        // 리사이클러뷰에 표시할 데이터 리스트 생성.
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

        // 리사이클러뷰에 LinearLayoutManager 객체 지정.
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

        order_price.setText("총 " + formattedStringPrice + "원 주문하기");
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

        order_price.setText("총 " + formattedStringPrice + "원 주문하기");

    }

    public void removeMenu(int price) {
        all_price = all_price - price;

        if (all_price > 0) {
            DecimalFormat myFormatter = new DecimalFormat("###,###");
            String formattedStringPrice = myFormatter.format(all_price);
            order_price.setText("총 " + formattedStringPrice + "원 주문하기");
        } else {
            all_price = 0;
            order_price.setText("총 0원 주문하기");
            isOrder = false;
        }


    }

    public void sendData() {
        isOrder = false;

        String order = "";
        for (int i = 0; i < order_list.size(); i++) {
            String order_name = order_list.get(i).getName();
            order_name = order_name.replace("흑돼지", "");
            order = order + order_name + " " + order_list.get(i).getCount() + "개" + "\n\n";
        }
        if (order_list.size() > 0) {
//            sendFCM(order);
//            sendFirebaseOrder(order);
            removePrice = String.valueOf(all_price);
            orderAdapter.removeData();
            all_price = 0;
            order_price.setText("총 0원 주문하기");
            isOrder = false;
            Toast toast = Toast.makeText(MenuActivity.this, "주문이 전달되었습니다.", Toast.LENGTH_LONG);
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
        PrintOrderModel printOrderModel = new PrintOrderModel(pref.getString("table", "") + "번 주문", order, time, "x", "order");

        myRef.child(pref.getString("storename", "")).child(time2).push().setValue(printOrderModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    removePrice = String.valueOf(all_price);
                    orderAdapter.removeData();
                    all_price = 0;
                    order_price.setText("총 0원 주문하기");
                    isOrder = false;
                    Toast toast = Toast.makeText(MenuActivity.this, "주문이 전달되었습니다.", Toast.LENGTH_LONG);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    toast.show();
                } else {
                    Toast toast = Toast.makeText(MenuActivity.this, "다시 시도해 주세요.", Toast.LENGTH_LONG);
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

            order = order + order_list.get(i).getName() + " " + order_list.get(i).getCount() + "개" + "\n";
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
        /*고정 사용필드*/
        m_hash.put("TelegramType", "0200".getBytes());                                    // 전문 구분 ,  승인(0200) 취소(0420)
        m_hash.put("DPTID", "DPT0TEST03".getBytes());                                     // 단말기번호 , 테스트단말번호 DPT0TEST03
        m_hash.put("PosEntry", "S".getBytes());                                           // Pos Entry Mode , 현금영수증 거래 시 키인거래에만 'K'사용
        m_hash.put("PayType", "00".getBytes());                                           // [신용]할부개월수(default '00') [현금]거래자구분
        m_hash.put("TotalAmount", getStrMoneytoTgAmount(amount)); // 총금액
        m_hash.put("Amount", getStrMoneytoTgAmount(String.valueOf(amount)));      // 공급금액 = 총금액 - 부가세 - 봉사료
        m_hash.put("ServicAmount", getStrMoneytoTgAmount("0"));                           // 봉사료
        m_hash.put("TaxAmount", getStrMoneytoTgAmount("0"));                              // 부가세
        m_hash.put("FreeAmount", getStrMoneytoTgAmount("0"));                             // 면세 0처리  / 면세 1004원일 경우 총금액 1004원 봉사료(ServiceAmount),부가세(TaxAmount) 0원 공급금액 1004원/ 면세(FreeAmount)  1004원
        m_hash.put("AuthNum", "".getBytes());                                            //원거래 승인번호 , 취소시에만 사용
        m_hash.put("Authdate", "".getBytes());                                           //원거래 승인일자 , 취소시에만 사용
        m_hash.put("Filler", "".getBytes());                                              // 여유필드 - 판매차 필요시에만 입력처리
        m_hash.put("SignTrans", "N".getBytes());                                          // 서명거래 필드, 무서명(N) 50000원 초과시 서명 "N" => "S"변경 필수
        if (Long.parseLong(amount) > 50000)
            m_hash.put("SignTrans", "S".getBytes());                                          // 서명거래 필드, 무서명(N) 50000원 초과시 서명 "N" => "S"변경 필수
        m_hash.put("PlayType", "D".getBytes());                                           // 실행구분,  데몬사용시 고정값(D)
        m_hash.put("CardType", "".getBytes());                                            // 은련선택 여부필드 (현재 사용안함), "" 고정
        m_hash.put("BranchNM", "".getBytes());                                            // 가맹점명 ,관련 개발 필요가맹점만 입력 , 없을시 "" 고정
        m_hash.put("BIZNO", "".getBytes());                                               // 사업자번호 ,KSNET 서버 정의된 가맹정일경우만 사용, 없을 시"" 고정
        m_hash.put("TransType", "".getBytes());                                           // "" 고정
        m_hash.put("AutoClose_Time", "30".getBytes());                                    // 사용자 동작 없을 시 자동 종료 ex)30초 후 종료
        /*선택 사용필드*/
        //m_hash.put("SubBIZNO","".getBytes());                                            // 하위 사업자번호 ,하위사업자 현금영수증 승인 및 취소시 적용
        //m_hash.put("Device_PortName","/dev/bus/usb/001/002".getBytes());                 //리더기 포트 설정 필요 시 UsbDevice 인스턴스의 getDeviceName() 리턴값입력 , 필요없을경우 생략가능
        //m_hash.put("EncryptSign","A!B@C#D4".getBytes());                                 // SignTrans "T"일경우 KSCIC에서 서명 받지않고 해당 사인데이터로 승인진행, 특정업체사용

        ComponentName compName = new ComponentName("ks.kscic_ksr01", "ks.kscic_ksr01.PaymentDlg");

        Intent intent = new Intent(Intent.ACTION_MAIN);

        if (type.equals("credit")) {
            m_hash.put("ReceiptNo", "X".getBytes());  // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
        } else if (type.equals("cancle")) {

            //신용취소 호출 부
            m_hash.put("TelegramType", "0420".getBytes());  // 전문 구분 ,  승인(0200) 취소(0420)
            m_hash.put("ReceiptNo", "X".getBytes());        // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
            m_hash.put("AuthNum", prevAuthNum.getBytes());
            m_hash.put("Authdate", prevAuthDate.getBytes());
        } else if (type.equals("cancleNocard")) {
            //신용 무카드 취소 호출부
            m_hash.put("TelegramType", "0420".getBytes()); // 전문 구분 ,  승인(0200) 취소(0420)
            m_hash.put("ReceiptNo", "X".getBytes());      // 현금영수증 거래필드, 신용결제 시 "X", 현금영수증 카드거래시 "", Key-In거래시 "휴대폰번호 등 입력" -> Pos Entry Mode 'K;
            m_hash.put("VanTr", vanTr.getBytes());        // 거래고유번호 , 무카드 취소일 경우 필수 필드
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

                //KTC 인증용 출력
                Log.d("payment", "recv [Classification]:: " + (m_hash.get("Classification")));
                System.out.println("recv [TelegramType]:: " + (m_hash.get("TelegramType")));
                System.out.println("recv [Dpt_Id]:: " + (m_hash.get("Dpt_Id")));
                System.out.println("recv [Enterprise_Info]:: " + (m_hash.get("Enterprise_Info")));
                System.out.println("recv [Full_Text_Num]:: " + (m_hash.get("Full_Text_Num")));
                System.out.println("recv [Status]:: " + (m_hash.get("Status")));
                System.out.println("recv [CardType]:: " + (m_hash.get("CardType")));              //'N':신용카드 'G':기프트카드 'C':체크카드 'P'선불카드 'P'고운맘 바우처
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

//            Toast.makeText(this, "성공" + (m_hash.get("AuthNum")), Toast.LENGTH_LONG).show();
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
//                        order_price.setText("총 0원 주문하기");
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
            //케이에스체크IC 초기버전 이후 가맹점 다운로드 없이 승인 가능
            //Toast.makeText(this, "케이에스체크IC 에서 가맹점 다운로드 후 사용하시기 바랍니다", Toast.LENGTH_LONG).show();

        } else {

            Toast.makeText(this, "응답값 리턴 실패", Toast.LENGTH_LONG).show();
        }
        // 수행을 제대로 하지 못한 경우
        if (resultCode == RESULT_CANCELED) {
            Toast.makeText(this, "앱 호출 실패", Toast.LENGTH_LONG).show();
        }

    }

    public byte[] getStrMoneytoTgAmount(String Money) {
        byte[] TgAmount = null;
        if (Money.length() == 0) {
//            Toast.makeText(MainActivity.this, "테스트 금액으로 승인진행", Toast.LENGTH_SHORT).show();
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
            sampleCategoryModels.add(new SampleCategoryModel("120", storecode, "베스트메뉴"));
            sampleCategoryModels.add(new SampleCategoryModel("121", storecode, "스페셜메뉴"));
            sampleCategoryModels.add(new SampleCategoryModel("122", storecode, "추가사리류"));
            sampleCategoryModels.add(new SampleCategoryModel("123", storecode, "밥류"));
            sampleCategoryModels.add(new SampleCategoryModel("124", storecode, "사이드"));
            sampleCategoryModels.add(new SampleCategoryModel("125", storecode, "술과음료"));
        }else if (storecode.equals("ots_test")){
            sampleCategoryModels.add(new SampleCategoryModel("11", storecode, "스페셜치킨"));
            sampleCategoryModels.add(new SampleCategoryModel("12", storecode, "치킨"));
            sampleCategoryModels.add(new SampleCategoryModel("13", storecode, "포차메뉴"));
            sampleCategoryModels.add(new SampleCategoryModel("14", storecode, "탕류"));
            sampleCategoryModels.add(new SampleCategoryModel("15", storecode, "튀김류"));
            sampleCategoryModels.add(new SampleCategoryModel("16", storecode, "음료"));
            sampleCategoryModels.add(new SampleCategoryModel("17", storecode, "주류"));
//            sampleCategoryModels.add(new SampleCategoryModel("19", storecode, "이벤트"));

        }
    }

    public void setLiset2(String storecode) {
        if (storecode.equals("hdmg_test")) {
            sampleMenuModels.add(new SampleMenuModel("흑돼지콩나물불고기", "b_1", "3000", "9000", "", "120"));
            sampleMenuModels.add(new SampleMenuModel("흑돼지숙주불고기", "b_2", "3002", "10000", "", "120"));
            sampleMenuModels.add(new SampleMenuModel("한치흑돼지숙주불고기", "b_3", "3003", "13000", "", "121"));
            sampleMenuModels.add(new SampleMenuModel("낙지흑돼지콩나물불고기", "b_4", "3004", "12000", "", "121"));
            sampleMenuModels.add(new SampleMenuModel("김치흑돼지콩나물불고기", "b_5", "3005", "10000", "", "121"));
            sampleMenuModels.add(new SampleMenuModel("김치흑돼지콩나물전골(중)", "b_6", "3006", "18000", "", "121"));
            sampleMenuModels.add(new SampleMenuModel("김치흑돼지콩나물전골(대)", "b_6", "3007", "26000", "", "121"));
            sampleMenuModels.add(new SampleMenuModel("떡사리", "b_7", "3008", "1000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("라면사리", "b_8", "3009", "1000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("쫄면사리", "b_9", "3010", "2000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("우동사리", "b_10", "3011", "2000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("감자당면", "b_11", "3012", "2000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("비엔나사리", "b_12", "3013", "2000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("콩나물추가", "b_13", "3014", "1000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("숙주추가", "b_14", "3015", "1000", "", "122"));
            sampleMenuModels.add(new SampleMenuModel("공기밥별도", "b_15", "3016", "1000", "", "123"));
            sampleMenuModels.add(new SampleMenuModel("셀프주먹밥", "b_16", "3017", "2000", "", "123"));
            sampleMenuModels.add(new SampleMenuModel("셀프볶음밥", "b_17", "3018", "2000", "", "123"));
            sampleMenuModels.add(new SampleMenuModel("셀프 치즈볶음밥", "b_18", "3019", "3000", "", "123"));
            sampleMenuModels.add(new SampleMenuModel("폭탄 계란찜", "b_19", "3020", "4000", "", "124"));
            sampleMenuModels.add(new SampleMenuModel("계란 두알", "b_20", "3021", "1000", "", "124"));
            sampleMenuModels.add(new SampleMenuModel("북어국 추가", "b_21", "3022", "4000", "", "124"));
            sampleMenuModels.add(new SampleMenuModel("한라산17도", "b_22", "3023", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("한라산21도", "b_23", "3024", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("진로", "b_24", "3025", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("참이슬", "b_25", "3026", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("카스", "b_27", "3027", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("테라", "b_28", "3028", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("사이다", "b_29", "3029", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("콜라", "b_30", "3030", "4000", "", "125"));
            sampleMenuModels.add(new SampleMenuModel("미란다파인", "b_31", "3031", "4000", "", "125"));

        }else if (storecode.equals("ots_test")) {
            sampleMenuModels.add(new SampleMenuModel("북경깐풍장어치킨", "a_1", "100", "27900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("북경깐풍장어치킨(순살)", "a_1", "101", "27900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("장어추가", "a_1", "102", "12000", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("베이비크랩치킨", "a_2", "100", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("베이비크랩치킨(순살)", "a_2", "101", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("크랩추가", "a_2", "102", "12000", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("오다리치킨", "a_3", "100", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("오다리치킨(순살)", "a_3", "101", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("오다리추가", "a_3", "102", "12000", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("옛날통닭 한마리", "a_4", "100", "9900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("옛날통닭 두마리", "a_4", "101", "18900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("옛날통닭 한마리+똥집튀김", "a_4", "102", "18900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("똥집튀김", "a_5", "102", "9900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("치즈 할라피뇨 샐러드 치킨", "a_6", "102", "14900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("케이준 샐러드 치킨", "a_7", "102", "14900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("후라이드+양념+간장", "a_8", "102", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("후라이드+양념+간장(순살)", "a_8", "102", "23900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("후라이드+레드핫+깐풍", "a_9", "102", "24900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("후라이드+레드핫+깐풍(순살)", "a_9", "102", "23900", "", "11"));
            sampleMenuModels.add(new SampleMenuModel("오태식 후라이드 치킨", "a_10", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("오태식 후라이드 치킨(윙봉)", "a_10", "102", "19900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("오태식 후라이드 치킨(순살)", "a_10", "102", "16900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("조판수 양념치킨", "a_11", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("조판수 양념치킨(윙봉)", "a_11", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("조판수 양념치킨(순살)", "a_11", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("해바라기아줌마반반치킨", "a_12", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("해바라기아줌마반반치킨(윙봉)", "a_12", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("해바라기아줌마반반치킨(순살)", "a_12", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("병진이형 간장치킨", "a_13", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("병진이형 간장치킨(윙봉)", "a_13", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("병진이형 간장치킨(순살)", "a_13", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("오라클 레드핫치킨", "a_14", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("오라클 레드핫치킨(윙봉)", "a_14", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("오라클 레드핫치킨(순살)", "a_14", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("웰빙 깐풍치킨", "a_15", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("웰빙 깐풍치킨(윙봉)", "a_15", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("웰빙 깐풍치킨(순살)", "a_15", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("희주 시즈닝치킨", "a_16", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("희주 시즈닝치킨(윙봉)", "a_16", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("희주 시즈닝치킨(순살)", "a_16", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("창무 매콤갈비치킨", "a_14", "102", "18900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("창무 매콤갈비치킨(윙봉)", "a_14", "102", "20900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("창무 매콤갈비치킨(순살)", "a_14", "102", "17900", "", "12"));
            sampleMenuModels.add(new SampleMenuModel("똥집+닭다리 통마늘구이", "a_17", "102", "18900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("닭다리+삼겹살 통마늘구이", "a_18", "102", "20900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("똥집+삼겹살 통마늘구이", "a_18", "102", "18900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("전복+소고기 통마늘구이", "a_18", "102", "18900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("매콤오돌뼈", "a_20", "102", "17900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("매콤무뼈닭발", "a_18", "102", "17900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("매콤닭목살구이", "a_19", "102", "17900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("양념막창구이", "a_21", "102", "18900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("숯불막창구이", "a_22", "102", "18900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("바삭먹태구이", "a_23", "102", "15900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("미니게튀김", "a_24", "102", "15900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("오다리튀김", "a_25", "102", "15900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("날치알밥", "a_26", "102", "5000", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("육전&쫄면", "a_27", "102", "12900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("아이스황도", "a_28", "102", "9900", "", "13"));
            sampleMenuModels.add(new SampleMenuModel("짬뽕탕", "a_29", "102", "16900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("백짬뽕탕", "a_30", "102", "16900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("얼큰 바지락홍합탕", "a_31", "102", "15900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("국물닭발", "a_32", "102", "18900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("닭볶음탕", "a_33", "102", "20900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("어묵탕", "a_34", "102", "15900", "", "14"));
            sampleMenuModels.add(new SampleMenuModel("깐풍장어튀김", "a_35", "102", "14900", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("모듬감자튀김", "a_36", "102", "8000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("떡볶이", "a_37", "102", "9900", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("미트칠리포테이토", "a_38", "102", "10900", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("우유튀김+감자튀김", "a_39", "102", "8000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("소떡소떡", "a_40", "102", "5000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("치즈스틱", "a_41", "102", "5000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("집게살튀김", "a_42", "102", "5000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("치즈볼", "a_43", "102", "5000", "", "15"));
            sampleMenuModels.add(new SampleMenuModel("콜라", "a_73", "102", "2000", "", "16"));
            sampleMenuModels.add(new SampleMenuModel("미린다", "a_71", "102", "2000", "", "16"));
            sampleMenuModels.add(new SampleMenuModel("사이다", "a_72", "102", "2000", "", "16"));
            sampleMenuModels.add(new SampleMenuModel("밀키스", "a_74", "102", "2000", "", "16"));
            sampleMenuModels.add(new SampleMenuModel("카스생", "a_50", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("테라생", "a_51", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("참이슬", "a_52", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("한라산 17도", "a_53", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("푸른밤", "a_54", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("한라산물순한소주", "a_55", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("진로", "a_56", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("카스(병)", "a_57", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("테라(병)", "a_58", "102", "4500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("하이네켄", "a_59", "102", "7500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("호가든", "a_60", "102", "7500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("코로나", "a_61", "102", "7500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("기네스", "a_62", "102", "8500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("제주 위트에일", "a_66", "102", "7900", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("제주 맥파이 쾰시캔", "a_67", "102", "8500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("제주 맥파이 포터캔", "a_68", "102", "8500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("제주 맥파이 페일에일캔", "a_69", "102", "8500", "", "17"));
            sampleMenuModels.add(new SampleMenuModel("제주 맥파이 IPA캔", "a_70", "102", "9000", "", "17"));

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
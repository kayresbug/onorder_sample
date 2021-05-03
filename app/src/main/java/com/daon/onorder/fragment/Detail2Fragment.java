package com.daon.onorder.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.daon.onorder.MenuActivity;
import com.daon.onorder.Model.MenuModel;
import com.daon.onorder.R;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class Detail2Fragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String url;
    private String name;
    private String price;
    private String info;
    private String code;

    View RootView;

    ImageView menuImg;
    TextView nameText;
    TextView priceText;
    TextView infoText;
    LinearLayout close;
    LinearLayout add_menu;
    LinearLayout radio;
    RadioGroup radioGroup;
    RadioButton radio1;
    int position;
    public Detail2Fragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Detail2Fragment newInstance(String param1, String param2) {
        Detail2Fragment fragment = new Detail2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            ArrayList<MenuModel> model =  (ArrayList<MenuModel>) getArguments().get("list");
            position = (int) getArguments().get("position");

            url = model.get(position).getPicurl();
            name = model.get(position).getName();
            info = model.get(position).getInfo();
            price = model.get(position).getPrice();
            code = model.get(position).getCode();
            Log.d("daon", "aiiiiii = "+url);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        RootView = inflater.inflate(R.layout.fragment_detail2, container, false);
        initViews();
        return RootView;
    }
    public void initViews(){
        priceText = RootView.findViewById(R.id.detailfragment_text_price);
        DecimalFormat myFormatter = new DecimalFormat("###,###");
        String formattedStringPrice = myFormatter.format(Integer.parseInt(price));
        priceText.setText("총 "+formattedStringPrice+ "원");
        radio1 = RootView.findViewById(R.id.l_btn1);
        radioGroup = RootView.findViewById(R.id.radioGroup);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId != R.id.l_btn1){
                    DecimalFormat myFormatter = new DecimalFormat("###,###");
                    String formattedStringPrice = myFormatter.format(Integer.parseInt(price) + 1000);
                    priceText.setText("총 "+formattedStringPrice+ "원");
                }else{
                    DecimalFormat myFormatter = new DecimalFormat("###,###");
                    String formattedStringPrice = myFormatter.format(Integer.parseInt(price));
                    priceText.setText("총 "+formattedStringPrice+ "원");
                }
            }
        });
        radio = RootView.findViewById(R.id.radio_layout);
        if (name.equals("해바라기아줌마반반치킨") || name.equals("해바라기아줌마반반치킨(윙봉)") || name.equals("해바라기아줌마반반치킨(순살)")){
            radio.setVisibility(View.VISIBLE);
        }
        int imenu = RootView.getResources().getIdentifier(url, "drawable", getContext().getPackageName());

        menuImg = RootView.findViewById(R.id.detailfragment_img);
        Glide.with(getContext()).load(imenu).circleCrop().into(menuImg);

//        GlideApp.with(getContext()).load(url).circleCrop().thumbnail(0.2f).into(menuImg);

        nameText = RootView.findViewById(R.id.detailfragment_text_name);
        nameText.setText(name);

        infoText = RootView.findViewById(R.id.detailfragment_text_info);
        infoText.setText(info);

        close = RootView.findViewById(R.id.detailfragment_layout_cancle);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MenuActivity)getActivity()).closeDetail(position);

            }
        });
        add_menu = RootView.findViewById(R.id.detailfragment_layout_add);
        add_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (name.equals("해바라기아줌마반반치킨") || name.equals("해바라기아줌마반반치킨(윙봉)") || name.equals("해바라기아줌마반반치킨(순살)")){
                    if (radio1.isChecked()) {
                        ((MenuActivity) getActivity()).callItem(name, price, url, code,"1");
                        ((MenuActivity) getActivity()).closeDetail(position);
                    }else{
                        price = String.valueOf(Integer.parseInt(price) + 1000);
                        ((MenuActivity) getActivity()).callItem(name, price, url, code,"1");
                        ((MenuActivity) getActivity()).closeDetail(position);
                    }
                }else {
                    ((MenuActivity) getActivity()).callItem(name, price, url, code, "1");
                    ((MenuActivity) getActivity()).closeDetail(position);
                }


            }
        });



    }
}
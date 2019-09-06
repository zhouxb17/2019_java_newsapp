package com.java.ZhouXuanBai;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewParent;
import android.view.View.OnTouchListener;


import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;
import com.java.ZhouXuanBai.R;
import com.java.ZhouXuanBai.ui.News.NewsFragment;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;



import android.view.Menu;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static androidx.navigation.ui.NavigationUI.onNavDestinationSelected;

public class MainActivity extends AppCompatActivity {

//    public Fragment currentFragment;
    private AppBarConfiguration mAppBarConfiguration;
    public ListView listview;
    public String[] datas = {"张三","李四","王五","麻子","小强"};
    public ArrayAdapter<String> arrayAdapter;
    public static ArrayList<String> titleList = new ArrayList();
    public static Context mContext;
    LinearLayout layout;
    boolean buttonlist[] = {true, true, true, true, true, false, false, false, false, false, false};
    NetConnection netConnection = new NetConnection();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = MainActivity.this;

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ToNewsArray.toNewsArray( NetConnection.httpRequest("size=5"));
        titleList = ToNewsArray.getInfo();

//        titleList.add("新闻1");
//        titleList.add("新闻2");
//        titleList.add("新闻3");
//        titleList.add("新闻4");
//        titleList.add("新闻5");
//        titleList.add("新闻6");
 //       arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, datas);
//        listview.setAdapter(arrayAdapter);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_news, R.id.nav_login, R.id.nav_collection,
                R.id.nav_shield, R.id.nav_night, R.id.nav_send)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
//        mAppBarConfiguration.
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

//        listview = NewsFragment.getListView();
//        arrayAdapter = new ArrayAdapter<String>(this, R.layout.item_list , datas);
//        listview.setAdapter(arrayAdapter);
    }
    public static Context getContext()
    {
        return mContext;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    public void EditClicked(View view) {
        // 一个自定义的布局，作为显示的内容
        View contentView = LayoutInflater.from(mContext).inflate(
                R.layout.pop_window, null);

        final PopupWindow popupWindow = new PopupWindow(contentView,
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, true);

        popupWindow.setTouchable(true);

        popupWindow.setTouchInterceptor(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                Log.i("mengdd", "onTouch : ");

                return false;
                // 这里如果返回true的话，touch事件将被拦截
                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
            }
        });

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(
//                R.drawable.));

        // 设置按钮的点击事件
        final Button button1 = (Button)contentView.findViewById(R.id.EditButton_娱乐);
        final Button button2 = (Button)contentView.findViewById(R.id.EditButton_军事);
        final Button button3 = (Button)contentView.findViewById(R.id.EditButton_教育);
        final Button button4 = (Button)contentView.findViewById(R.id.EditButton_文化);
        final Button button5 = (Button)contentView.findViewById(R.id.EditButton_健康);
        final Button button6 = (Button)contentView.findViewById(R.id.EditButton_财经);
        final Button button7 = (Button)contentView.findViewById(R.id.EditButton_体育);
        final Button button8 = (Button)contentView.findViewById(R.id.EditButton_汽车);
        final Button button9 = (Button)contentView.findViewById(R.id.EditButton_科技);
        final Button button10 = (Button)contentView.findViewById(R.id.EditButton_社会);
        if(buttonlist[1] == true)
            button1.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
        else
            button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
        if(buttonlist[2] == true)
            button2.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
        else
            button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
        if(buttonlist[3] == true)
            button3.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
        else
            button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
        if(buttonlist[4] == true)
            button4.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
        else
            button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
        if(buttonlist[5] == true)
            button5.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
        else
            button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
        if(buttonlist[6] == true)
            button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
        else
            button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
        if(buttonlist[7] == true)
            button7.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
        else
            button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
        if(buttonlist[8] == true)
            button8.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
        else
            button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
        if(buttonlist[9] == true)
            button9.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
        else
            button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
        if(buttonlist[10] == true)
            button10.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
        else
            button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonlist[1] == true) {
                    button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                    buttonlist[1] = false;
                    reset();
                } else {
                    button1.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                    buttonlist[1] = true;
                    reset();
                }
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonlist[2] == true) {
                    button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                    buttonlist[2] = false;
                    reset();
                } else {
                    button2.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                    buttonlist[2] = true;
                    reset();
                }
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonlist[3] == true) {
                    button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                    buttonlist[3] = false;
                    reset();
                } else {
                    button3.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                    buttonlist[3] = true;
                    reset();
                }
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonlist[4] == true) {
                    button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                    buttonlist[4] = false;
                    reset();
                } else {
                    button4.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                    buttonlist[4] = true;
                    reset();
                }
            }
        });
        button5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonlist[5] == true) {
                    button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                    buttonlist[5] = false;
                    reset();
                } else {
                    button5.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                    buttonlist[5] = true;
                    reset();
                }
            }
        });
        button6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonlist[6] == true) {
                    button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                    buttonlist[6] = false;
                    reset();
                } else {
                    button6.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                    buttonlist[6] = true;
                    reset();
                }
            }
        });
        button7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonlist[7] == true) {
                    button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                    buttonlist[7] = false;
                    reset();
                } else {
                    button7.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                    buttonlist[7] = true;
                    reset();
                }
            }
        });
        button8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonlist[8] == true) {
                    button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                    buttonlist[8] = false;
                    reset();
                } else {
                    button8.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                    buttonlist[8] = true;
                    reset();
                }
            }
        });
        button9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonlist[9] == true) {
                    button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                    buttonlist[9] = false;
                    reset();
                } else {
                    button9.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                    buttonlist[9] = true;
                    reset();
                }
            }
        });
        button10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (buttonlist[10] == true) {
                    button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                    buttonlist[10] = false;
                    reset();
                } else {
                    button10.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                    buttonlist[10] = true;
                    reset();
                }
            }
        });
        // 设置好参数之后再show
        popupWindow.showAsDropDown(view);

    }

    public void reset()
    {
        final Button button1 = (Button)findViewById(R.id.button_娱乐);
        final Button button2 = (Button)findViewById(R.id.button_军事);
        final Button button3 = (Button)findViewById(R.id.button_教育);
        final Button button4 = (Button)findViewById(R.id.button_文化);
        final Button button5 = (Button)findViewById(R.id.button_健康);
        final Button button6 = (Button)findViewById(R.id.button_财经);
        final Button button7 = (Button)findViewById(R.id.button_体育);
        final Button button8 = (Button)findViewById(R.id.button_汽车);
        final Button button9 = (Button)findViewById(R.id.button_科技);
        final Button button10 = (Button)findViewById(R.id.button_社会);
        if(buttonlist[1] == true)
                button1.setVisibility(View.VISIBLE);
            else
                button1.setVisibility(View.GONE);
        if(buttonlist[2] == true)
            button2.setVisibility(View.VISIBLE);
        else
            button2.setVisibility(View.GONE);
        if(buttonlist[3] == true)
            button3.setVisibility(View.VISIBLE);
        else
            button3.setVisibility(View.GONE);
        if(buttonlist[4] == true)
            button4.setVisibility(View.VISIBLE);
        else
            button4.setVisibility(View.GONE);
        if(buttonlist[5] == true)
            button5.setVisibility(View.VISIBLE);
        else
            button5.setVisibility(View.GONE);
        if(buttonlist[6] == true)
            button6.setVisibility(View.VISIBLE);
        else
            button6.setVisibility(View.GONE);
        if(buttonlist[7] == true)
            button7.setVisibility(View.VISIBLE);
        else
            button7.setVisibility(View.GONE);
        if(buttonlist[8] == true)
            button8.setVisibility(View.VISIBLE);
        else
            button8.setVisibility(View.GONE);
        if(buttonlist[9] == true)
            button9.setVisibility(View.VISIBLE);
        else
            button9.setVisibility(View.GONE);
        if(buttonlist[10] == true)
            button10.setVisibility(View.VISIBLE);
        else
            button10.setVisibility(View.GONE);
    }

    public void ClickCategory(View view) {
        final Button button1 = (Button)findViewById(R.id.button_推荐);
        final Button button2 = (Button)findViewById(R.id.button_体育);
        final Button button3 = (Button)findViewById(R.id.button_健康);
        final Button button4 = (Button)findViewById(R.id.button_军事);
        final Button button5 = (Button)findViewById(R.id.button_娱乐);
        final Button button6 = (Button)findViewById(R.id.button_教育);
        final Button button7 = (Button)findViewById(R.id.button_文化);
        final Button button8 = (Button)findViewById(R.id.button_汽车);
        final Button button9 = (Button)findViewById(R.id.button_社会);
        final Button button10 = (Button)findViewById(R.id.button_科技);
        final Button button11 = (Button)findViewById(R.id.button_财经);
        switch(view.getId())
        {
            case R.id.button_推荐:
                button1.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button11.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                break;
            case R.id.button_体育:
                button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button2.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button11.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                break;
            case R.id.button_健康:
                button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button3.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button11.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                break;
            case R.id.button_军事:
                button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button4.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button11.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                break;
            case R.id.button_娱乐:
                button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button5.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button11.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                break;
            case R.id.button_教育:
                button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button6.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button11.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                break;
            case R.id.button_文化:
                button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button7.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button11.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                break;
            case R.id.button_汽车:
                button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button8.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button11.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                break;
            case R.id.button_社会:
                button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button9.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button11.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                break;
            case R.id.button_科技:
                button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button10.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                button11.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                break;
            case R.id.button_财经:
                button1.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button2.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button3.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button4.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button5.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button6.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button7.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button8.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button9.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button10.setBackgroundColor(getResources().getColor(R.color.buttonUnClicked));
                button11.setBackgroundColor(getResources().getColor(R.color.buttonClicked));
                break;
        }
    }


    }



//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
//
//            // 获得当前得到焦点的View，一般情况下就是EditText（特殊情况就是轨迹求或者实体案件会移动焦点）
//            View v = getCurrentFocus();
//
//            if (isShouldHideInput(v, ev)) {
//                hideKeyboard(v);
//            }
//        }
//        return super.dispatchTouchEvent(ev);
//    }

    /**
     * 根据EditText所在坐标和用户点击的坐标相对比，来判断是否隐藏键盘，因为当用户点击EditText时没必要隐藏
     */
//    private boolean isShouldHideInput(View v, MotionEvent event) {
//        if (v != null && (v instanceof EditText)) {
//            int[] l = { 0, 0 };
//            v.getLocationInWindow(l);
//            int left = l[0], top = l[1], bottom = top + v.getHeight(), right = left
//                    + v.getWidth();
//            if (event.getX() > left && event.getX() < right
//                    && event.getY() > top && event.getY() < bottom) {
//                // 点击EditText的事件，忽略它。
//                return false;
//            } else {
//                return true;
//            }
//        }
//        // 如果焦点不是EditText则忽略，这个发生在视图刚绘制完，第一个焦点不在EditView上，和用户用轨迹球选择其他的焦点
//        return false;
//    }

//    private void hideKeyboard(View v) {
//        InputMethodManager imm = (InputMethodManager) NewHomeSubInclusiveActivity.this
//                .getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
//    }

//}

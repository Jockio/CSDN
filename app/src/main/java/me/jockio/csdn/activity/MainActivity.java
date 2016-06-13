package me.jockio.csdn.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.DraweeView;

import java.util.ArrayList;
import java.util.List;

import me.jockio.csdn.R;
import me.jockio.csdn.adapter.NormalRecyclerViewAdapter;
import me.jockio.csdn.model.Article;
import me.jockio.csdn.utils.MyApplication;
import me.jockio.csdn.utils.Tools;
import me.jockio.csdn.view.DividerItemDecoration;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static RecyclerView recyclerView;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private DraweeView draweeView;
    private static LinearLayout headerLayout;
    private static NormalRecyclerViewAdapter adapter;

    private static String currentUrl;
    private final static int SUCCEED = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        initView();
    }

    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("CSDN博客");
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View headerView = navigationView.getHeaderView(0);
        draweeView = (DraweeView) headerView.findViewById(R.id.nav_imageView);
        draweeView.setOnClickListener(new MyOnClickListener());

        headerLayout = (LinearLayout) findViewById(R.id.header_layout);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(
                MyApplication.getContext(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new NormalRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        currentUrl = getResources().getString(R.string.base_url) + "/home/getlist?page=1";
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("MainActivity", "swipeRefreshLayout onRefresh...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Tools.getInfo(currentUrl);
                    }
                }, 1000);
            }
        });
    }

    public class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.nav_imageView:

//                    AlertDialog dialog = new AlertDialog.Builder(MyApplication.getContext())
//                            .setTitle("登录")
//                            .setMessage("快来登录啊")
//                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//
//                                }
//                            })
//                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
//                                @Override
//                                public void onClick(DialogInterface dialog, int which) {
//                                    dialog.dismiss();
//                                }
//                            }).create();
//                    dialog.show();
                    break;
                default:
                    break;
            }
        }
    }

    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SUCCEED:
                    List<Article> articleList = (List<Article>) msg.obj;
                    adapter.addItem(articleList);
                    swipeRefreshLayout.setRefreshing(false);
                    Toast.makeText(MyApplication.getContext(),
                            "更新了" + articleList.size() + "条数据...", Toast.LENGTH_SHORT).show();
                    headerLayout.setVisibility(View.GONE);
                    Log.v("MainActivity", "666666666666666666666");
                    //recyclerView.setAdapter(adapter);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
            toolbar.setTitle("主页");
            recyclerView.smoothScrollToPosition(0);
            currentUrl = getResources().getString(R.string.base_url) + "/home/getlist?page=1";

        } else if (id == R.id.nav_gallery) {
            toolbar.setTitle("移动开发");
            recyclerView.smoothScrollToPosition(0);
            currentUrl = getResources().getString(R.string.base_url) + "/column/getlist?Channel=mobile&Type=new&page=1";
        } else if (id == R.id.nav_slideshow) {
            toolbar.setTitle("Web前端");
            recyclerView.smoothScrollToPosition(0);
            currentUrl = getResources().getString(R.string.base_url) + "/column/getlist?Channel=web&Type=new&page=1";
        } else if (id == R.id.nav_manage) {
            toolbar.setTitle("架构设计");
        } else if (id == R.id.nav_share) {
            toolbar.setTitle("编程语言");
        } else if (id == R.id.nav_send) {
            toolbar.setTitle("互联网");
        }

        Tools.getInfo(currentUrl);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

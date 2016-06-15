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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.view.DraweeView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.util.List;

import me.jockio.csdn.R;
import me.jockio.csdn.adapter.NormalRecyclerViewAdapter;
import me.jockio.csdn.model.Article;
import me.jockio.csdn.utils.MyApplication;
import me.jockio.csdn.utils.Tools;
import me.jockio.csdn.view.DividerItemDecoration;

import static java.lang.Integer.parseInt;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private MaterialSearchView searchView;
    private static RecyclerView recyclerView;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private DraweeView draweeView;
    private static LinearLayout headerLayout;
    private static NormalRecyclerViewAdapter adapter;

    private static boolean isLoading;
    private static String currentUrl;
    private final static int SUCCEED = 1;
    private final static int LOAD_MORE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fresco.initialize(this);
        setContentView(R.layout.activity_main);

        currentUrl = getResources().getString(R.string.base_url) + "/home/getlist?page=1";
        initView();
    }

    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("CSDN博客");
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
                String url = MyApplication.getContext().getResources().getString(R.string.base_url)
                        + "/search/searchdata?r=" + String.valueOf(Math.random());
                url = url + "&keyword=" + query + "&page=1";
                Log.v("URL", url);
                Tools.getSearchResult(url);
                Toast.makeText(MyApplication.getContext(), query, Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic
            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
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
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
        recyclerView.addItemDecoration(new DividerItemDecoration(
                MyApplication.getContext(), DividerItemDecoration.VERTICAL_LIST));
        adapter = new NormalRecyclerViewAdapter();
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.d("test", "StateChanged = " + newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                Log.d("test", "onScrolled");

                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                if (lastVisibleItemPosition + 1 == adapter.getItemCount()) {
                    Log.d("test", "loading executed");

                    boolean isRefreshing = swipeRefreshLayout.isRefreshing();
                    if (isRefreshing) {
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        return;
                    }
                    if (!isLoading) {
                        isLoading = true;
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                //上拉加载更多数据
                                String[] array = currentUrl.split("=");
                                int number = Integer.parseInt(array[array.length - 1]) + 1;
                                currentUrl = "";
                                for(int i = 0; i < array.length - 1; i++){
                                    currentUrl = currentUrl + array[i] + "=";
                                }
                                currentUrl += number;
                                Log.v("URL", currentUrl);
                                Tools.getInfo(currentUrl, LOAD_MORE);
                            }
                        }, 1000);
                    }
                }
            }
        });


        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("MainActivity", "swipeRefreshLayout onRefresh...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Tools.getInfo(currentUrl, SUCCEED);
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
                case LOAD_MORE:
                    articleList = (List<Article>) msg.obj;
                    if(articleList.size() == 0){
                        Toast.makeText(MyApplication.getContext(),
                                "没有更多数据了",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    adapter.addMoreItem(articleList);
                    swipeRefreshLayout.setRefreshing(false);
                    adapter.notifyItemRemoved(adapter.getItemCount());
                    Log.d("test", "load more completed");
                    isLoading = false;
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onBackPressed() {
        if (searchView.isSearchOpen()) {
            searchView.closeSearch();
            return;
        }

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
        MenuItem item = menu.findItem(R.id.action_search);
        searchView.setMenuItem(item);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
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

        Tools.getInfo(currentUrl, SUCCEED);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

package me.jockio.csdn.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
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
import android.widget.Toast;

import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import me.jockio.csdn.R;
import me.jockio.csdn.adapter.NormalRecyclerViewAdapter;
import me.jockio.csdn.model.Article;
import me.jockio.csdn.utils.MyApplication;
import me.jockio.csdn.utils.Tools;
import me.jockio.csdn.view.CircleImageView;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static MaterialSearchView searchView;
    private static RecyclerView recyclerView;
    private static SwipeRefreshLayout swipeRefreshLayout;
    private Toolbar toolbar;
    private CircleImageView circleImageView;
    private static NormalRecyclerViewAdapter adapter;
    private FloatingActionButton fab;

    private static final String CSDN_CACHE = "CSDN_Cache";
    private static boolean isLoading;
    private static boolean isSearchMode = false;
    private static boolean isFirstOpen = true;
    private static int currentSearchPage;
    private static String currentSearchWord;
    private static String currentUrl;
    private final static int SUCCEED = 1;
    private final static int LOAD_MORE = 2;
    private final static int SEARCH = 3;
    private final static int SEARCH_MORE = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        currentUrl = getResources().getString(R.string.base_url) + "/home/getlist?page=1";
        initView();
    }

    public void initView() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("CSDN博客");
        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        searchView.setSuggestions(getResources().getStringArray(R.array.query_suggestions));
        //设置点击搜索建议中 item 时,开始搜索
        searchView.setSubmitOnClick(true);
        searchView.setVoiceSearch(false);
        searchView.setCursorDrawable(R.drawable.custom_cursor);
        searchView.setEllipsize(true);
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                toolbar.setTitle("搜索" + "\"" + query + "\"" + "的结果:");
                isSearchMode = true;
                currentSearchWord = query;
                currentSearchPage = 1;
                String url = MyApplication.getContext().getResources().getString(R.string.base_url)
                        + "/search/searchdata?r=" + String.valueOf(Math.random());
                Log.v("URL", url);
                Tools.getSearchResult(url, query, currentSearchPage, SEARCH);
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

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setRippleColor(Color.parseColor("#00000000"));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.smoothScrollToPosition(0);
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
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
        circleImageView = (CircleImageView) headerView.findViewById(R.id.nav_imageView);
        circleImageView.setOnClickListener(new MyOnClickListener());


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        //设置Item增加、移除动画
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        //添加分割线
//        recyclerView.addItemDecoration(new DividerItemDecoration(
//                MyApplication.getContext(), DividerItemDecoration.VERTICAL_LIST));
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

                int first = layoutManager.findFirstVisibleItemPosition();
                if(first == 0){
                    fab.setVisibility(View.GONE);
                }else{
                    fab.setVisibility(View.VISIBLE);
                }

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
//                                if(isSearchMode){
//                                    currentSearchPage++;
//                                    String url = MyApplication.getContext().getResources().getString(R.string.base_url)
//                                            + "/search/searchdata?r=" + String.valueOf(Math.random());
//                                    Tools.getSearchResult(url, currentSearchWord, currentSearchPage, SEARCH_MORE);
//                                    Toast.makeText(MyApplication.getContext(),
//                                            currentSearchPage + "页已经在加载",
//                                            Toast.LENGTH_SHORT).show();
//                                }else{
                                    String[] array = currentUrl.split("=");
                                    int currentNumber = Integer.parseInt(array[array.length - 1]) + 1;
                                    currentUrl = "";
                                    for(int i = 0; i < array.length - 1; i++){
                                        currentUrl = currentUrl + array[i] + "=";
                                    }
                                    currentUrl += currentNumber;
                                    Log.v("URL", currentUrl);
                                    Tools.getInfo(currentUrl, LOAD_MORE);
                                //}
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
                        //下拉刷新
                        if(isSearchMode){
                            currentSearchPage = 1;
                            String url = MyApplication.getContext().getResources().getString(R.string.base_url)
                                    + "/search/searchdata?r=" + String.valueOf(Math.random());
                            Tools.getSearchResult(url, currentSearchWord, currentSearchPage, SEARCH);
                        }else {
                            Tools.getInfo(currentUrl, SUCCEED);
                        }
                    }
                }, 1000);
            }
        });

        List<Article> list = new ArrayList<>();
        if((list = readFromFile(list)) != null){
            Toast.makeText(MyApplication.getContext(), "size = " + list.size(), Toast.LENGTH_SHORT).show();
            adapter.addItem(list);
        }else{
            swipeRefreshLayout.setRefreshing(true);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Tools.getInfo(currentUrl, SUCCEED);
                }
            }, 1000);
        }
    }

    /**
     * 刚打开APP时，从文件中读取数据显示在屏幕上
     *
     * @return
     */
    private List<Article> readFromFile(List<Article> list) {
        File file = new File(getDirectory(), "csdn.cache");
        ObjectInputStream ois = null;
        try {
            if (!file.exists()) return null;
            ois = new ObjectInputStream(new FileInputStream(file));
            list = (List<Article>)ois.readObject();
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        }catch (ClassNotFoundException e) {
            e.printStackTrace();
        }finally {
            if(ois != null){
                try {
                    ois.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    /**
     * 将数据写入文件
     *
     * @return
     */
    private static boolean writeToFile(List<Article> list) {
        File file = new File(getDirectory(), "csdn.cache");
        ObjectOutputStream oos = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            oos = new ObjectOutputStream(new FileOutputStream(file));
            if(list != null){
                oos.writeObject(list);
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * 获得缓存目录
     **/
    private static String getDirectory() {
        String dir = getSDPath() + "/" + CSDN_CACHE;
        return dir;
    }

    /**
     * 取SD卡路径
     **/
    private static String getSDPath() {
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                android.os.Environment.MEDIA_MOUNTED);  //判断sd卡是否存在
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory();  //获取根目录
        }
        if (sdDir != null) {
            return sdDir.toString();
        } else {
            return "";
        }
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
                    if(isFirstOpen){
                        isFirstOpen = false;
                        writeToFile(articleList);
                    }
                    Toast.makeText(MyApplication.getContext(),
                            "更新了" + articleList.size() + "条数据...", Toast.LENGTH_SHORT).show();
                    //recyclerView.setAdapter(adapter);
                    break;
                case LOAD_MORE:
                    articleList = (List<Article>) msg.obj;
                    swipeRefreshLayout.setRefreshing(false);
                    if(articleList.size() == 0){
                        adapter.notifyItemRemoved(adapter.getItemCount());
                        Toast.makeText(MyApplication.getContext(),
                                "没有更多数据了",
                                Toast.LENGTH_SHORT).show();
                        break;
                    }
                    adapter.addMoreItem(articleList);
                    adapter.notifyItemRemoved(adapter.getItemCount());
                    Log.d("test", "load more completed");
                    isLoading = false;
                    break;
                case SEARCH:
                    swipeRefreshLayout.setRefreshing(false);
                    articleList = (List<Article>) msg.obj;
                    adapter.addItem(articleList);
                    searchView.closeSearch();
                    Toast.makeText(MyApplication.getContext(),
                            "更新了" + articleList.size() + "条数据...", Toast.LENGTH_SHORT).show();
                    break;
                case SEARCH_MORE:
                    swipeRefreshLayout.setRefreshing(false);
                    articleList = (List<Article>) msg.obj;
                    adapter.addMoreItem(articleList);
                    Toast.makeText(MyApplication.getContext(),
                            "更新了" + articleList.size() + "条数据...", Toast.LENGTH_SHORT).show();
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
            currentUrl = getResources().getString(R.string.base_url) + "/home/getlist?page=1";
        } else if (id == R.id.nav_gallery) {
            toolbar.setTitle("移动开发");
            currentUrl = getResources().getString(R.string.base_url) + "/column/getlist?Channel=mobile&Type=new&page=1";
        } else if (id == R.id.nav_slideshow) {
            toolbar.setTitle("Web前端");
            currentUrl = getResources().getString(R.string.base_url) + "/column/getlist?Channel=web&Type=new&page=1";
        } else if (id == R.id.nav_manage) {
            toolbar.setTitle("架构设计");
            currentUrl = getResources().getString(R.string.base_url) + "/column/getlist?Channel=enterprise&Type=new&page=1";
        } else if (id == R.id.nav_share) {
            toolbar.setTitle("编程语言");
            currentUrl = getResources().getString(R.string.base_url) + "/column/getlist?Channel=code&Type=new&page=1";
        } else if (id == R.id.nav_send) {
            toolbar.setTitle("互联网");
            currentUrl = getResources().getString(R.string.base_url) + "/column/getlist?Channel=www&Type=new&page=1";
        } else if (id == R.id.nav_database) {
            toolbar.setTitle("数据库");
            currentUrl = getResources().getString(R.string.base_url) + "/column/getlist?Channel=database&Type=new&page=1";
        } else if (id == R.id.nav_system) {
            toolbar.setTitle("系统运维");
            currentUrl = getResources().getString(R.string.base_url) + "/column/getlist?Channel=system&Type=new&page=1";
        }

        //数据库
        ///column/getlist?Channel=database&Type=new&page=1
        //系统运维
        ///column/getlist?Channel=system&Type=new&page=1

        recyclerView.smoothScrollToPosition(0);
        isSearchMode = false;

        Tools.getInfo(currentUrl, SUCCEED);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

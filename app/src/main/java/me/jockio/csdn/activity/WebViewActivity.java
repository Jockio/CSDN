package me.jockio.csdn.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import me.jockio.csdn.R;

/**
 * Created by jockio on 16/6/11.
 */

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    
    private String currentUrl = "";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);

        initView();

        Intent intent = getIntent();
        currentUrl = intent.getStringExtra("url");
        webView.loadUrl(currentUrl);
    }

    private void initView() {
        webView = (WebView) findViewById(R.id.webView);
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //屏蔽超链接
                return true;
            }
        });

        webView.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 加载完成
                    swipeRefreshLayout.setRefreshing(false);
                }
            }
        });

        WebSettings settings = webView.getSettings();
        //设置 缓存模式
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        // 开启 DOM storage API 功能
        settings.setDomStorageEnabled(true);
        settings.setJavaScriptEnabled(true);  //支持js
        settings.setUseWideViewPort(false);  //将图片调整到适合webview的大小
        settings.setSupportZoom(true);  //支持缩放
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN); //支持内容重新布局
        settings.supportMultipleWindows();  //多窗口
        //settings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);  //关闭webview中缓存
        settings.setAllowFileAccess(true);  //设置可以访问文件
        settings.setNeedInitialFocus(true); //当webview调用requestFocus时为webview设置节点
        settings.setBuiltInZoomControls(true); //设置支持缩放
        settings.setJavaScriptCanOpenWindowsAutomatically(true); //支持通过JS打开新窗口
        settings.setLoadWithOverviewMode(true); // 缩放至屏幕的大小
        settings.setLoadsImagesAutomatically(true);  //支持自动加载图片
        
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        //设置刷新时动画的颜色，可以设置4个
        swipeRefreshLayout.setProgressBackgroundColorSchemeResource(android.R.color.white);
        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                android.R.color.holo_red_light, android.R.color.holo_orange_light,
                android.R.color.holo_green_light);
        swipeRefreshLayout.setProgressViewOffset(false, 0, (int) TypedValue
                .applyDimension(TypedValue.COMPLEX_UNIT_DIP, 24, getResources()
                        .getDisplayMetrics()));
        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Log.d("MainActivity", "swipeRefreshLayout onRefresh...");
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        //下拉刷新
                        webView.loadUrl(currentUrl);
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(webView != null) {
            webView.clearHistory();
            webView.clearCache(true);
            webView.loadUrl("about:blank");
            webView.pauseTimers();
            // Note that webView.destroy() and mWebView = null do the exact same thing
            webView = null;
        }
    }
}

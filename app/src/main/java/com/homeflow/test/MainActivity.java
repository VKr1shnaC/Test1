package com.homeflow.test;

import android.app.Activity;
import android.app.Dialog;
import android.print.PrintManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Base64;
import android.view.ViewGroup;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

public class MainActivity extends Activity {
    private static final int FILE_CHOOSER = 501;
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        configure(webView, false, null);
        if (state == null) {
            try { webView.loadUrl("file://" + prepareHtml().getAbsolutePath()); }
            catch (Exception e) { webView.loadData("<h2>HomeFlow test could not unpack the UI.</h2><pre>" + e + "</pre>", "text/html", "UTF-8"); }
        }
    }

    private static void appendAsset(android.content.res.AssetManager assets, String name, ByteArrayOutputStream out) throws Exception {
        InputStream in = assets.open(name);
        byte[] buf = new byte[8192]; int n;
        while ((n = in.read(buf)) > 0) out.write(buf, 0, n);
        in.close();
    }

    private File prepareHtml() throws Exception {
        File out = new File(getFilesDir(), "homeflow.html");
        ByteArrayOutputStream text = new ByteArrayOutputStream();
        appendAsset(getAssets(), "homeflow.html.gz.b64.prefix", text);
        appendAsset(getAssets(), "homeflow.html.gz.b64", text);
        byte[] compressed = Base64.decode(text.toByteArray(), Base64.DEFAULT);
        GZIPInputStream gz = new GZIPInputStream(new ByteArrayInputStream(compressed));
        FileOutputStream fos = new FileOutputStream(out, false);
        byte[] buf = new byte[8192]; int n;
        while ((n = gz.read(buf)) > 0) fos.write(buf, 0, n);
        gz.close(); fos.close();
        return out;
    }

    private void configure(WebView w, boolean child, Dialog dialog) {
        WebSettings s = w.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setJavaScriptCanOpenWindowsAutomatically(true);
        s.setSupportMultipleWindows(true);
        w.setBackgroundColor(Color.TRANSPARENT);
        w.addJavascriptInterface(new PrintBridge(this), "AndroidPrint");
        w.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.evaluateJavascript("window.print=function(){if(window.AndroidPrint){AndroidPrint.printHtml(document.documentElement.outerHTML);}};", null);
            }
        });
        w.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                Intent intent;
                try { intent = params.createIntent(); }
                catch (Exception ex) { intent = new Intent(Intent.ACTION_OPEN_DOCUMENT).setType("*/*").addCategory(Intent.CATEGORY_OPENABLE); }
                startActivityForResult(intent, FILE_CHOOSER);
                return true;
            }
            @Override public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, android.os.Message resultMsg) {
                Dialog d = new Dialog(MainActivity.this, android.R.style.Theme_Material_Light_NoActionBar_Fullscreen);
                WebView childView = new WebView(MainActivity.this);
                configure(childView, true, d);
                d.setContentView(childView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                d.show();
                WebView.WebViewTransport transport = (WebView.WebViewTransport) resultMsg.obj;
                transport.setWebView(childView);
                resultMsg.sendToTarget();
                return true;
            }
            @Override public void onCloseWindow(WebView window) { if (child && dialog != null) dialog.dismiss(); }
        });
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FILE_CHOOSER && fileCallback != null) {
            Uri[] out = null;
            if (resultCode == RESULT_OK) out = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
            fileCallback.onReceiveValue(out);
            fileCallback = null;
        }
    }

    @Override public void onBackPressed() {
        if (webView.canGoBack()) webView.goBack(); else super.onBackPressed();
    }

    public static class PrintBridge {
        private final Activity activity;
        PrintBridge(Activity a) { activity = a; }
        @JavascriptInterface public void printHtml(String html) {
            activity.runOnUiThread(() -> {
                WebView printView = new WebView(activity);
                printView.getSettings().setJavaScriptEnabled(true);
                printView.setWebViewClient(new WebViewClient() {
                    @Override public void onPageFinished(WebView view, String url) {
                        PrintManager pm = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
                        pm.print("HomeFlow monthly statement", view.createPrintDocumentAdapter("HomeFlow monthly statement"), null);
                    }
                });
                printView.loadDataWithBaseURL("https://homeflow.local/", html, "text/html", "UTF-8", null);
            });
        }
    }
}

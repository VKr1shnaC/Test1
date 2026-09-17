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
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class MainActivity extends Activity {
    private static final int FILE_CHOOSER = 501;
    private static final String UI_FIX_B64 = "KGZ1bmN0aW9uKCl7CmlmKHdpbmRvdy5fX2hvbWVmbG93SXFvb0ZpeClyZXR1cm47IHdpbmRvdy5fX2hvbWVmbG93SXFvb0ZpeD10cnVlOwpjb25zdCBjc3M9YApodG1sLGJvZHl7d2lkdGg6MTAwJTttaW4taGVpZ2h0OjEwMCU7b3ZlcmZsb3cteDpoaWRkZW59CkBtZWRpYShtYXgtd2lkdGg6NjAwcHgpewogYm9keXtwYWRkaW5nOjAhaW1wb3J0YW50O21pbi1oZWlnaHQ6MTAwZHZoIWltcG9ydGFudDtiYWNrZ3JvdW5kOmxpbmVhci1ncmFkaWVudCgxNDVkZWcsI2ZiZmNmOSwjZjVmOGY4IDUyJSwjZmJmNWVmKSFpbXBvcnRhbnR9CiAucGhvbmV7d2lkdGg6MTAwJSFpbXBvcnRhbnQ7bWF4LXdpZHRoOm5vbmUhaW1wb3J0YW50O21pbi1oZWlnaHQ6MTAwZHZoIWltcG9ydGFudDttYXJnaW46MCFpbXBvcnRhbnQ7Ym9yZGVyOjAhaW1wb3J0YW50O2JvcmRlci1yYWRpdXM6MCFpbXBvcnRhbnQ7Ym94LXNoYWRvdzpub25lIWltcG9ydGFudDtwYWRkaW5nOm1heCgxMnB4LGVudihzYWZlLWFyZWEtaW5zZXQtdG9wKSkgbWF4KDEwcHgsZW52KHNhZmUtYXJlYS1pbnNldC1yaWdodCkpIG1heCg4cHgsZW52KHNhZmUtYXJlYS1pbnNldC1ib3R0b20pKSBtYXgoMTBweCxlbnYoc2FmZS1hcmVhLWluc2V0LWxlZnQpKSFpbXBvcnRhbnR9CiAudG9we3BhZGRpbmctdG9wOjEwcHghaW1wb3J0YW50fS5oZWFkbGluZXtmb250LXNpemU6Y2xhbXAoMjNweCw3dncsMjdweCkhaW1wb3J0YW50O2xpbmUtaGVpZ2h0OjEuMDghaW1wb3J0YW50fQogLnN1bW1hcnl7bWF4LXdpZHRoOjUydnchaW1wb3J0YW50fS5zY3JlZW57cGFkZGluZy1ib3R0b206MTAwcHghaW1wb3J0YW50fQogLm5hdntib3R0b206bWF4KDZweCxlbnYoc2FmZS1hcmVhLWluc2V0LWJvdHRvbSkpIWltcG9ydGFudDtwYWRkaW5nLWJvdHRvbTpjYWxjKDdweCArIGVudihzYWZlLWFyZWEtaW5zZXQtYm90dG9tKSkhaW1wb3J0YW50O2JhY2tncm91bmQ6I2ZmZmZmZmVmIWltcG9ydGFudH0KIC5zaGVldHtwYWRkaW5nOjAhaW1wb3J0YW50O2FsaWduLWl0ZW1zOmZsZXgtZW5kIWltcG9ydGFudH0KIC5tb2RhbHt3aWR0aDoxMDAlIWltcG9ydGFudDttYXgtd2lkdGg6bm9uZSFpbXBvcnRhbnQ7bWF4LWhlaWdodDpjYWxjKHZhcigtLWhmLXZoLDEwMGR2aCkgLSBtYXgoMTJweCxlbnYoc2FmZS1hcmVhLWluc2V0LXRvcCkpKSFpbXBvcnRhbnQ7Ym9yZGVyLXJhZGl1czoyNnB4IDI2cHggMCAwIWltcG9ydGFudDtwYWRkaW5nLWJvdHRvbTpjYWxjKDE4cHggKyBlbnYoc2FmZS1hcmVhLWluc2V0LWJvdHRvbSkpIWltcG9ydGFudDtzY3JvbGwtcGFkZGluZy1ib3R0b206MTEwcHghaW1wb3J0YW50O292ZXJzY3JvbGwtYmVoYXZpb3I6Y29udGFpbiFpbXBvcnRhbnR9CiAjbW9kYWxCb2R5PmJ1dHRvbi5wcmltYXJ5LmZ1bGw6bGFzdC1jaGlsZCwjbW9kYWxCb2R5Pi5ncmlkMjpsYXN0LWNoaWxke3Bvc2l0aW9uOnN0aWNreSFpbXBvcnRhbnQ7Ym90dG9tOjAhaW1wb3J0YW50O3otaW5kZXg6MTUhaW1wb3J0YW50O21hcmdpbi10b3A6MTJweCFpbXBvcnRhbnQ7cGFkZGluZy10b3A6MTBweCFpbXBvcnRhbnQ7cGFkZGluZy1ib3R0b206Y2FsYyg4cHggKyBlbnYoc2FmZS1hcmVhLWluc2V0LWJvdHRvbSkpIWltcG9ydGFudDtiYWNrZ3JvdW5kOmxpbmVhci1ncmFkaWVudCgxODBkZWcscmdiYSgyNTUsMjUwLDI0NSwwKSwjZmZmYWY1IDI0JSkhaW1wb3J0YW50fQogI21vZGFsQm9keT4uZ3JpZDI6bGFzdC1jaGlsZCBidXR0b257bWluLWhlaWdodDo0NHB4fQp9CmA7CmNvbnN0IHN0PWRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoJ3N0eWxlJyk7c3QuaWQ9J2hmLWlxb28tZml4JztzdC50ZXh0Q29udGVudD1jc3M7ZG9jdW1lbnQuaGVhZC5hcHBlbmRDaGlsZChzdCk7CmZ1bmN0aW9uIHZ2KCl7Y29uc3QgaD13aW5kb3cudmlzdWFsVmlld3BvcnQ/d2luZG93LnZpc3VhbFZpZXdwb3J0LmhlaWdodDp3aW5kb3cuaW5uZXJIZWlnaHQ7ZG9jdW1lbnQuZG9jdW1lbnRFbGVtZW50LnN0eWxlLnNldFByb3BlcnR5KCctLWhmLXZoJyxoKydweCcpfQp2digpO2lmKHdpbmRvdy52aXN1YWxWaWV3cG9ydCl7d2luZG93LnZpc3VhbFZpZXdwb3J0LmFkZEV2ZW50TGlzdGVuZXIoJ3Jlc2l6ZScsdnYpO3dpbmRvdy52aXN1YWxWaWV3cG9ydC5hZGRFdmVudExpc3RlbmVyKCdzY3JvbGwnLHZ2KX0KZG9jdW1lbnQuYWRkRXZlbnRMaXN0ZW5lcignZm9jdXNpbicsZT0+e2lmKGUudGFyZ2V0JiZlLnRhcmdldC5tYXRjaGVzJiZlLnRhcmdldC5tYXRjaGVzKCdpbnB1dCxzZWxlY3QsdGV4dGFyZWEnKSlzZXRUaW1lb3V0KCgpPT5lLnRhcmdldC5zY3JvbGxJbnRvVmlldyh7YmxvY2s6J2NlbnRlcicsYmVoYXZpb3I6J3Ntb290aCd9KSwxODApfSk7CndpbmRvdy5jYWxjU2VydmljZXNUb0RhdGU9ZnVuY3Rpb24oYXNPZil7YXNPZj1hc09mfHwxNjtjb25zdCBmdWxsPWNhbGNTZXJ2aWNlcygpO2xldCBzY2hlZHVsZWQ9MCxkZWR1Y3Q9MCxtaWxrRGVsaXZlcmVkPTA7Zm9yKGxldCBkPTE7ZDw9TWF0aC5taW4oYXNPZixmdWxsLmRpbSk7ZCsrKXtjb25zdCB4PWdldERheShkKTtpZighaXNTdW5kYXkoZCkpe3NjaGVkdWxlZCsrO2lmKHgubWFpZCE9PSdDYW1lJylkZWR1Y3QrK31pZih4Lm1pbGs9PT0nRGVsaXZlcmVkJyltaWxrRGVsaXZlcmVkKyt9Y29uc3QgbWFpZFBheT0oc2NoZWR1bGVkLWRlZHVjdCkqZnVsbC5kYWlseSxtaWxrUGF5PW1pbGtEZWxpdmVyZWQqc3RhdGUuc2V0dGluZ3MubWlsa1JhdGU7cmV0dXJue3NjaGVkdWxlZCxkZWR1Y3QsbWlsa0RlbGl2ZXJlZCxkYWlseTpmdWxsLmRhaWx5LG1haWRQYXksbWlsa1BheSx0b3RhbDptYWlkUGF5K21pbGtQYXkscHJvamVjdGVkOmZ1bGwudG90YWx9fQpyZW5kZXJIb21lPWZ1bmN0aW9uKCl7c3luY1RvZGF5KCk7Y29uc3Qgcz1jYWxjU2VydmljZXMoKSxkdWU9Y2FsY1NlcnZpY2VzVG9EYXRlKDE2KTtkb2N1bWVudC5nZXRFbGVtZW50QnlJZCgnaG9tZUdyZWV0aW5nJykudGV4dENvbnRlbnQ9YCR7c3RhdGUuc2V0dGluZ3MuaG9tZU5hbWV9IOKAoiBXZWQsIDE2IFNlcCAyMDI2YDtkb2N1bWVudC5nZXRFbGVtZW50QnlJZCgnc2VydmljZVRvdGFsJykudGV4dENvbnRlbnQ9c3RhdGUuc2V0dGluZ3Muc2hvd0hvbWVBbW91bnRzP21vbmV5KGR1ZS50b3RhbCk6J+KAouKAouKAouKAoic7Y29uc3Qgc3VtPWRvY3VtZW50LnF1ZXJ5U2VsZWN0b3IoJyNzZXJ2aWNlVG90YWwnKT8uY2xvc2VzdCgnLnRyaWdnZXInKT8ucXVlcnlTZWxlY3RvcignLnN1bW1hcnknKTtpZihzdW0pc3VtLnRleHRDb250ZW50PSdQYXlhYmxlIHRvIGRhdGUg4oCiIHByb2plY3Rpb24gaW5zaWRlJztkb2N1bWVudC5nZXRFbGVtZW50QnlJZCgnc2VydmljZUJyZWFrZG93bicpLmlubmVySFRNTD1gPGRpdiBjbGFzcz0iYmlsbCI+PGRpdiBjbGFzcz0icm93Ij48ZGl2PjxiPvCfp7kgTWFpZCDigKIgcGF5YWJsZSB0byBkYXRlPC9iPjxkaXYgY2xhc3M9InRpbnkiPiR7ZHVlLnNjaGVkdWxlZC1kdWUuZGVkdWN0fS8ke2R1ZS5zY2hlZHVsZWR9IGVsYXBzZWQgc2NoZWR1bGVkIGRheXMg4oCiICR7bW9uZXkoZHVlLmRhaWx5KX0vZGF5PC9kaXY+PC9kaXY+PGI+JHttb25leShkdWUubWFpZFBheSl9PC9iPjwvZGl2PjwvZGl2PjxkaXYgY2xhc3M9ImJpbGwiPjxkaXYgY2xhc3M9InJvdyI+PGRpdj48Yj7wn6WbIE1pbGsg4oCiIHBheWFibGUgdG8gZGF0ZTwvYj48ZGl2IGNsYXNzPSJ0aW55Ij4ke2R1ZS5taWxrRGVsaXZlcmVkfSBkZWxpdmVyZWQgdGhyb3VnaCB0b2RheSDDlyAke21vbmV5KHN0YXRlLnNldHRpbmdzLm1pbGtSYXRlKX08L2Rpdj48L2Rpdj48Yj4ke21vbmV5KGR1ZS5taWxrUGF5KX08L2I+PC9kaXY+PC9kaXY+PGRpdiBjbGFzcz0idGlueSIgc3R5bGU9Im1hcmdpbi10b3A6OHB4Ij5Qcm9qZWN0ZWQgbW9udGggdG90YWwgZnJvbSBjdXJyZW50IHN0YXR1c2VzOiA8Yj4ke21vbmV5KHMudG90YWwpfTwvYj48L2Rpdj5gO2RvY3VtZW50LmdldEVsZW1lbnRCeUlkKCdob21lU3BlbmRUb3RhbCcpLnRleHRDb250ZW50PXN0YXRlLnNldHRpbmdzLnNob3dIb21lQW1vdW50cz9tb25leShob3VzZWhvbGRTcGVuZCgpKTon4oCi4oCi4oCi4oCiJztjb25zdCBjYXRzPWNhbGNDYXRlZ29yaWVzKCk7ZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoJ2hvbWVDYXRlZ29yeVByZXZpZXcnKS5pbm5lckhUTUw9T2JqZWN0LmVudHJpZXMoY2F0cykuZmlsdGVyKChbayx2XSk9PnY+MCkuc29ydCgoYSxiKT0+YlsxXS1hWzFdKS5zbGljZSgwLDUpLm1hcCgoW2ssdl0pPT5gPGRpdiBjbGFzcz0iYmlsbCI+PGRpdiBjbGFzcz0icm93Ij48c3Bhbj4ke2t9PC9zcGFuPjxiPiR7bW9uZXkodil9PC9iPjwvZGl2PjwvZGl2PmApLmpvaW4oJycpO2NvbnN0IGF0dD1hdHRlbnRpb25JdGVtcygpO2RvY3VtZW50LmdldEVsZW1lbnRCeUlkKCdhdHRlbnRpb25Db3VudCcpLnRleHRDb250ZW50PWF0dC5sZW5ndGg7ZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoJ2F0dGVudGlvbkxpc3QnKS5pbm5lckhUTUw9YXR0Lmxlbmd0aD9hdHQubWFwKHg9PmA8ZGl2IGNsYXNzPSJiaWxsIj48ZGl2IGNsYXNzPSJyb3ciPjxzcGFuPiR7eC50ZXh0fTwvc3Bhbj48Yj4ke3gudmFsdWV9PC9iPjwvZGl2PjwvZGl2PmApLmpvaW4oJycpOic8ZGl2IGNsYXNzPSJvayI+Tm90aGluZyBuZWVkcyBhdHRlbnRpb24uPC9kaXY+J30KcmVuZGVySG9tZSgpOwp9KSgpOw==";
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

    private String uiFixScript() {
        return new String(Base64.decode(UI_FIX_B64, Base64.DEFAULT), StandardCharsets.UTF_8);
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
                if (!child) view.evaluateJavascript(uiFixScript(), null);
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

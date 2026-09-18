package com.homeflow.test;

import android.Manifest;\nimport android.app.Activity;
import android.app.Dialog;
import android.print.PrintManager;
import android.content.ContentResolver;\nimport android.content.Context;
import android.content.Intent;\nimport android.content.pm.PackageManager;\nimport android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;\nimport android.provider.Telephony;\nimport android.view.View;\nimport android.view.WindowManager;
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
import java.io.InputStream;\nimport java.text.SimpleDateFormat;\nimport java.util.Date;\nimport java.util.Locale;\nimport java.util.regex.Pattern;\n\nimport org.json.JSONArray;\nimport org.json.JSONObject;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;

public class MainActivity extends Activity {
    private static final int FILE_CHOOSER = 501;\n    private static final int SMS_PERMISSION_REQ = 902;
    private static final String UI_PATCH_B64 = "KGZ1bmN0aW9uKCl7CmlmKHdpbmRvdy5fX2hmRml4NClyZXR1cm47d2luZG93Ll9faGZGaXg0PXRydWU7CmNvbnN0IHN0PWRvY3VtZW50LmNyZWF0ZUVsZW1lbnQoJ3N0eWxlJyk7c3QudGV4dENvbnRlbnQ9YApodG1sLGJvZHl7d2lkdGg6MTAwJTttaW4taGVpZ2h0OjEwMCU7b3ZlcmZsb3cteDpoaWRkZW47b3ZlcnNjcm9sbC1iZWhhdmlvcjpub25lIWltcG9ydGFudH1ib2R5e3BhZGRpbmc6MCFpbXBvcnRhbnQ7bWFyZ2luOjAhaW1wb3J0YW50O21pbi1oZWlnaHQ6MTAwdmghaW1wb3J0YW50Oy13ZWJraXQtb3ZlcmZsb3ctc2Nyb2xsaW5nOmF1dG8haW1wb3J0YW50O3RvdWNoLWFjdGlvbjpwYW4teX0ucGhvbmV7b3ZlcnNjcm9sbC1iZWhhdmlvcjpub25lIWltcG9ydGFudH1idXR0b24sLnNldHRpbmdFbnRyeSwuc3RhdHVzQnRuLC5lZGl0b3JCdG57LXdlYmtpdC10YXAtaGlnaGxpZ2h0LWNvbG9yOnRyYW5zcGFyZW50IWltcG9ydGFudH1AbWVkaWEobWF4LXdpZHRoOjYwMHB4KXtib2R5e2JhY2tncm91bmQ6bGluZWFyLWdyYWRpZW50KDE0NWRlZywjZmJmY2Y5LCNmNWY4ZjggNTIlLCNmYmY1ZWYpIWltcG9ydGFudH0ucGhvbmV7d2lkdGg6MTAwJSFpbXBvcnRhbnQ7bWF4LXdpZHRoOm5vbmUhaW1wb3J0YW50O21pbi1oZWlnaHQ6MTAwdmghaW1wb3J0YW50O21hcmdpbjowIWltcG9ydGFudDtib3JkZXI6MCFpbXBvcnRhbnQ7Ym9yZGVyLXJhZGl1czowIWltcG9ydGFudDtib3gtc2hhZG93Om5vbmUhaW1wb3J0YW50O3BhZGRpbmc6MTJweCAxMHB4IDk4cHghaW1wb3J0YW50fS5zY3JlZW57cGFkZGluZy1ib3R0b206MTAwcHghaW1wb3J0YW50fS50b3B7cGFkZGluZy10b3A6OHB4IWltcG9ydGFudH0uaGVhZGxpbmV7Zm9udC1zaXplOmNsYW1wKDIzcHgsN3Z3LDI4cHgpIWltcG9ydGFudDtsaW5lLWhlaWdodDoxLjA4IWltcG9ydGFudH0uc3VtbWFyeXttYXgtd2lkdGg6NTh2dyFpbXBvcnRhbnR9Lm5hdntwb3NpdGlvbjpmaXhlZCFpbXBvcnRhbnQ7bGVmdDo4cHghaW1wb3J0YW50O3JpZ2h0OjhweCFpbXBvcnRhbnQ7Ym90dG9tOjZweCFpbXBvcnRhbnQ7d2lkdGg6YXV0byFpbXBvcnRhbnQ7ei1pbmRleDo4MCFpbXBvcnRhbnR9LnNoZWV0e2FsaWduLWl0ZW1zOmZsZXgtZW5kIWltcG9ydGFudDtvdmVyc2Nyb2xsLWJlaGF2aW9yOm5vbmUhaW1wb3J0YW50fS5tb2RhbHt3aWR0aDoxMDAlIWltcG9ydGFudDttYXgtd2lkdGg6bm9uZSFpbXBvcnRhbnQ7bWF4LWhlaWdodDpjYWxjKDEwMHZoIC0gMThweCkhaW1wb3J0YW50O2JvcmRlci1yYWRpdXM6MjRweCAyNHB4IDAgMCFpbXBvcnRhbnQ7cGFkZGluZy1ib3R0b206MjJweCFpbXBvcnRhbnQ7b3ZlcnNjcm9sbC1iZWhhdmlvcjpjb250YWluIWltcG9ydGFudH0jbW9kYWxCb2R5e3BhZGRpbmctYm90dG9tOjg2cHghaW1wb3J0YW50fSNtb2RhbEJvZHk+YnV0dG9uLnByaW1hcnkuZnVsbDpsYXN0LWNoaWxkLCNtb2RhbEJvZHk+LmdyaWQyOmxhc3QtY2hpbGR7cG9zaXRpb246c3RpY2t5IWltcG9ydGFudDtib3R0b206MCFpbXBvcnRhbnQ7ei1pbmRleDozMCFpbXBvcnRhbnQ7bWFyZ2luLXRvcDoxMnB4IWltcG9ydGFudDtwYWRkaW5nLXRvcDoxMHB4IWltcG9ydGFudDtwYWRkaW5nLWJvdHRvbTo4cHghaW1wb3J0YW50O2JhY2tncm91bmQ6bGluZWFyLWdyYWRpZW50KDE4MGRlZyxyZ2JhKDI1NSwyNTAsMjQ1LDApLCNmZmZhZjUgMjQlKSFpbXBvcnRhbnR9I21vZGFsQm9keT4uZ3JpZDI6bGFzdC1jaGlsZCBidXR0b257bWluLWhlaWdodDo0NnB4IWltcG9ydGFudH19CmA7ZG9jdW1lbnQuaGVhZC5hcHBlbmRDaGlsZChzdCk7CmZ1bmN0aW9uIGRheSgpe2NvbnN0IG49bmV3IERhdGUoKTtyZXR1cm4gbi5nZXRGdWxsWWVhcigpPT09MjAyNiYmbi5nZXRNb250aCgpPT09OD9NYXRoLm1pbigzMCxNYXRoLm1heCgxLG4uZ2V0RGF0ZSgpKSk6MTZ9d2luZG93LnRvZGF5RGF5PWRheTsKZnVuY3Rpb24gbGFiZWwoKXtyZXR1cm4gbmV3IERhdGUoMjAyNiw4LGRheSgpKS50b0xvY2FsZURhdGVTdHJpbmcoJ2VuLUdCJyx7d2Vla2RheTonc2hvcnQnLGRheTonMi1kaWdpdCcsbW9udGg6J3Nob3J0Jyx5ZWFyOidudW1lcmljJ30pfQp3aW5kb3cuc3luY1RvZGF5PWZ1bmN0aW9uKCl7Y29uc3QgeD1nZXREYXkoZGF5KCkpO21haWRTdGF0ZS50ZXh0Q29udGVudD1tYWlkTGFiZWwoeC5tYWlkKSsoeC5tYWlkPT09J0NhbWUnPycg4pyTJzonJyk7bWlsa1N0YXRlLnRleHRDb250ZW50PW1pbGtMYWJlbCh4Lm1pbGspKyh4Lm1pbGs9PT0nRGVsaXZlcmVkJz8nIOKckyc6JycpO3RvZGF5U3VtbWFyeS50ZXh0Q29udGVudD14LmF3YXk/J091dCBvZiBzdGF0aW9uIOKAoiBNYWlkIG5vdCByZXF1aXJlZCDigKIgTWlsayBza2lwcGVkJzpgJHttYWlkTGFiZWwoeC5tYWlkKX0g4oCiICR7bWlsa0xhYmVsKHgubWlsayl9JHt4LnZlcmlmaWVkPycg4oCiIFZlcmlmaWVkJzonJ31gO2F3YXlCdG4udGV4dENvbnRlbnQ9eC5hd2F5PyfinIjvuI8gT3V0IG9mIHN0YXRpb24gT04nOifinIjvuI8gT3V0IG9mIHN0YXRpb24nfTsKd2luZG93LnNldFRvZGF5TWFpZD12PT57c2V0RGF5KGRheSgpLHttYWlkOnYsYXdheTpmYWxzZSx2ZXJpZmllZDpmYWxzZX0pO2Nsb3NlU2hlZXQoKX07d2luZG93LnNldFRvZGF5TWlsaz12PT57c2V0RGF5KGRheSgpLHttaWxrOnYsYXdheTpmYWxzZSx2ZXJpZmllZDpmYWxzZX0pO2Nsb3NlU2hlZXQoKX07CndpbmRvdy5vcGVuVG9kYXlNYWlkTWVudT1mdW5jdGlvbigpe2NvbnN0IHg9Z2V0RGF5KGRheSgpKTtpZih4LmF3YXkpe3RvYXN0KCdUdXJuIG9mZiBPdXQgb2Ygc3RhdGlvbiBiZWZvcmUgY2hhbmdpbmcgTWFpZCcpO3JldHVybn1vcGVuU2hlZXQoJ01haWQg4oCiIFRvZGF5JywnQ2hvb3NlIHdoYXQgaGFwcGVuZWQgdG9kYXkuJyxgPGRpdiBjbGFzcz0ic2V0dGluZ3NNZW51Ij48YnV0dG9uIGNsYXNzPSJzZXR0aW5nRW50cnkiIG9uY2xpY2s9InNldFRvZGF5TWFpZCgnQ2FtZScpIj48c3BhbiBjbGFzcz0ic2ljb24iPvCfp7k8L3NwYW4+PHNwYW4gY2xhc3M9InNtYWluIj48c3BhbiBjbGFzcz0ic3RpdGxlIj5DYW1lIHRvZGF5PC9zcGFuPjwvc3Bhbj48L2J1dHRvbj48YnV0dG9uIGNsYXNzPSJzZXR0aW5nRW50cnkiIG9uY2xpY2s9InNldFRvZGF5TWFpZCgnQWJzZW50JykiPjxzcGFuIGNsYXNzPSJzaWNvbiI+8J+RpDwvc3Bhbj48c3BhbiBjbGFzcz0ic21haW4iPjxzcGFuIGNsYXNzPSJzdGl0bGUiPkFic2VudDwvc3Bhbj48L3NwYW4+PC9idXR0b24+PGJ1dHRvbiBjbGFzcz0ic2V0dGluZ0VudHJ5IiBvbmNsaWNrPSJzZXRUb2RheU1haWQoJ05vdFJlcXVpcmVkJykiPjxzcGFuIGNsYXNzPSJzaWNvbiI+4o+477iPPC9zcGFuPjxzcGFuIGNsYXNzPSJzbWFpbiI+PHNwYW4gY2xhc3M9InN0aXRsZSI+Tm90IHJlcXVpcmVkIHRvZGF5PC9zcGFuPjwvc3Bhbj48L2J1dHRvbj48L2Rpdj5gKX07CndpbmRvdy5vcGVuVG9kYXlNaWxrTWVudT1mdW5jdGlvbigpe2NvbnN0IHg9Z2V0RGF5KGRheSgpKTtpZih4LmF3YXkpe3RvYXN0KCdUdXJuIG9mZiBPdXQgb2Ygc3RhdGlvbiBiZWZvcmUgY2hhbmdpbmcgTWlsaycpO3JldHVybn1vcGVuU2hlZXQoJ01pbGsg4oCiIFRvZGF5JywnQ2hvb3NlIHRoZSBkZWxpdmVyeSByZXN1bHQuJyxgPGRpdiBjbGFzcz0ic2V0dGluZ3NNZW51Ij48YnV0dG9uIGNsYXNzPSJzZXR0aW5nRW50cnkiIG9uY2xpY2s9InNldFRvZGF5TWlsaygnRGVsaXZlcmVkJykiPjxzcGFuIGNsYXNzPSJzaWNvbiI+8J+lmzwvc3Bhbj48c3BhbiBjbGFzcz0ic21haW4iPjxzcGFuIGNsYXNzPSJzdGl0bGUiPkRlbGl2ZXJlZDwvc3Bhbj48L3NwYW4+PC9idXR0b24+PGJ1dHRvbiBjbGFzcz0ic2V0dGluZ0VudHJ5IiBvbmNsaWNrPSJzZXRUb2RheU1pbGsoJ1NraXAnKSI+PHNwYW4gY2xhc3M9InNpY29uIj7ij63vuI88L3NwYW4+PHNwYW4gY2xhc3M9InNtYWluIj48c3BhbiBjbGFzcz0ic3RpdGxlIj5Ta2lwIHRvZGF5PC9zcGFuPjwvc3Bhbj48L2J1dHRvbj48YnV0dG9uIGNsYXNzPSJzZXR0aW5nRW50cnkiIG9uY2xpY2s9InNldFRvZGF5TWlsaygnTm90RGVsaXZlcmVkJykiPjxzcGFuIGNsYXNzPSJzaWNvbiI+4p2MPC9zcGFuPjxzcGFuIGNsYXNzPSJzbWFpbiI+PHNwYW4gY2xhc3M9InN0aXRsZSI+UmVxdWVzdGVkIGJ1dCBub3QgZGVsaXZlcmVkPC9zcGFuPjwvc3Bhbj48L2J1dHRvbj48L2Rpdj5gKX07CndpbmRvdy50b2dnbGVBd2F5PWZ1bmN0aW9uKCl7Y29uc3QgZD1kYXkoKSx4PWdldERheShkKSxhPSF4LmF3YXk7aWYoYSlzZXREYXkoZCx7YXdheTp0cnVlLHByZUF3YXlNYWlkOngubWFpZCxwcmVBd2F5TWlsazp4Lm1pbGssbWFpZDppc1N1bmRheShkKT8nU3VuZGF5JzonTm90UmVxdWlyZWQnLG1pbGs6J1NraXAnLHZlcmlmaWVkOmZhbHNlfSk7ZWxzZSBzZXREYXkoZCx7YXdheTpmYWxzZSxtYWlkOmlzU3VuZGF5KGQpPydTdW5kYXknOihzdGF0ZS5zZXR0aW5ncy5yZXN0b3JlQWZ0ZXJBd2F5IT09ZmFsc2U/KHgucHJlQXdheU1haWR8fCdDYW1lJyk6J0NhbWUnKSxtaWxrOnN0YXRlLnNldHRpbmdzLnJlc3RvcmVBZnRlckF3YXkhPT1mYWxzZT8oeC5wcmVBd2F5TWlsa3x8J0RlbGl2ZXJlZCcpOidEZWxpdmVyZWQnLHByZUF3YXlNYWlkOm51bGwscHJlQXdheU1pbGs6bnVsbCx2ZXJpZmllZDpmYWxzZX0pfTt3aW5kb3cudmVyaWZ5VG9kYXk9KCk9PntzZXREYXkoZGF5KCkse3ZlcmlmaWVkOnRydWV9KTt0b2FzdCgnVG9kYXkgdmVyaWZpZWQg4pyTJyl9OwpmdW5jdGlvbiBkdWUoKXtjb25zdCBmPWNhbGNTZXJ2aWNlcygpO2xldCBzY2g9MCxkZWQ9MCxtPTA7Zm9yKGxldCBkPTE7ZDw9TWF0aC5taW4oZGF5KCksZi5kaW0pO2QrKyl7Y29uc3QgeD1nZXREYXkoZCk7aWYoIWlzU3VuZGF5KGQpKXtzY2grKztpZih4Lm1haWQhPT0nQ2FtZScpZGVkKyt9aWYoeC5taWxrPT09J0RlbGl2ZXJlZCcpbSsrfXJldHVybntzY2gsZGVkLG0sZGFpbHk6Zi5kYWlseSxtYWlkOihzY2gtZGVkKSpmLmRhaWx5LG1pbGs6bSpzdGF0ZS5zZXR0aW5ncy5taWxrUmF0ZSx0b3RhbDooc2NoLWRlZCkqZi5kYWlseSttKnN0YXRlLnNldHRpbmdzLm1pbGtSYXRlLGZ1bGw6Zn19CndpbmRvdy5nb0JpbGxzPWZ1bmN0aW9uKCl7Y2xvc2VTaGVldCgpO2NvbnN0IG49ZG9jdW1lbnQucXVlcnlTZWxlY3RvcignLm5hdiBidXR0b25bZGF0YS1zY3JlZW49Im1vbmV5Il0nKTtnb1NjcmVlbignbW9uZXknLG4pO2NvbnN0IGI9ZG9jdW1lbnQucXVlcnlTZWxlY3RvcignLnN1YnRhYnMgYnV0dG9uW2RhdGEtc3ViPSJiaWxsccyJdJyk7aWYoYilzd2l0Y2hNb25leVN1YignYmlsbHMnLGIpO3NldFRpbWVvdXQoKCk9PnNjcm9sbFRvKDAsMCksMjApfTsKd2luZG93LmRlbGV0ZUJpbGw9ZnVuY3Rpb24oaWQpe2NvbnN0IGI9c3RhdGUuYmlsbHMuZmluZCh4PT54LmlkPT09aWQpO2lmKCFiKXJldHVybjtpZihzdGF0ZS5zZXR0aW5ncy5jb25maXJtRGVsZXRlIT09ZmFsc2UmJiFjb25maXJtKGBEZWxldGUgJHtiLm5hbWV9P2ApKXJldHVybjtzdGF0ZS5iaWxscz1zdGF0ZS5iaWxscy5maWx0ZXIoeD0+eC5pZCE9PWlkKTtzYXZlU3RhdGUoKTtyZW5kZXJCaWxscygpO3RvYXN0KCdCaWxsIGRlbGV0ZWQnKX07CndpbmRvdy5yZW5kZXJCaWxscz1mdW5jdGlvbigpe2NvbnN0IGw9ZG9jdW1lbnQuZ2V0RWxlbWVudEJ5SWQoJ2JpbGxzTGlzdCcpLHI9c3RhdGUuYmlsbHMuc2xpY2UoKS5zb3J0KChhLGIpPT5TdHJpbmcoYS5kdWUpLmxvY2FsZUNvbXBhcmUoU3RyaW5nKGIuZHVlKSkpO2wuaW5uZXJIVE1MPXIubGVuZ3RoP3IubWFwKGI9PmA8ZGl2IGNsYXNzPSJjYXJkIj48ZGl2IGNsYXNzPSJyb3ciPjxkaXY+PGI+JHtiLm5hbWV9PC9iPjxkaXYgY2xhc3M9IiR7Yi5wYWlkPydwYWlkJzonZHVlJ30iPiR7Yi5wYWlkPydQYWlkICcrKGIucGFpZERhdGV8fCcnKTonRHVlICcrYi5kdWV9JHtiLnJlY3VycmluZz8nIOKAoiBSZWN1cnJpbmcnOicnfTwvZGl2PjwvZGl2PjxkaXYgc3R5bGU9InRleHQtYWxpZ246cmlnaHQiPjxiPiR7bW9uZXkoYi5hbW91bnQpfTwvYj48ZGl2IGNsYXNzPSJ0aW55Ij4ke3BheW1lbnROYW1lKGIucGF5bWVudCl9PC9kaXY+PC9kaXY+PC9kaXY+PGRpdiBjbGFzcz0iZ3JpZDMiIHN0eWxlPSJtYXJnaW4tdG9wOjlweCI+PGJ1dHRvbiBjbGFzcz0iZ2hvc3QiIG9uY2xpY2s9ImVkaXRCaWxsKCcke2IuaWR9JykiPkVkaXQ8L2J1dHRvbj48YnV0dG9uIGNsYXNzPSIke2IucGFpZD8nc29mdCc6J3ByaW1hcnknfSIgb25jbGljaz0idG9nZ2xlQmlsbFBhaWQoJyR7Yi5pZH0nKSI+JHtiLnBhaWQ/J01hcmsgdW5wYWlkJzonTWFyayBwYWlkJ308L2J1dHRvbj48YnV0dG9uIGNsYXNzPSJkYW5nZXIiIG9uY2xpY2s9ImRlbGV0ZUJpbGwoJyR7Yi5pZH0nKSI+RGVsZXRlPC9idXR0b24+PC9kaXY+PC9kaXY+YCkuam9pbignJyk6JzxkaXYgY2xhc3M9Im9rIj5ObyBiaWxscyB5ZXQuIEFkZCB5b3VyIGZpcnN0IGJpbGwgYmVsb3cuPC9kaXY+J307CndpbmRvdy5yZW5kZXJIb21lPWZ1bmN0aW9uKCl7c3luY1RvZGF5KCk7Y29uc3QgZj1jYWxjU2VydmljZXMoKSxkPWR1ZSgpO2hvbWVHcmVldGluZy50ZXh0Q29udGVudD1gJHtzdGF0ZS5zZXR0aW5ncy5ob21lTmFtZX0g4oCiICR7bGFiZWwoKX1gO3NlcnZpY2VUb3RhbC50ZXh0Q29udGVudD1zdGF0ZS5zZXR0aW5ncy5zaG93SG9tZUFtb3VudHM/bW9uZXkoZC50b3RhbCk6J+KAouKAouKAouKAoic7Y29uc3QgcT1zZXJ2aWNlVG90YWwuY2xvc2VzdCgnLnRyaWdnZXInKT8ucXVlcnlTZWxlY3RvcignLnN1bW1hcnknKTtpZihxKXEudGV4dENvbnRlbnQ9J1BheWFibGUgdG8gZGF0ZSDigKIgcHJvamVjdGVkIG1vbnRoIHRvdGFsIGluc2lkZSc7c2VydmljZUJyZWFrZG93bi5pbm5lckhUTUw9YDxkaXYgY2xhc3M9ImJpbGwiPjxkaXYgY2xhc3M9InJvdyI+PGRpdj48Yj7wn6e5IE1haWQg4oCiIHBheWFibGUgdG8gZGF0ZTwvYj48ZGl2IGNsYXNzPSJ0aW55Ij4ke2Quc2NoLWQuZGVkfS8ke2Quc2NofSBlbGFwc2VkIHNjaGVkdWxlZCBkYXlzIOKAoiAke21vbmV5KGQuZGFpbHkpfS9kYXk8L2Rpdj48L2Rpdj48Yj4ke21vbmV5KGQubWFpZCl9PC9iPjwvZGl2PjwvZGl2PjxkaXYgY2xhc3M9ImJpbGwiPjxkaXYgY2xhc3M9InJvdyI+PGRpdj48Yj7wn6WbIE1pbGsg4oCiIHBheWFibGUgdG8gZGF0ZTwvYj48ZGl2IGNsYXNzPSJ0aW55Ij4ke2QubX0gZGVsaXZlcmVkIHRocm91Z2ggJHtsYWJlbCgpfTwvZGl2PjwvZGl2PjxiPiR7bW9uZXkoZC5taWxrKX08L2I+PC9kaXY+PC9kaXY+PGRpdiBjbGFzcz0idGlueSIgc3R5bGU9Im1hcmdpbi10b3A6OHB4Ij5Qcm9qZWN0ZWQgbW9udGggdG90YWw6IDxiPiR7bW9uZXkoZi50b3RhbCl9PC9iPjwvZGl2PmA7aG9tZVNwZW5kVG90YWwudGV4dENvbnRlbnQ9c3RhdGUuc2V0dGluZ3Muc2hvd0hvbWVBbW91bnRzP21vbmV5KGhvdXNlaG9sZFNwZW5kKCkpOifigKLigKLigKLigKInO2NvbnN0IGM9Y2FsY0NhdGVnb3JpZXMoKTtob21lQ2F0ZWdvcnlQcmV2aWV3LmlubmVySFRNTD1PYmplY3QuZW50cmllcyhjKS5maWx0ZXIoKFtrLHZdKT0+dj4wKS5zb3J0KChhLGIpPT5iWzFdLWFbMV0pLnNsaWNlKDAsNSkubWFwKChbayx2XSk9PmA8ZGl2IGNsYXNzPSJiaWxsIj48ZGl2IGNsYXNzPSJyb3ciPjxzcGFuPiR7a308L3NwYW4+PGI+JHttb25leSh2KX08L2I+PC9kaXY+PC9kaXY+YCkuam9pbignJykrYDxidXR0b24gY2xhc3M9Imdob3N0IGZ1bGwiIHN0eWxlPSJtYXJnaW4tdG9wOjEwcHgiIG9uY2xpY2s9ImV2ZW50LnN0b3BQcm9wYWdhdGlvbigpO2dvQmlsbHMoKSI+8J+nviBNYW5hZ2UgLyBlZGl0IGJpbGxzPC9idXR0b24+YDtjb25zdCBhPWF0dGVudGlvbkl0ZW1zKCk7YXR0ZW50aW9uQ291bnQudGV4dENvbnRlbnQ9YS5sZW5ndGg7YXR0ZW50aW9uTGlzdC5pbm5lckhUTUw9YS5sZW5ndGg/YS5tYXAoeD0+YDxkaXYgY2xhc3M9ImJpbGwiPjxkaXYgY2xhc3M9InJvdyI+PHNwYW4+JHt4LnRleHR9PC9zcGFuPjxiPiR7eC52YWx1ZX08L2I+PC9kaXY+PC9kaXY+YCkuam9pbignJyk6JzxkaXYgY2xhc3M9Im9rIj5Ob3RoaW5nIG5lZWRzIGF0dGVudGlvbi48L2Rpdj4nfTsKd2luZG93LnNjYW5QaG9uZVNtcz1mdW5jdGlvbigpe2lmKCFzdGF0ZS5zZXR0aW5ncy5zbXNJbXBvcnQpe2ltcG9ydFN0YXR1cy5pbm5lckhUTUw9JzxkaXYgY2xhc3M9ImVycm9yIj5EaXJlY3QgU01TIGltcG9ydCBpcyBkaXNhYmxlZCBpbiBTZXR0aW5ncy48L2Rpdj4nO3JldHVybn1pZighKHdpbmRvdy5BbmRyb2lkU01TJiZBbmRyb2lkU01TLnNjYW5TbXMpKXtpbXBvcnRTdGF0dXMuaW5uZXJIVE1MPSc8ZGl2IGNsYXNzPSJlcnJvciI+RGlyZWN0IFNNUyBhY2Nlc3MgaXMgdW5hdmFpbGFibGUgaW4gdGhpcyBidWlsZC4gWE1MIC8gWklQIGltcG9ydCBzdGlsbCB3b3Jrcy48L2Rpdj4nO3JldHVybn1pbXBvcnRTdGF0dXMuaW5uZXJIVE1MPSc8ZGl2IGNsYXNzPSJvayI+UmVxdWVzdGluZyBTTVMgcGVybWlzc2lvbiBhbmQgc2Nhbm5pbmcgbG9jYWxseeKApjwvZGl2Pic7QW5kcm9pZFNNUy5zY2FuU21zKCl9Owp3aW5kb3cuaG9tZWZsb3dSZWNlaXZlU21zPWZ1bmN0aW9uKGope3RyeXtjb25zdCBhPXR5cGVvZiBqPT09J3N0cmluZyc/SlNPTi5wYXJzZShqKTpqO2ltcG9ydFJhdz0oYXx8W10pLm1hcCgobSxpKT0+KHtpZDonUCcraSxzZW5kZXI6bS5zZW5kZXJ8fCcnLGJvZHk6bS5ib2R5fHwnJyxkYXRlOlN0cmluZyhtLmRhdGV8fCcnKSxyZWFkYWJsZTptLnJlYWRhYmxlfHwnJ30pKTtpZighaW1wb3J0UmF3Lmxlbmd0aCl0aHJvdyBFcnJvcignTm8gZmluYW5jaWFsIFNNUyBjYW5kaWRhdGVzIHdlcmUgZm91bmQuJyk7Y29uc3QgZHM9aW1wb3J0UmF3Lm1hcChtPT5wYXJzZURhdGUobS5kYXRlLG0ucmVhZGFibGUpKS5maWx0ZXIoQm9vbGVhbik7ZGV0ZWN0ZWRNaW49bmV3IERhdGUoTWF0aC5taW4oLi4uZHMpKTtkZXRlY3RlZE1heD1uZXcgRGF0ZShNYXRoLm1heCguLi5kcykpO2ltcG9ydEZvcm1hdD0nRGlyZWN0IHBob25lIFNNUyc7ZmlsZVRpdGxlLnRleHRDb250ZW50PSdQaG9uZSBTTVMgc2Nhbm5lZCc7ZmlsZU5hbWUudGV4dENvbnRlbnQ9J0ZpbmFuY2lhbCBTTVMgY2FuZGlkYXRlcyc7ZGV0ZWN0ZWRSYW5nZS50ZXh0Q29udGVudD1gJHtkZXRlY3RlZE1pbi50b0xvY2FsZURhdGVTdHJpbmcoJ2VuLUdCJyl9IOKGkiAke2RldGVjdGVkTWF4LnRvTG9jYWxlRGF0ZVN0cmluZygnZW4tR0InKX1gO3Ntc0NvdW50LnRleHRDb250ZW50PWltcG9ydFJhdy5sZW5ndGgrJyBTTVMnO2Zvcm1hdC50ZXh0Q29udGVudD1pbXBvcnRGb3JtYXQ7ZGV0ZWN0ZWQuY2xhc3NMaXN0LnJlbW92ZSgnaGlkZGVuJyk7YW5hbHlzZUltcG9ydC5kaXNhYmxlZD1mYWxzZTtpbXBvcnRTdGF0dXMuaW5uZXJIVE1MPSc8ZGl2IGNsYXNzPSJvayI+UGhvbmUgU01TIGxvYWRlZCBsb2NhbGx5LiBUYXAgQW5hbHlzZSArIGNoZWNrIGR1cGxpY2F0ZXMuPC9kaXY+J31jYXRjaChlKXtob21lZmxvd1Ntc0Vycm9yKGUubWVzc2FnZXx8U3RyaW5nKGUpKX19O3dpbmRvdy5ob21lZmxvd1Ntc0Vycm9yPW09PntpbXBvcnRTdGF0dXMuaW5uZXJIVE1MPSc8ZGl2IGNsYXNzPSJlcnJvciI+Jytlc2MobSkrJzwvZGl2Pic7dG9hc3QoJ1NNUyBzY2FuIGNvdWxkIG5vdCBiZSBjb21wbGV0ZWQnKX07CmNvbnN0IHFiPVsuLi5kb2N1bWVudC5xdWVyeVNlbGVjdG9yQWxsKCcucXVpY2sgYnV0dG9uJyldLmZpbmQoYj0+Yi50ZXh0Q29udGVudC50cmltKCkuZW5kc1dpdGgoJ0JpbGwnKSk7aWYocWIpe3FiLmlubmVySFRNTD0nPGk+8J+nvjwvaT5CaWxscyc7cWIub25jbGljaz1nb0JpbGxzfWNvbnN0IHNiPVsuLi5kb2N1bWVudC5xdWVyeVNlbGVjdG9yQWxsKCcjaW1wb3J0IC5ncmlkMiBidXR0b24nKV0uZmluZChiPT5iLnRleHRDb250ZW50LmluY2x1ZGVzKCdTY2FuIHBob25lIFNNUycpKTtpZihzYilzYi5vbmNsaWNrPXNjYW5QaG9uZVNtczsKcmVuZGVyQWxsKCk7Cn0pKCk7Cg==";
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
        w.setBackgroundColor(Color.TRANSPARENT);\n        w.setOverScrollMode(View.OVER_SCROLL_NEVER);\n        w.setVerticalScrollBarEnabled(false);\n        w.setHorizontalScrollBarEnabled(false);
        w.addJavascriptInterface(new PrintBridge(this), "AndroidPrint");\n        w.addJavascriptInterface(new SmsBridge(this), "AndroidSMS");
        w.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                view.evaluateJavascript("window.print=function(){if(window.AndroidPrint){AndroidPrint.printHtml(document.documentElement.outerHTML);}};", null);
                if (!child) { String p = new String(Base64.decode(UI_PATCH_B64, Base64.DEFAULT), java.nio.charset.StandardCharsets.UTF_8); view.evaluateJavascript(p, null); }
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


    public class SmsBridge {
        private final Activity activity;
        SmsBridge(Activity activity) { this.activity = activity; }
        @JavascriptInterface public void scanSms() {
            activity.runOnUiThread(() -> {
                if (android.os.Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.READ_SMS}, SMS_PERMISSION_REQ);
                } else scanSmsNow();
            });
        }
    }

    private void scanSmsNow() {
        new Thread(() -> {
            JSONArray rows = new JSONArray(); Cursor cur = null;
            try {
                ContentResolver cr = getContentResolver();
                String[] projection = {Telephony.Sms.ADDRESS, Telephony.Sms.BODY, Telephony.Sms.DATE};
                cur = cr.query(Telephony.Sms.Inbox.CONTENT_URI, projection, null, null, Telephony.Sms.DEFAULT_SORT_ORDER);
                Pattern money = Pattern.compile("(?:₹|\\\\b(?:rs\\\\.?|inr)\\\\s*[:\\\\-]?\\\\s*[0-9])", Pattern.CASE_INSENSITIVE);
                Pattern finance = Pattern.compile("\\\\b(debit|debited|credit|credited|spent|paid|purchase|txn|transaction|upi|card|account|a/c|refund|withdrawn|withdrawal|received|sent)\\\\b", Pattern.CASE_INSENSITIVE);
                SimpleDateFormat fmt = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.ENGLISH);
                int seen=0;
                if(cur!=null){int ia=cur.getColumnIndex(Telephony.Sms.ADDRESS), ib=cur.getColumnIndex(Telephony.Sms.BODY), id=cur.getColumnIndex(Telephony.Sms.DATE);
                    while(cur.moveToNext()&&seen<10000){seen++; String body=ib>=0?cur.getString(ib):""; if(body==null||!money.matcher(body).find()||!finance.matcher(body).find())continue; long date=id>=0?cur.getLong(id):0L; JSONObject o=new JSONObject(); o.put("sender",ia>=0?cur.getString(ia):"");o.put("body",body);o.put("date",String.valueOf(date));o.put("readable",date>0?fmt.format(new Date(date)):"");rows.put(o);}
                }
                final String json=rows.toString(); runOnUiThread(() -> webView.evaluateJavascript("window.homeflowReceiveSms(" + JSONObject.quote(json) + ");", null));
            } catch(Exception e){final String msg=e.getClass().getSimpleName()+": "+(e.getMessage()==null?"SMS access failed":e.getMessage());runOnUiThread(() -> webView.evaluateJavascript("window.homeflowSmsError(" + JSONObject.quote(msg) + ");", null));}
            finally{if(cur!=null)cur.close();}
        }).start();
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==SMS_PERMISSION_REQ){if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)scanSmsNow();else webView.evaluateJavascript("window.homeflowSmsError('SMS permission was denied. You can still import an XML / ZIP backup.');", null);}
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

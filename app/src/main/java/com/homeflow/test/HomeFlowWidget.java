package com.homeflow.test;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.widget.RemoteViews;

public class HomeFlowWidget extends AppWidgetProvider {
    public static final String ACTION_MAID = "com.homeflow.test.WIDGET_MAID";
    public static final String ACTION_MILK = "com.homeflow.test.WIDGET_MILK";
    public static final String ACTION_AWAY = "com.homeflow.test.WIDGET_AWAY";
    public static final String ACTION_VERIFY = "com.homeflow.test.WIDGET_VERIFY";
    private static final String PREFS = "homeflow_widget";

    @Override public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) update(context, manager, id);
    }

    @Override public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        String action = intent.getAction();
        if (ACTION_MAID.equals(action) || ACTION_MILK.equals(action) || ACTION_AWAY.equals(action) || ACTION_VERIFY.equals(action)) {
            SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
            String maid = p.getString("maid", "Came today");
            String milk = p.getString("milk", "Delivered");
            boolean away = p.getBoolean("away", false);
            SharedPreferences.Editor e = p.edit();
            if (ACTION_MAID.equals(action) && !away) {
                maid = maid.equals("Came today") ? "Absent" : maid.equals("Absent") ? "Not required today" : "Came today";
                e.putString("maid", maid).putBoolean("verified", false);
            } else if (ACTION_MILK.equals(action) && !away) {
                milk = milk.equals("Delivered") ? "Skip today" : milk.equals("Skip today") ? "Requested but not delivered" : "Delivered";
                e.putString("milk", milk).putBoolean("verified", false);
            } else if (ACTION_AWAY.equals(action)) {
                if (!away) {
                    e.putString("preMaid", maid).putString("preMilk", milk).putBoolean("away", true).putString("maid", "Not required today").putString("milk", "Skip today").putBoolean("verified", false);
                } else {
                    e.putBoolean("away", false).putString("maid", p.getString("preMaid", "Came today")).putString("milk", p.getString("preMilk", "Delivered")).putBoolean("verified", false);
                }
            } else if (ACTION_VERIFY.equals(action)) {
                e.putBoolean("verified", true);
            }
            e.apply();
            AppWidgetManager m = AppWidgetManager.getInstance(context);
            ComponentName c = new ComponentName(context, HomeFlowWidget.class);
            for (int id : m.getAppWidgetIds(c)) update(context, m, id);
        }
    }

    private static PendingIntent broadcast(Context c, String action, int requestCode) {
        Intent i = new Intent(c, HomeFlowWidget.class).setAction(action);
        return PendingIntent.getBroadcast(c, requestCode, i, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
    }

    private static void update(Context context, AppWidgetManager manager, int id) {
        SharedPreferences p = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        String maid = p.getString("maid", "Came today");
        String milk = p.getString("milk", "Delivered");
        boolean away = p.getBoolean("away", false);
        boolean verified = p.getBoolean("verified", false);
        RemoteViews v = new RemoteViews(context.getPackageName(), R.layout.widget);
        v.setTextViewText(R.id.widget_maid, "🧹 " + maid);
        v.setTextViewText(R.id.widget_milk, "🥛 " + milk);
        v.setTextViewText(R.id.widget_away, away ? "✈ Out of station ON" : "✈ Out of station");
        v.setTextViewText(R.id.widget_verify, verified ? "✓ Verified" : "✓ Verify today");
        v.setTextViewText(R.id.widget_mode, away ? "AWAY" : (verified ? "VERIFIED" : "AT HOME"));
        v.setOnClickPendingIntent(R.id.widget_maid, broadcast(context, ACTION_MAID, 1));
        v.setOnClickPendingIntent(R.id.widget_milk, broadcast(context, ACTION_MILK, 2));
        v.setOnClickPendingIntent(R.id.widget_away, broadcast(context, ACTION_AWAY, 3));
        v.setOnClickPendingIntent(R.id.widget_verify, broadcast(context, ACTION_VERIFY, 4));
        Intent open = new Intent(context, MainActivity.class);
        v.setOnClickPendingIntent(R.id.widget_root, PendingIntent.getActivity(context, 10, open, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        manager.updateAppWidget(id, v);
    }
}

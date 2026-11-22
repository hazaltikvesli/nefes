package com.nefes.app;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.View;
import android.widget.RemoteViews;
import org.json.JSONArray;
import org.json.JSONObject;

public class NefesWidgetProvider extends AppWidgetProvider {

    private static final int INK = 0xFFE4EDEF;
    private static final int TEAL = 0xFF62B6BE;
    private static final int AMBER = 0xFFE6AE52;
    private static final int CORAL = 0xFFE7766C;

    @Override
    public void onUpdate(Context context, AppWidgetManager manager, int[] ids) {
        for (int id : ids) {
            manager.updateAppWidget(id, build(context));
        }
    }

    public static void updateAll(Context context) {
        AppWidgetManager manager = AppWidgetManager.getInstance(context);
        int[] ids = manager.getAppWidgetIds(new ComponentName(context, NefesWidgetProvider.class));
        for (int id : ids) {
            manager.updateAppWidget(id, build(context));
        }
    }

    static RemoteViews build(Context c) {
        JSONObject s = WidgetStore.summary(c);
        JSONArray pending = WidgetStore.pending(c);

        int count = s.optInt("count", 0) + pending.length();
        int limit = s.optInt("limit", 0);
        long last = s.optLong("lastTs", 0);
        for (int i = 0; i < pending.length(); i++) {
            JSONObject p = pending.optJSONObject(i);
            if (p != null) last = Math.max(last, p.optLong("ts", 0));
        }

        RemoteViews v = new RemoteViews(c.getPackageName(), R.layout.nefes_widget);
        v.setTextViewText(R.id.w_count, String.valueOf(count));
        v.setTextViewText(R.id.w_limit, limit > 0 ? "/ " + limit : "");

        String status;
        int color;
        if (limit <= 0) {
            status = c.getString(R.string.w_setup);
            color = TEAL;
        } else if (count < limit) {
            status = c.getString(R.string.w_left, limit - count);
            color = TEAL;
        } else if (count == limit) {
            status = c.getString(R.string.w_at_limit);
            color = AMBER;
        } else {
            status = c.getString(R.string.w_over, count - limit);
            color = CORAL;
        }
        v.setTextViewText(R.id.w_status, status);
        v.setTextColor(R.id.w_status, color);
        v.setTextColor(R.id.w_count, limit > 0 && count > limit ? CORAL : INK);

        if (last > 0) {
            long base = SystemClock.elapsedRealtime() - (System.currentTimeMillis() - last);
            v.setChronometer(R.id.w_since, base, c.getString(R.string.w_since_fmt), true);
            v.setViewVisibility(R.id.w_since, View.VISIBLE);
            v.setViewVisibility(R.id.w_none, View.GONE);
        } else {
            v.setChronometer(R.id.w_since, SystemClock.elapsedRealtime(), null, false);
            v.setViewVisibility(R.id.w_since, View.GONE);
            v.setViewVisibility(R.id.w_none, View.VISIBLE);
        }

        int flags = PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE;

        Intent add = new Intent(c, TagPickerActivity.class);
        add.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        v.setOnClickPendingIntent(R.id.w_add, PendingIntent.getActivity(c, 1, add, flags));

        Intent open = new Intent(c, MainActivity.class);
        open.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        v.setOnClickPendingIntent(R.id.w_root, PendingIntent.getActivity(c, 2, open, flags));

        return v;
    }
}

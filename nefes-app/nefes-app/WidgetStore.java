package com.nefes.app;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Shared storage between the app and the home screen widget.
 * "summary" is written by the app (today's count, limit, last cigarette, tags, price).
 * "pending" holds cigarettes added from the widget until the app imports them.
 */
public final class WidgetStore {
    private static final String PREFS = "nefes_widget";

    private WidgetStore() {}

    private static SharedPreferences prefs(Context c) {
        return c.getApplicationContext().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static synchronized void setSummary(Context c, String json) {
        prefs(c).edit().putString("summary", json).commit();
    }

    public static synchronized JSONObject summary(Context c) {
        try {
            return new JSONObject(prefs(c).getString("summary", "{}"));
        } catch (JSONException e) {
            return new JSONObject();
        }
    }

    public static synchronized JSONArray pending(Context c) {
        try {
            return new JSONArray(prefs(c).getString("pending", "[]"));
        } catch (JSONException e) {
            return new JSONArray();
        }
    }

    public static synchronized void addPending(Context c, JSONObject entry) {
        JSONArray list = pending(c);
        list.put(entry);
        prefs(c).edit().putString("pending", list.toString()).commit();
    }

    /** Returns all pending entries and clears them in one step. */
    public static synchronized String takePending(Context c) {
        String s = prefs(c).getString("pending", "[]");
        prefs(c).edit().putString("pending", "[]").commit();
        return s;
    }
}

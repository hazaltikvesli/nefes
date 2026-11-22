package com.nefes.app;

import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONArray;
import org.json.JSONObject;

/** Small dialog opened from the widget: pick triggers and save the cigarette. */
public class TagPickerActivity extends AppCompatActivity {

    private long ts;
    private String[] tags;
    private boolean[] checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ts = System.currentTimeMillis();

        JSONObject s = WidgetStore.summary(this);
        JSONArray list = s.optJSONArray("tags");
        if (list != null && list.length() > 0) {
            tags = new String[list.length()];
            for (int i = 0; i < list.length(); i++) tags[i] = list.optString(i);
        } else {
            tags = getResources().getStringArray(R.array.w_default_tags);
        }
        checked = new boolean[tags.length];

        String time = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date(ts));

        AlertDialog dialog = new AlertDialog.Builder(this)
            .setTitle(getString(R.string.w_dialog_title, time))
            .setMultiChoiceItems(tags, checked, (d, which, isChecked) -> checked[which] = isChecked)
            .setPositiveButton(R.string.w_save, (d, w) -> save(true))
            .setNeutralButton(R.string.w_save_notag, (d, w) -> save(false))
            .setNegativeButton(R.string.w_cancel, null)
            .create();
        dialog.setOnDismissListener(d -> finish());
        dialog.show();
    }

    private void save(boolean withTags) {
        try {
            JSONObject s = WidgetStore.summary(this);
            JSONArray chosen = new JSONArray();
            if (withTags) {
                for (int i = 0; i < tags.length; i++) if (checked[i]) chosen.put(tags[i]);
            }
            JSONObject entry = new JSONObject();
            entry.put("id", "w" + Long.toString(ts, 36) + Integer.toString((int) (Math.random() * 46656), 36));
            entry.put("ts", ts);
            entry.put("brand", s.optString("brand", ""));
            entry.put("unitPrice", s.optDouble("unitPrice", 0));
            entry.put("tags", chosen);
            WidgetStore.addPending(this, entry);
            NefesWidgetProvider.updateAll(this);
            Toast.makeText(getApplicationContext(), R.string.w_saved, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(), R.string.w_failed, Toast.LENGTH_SHORT).show();
        }
    }
}

package pl.jaguarhud.mapsprobe;

import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/** Local-only ring buffer: max 200 Google Maps notification events. */
final class Events {
    private static final String PREFS = "events_private";
    private static final String KEY = "history";
    private static final int LIMIT = 200;

    static synchronized JSONArray history(Context context) {
        String data = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .getString(KEY, "[]");
        try { return new JSONArray(data); }
        catch (JSONException ex) { return new JSONArray(); }
    }

    static synchronized void add(Context context, JSONObject event) {
        JSONArray old = history(context);
        JSONArray next = new JSONArray();
        next.put(event);
        for (int i = 0; i < old.length() && i < LIMIT - 1; i++) {
            JSONObject item = old.optJSONObject(i);
            if (item != null) next.put(item);
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().putString(KEY, next.toString()).apply();
    }

    static synchronized void clear(Context context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
                .edit().remove(KEY).commit();
    }

    static JSONObject snapshot(Context context) {
        return history(context).optJSONObject(0);
    }

    static String safe(JSONObject object, String key) {
        return object == null ? "—" : object.optString(key, "—");
    }

    private Events() { }
}

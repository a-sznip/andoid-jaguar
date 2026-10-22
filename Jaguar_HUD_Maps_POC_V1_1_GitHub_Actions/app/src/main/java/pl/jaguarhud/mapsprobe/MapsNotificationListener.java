package pl.jaguarhud.mapsprobe;

import android.app.Notification;
import android.os.Bundle;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/** Diagnostic ONLY: reads Google Maps' phone notifications. No CAN / HUD commands. */
public class MapsNotificationListener extends NotificationListenerService {
    private static final String GOOGLE_MAPS = "com.google.android.apps.maps";
    private static final Pattern DISTANCE = Pattern.compile(
            "(?<![0-9])([0-9]+(?:[.,][0-9]+)?)\\s*(km|m|mi|ft)\\b",
            Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE);

    @Override public void onListenerConnected() {
        super.onListenerConnected();
        try {
            StatusBarNotification[] current = getActiveNotifications();
            if (current != null)
                for (StatusBarNotification sbn : current) save(sbn, "ACTIVE_AT_CONNECT");
        } catch (SecurityException | IllegalStateException ignored) {
            // Permission not fully active yet; future callbacks may still arrive.
        }
    }

    @Override public void onNotificationPosted(StatusBarNotification sbn) {
        save(sbn, "POSTED_OR_UPDATED");
    }

    @Override public void onNotificationRemoved(StatusBarNotification sbn) {
        if (sbn == null || !GOOGLE_MAPS.equals(sbn.getPackageName())) return;
        JSONObject event = new JSONObject();
        try {
            event.put("event", "REMOVED");
            event.put("timeMs", System.currentTimeMillis());
            event.put("notificationKey", sbn.getKey());
            event.put("package", GOOGLE_MAPS);
            Events.add(this, event);
        } catch (JSONException ignored) { }
    }

    private void save(StatusBarNotification sbn, String state) {
        if (sbn == null || !GOOGLE_MAPS.equals(sbn.getPackageName())) return;
        Notification n = sbn.getNotification();
        if (n == null) return;
        Bundle e = n.extras;
        if (e == null) e = new Bundle();
        String title = get(e, Notification.EXTRA_TITLE);
        String text = get(e, Notification.EXTRA_TEXT);
        String bigText = get(e, Notification.EXTRA_BIG_TEXT);
        String subText = get(e, Notification.EXTRA_SUB_TEXT);
        String info = get(e, Notification.EXTRA_INFO_TEXT);
        String lines = lines(e);
        String ticker = n.tickerText == null ? "" : n.tickerText.toString();
        String combined = title + " " + text + " " + bigText + " " + subText + " " + lines + " " + ticker;
        JSONObject event = new JSONObject();
        try {
            event.put("event", state);
            event.put("timeMs", System.currentTimeMillis());
            event.put("notificationPostMs", sbn.getPostTime());
            event.put("package", GOOGLE_MAPS);
            event.put("notificationKey", sbn.getKey());
            event.put("notificationId", sbn.getId());
            event.put("channelId", n.getChannelId());
            event.put("category", n.category == null ? "" : n.category);
            event.put("title", title);
            event.put("text", text);
            event.put("bigText", bigText);
            event.put("subText", subText);
            event.put("info", info);
            event.put("lines", lines);
            event.put("ticker", ticker);
            event.put("guessManeuver", guessManeuver(combined));
            event.put("guessDistance", guessDistance(combined));
            Events.add(this, event);
        } catch (JSONException ignored) { }
    }

    private static String get(Bundle extras, String key) {
        CharSequence value = extras.getCharSequence(key);
        return value == null ? "" : value.toString();
    }

    private static String lines(Bundle extras) {
        CharSequence[] values = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES);
        if (values == null) return "";
        StringBuilder out = new StringBuilder();
        for (CharSequence v : values) {
            if (v == null) continue;
            if (out.length() > 0) out.append(" | ");
            out.append(v);
        }
        return out.toString();
    }

    /** Illustrative heuristic; MUST NOT be used as a driving instruction. */
    private static String guessManeuver(String s) {
        String x = s.toLowerCase(Locale.ROOT);
        if (x.contains("zawróć") || x.contains("zawroc") || x.contains("u-turn")) return "ZAWRACANIE?";
        if (x.contains("rondo") || x.contains("roundabout")) return "RONDO?";
        if (x.contains("w lewo") || x.contains("turn left") || x.contains("bear left")) return "LEWO?";
        if (x.contains("w prawo") || x.contains("turn right") || x.contains("bear right")) return "PRAWO?";
        if (x.contains("prosto") || x.contains("straight")) return "PROSTO?";
        return "NIEUSTALONY";
    }

    private static String guessDistance(String s) {
        Matcher match = DISTANCE.matcher(s);
        return match.find() ? match.group(1) + " " + match.group(2) + "?" : "NIEUSTALONA";
    }
}

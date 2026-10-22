package pl.jaguarhud.mapsprobe;

import android.app.Activity;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.util.Date;

public class MainActivity extends Activity {
    private static final int REQUEST_EXPORT = 120;
    private TextView statusView;
    private TextView dataView;
    private final Handler handler = new Handler(Looper.getMainLooper());
    private boolean resumed = false;
    private final Runnable poll = new Runnable() {
        @Override public void run() {
            if (!resumed) return;
            refresh();
            handler.postDelayed(this, 1200);
        }
    };

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView outer = new ScrollView(this);
        LinearLayout panel = new LinearLayout(this);
        panel.setPadding(dp(20), dp(20), dp(20), dp(20));
        panel.setOrientation(LinearLayout.VERTICAL);
        outer.addView(panel);
        TextView title = new TextView(this);
        title.setText("Jaguar HUD — diagnostyka Google Maps");
        title.setTextSize(22);
        title.setPadding(0, 0, 0, dp(10));
        panel.addView(title);
        TextView notice = new TextView(this);
        notice.setText("Wersja 1.0 — tylko diagnostyka. Nie łączy się z Jaguarem ani nie steruje HUD. " +
                "Zapisuje lokalnie wyłącznie powiadomienia aplikacji Google Maps. " +
                "Interpretacje z pytajnikiem są orientacyjne i NIE są wskazówkami do jazdy.");
        notice.setPadding(0, 0, 0, dp(14));
        panel.addView(notice);
        Button access = button("1. Zezwól na dostęp do powiadomień", panel);
        access.setOnClickListener(v -> startActivity(
                new Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)));
        Button export = button("2. Eksportuj dziennik JSON", panel);
        export.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("application/json");
            i.putExtra(Intent.EXTRA_TITLE, "jaguar_google_maps_powiadomienia.json");
            startActivityForResult(i, REQUEST_EXPORT);
        });
        Button clear = button("Usuń lokalny dziennik", panel);
        clear.setOnClickListener(v -> {
            Events.clear(this);
            refresh();
            Toast.makeText(this, "Dziennik usunięty", Toast.LENGTH_SHORT).show();
        });
        statusView = new TextView(this);
        statusView.setTextSize(16);
        statusView.setPadding(0, dp(16), 0, dp(15));
        panel.addView(statusView);
        dataView = new TextView(this);
        dataView.setTextSize(15);
        dataView.setTextIsSelectable(true);
        panel.addView(dataView);
        setContentView(outer);
    }

    private Button button(String label, LinearLayout panel) {
        Button b = new Button(this);
        b.setText(label);
        b.setAllCaps(false);
        panel.addView(b);
        return b;
    }

    private int dp(int v) { return (int) (v * getResources().getDisplayMetrics().density); }

    @Override protected void onResume() {
        super.onResume();
        resumed = true;
        handler.removeCallbacks(poll);
        handler.post(poll);
    }

    @Override protected void onPause() {
        resumed = false;
        handler.removeCallbacks(poll);
        super.onPause();
    }

    private void refresh() {
        NotificationManager nm = getSystemService(NotificationManager.class);
        boolean enabled = nm != null && nm.isNotificationListenerAccessGranted(
                new ComponentName(this, MapsNotificationListener.class));
        JSONArray history = Events.history(this);
        statusView.setText("Dostęp do powiadomień: " + (enabled ? "WŁĄCZONY" : "WYŁĄCZONY") +
                "\nLiczba zdarzeń Google Maps (maks. 200): " + history.length());
        JSONObject latest = history.optJSONObject(0);
        if (latest == null) {
            dataView.setText("Brak zdarzeń. Po włączeniu dostępu uruchom trasę w Google Maps. " +
                    "Następnie porównaj działanie BEZ i Z Android Auto. " +
                    "Jeśli przy podłączonym Android Auto brak zdarzeń, nie oznacza to usterki telefonu — " +
                    "ta metoda może być w danej konfiguracji niewystarczająca.");
            return;
        }
        long ms = latest.optLong("timeMs", 0);
        String when = ms > 0 ? DateFormat.getDateTimeInstance().format(new Date(ms)) : "—";
        StringBuilder out = new StringBuilder();
        out.append("OSTATNIE ZDARZENIE\n")
                .append("Czas: ").append(when).append("\n")
                .append("Rodzaj: ").append(Events.safe(latest, "event")).append("\n")
                .append("Kanał: ").append(Events.safe(latest, "channelId")).append("\n\n")
                .append("TYTUŁ\n").append(Events.safe(latest, "title")).append("\n\n")
                .append("TEKST\n").append(Events.safe(latest, "text")).append("\n\n")
                .append("DODATKOWY TEKST\n").append(Events.safe(latest, "bigText")).append("\n\n")
                .append("LINIE\n").append(Events.safe(latest, "lines")).append("\n\n")
                .append("TICKER\n").append(Events.safe(latest, "ticker")).append("\n\n")
                .append("SZACOWANY MANEWR\n").append(Events.safe(latest, "guessManeuver"))
                .append("\n\nSZACOWANA ODLEGŁOŚĆ\n")
                .append(Events.safe(latest, "guessDistance"))
                .append("\n\nTo jest podgląd diagnostyczny, nie nawigacja.");
        dataView.setText(out.toString());
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != REQUEST_EXPORT || resultCode != RESULT_OK || data == null || data.getData() == null)
            return;
        try (OutputStream out = getContentResolver().openOutputStream(data.getData())) {
            if (out == null) throw new IllegalStateException("Brak dostępu do wybranego pliku");
            JSONArray history = Events.history(this);
            out.write(history.toString(2).getBytes(StandardCharsets.UTF_8));
            Toast.makeText(this, "Dziennik zapisany: " + history.length() + " zdarzeń", Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Toast.makeText(this, "Eksport nieudany: " + ex.getMessage(), Toast.LENGTH_LONG).show();
        }
    }
}

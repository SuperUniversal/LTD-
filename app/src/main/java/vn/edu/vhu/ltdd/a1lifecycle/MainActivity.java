package vn.edu.vhu.ltdd.a1lifecycle;

import android.content.Intent;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A1_231A290021";

    private static final String[] CALLBACKS = {
            "onCreate",
            "onStart",
            "onResume",
            "onPause",
            "onStop",
            "onRestart",
            "onDestroy",
            "onSaveInstanceState"
    };

    private TextView tvLog;
    private TextView tvCounts;
    private ScrollView scrollLog;
    private final SpannableStringBuilder history = new SpannableStringBuilder();
    private final LinkedHashMap<String, Integer> counts = new LinkedHashMap<>();
    private int step = 0;

    /** Ghi một sự kiện ra Logcat, tô màu và hiển thị lên màn hình. */
    private void logEvent(String event) {
        step++;
        String time = new SimpleDateFormat("HH:mm:ss.SSS", Locale.getDefault())
                .format(new Date());
        String line = step + ". [" + time + "] " + event;
        Log.d(TAG, line);

        int start = history.length();
        history.append(line).append('\n');
        history.setSpan(
                new ForegroundColorSpan(colorFor(callbackName(event))),
                start,
                history.length(),
                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        if (tvLog != null) {
            tvLog.setText(history);
        }
        if (scrollLog != null) {
            scrollLog.post(() -> scrollLog.fullScroll(ScrollView.FOCUS_DOWN));
        }
        incrementCount(callbackName(event));
    }

    private String callbackName(String event) {
        int cut = event.indexOf(' ');
        return cut > 0 ? event.substring(0, cut) : event;
    }

    private int colorFor(String callback) {
        int colorId;
        switch (callback) {
            case "onPause":
            case "onStop":
                colorId = R.color.log_pause;
                break;
            case "onDestroy":
                colorId = R.color.log_destroy;
                break;
            case "onRestart":
                colorId = R.color.log_restart;
                break;
            case "onSaveInstanceState":
                colorId = R.color.log_save;
                break;
            default:
                colorId = R.color.log_active;
                break;
        }
        return ContextCompat.getColor(this, colorId);
    }

    private void resetCounts() {
        counts.clear();
        for (String callback : CALLBACKS) {
            counts.put(callback, 0);
        }
        updateCounts();
    }

    private void incrementCount(String callback) {
        Integer current = counts.get(callback);
        counts.put(callback, current == null ? 1 : current + 1);
        updateCounts();
    }

    private void updateCounts() {
        if (tvCounts == null) {
            return;
        }
        StringBuilder table = new StringBuilder();
        int column = 0;
        for (Map.Entry<String, Integer> entry : counts.entrySet()) {
            if (column == 2) {
                table.append('\n');
                column = 0;
            } else if (column > 0) {
                table.append("    ");
            }
            table.append(entry.getKey()).append(": ").append(entry.getValue());
            column++;
        }
        tvCounts.setText(table.toString());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvLog = findViewById(R.id.tvLog);
        tvCounts = findViewById(R.id.tvCounts);
        scrollLog = findViewById(R.id.scrollLog);
        Button btnClear = findViewById(R.id.btnClear);
        Button btnCrash = findViewById(R.id.btnCrash);
        Button btnFinish = findViewById(R.id.btnFinish);
        Button btnOpenSecond = findViewById(R.id.btnOpenSecond);

        resetCounts();

        btnClear.setOnClickListener(v -> {
            history.clear();
            step = 0;
            tvLog.setText("");
            resetCounts();
            Log.i(TAG, "---- Đã xóa lịch sử ----");
        });

        // Nút cố ý gây NullPointerException rồi bắt lại để luyện đọc Logcat (NC2).
        btnCrash.setOnClickListener(v -> {
            try {
                String ten = null;
                Log.d(TAG, "Độ dài tên: " + ten.length());
            } catch (NullPointerException e) {
                Log.e(TAG, "Bắt được lỗi NullPointerException", e);
                Toast.makeText(this, R.string.npe_caught, Toast.LENGTH_SHORT).show();
            }
        });

        // finish() để quan sát onDestroy (Android 12+: Back không hủy Activity gốc)
        btnFinish.setOnClickListener(v -> finish());

        btnOpenSecond.setOnClickListener(v ->
                startActivity(new Intent(this, SecondActivity.class)));

        String state = (savedInstanceState == null) ? "= null" : "!= null";
        logEvent("onCreate (savedInstanceState " + state + ")");
    }

    @Override
    protected void onStart() {
        super.onStart();
        logEvent("onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        logEvent("onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        logEvent("onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        logEvent("onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        logEvent("onRestart");
    }

    @Override
    protected void onDestroy() {
        logEvent("onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        logEvent("onSaveInstanceState");
    }
}

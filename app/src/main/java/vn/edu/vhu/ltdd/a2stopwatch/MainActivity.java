package vn.edu.vhu.ltdd.a2stopwatch;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.os.VibratorManager;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A2_231A290021";

    private static final String KEY_RUNNING = "running";
    private static final String KEY_ACCUMULATED = "accumulated";
    private static final String KEY_START = "start";
    private static final String KEY_RECREATE = "recreate";
    private static final String KEY_LAPS = "laps";
    private static final String KEY_PAUSE_ON_STOP = "pauseOnStop";

    private TextView tvTime;
    private TextView tvStatus;
    private TextView tvRecreate;
    private TextView tvLaps;
    private ScrollView scrollLaps;
    private Button btnStartPause;
    private CheckBox cbPauseOnStop;

    private boolean running = false;
    private long accumulated = 0L;
    private long startTime = 0L;
    private int recreateCount = 0;
    private final ArrayList<String> laps = new ArrayList<>();
    private int normalTimeColor;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable ticker = new Runnable() {
        @Override
        public void run() {
            updateTimeText();
            handler.postDelayed(this, 100);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        tvTime = findViewById(R.id.tvTime);
        tvStatus = findViewById(R.id.tvStatus);
        tvRecreate = findViewById(R.id.tvRecreate);
        tvLaps = findViewById(R.id.tvLaps);
        scrollLaps = findViewById(R.id.scrollLaps);
        btnStartPause = findViewById(R.id.btnStartPause);
        Button btnReset = findViewById(R.id.btnReset);
        Button btnLap = findViewById(R.id.btnLap);
        cbPauseOnStop = findViewById(R.id.cbPauseOnStop);
        normalTimeColor = tvTime.getCurrentTextColor();

        if (savedInstanceState != null) {
            running = savedInstanceState.getBoolean(KEY_RUNNING);
            accumulated = savedInstanceState.getLong(KEY_ACCUMULATED);
            startTime = savedInstanceState.getLong(KEY_START);
            recreateCount = savedInstanceState.getInt(KEY_RECREATE) + 1;
            ArrayList<String> savedLaps = savedInstanceState.getStringArrayList(KEY_LAPS);
            if (savedLaps != null) {
                laps.addAll(savedLaps);
            }
            cbPauseOnStop.setChecked(savedInstanceState.getBoolean(KEY_PAUSE_ON_STOP));
            Log.d(TAG, "onCreate: KHÔI PHỤC trạng thái, running=" + running
                    + ", accumulated=" + accumulated + "ms");
        } else {
            Log.d(TAG, "onCreate: khởi tạo mới (savedInstanceState = null)");
        }

        btnStartPause.setOnClickListener(v -> {
            if (running) {
                pauseStopwatch();
            } else {
                startStopwatch();
            }
        });
        btnReset.setOnClickListener(v -> resetStopwatch());
        btnLap.setOnClickListener(v -> addLap());

        updateUi();
    }

    private long elapsed() {
        return running ? accumulated + (SystemClock.elapsedRealtime() - startTime) : accumulated;
    }

    private void startStopwatch() {
        running = true;
        startTime = SystemClock.elapsedRealtime();
        startTicking();
        updateUi();
        Log.i(TAG, "BẮT ĐẦU đếm giờ");
    }

    private void pauseStopwatch() {
        if (!running) {
            return;
        }
        accumulated += SystemClock.elapsedRealtime() - startTime;
        running = false;
        stopTicking();
        updateUi();
        Log.i(TAG, "TẠM DỪNG tại " + accumulated + "ms");
    }

    private void resetStopwatch() {
        running = false;
        accumulated = 0L;
        startTime = 0L;
        laps.clear();
        stopTicking();
        updateUi();
        vibrateShort();
        Log.i(TAG, "ĐẶT LẠI về 00:00.0");
    }

    private void addLap() {
        laps.add(formatTime(elapsed()));
        updateLaps();
        Log.i(TAG, "VÒNG " + laps.size() + " tại " + laps.get(laps.size() - 1));
    }

    private void startTicking() {
        handler.removeCallbacks(ticker);
        handler.post(ticker);
    }

    private void stopTicking() {
        handler.removeCallbacks(ticker);
    }

    private String formatTime(long ms) {
        long phut = ms / 60000;
        long giay = (ms % 60000) / 1000;
        long phanMuoi = (ms % 1000) / 100;
        return String.format(Locale.getDefault(), "%02d:%02d.%d", phut, giay, phanMuoi);
    }

    private void updateTimeText() {
        long ms = elapsed();
        tvTime.setText(formatTime(ms));
        if (ms >= 60000) {
            tvTime.setTextColor(ContextCompat.getColor(this, R.color.time_over));
        } else {
            tvTime.setTextColor(normalTimeColor);
        }
    }

    private void updateLaps() {
        StringBuilder lines = new StringBuilder();
        for (int i = 0; i < laps.size(); i++) {
            if (i > 0) {
                lines.append('\n');
            }
            lines.append(getString(R.string.lap_line, i + 1, laps.get(i)));
        }
        tvLaps.setText(lines.toString());
        scrollLaps.post(() -> scrollLaps.fullScroll(ScrollView.FOCUS_DOWN));
    }

    private void updateUi() {
        updateTimeText();
        btnStartPause.setText(running ? R.string.pause : R.string.start);
        tvStatus.setText(running ? R.string.status_running : R.string.status_paused);
        tvRecreate.setText(getString(R.string.recreate_count, recreateCount));
        updateLaps();
    }

    private void vibrateShort() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            VibratorManager manager = (VibratorManager) getSystemService(VIBRATOR_MANAGER_SERVICE);
            if (manager != null) {
                manager.getDefaultVibrator().vibrate(
                        VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE));
            }
            return;
        }
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator == null) {
            return;
        }
        vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE));
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume – bật lại việc cập nhật giao diện nếu đồng hồ đang chạy");
        if (running) {
            startTicking();
        }
        updateUi();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTicking();
        Log.d(TAG, "onPause – tạm dừng cập nhật giao diện");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (cbPauseOnStop.isChecked() && !isChangingConfigurations()) {
            pauseStopwatch();
        }
        Log.d(TAG, "onStop");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
    }

    @Override
    protected void onDestroy() {
        stopTicking();
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_RUNNING, running);
        outState.putLong(KEY_ACCUMULATED, accumulated);
        outState.putLong(KEY_START, startTime);
        outState.putInt(KEY_RECREATE, recreateCount);
        outState.putStringArrayList(KEY_LAPS, laps);
        outState.putBoolean(KEY_PAUSE_ON_STOP, cbPauseOnStop.isChecked());
        Log.d(TAG, "onSaveInstanceState – đã lưu " + elapsed() + "ms vào Bundle");
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(TAG, "onRestoreInstanceState – được gọi sau onStart()");
    }
}

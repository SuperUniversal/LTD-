package vn.edu.vhu.ltdd.a7permission;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A7_231A290021";

    private static final String CHANNEL_ID = "a7_channel";
    private static final int NOTI_ID = 1001;

    private ImageView imgAnh;
    private TextView tvTrangThai;

    private final ActivityResultLauncher<String> xinQuyenCamera = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            duocCap -> {
                Log.d(TAG, "Kết quả xin quyền CAMERA: " + duocCap);
                if (duocCap) {
                    moCamera();
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    hienGiaiThich();
                } else {
                    hienMoCaiDat();
                }
            });

    private final ActivityResultLauncher<String> xinQuyenThongBao = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            duocCap -> {
                Log.d(TAG, "Kết quả xin quyền THÔNG BÁO: " + duocCap);
                if (duocCap) {
                    guiThongBao();
                } else {
                    Toast.makeText(this, R.string.noti_denied, Toast.LENGTH_SHORT).show();
                }
                capNhatTrangThai();
            });

    /** NC1: xin CAMERA và POST_NOTIFICATIONS trong một hộp thoại, xử lý từng quyền. */
    private final ActivityResultLauncher<String[]> xinNhieuQuyen = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            this::xuLyNhieuQuyen);

    private final ActivityResultLauncher<Void> chupAnh = registerForActivityResult(
            new ActivityResultContracts.TakePicturePreview(),
            (Bitmap bitmap) -> {
                if (bitmap != null) {
                    imgAnh.setImageBitmap(bitmap);
                    tvTrangThai.setText(getString(R.string.status_photo,
                            bitmap.getWidth(), bitmap.getHeight()));
                } else {
                    Toast.makeText(this, R.string.photo_cancelled, Toast.LENGTH_SHORT).show();
                }
            });

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

        imgAnh = findViewById(R.id.imgAnh);
        tvTrangThai = findViewById(R.id.tvTrangThai);
        Button btnChupAnh = findViewById(R.id.btnChupAnh);
        Button btnThongBao = findViewById(R.id.btnThongBao);
        Button btnCaHai = findViewById(R.id.btnCaHai);
        Button btnCaiDat = findViewById(R.id.btnCaiDat);

        taoKenhThongBao();

        btnChupAnh.setOnClickListener(v -> kiemTraRoiChup());
        btnThongBao.setOnClickListener(v -> kiemTraRoiGuiThongBao());
        btnCaHai.setOnClickListener(v -> xinCaHai());
        btnCaiDat.setOnClickListener(v -> moCaiDatUngDung());
    }

    @Override
    protected void onResume() {
        super.onResume();
        capNhatTrangThai();
    }

    private void kiemTraRoiChup() {
        int trangThai = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);

        if (trangThai == PackageManager.PERMISSION_GRANTED) {
            moCamera();
        } else if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            hienGiaiThich();
        } else {
            xinQuyenCamera.launch(Manifest.permission.CAMERA);
        }
    }

    private void hienGiaiThich() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.rationale_title)
                .setMessage(R.string.rationale_camera)
                .setPositiveButton(R.string.agree,
                        (d, w) -> xinQuyenCamera.launch(Manifest.permission.CAMERA))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void hienMoCaiDat() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.blocked_title)
                .setMessage(R.string.blocked_camera)
                .setPositiveButton(R.string.open_settings, (d, w) -> moCaiDatUngDung())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void moCamera() {
        chupAnh.launch(null);
    }

    private void moCaiDatUngDung() {
        Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.setData(Uri.fromParts("package", getPackageName(), null));
        startActivity(i);
    }

    private void kiemTraRoiGuiThongBao() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            guiThongBao();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            guiThongBao();
        } else {
            xinQuyenThongBao.launch(Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    private void xinCaHai() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            xinNhieuQuyen.launch(new String[]{
                    Manifest.permission.CAMERA,
                    Manifest.permission.POST_NOTIFICATIONS
            });
        } else {
            xinNhieuQuyen.launch(new String[]{Manifest.permission.CAMERA});
        }
    }

    private void xuLyNhieuQuyen(Map<String, Boolean> ketQua) {
        boolean camera = Boolean.TRUE.equals(ketQua.get(Manifest.permission.CAMERA));
        boolean thongBao = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || Boolean.TRUE.equals(ketQua.get(Manifest.permission.POST_NOTIFICATIONS));
        Log.d(TAG, "Xin nhiều quyền: CAMERA=" + camera + " THONG_BAO=" + thongBao);
        capNhatTrangThai();
        Toast.makeText(this, getString(R.string.ket_qua_nhieu,
                camera ? getString(R.string.granted) : getString(R.string.denied),
                thongBao ? getString(R.string.granted) : getString(R.string.denied)),
                Toast.LENGTH_LONG).show();
    }

    private void taoKenhThongBao() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel kenh = new NotificationChannel(
                    CHANNEL_ID, getString(R.string.channel_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            kenh.setDescription(getString(R.string.channel_desc));
            getSystemService(NotificationManager.class).createNotificationChannel(kenh);
        }
    }

    private void guiThongBao() {
        Intent moApp = new Intent(this, MainActivity.class);
        PendingIntent moLai = PendingIntent.getActivity(
                this,
                0,
                moApp,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder b = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle(getString(R.string.noti_title))
                .setContentText(getString(R.string.noti_text))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(moLai)
                .setAutoCancel(true);
        try {
            NotificationManagerCompat.from(this).notify(NOTI_ID, b.build());
        } catch (SecurityException e) {
            Log.w(TAG, "Thiếu quyền gửi thông báo", e);
        }
    }

    private void capNhatTrangThai() {
        boolean camera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
        boolean thongBao = Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU
                || ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED;
        String s = getString(R.string.status_format,
                camera ? getString(R.string.granted) : getString(R.string.denied),
                thongBao ? getString(R.string.granted) : getString(R.string.denied));
        tvTrangThai.setText(s);
        Log.d(TAG, s.replace("\n", " | "));
    }
}

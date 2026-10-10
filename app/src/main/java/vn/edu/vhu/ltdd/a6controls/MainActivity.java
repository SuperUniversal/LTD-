package vn.edu.vhu.ltdd.a6controls;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.materialswitch.MaterialSwitch;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A6_231A290021";

    public static final String EXTRA_TOM_TAT = "extra_tom_tat";

    private EditText edtHoTen, edtMssv;
    private Spinner spKhoa, spMonHoc;
    private RadioGroup rgHeDaoTao;
    private CheckBox cbSang, cbChieu, cbToi;
    private TextView tvDemBuoi;
    private MaterialSwitch swThongBao;
    private ToggleButton tgUuTien;

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

        edtHoTen = findViewById(R.id.edtHoTen);
        edtMssv = findViewById(R.id.edtMssv);
        spKhoa = findViewById(R.id.spKhoa);
        spMonHoc = findViewById(R.id.spMonHoc);
        rgHeDaoTao = findViewById(R.id.rgHeDaoTao);
        cbSang = findViewById(R.id.cbSang);
        cbChieu = findViewById(R.id.cbChieu);
        cbToi = findViewById(R.id.cbToi);
        tvDemBuoi = findViewById(R.id.tvDemBuoi);
        swThongBao = findViewById(R.id.swThongBao);
        tgUuTien = findViewById(R.id.tgUuTien);
        Button btnXacNhan = findViewById(R.id.btnXacNhan);
        Button btnLamLai = findViewById(R.id.btnLamLai);

        ArrayAdapter<CharSequence> khoaAdapter = ArrayAdapter.createFromResource(
                this, R.array.khoa, android.R.layout.simple_spinner_item);
        khoaAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spKhoa.setAdapter(khoaAdapter);
        spKhoa.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Đã chọn khoa: " + parent.getItemAtPosition(position));
                ganDanhSachMon(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spMonHoc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "Đã chọn học phần: " + parent.getItemAtPosition(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        CompoundButton.OnCheckedChangeListener demBuoi = (buttonView, isChecked) -> {
            if (buttonView.getId() == R.id.cbToi) {
                Log.d(TAG, "Buổi Tối: " + (isChecked ? "chọn" : "bỏ chọn"));
            }
            capNhatDemBuoi();
        };
        cbSang.setOnCheckedChangeListener(demBuoi);
        cbChieu.setOnCheckedChangeListener(demBuoi);
        cbToi.setOnCheckedChangeListener(demBuoi);
        capNhatDemBuoi();

        rgHeDaoTao.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == -1) {
                return;
            }
            String he = (checkedId == R.id.rbChinhQuy) ? getString(R.string.he_chinh_quy)
                    : getString(R.string.he_vlvh);
            Log.d(TAG, "Hệ đào tạo: " + he);
        });

        swThongBao.setOnCheckedChangeListener((buttonView, isChecked) ->
                Log.d(TAG, "Nhận thông báo: " + isChecked));

        btnXacNhan.setOnClickListener(v -> xacNhan());
        btnLamLai.setOnClickListener(v -> lamLai());
    }

    /** NC1: danh sách học phần đổi theo khoa đang chọn. */
    private void ganDanhSachMon(int khoaIndex) {
        int arrayId = (khoaIndex == 0) ? R.array.mon_hoc : R.array.mon_kinh_te;
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, arrayId, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spMonHoc.setAdapter(adapter);
    }

    /** NC2: đếm số buổi đang được tick. */
    private void capNhatDemBuoi() {
        int soBuoi = 0;
        if (cbSang.isChecked()) soBuoi++;
        if (cbChieu.isChecked()) soBuoi++;
        if (cbToi.isChecked()) soBuoi++;
        tvDemBuoi.setText(getString(R.string.dem_buoi_format, soBuoi));
    }

    /** Kiểm tra dữ liệu rồi chuyển sang màn hình xác nhận. */
    private void xacNhan() {
        String hoTen = edtHoTen.getText().toString().trim();
        String mssv = edtMssv.getText().toString().trim();

        if (hoTen.isEmpty()) {
            edtHoTen.setError(getString(R.string.err_empty));
            edtHoTen.requestFocus();
            return;
        }
        if (mssv.length() != 10) {
            edtMssv.setError(getString(R.string.err_mssv));
            edtMssv.requestFocus();
            return;
        }
        if (rgHeDaoTao.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, R.string.err_he, Toast.LENGTH_SHORT).show();
            return;
        }

        List<String> buoiHoc = new ArrayList<>();
        if (cbSang.isChecked()) buoiHoc.add(getString(R.string.buoi_sang));
        if (cbChieu.isChecked()) buoiHoc.add(getString(R.string.buoi_chieu));
        if (cbToi.isChecked()) buoiHoc.add(getString(R.string.buoi_toi));
        if (buoiHoc.isEmpty()) {
            Toast.makeText(this, R.string.err_buoi, Toast.LENGTH_SHORT).show();
            return;
        }

        String he = (rgHeDaoTao.getCheckedRadioButtonId() == R.id.rbChinhQuy)
                ? getString(R.string.he_chinh_quy) : getString(R.string.he_vlvh);

        String tomTat = getString(R.string.tom_tat_format,
                hoTen,
                mssv,
                spMonHoc.getSelectedItem().toString(),
                he,
                TextUtils.join(", ", buoiHoc),
                swThongBao.isChecked() ? getString(R.string.co) : getString(R.string.khong),
                tgUuTien.isChecked() ? getString(R.string.bat) : getString(R.string.tat));

        Intent intent = new Intent(this, ConfirmActivity.class);
        intent.putExtra(EXTRA_TOM_TAT, tomTat);
        startActivity(intent);
    }

    /** Đưa mọi control về trạng thái ban đầu. */
    private void lamLai() {
        edtHoTen.setText("");
        edtMssv.setText("");
        edtHoTen.setError(null);
        edtMssv.setError(null);
        spKhoa.setSelection(0);
        ganDanhSachMon(0);
        rgHeDaoTao.clearCheck();
        cbSang.setChecked(false);
        cbChieu.setChecked(false);
        cbToi.setChecked(false);
        swThongBao.setChecked(true);
        tgUuTien.setChecked(false);
        edtHoTen.requestFocus();
    }
}

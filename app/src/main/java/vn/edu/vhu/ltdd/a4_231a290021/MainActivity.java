package vn.edu.vhu.ltdd.a4_231a290021;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "A4_231A290021";
    private static final int LICH_SU_TOI_DA = 5;
    private static final String KEY_LICH_SU = "lichSu";
    private static final String KEY_KET_QUA = "ketQua";
    private static final String KEY_HAS_BMI = "hasBmi";
    private static final String KEY_BMI = "bmi";
    private static final String KEY_BMI_TEXT = "bmiText";
    private static final String KEY_PHAN_LOAI = "phanLoai";

    private EditText edtSoA, edtSoB, edtCanNang, edtChieuCao;
    private TextView tvKetQua, tvLichSu, tvBmi, tvPhanLoai;
    private EditText oDangChon;

    private final ArrayList<String> lichSu = new ArrayList<>();
    private boolean daTinhBmi = false;
    private double bmiHienTai = 0;

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

        edtSoA = findViewById(R.id.edtSoA);
        edtSoB = findViewById(R.id.edtSoB);
        tvKetQua = findViewById(R.id.tvKetQua);
        tvLichSu = findViewById(R.id.tvLichSu);
        edtCanNang = findViewById(R.id.edtCanNang);
        edtChieuCao = findViewById(R.id.edtChieuCao);
        tvBmi = findViewById(R.id.tvBmi);
        tvPhanLoai = findViewById(R.id.tvPhanLoai);

        oDangChon = edtSoA;
        edtSoA.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                oDangChon = edtSoA;
            }
        });
        edtSoB.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                oDangChon = edtSoB;
            }
        });

        Button btnCong = findViewById(R.id.btnCong);
        Button btnTru = findViewById(R.id.btnTru);
        Button btnNhan = findViewById(R.id.btnNhan);
        Button btnChia = findViewById(R.id.btnChia);
        Button btnPhanTram = findViewById(R.id.btnPhanTram);
        Button btnDaoDau = findViewById(R.id.btnDaoDau);
        Button btnXoa = findViewById(R.id.btnXoa);
        Button btnTinhBmi = findViewById(R.id.btnTinhBmi);

        // Cách 1: mỗi nút một listener bằng biểu thức lambda
        btnCong.setOnClickListener(v -> tinhToan('+'));
        btnTru.setOnClickListener(v -> tinhToan('-'));

        // Cách 2: một listener dùng chung, phân biệt bằng id
        View.OnClickListener chung = v -> {
            int id = v.getId();
            if (id == R.id.btnNhan) {
                tinhToan('*');
            } else if (id == R.id.btnChia) {
                tinhToan('/');
            } else if (id == R.id.btnPhanTram) {
                tinhToan('%');
            }
        };
        btnNhan.setOnClickListener(chung);
        btnChia.setOnClickListener(chung);
        btnPhanTram.setOnClickListener(chung);

        btnDaoDau.setOnClickListener(v -> daoDau());
        btnXoa.setOnClickListener(v -> xoaTrang());
        btnTinhBmi.setOnClickListener(v -> tinhBmi());

        if (savedInstanceState != null) {
            ArrayList<String> daLuu = savedInstanceState.getStringArrayList(KEY_LICH_SU);
            if (daLuu != null) {
                lichSu.addAll(daLuu);
            }
            daTinhBmi = savedInstanceState.getBoolean(KEY_HAS_BMI);
            bmiHienTai = savedInstanceState.getDouble(KEY_BMI);
            String ketQua = savedInstanceState.getString(KEY_KET_QUA);
            if (ketQua != null) {
                tvKetQua.setText(ketQua);
            }
            if (daTinhBmi) {
                tvBmi.setText(savedInstanceState.getString(KEY_BMI_TEXT, ""));
                tvPhanLoai.setText(savedInstanceState.getString(KEY_PHAN_LOAI, ""));
                toMauBmi(bmiHienTai);
            }
        }
        hienLichSu();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(KEY_LICH_SU, lichSu);
        outState.putString(KEY_KET_QUA, tvKetQua.getText().toString());
        outState.putBoolean(KEY_HAS_BMI, daTinhBmi);
        outState.putDouble(KEY_BMI, bmiHienTai);
        outState.putString(KEY_BMI_TEXT, tvBmi.getText().toString());
        outState.putString(KEY_PHAN_LOAI, tvPhanLoai.getText().toString());
    }

    private void tinhToan(char phepToan) {
        String chuoiA = edtSoA.getText().toString().trim();
        String chuoiB = edtSoB.getText().toString().trim();

        if (chuoiA.isEmpty()) {
            edtSoA.setError(getString(R.string.err_empty));
            edtSoA.requestFocus();
            return;
        }
        if (chuoiB.isEmpty()) {
            edtSoB.setError(getString(R.string.err_empty));
            edtSoB.requestFocus();
            return;
        }

        double a;
        double b;
        try {
            a = Double.parseDouble(chuanHoaSo(chuoiA));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Số A không hợp lệ: '" + chuoiA + "'", e);
            edtSoA.setError(getString(R.string.err_not_number));
            edtSoA.requestFocus();
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            b = Double.parseDouble(chuanHoaSo(chuoiB));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Số B không hợp lệ: '" + chuoiB + "'", e);
            edtSoB.setError(getString(R.string.err_not_number));
            edtSoB.requestFocus();
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
            return;
        }

        if (phepToan == '/' && b == 0) {
            edtSoB.setError(getString(R.string.err_divide_zero));
            edtSoB.requestFocus();
            Toast.makeText(this, R.string.err_divide_zero, Toast.LENGTH_SHORT).show();
            return;
        }

        double ketQua;
        switch (phepToan) {
            case '+':
                ketQua = a + b;
                break;
            case '-':
                ketQua = a - b;
                break;
            case '*':
                ketQua = a * b;
                break;
            case '%':
                ketQua = a * b / 100.0;
                break;
            default:
                ketQua = a / b;
                break;
        }

        String dong = String.format(Locale.getDefault(), "%.2f %c %.2f = %.2f",
                a, phepToan, b, ketQua);
        tvKetQua.setText(dong);
        themLichSu(dong);
        edtSoA.setError(null);
        edtSoB.setError(null);
        Log.d(TAG, "Phép tính: " + dong);
    }

    /** Đảo dấu số đang nhập (ô được chọn gần nhất), ghi lại vào chính ô đó. */
    private void daoDau() {
        EditText o = oDangChon == null ? edtSoA : oDangChon;
        String chuoi = o.getText().toString().trim();
        if (chuoi.isEmpty()) {
            o.setError(getString(R.string.err_empty));
            o.requestFocus();
            return;
        }
        try {
            double so = Double.parseDouble(chuanHoaSo(chuoi));
            o.setText(String.valueOf(so * -1));
            o.setError(null);
            o.setSelection(o.getText().length());
        } catch (NumberFormatException e) {
            Log.e(TAG, "Không đảo dấu được: '" + chuoi + "'", e);
            o.setError(getString(R.string.err_not_number));
            o.requestFocus();
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
        }
    }

    private void xoaTrang() {
        final String cuA = edtSoA.getText().toString();
        final String cuB = edtSoB.getText().toString();
        final CharSequence cuKetQua = tvKetQua.getText();

        edtSoA.setText("");
        edtSoB.setText("");
        edtSoA.setError(null);
        edtSoB.setError(null);
        tvKetQua.setText(R.string.result_placeholder);
        edtSoA.requestFocus();

        Snackbar.make(findViewById(R.id.main), R.string.cleared, Snackbar.LENGTH_LONG)
                .setAction(R.string.undo, v -> {
                    edtSoA.setText(cuA);
                    edtSoB.setText(cuB);
                    tvKetQua.setText(cuKetQua);
                })
                .show();
    }

    private void themLichSu(String dong) {
        lichSu.add(0, dong);
        while (lichSu.size() > LICH_SU_TOI_DA) {
            lichSu.remove(lichSu.size() - 1);
        }
        hienLichSu();
    }

    private void hienLichSu() {
        if (lichSu.isEmpty()) {
            tvLichSu.setText(R.string.history_empty);
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < lichSu.size(); i++) {
            if (i > 0) {
                sb.append('\n');
            }
            sb.append(i + 1).append(". ").append(lichSu.get(i));
        }
        tvLichSu.setText(sb.toString());
    }

    private void tinhBmi() {
        String chuoiNang = edtCanNang.getText().toString().trim();
        String chuoiCao = edtChieuCao.getText().toString().trim();

        if (chuoiNang.isEmpty()) {
            edtCanNang.setError(getString(R.string.err_empty));
            edtCanNang.requestFocus();
            return;
        }
        if (chuoiCao.isEmpty()) {
            edtChieuCao.setError(getString(R.string.err_empty));
            edtChieuCao.requestFocus();
            return;
        }

        double canNang;
        double chieuCao;
        try {
            canNang = Double.parseDouble(chuanHoaSo(chuoiNang));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Cân nặng không hợp lệ: '" + chuoiNang + "'", e);
            edtCanNang.setError(getString(R.string.err_not_number));
            edtCanNang.requestFocus();
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
            return;
        }
        try {
            chieuCao = Double.parseDouble(chuanHoaSo(chuoiCao));
        } catch (NumberFormatException e) {
            Log.e(TAG, "Chiều cao không hợp lệ: '" + chuoiCao + "'", e);
            edtChieuCao.setError(getString(R.string.err_not_number));
            edtChieuCao.requestFocus();
            Toast.makeText(this, R.string.err_not_number, Toast.LENGTH_SHORT).show();
            return;
        }

        if (canNang <= 0) {
            edtCanNang.setError(getString(R.string.err_positive));
            edtCanNang.requestFocus();
            Toast.makeText(this, R.string.err_positive, Toast.LENGTH_SHORT).show();
            return;
        }
        if (chieuCao <= 0) {
            edtChieuCao.setError(getString(R.string.err_positive));
            edtChieuCao.requestFocus();
            Toast.makeText(this, R.string.err_positive, Toast.LENGTH_SHORT).show();
            return;
        }

        double bmi = Bmi.tinh(canNang, chieuCao);
        if (Double.isInfinite(bmi) || Double.isNaN(bmi)) {
            edtChieuCao.setError(getString(R.string.err_positive));
            Toast.makeText(this, R.string.err_positive, Toast.LENGTH_SHORT).show();
            return;
        }

        daTinhBmi = true;
        bmiHienTai = bmi;
        tvBmi.setText(String.format(Locale.getDefault(), "BMI = %.1f", bmi));
        tvPhanLoai.setText(nhanPhanLoai(Bmi.phanLoai(bmi)));
        toMauBmi(bmi);
        edtCanNang.setError(null);
        edtChieuCao.setError(null);
        Log.d(TAG, "BMI = " + bmi + " (" + tvPhanLoai.getText() + ")");
    }

    private String nhanPhanLoai(Bmi.Nhom nhom) {
        switch (nhom) {
            case THIEU_CAN:
                return getString(R.string.bmi_under);
            case BINH_THUONG:
                return getString(R.string.bmi_normal);
            case THUA_CAN:
                return getString(R.string.bmi_over);
            default:
                return getString(R.string.bmi_obese);
        }
    }

    /** Xanh: bình thường. Vàng: thừa cân. Đỏ: béo phì. Xanh dương: thiếu cân. */
    private void toMauBmi(double bmi) {
        int colorId;
        switch (Bmi.phanLoai(bmi)) {
            case THIEU_CAN:
                colorId = R.color.bmi_under;
                break;
            case BINH_THUONG:
                colorId = R.color.bmi_normal;
                break;
            case THUA_CAN:
                colorId = R.color.bmi_over;
                break;
            default:
                colorId = R.color.bmi_obese;
                break;
        }
        int mau = ContextCompat.getColor(this, colorId);
        tvBmi.setTextColor(mau);
        tvPhanLoai.setTextColor(mau);
    }

    /** Dấu phẩy thập phân (bàn phím tiếng Việt) được đổi thành dấu chấm trước khi parse. */
    private String chuanHoaSo(String chuoi) {
        return chuoi.trim().replace(',', '.');
    }
}

package vn.edu.vhu.ltdd.a4_231a290021;

/**
 * Công thức BMI và ngưỡng WHO cho khu vực châu Á.
 * Tách khỏi giao diện để kiểm thử được bằng unit test.
 */
public final class Bmi {

    public enum Nhom {
        THIEU_CAN,
        BINH_THUONG,
        THUA_CAN,
        BEO_PHI
    }

    private Bmi() {
    }

    /** 1.70 giữ nguyên (mét). Giá trị lớn hơn 3 được hiểu là centimet. */
    public static double chieuCaoMet(double chieuCao) {
        if (chieuCao > 3) {
            return chieuCao / 100.0;
        }
        return chieuCao;
    }

    public static double tinh(double canNangKg, double chieuCao) {
        double met = chieuCaoMet(chieuCao);
        return canNangKg / (met * met);
    }

    /** Dưới 18.5 thiếu cân; dưới 23 bình thường; dưới 25 thừa cân; còn lại béo phì. */
    public static Nhom phanLoai(double bmi) {
        if (bmi < 18.5) {
            return Nhom.THIEU_CAN;
        }
        if (bmi < 23) {
            return Nhom.BINH_THUONG;
        }
        if (bmi < 25) {
            return Nhom.THUA_CAN;
        }
        return Nhom.BEO_PHI;
    }
}

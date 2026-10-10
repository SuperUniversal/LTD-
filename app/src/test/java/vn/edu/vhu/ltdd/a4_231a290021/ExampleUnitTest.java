package vn.edu.vhu.ltdd.a4_231a290021;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ExampleUnitTest {

    @Test
    public void bmi_60kg_170cm_va_1_70m_cung_ket_qua() {
        double tuMet = Bmi.tinh(60, 1.70);
        double tuCm = Bmi.tinh(60, 170);
        assertEquals(20.761, tuMet, 0.001);
        assertEquals(tuMet, tuCm, 0.0001);
        assertEquals(Bmi.Nhom.BINH_THUONG, Bmi.phanLoai(tuMet));
    }

    @Test
    public void nguong_who_chau_a() {
        assertEquals(Bmi.Nhom.THIEU_CAN, Bmi.phanLoai(18.49));
        assertEquals(Bmi.Nhom.BINH_THUONG, Bmi.phanLoai(18.5));
        assertEquals(Bmi.Nhom.BINH_THUONG, Bmi.phanLoai(22.9));
        assertEquals(Bmi.Nhom.THUA_CAN, Bmi.phanLoai(23));
        assertEquals(Bmi.Nhom.THUA_CAN, Bmi.phanLoai(24.9));
        assertEquals(Bmi.Nhom.BEO_PHI, Bmi.phanLoai(25));
    }

    @Test
    public void chieu_cao_hon_3_duoc_doi_sang_met() {
        assertEquals(1.70, Bmi.chieuCaoMet(170), 0.0001);
        assertEquals(1.70, Bmi.chieuCaoMet(1.70), 0.0001);
    }
}

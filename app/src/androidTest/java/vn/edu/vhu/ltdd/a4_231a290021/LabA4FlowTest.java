package vn.edu.vhu.ltdd.a4_231a290021;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasFocus;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertEquals;

import android.widget.EditText;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

@RunWith(AndroidJUnit4.class)
public class LabA4FlowTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void chia_12_cho_3() {
        nhap("12", "3");
        onView(withId(R.id.btnChia)).perform(click());
        onView(withId(R.id.tvKetQua)).check(matches(withText(ketQua(12, '/', 3, 4))));
    }

    @Test
    public void chia_cho_0_bao_loi_o_b() {
        nhap("12", "0");
        onView(withId(R.id.btnChia)).perform(click());
        onView(withId(R.id.edtSoB)).check(coLoi(R.string.err_divide_zero));
        onView(withId(R.id.tvKetQua)).check(matches(withText(R.string.result_placeholder)));
    }

    @Test
    public void o_a_trong_thi_focus_ve_a() {
        nhap("", "3");
        onView(withId(R.id.btnCong)).perform(click());
        onView(withId(R.id.edtSoA)).check(coLoi(R.string.err_empty));
        onView(withId(R.id.edtSoA)).check(matches(hasFocus()));
    }

    @Test
    public void chu_abc_khong_crash() {
        nhap("abc", "2");
        onView(withId(R.id.btnNhan)).perform(click());
        onView(withId(R.id.edtSoA)).check(coLoi(R.string.err_not_number));
    }

    @Test
    public void hai_dau_cham_khong_crash() {
        nhap("1.5.5", "1");
        onView(withId(R.id.btnCong)).perform(click());
        onView(withId(R.id.edtSoA)).check(coLoi(R.string.err_not_number));
    }

    @Test
    public void tru_so_am() {
        nhap("-5.5", "2");
        onView(withId(R.id.btnTru)).perform(click());
        onView(withId(R.id.tvKetQua)).check(matches(withText(ketQua(-5.5, '-', 2, -7.5))));
    }

    @Test
    public void bmi_met_va_cm() {
        tinhBmi("60", "1.70");
        onView(withId(R.id.tvBmi)).check(matches(withText(bmiText(60, 1.70))));
        onView(withId(R.id.tvPhanLoai)).check(matches(withText(R.string.bmi_normal)));
        kiemMau(R.color.bmi_normal);

        tinhBmi("60", "170");
        onView(withId(R.id.tvBmi)).check(matches(withText(bmiText(60, 170))));
        onView(withId(R.id.tvPhanLoai)).check(matches(withText(R.string.bmi_normal)));
    }

    @Test
    public void chieu_cao_0_khong_ra_infinity() {
        tinhBmi("60", "0");
        onView(withId(R.id.edtChieuCao)).check(coLoi(R.string.err_positive));
        onView(withId(R.id.tvBmi)).check(matches(withText("")));
    }

    @Test
    public void dao_dau_va_phan_tram() {
        onView(withId(R.id.edtSoA)).perform(click(), replaceText("8"), closeSoftKeyboard());
        onView(withId(R.id.btnDaoDau)).perform(click());
        onView(withId(R.id.edtSoA)).check(matches(withText("-8.0")));

        nhap("200", "10");
        onView(withId(R.id.btnPhanTram)).perform(click());
        onView(withId(R.id.tvKetQua)).check(matches(withText(ketQua(200, '%', 10, 20))));
    }

    @Test
    public void lich_su_con_sau_khi_xoay() {
        nhap("12", "3");
        onView(withId(R.id.btnChia)).perform(click());
        rule.getScenario().recreate();
        onView(withId(R.id.tvLichSu)).check(matches(withText(containsString("1."))));
        onView(withId(R.id.tvKetQua)).check(matches(withText(ketQua(12, '/', 3, 4))));
    }

    private static ViewAssertion coLoi(int stringId) {
        String mongDoi = InstrumentationRegistry.getInstrumentation()
                .getTargetContext()
                .getString(stringId);
        return (view, noViewFoundException) -> {
            CharSequence loi = ((EditText) view).getError();
            assertEquals(mongDoi, loi == null ? null : loi.toString());
        };
    }

    private void nhap(String a, String b) {
        onView(withId(R.id.edtSoA)).perform(replaceText(a), closeSoftKeyboard());
        onView(withId(R.id.edtSoB)).perform(replaceText(b), closeSoftKeyboard());
    }

    private void tinhBmi(String nang, String cao) {
        onView(withId(R.id.edtCanNang)).perform(replaceText(nang), closeSoftKeyboard());
        onView(withId(R.id.edtChieuCao)).perform(replaceText(cao), closeSoftKeyboard());
        onView(withId(R.id.btnTinhBmi)).perform(click());
    }

    private void kiemMau(int colorId) {
        onView(withId(R.id.tvPhanLoai)).check((view, noViewFoundException) -> {
            int mau = ContextCompat.getColor(view.getContext(), colorId);
            assertEquals(mau, ((TextView) view).getCurrentTextColor());
        });
    }

    private static String ketQua(double a, char op, double b, double kq) {
        return String.format(Locale.getDefault(), "%.2f %c %.2f = %.2f", a, op, b, kq);
    }

    private static String bmiText(double nang, double cao) {
        return String.format(Locale.getDefault(), "BMI = %.1f", Bmi.tinh(nang, cao));
    }
}

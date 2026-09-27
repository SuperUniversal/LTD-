package vn.edu.vhu.ltdd.a3layout;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Chỉ hiển thị layout dựng bằng ConstraintLayout để so sánh với bản LinearLayout. */
public class ConstraintDemoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_constraint_demo);
        setTitle(R.string.constraint_title);
        View root = findViewById(R.id.main);
        int pad = getResources().getDimensionPixelSize(R.dimen.space_lg);
        ViewCompat.setOnApplyWindowInsetsListener(root, (v, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    bars.left + pad,
                    bars.top + pad,
                    bars.right + pad,
                    bars.bottom + pad);
            return insets;
        });
    }
}

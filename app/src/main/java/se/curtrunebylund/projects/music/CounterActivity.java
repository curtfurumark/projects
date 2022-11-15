package se.curtrunebylund.projects.music;

import androidx.appcompat.app.AppCompatActivity;

import android.widget.EditText;
import se.curtrunebylund.projects.R;

public class CounterActivity extends AppCompatActivity {
    private int number_ok;
    private int number_fail;
    private EditText editText_ok;
    private EditText editText_fail;

    @Override
    protected void onCreate(android.os.Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.counter_activity);
        editText_fail = findViewById(R.id.editText_fails);
        editText_ok = findViewById(R.id.editText_correct);
    }

    public void onButtonOK(android.view.View view) {
        number_ok++;
        editText_ok.setText(String.valueOf(number_ok));
    }

    public void onButtonFail(android.view.View view) {
        number_fail++;
        editText_fail.setText(String.valueOf(number_fail));
    }
    public void onButtonReset(android.view.View view){
        number_ok =number_fail = 0;
        editText_ok.setText("0");
        editText_fail.setText("0");
    }
}
package se.curtrunebylund.projects.help;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import se.curtrunebylund.projects.R;

public class AttemptBottomSheet extends BottomSheetDialogFragment {
    private Button button_save;
    private EditText editText_heading;
    private EditText editText_number;
    public interface Listener{
        public void onAttemptSave(String text, int number);
    }
    private Listener listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attempt_bottom_sheet, container, false);
        button_save = view.findViewById(R.id.button_attemptButtonSheet_addAttempt);
        editText_heading = view.findViewById(R.id.editText_attemptBottomSheet_heading);
        editText_number = view.findViewById(R.id.editText_attemptBottomSheet_numberAttempts);
        editText_number.setText("1");

        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int number = 1;
                String str_number = editText_number.getText().toString();
                if( !str_number.isEmpty()) {
                    number = Integer.parseInt(str_number);
                }
                listener.onAttemptSave(editText_heading.getText().toString(), number);
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listener = (Listener) context;
    }
}

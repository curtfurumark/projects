package se.curtrunebylund.projects.art;

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

public class TaskBottomSheet extends BottomSheetDialogFragment {
    private Button button_save;
    private EditText editText;
    public interface Listener{
        public void onButtonSave(String text);
    }
    private Listener listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.task_bottom_sheet, container, false);
        button_save = view.findViewById(R.id.button_attemptButtonSheet_addAttempt);
        editText = view.findViewById(R.id.editText_myBottomSheet);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onButtonSave(editText.getText().toString());
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

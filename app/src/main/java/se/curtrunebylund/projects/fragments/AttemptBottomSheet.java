package se.curtrunebylund.projects.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import item.Type;
import logger.CRBLogger;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.infinity.ListItem;

public class AttemptBottomSheet extends BottomSheetDialogFragment implements AdapterView.OnItemClickListener {
    private EditText editText_heading;
    private Spinner spinner_types;

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        CRBLogger.log("AttemptBottomSheet()");
    }

    public interface Callback{
        void onAttemptSave(String text, Type type);
    }
    private Callback listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.attempt_bottom_sheet, container, false);
        Button button_save = view.findViewById(R.id.button_attemptButtonSheet_addAttempt);
        editText_heading = view.findViewById(R.id.editText_attemptBottomSheet_heading);
        spinner_types = view.findViewById(R.id.spinner_attemptBottomSheet);
        String[] types = ListItem.Type.toArray();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, types);
        spinner_types.setAdapter(adapter);
        spinner_types.setOnItemClickListener(this);
        button_save.setOnClickListener(view1 -> {
            String str_type = (String) spinner_types.getSelectedItem();
            listener.onAttemptSave(editText_heading.getText().toString(), Type.valueOf(str_type));
            dismiss();
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listener = (Callback) context;
    }
}

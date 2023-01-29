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
import android.widget.RadioGroup;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import item.Type;
import se.curtrunebylund.projects.R;

public class AddSessionFragment extends BottomSheetDialogFragment {
    private EditText editText_heading;
    private Spinner spinner_types;
    private Type type = Type.PENDING;

    public interface Callback{
        void onAttemptSave(String text, Type type);
    }
    private Callback listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_session_fragment, container, false);
        Button button_save = view.findViewById(R.id.addSession_button_save);
        editText_heading = view.findViewById(R.id.addSession_editText_heading);
        spinner_types = view.findViewById(R.id.addSession_spinner_types);
        String[] types = Type.toArray();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.support_simple_spinner_dropdown_item, types);
        spinner_types.setAdapter(adapter);
        spinner_types.setSelection(Type.PENDING.ordinal(), true);
        spinner_types.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                type = Type.values()[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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

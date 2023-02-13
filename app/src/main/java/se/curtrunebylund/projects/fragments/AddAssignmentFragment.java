package se.curtrunebylund.projects.fragments;

import static logger.CRBLogger.log;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import se.curtrunebylund.projects.R;

public class AddAssignmentFragment extends BottomSheetDialogFragment {
    private EditText editText_heading;
    private RadioGroup radioGroup;
    private RadioButton  radioButton_checked;

    public interface Callback{
        void onAddAssignment(String text);
    }
    private Callback listener;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_assignment_fragment, container, false);
        Button button_save = view.findViewById(R.id.addAssignment_button_add);
        editText_heading = view.findViewById(R.id.addAssignment_editText_heading);
        radioGroup = view.findViewById(R.id.addAssignment_radioGroup_type);
        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            log("AddAssignmentFragment.onCheckChanged(RadioGroup, int resource_id)");
            radioButton_checked = radioGroup.findViewById(i);
            //String  radioButtonString = radioButton.getText().toString();
            //log("radiogroup", radioButtonString);
        });
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //if( radioButton_checked.getId() == R.id.addAssignement_radioButton_laps
                //Assignment.Type type = radioButton_checked.getId() == R.id.addAssignement_radioButton_laps ?  Assignment.Type.LAPS: Assignment.Type.REPS;
                listener.onAddAssignment(editText_heading.getText().toString());
                dismiss();
            }
        });
        return view;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.listener = (Callback) context;
    }
}

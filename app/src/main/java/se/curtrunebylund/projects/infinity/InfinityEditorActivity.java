package se.curtrunebylund.projects.infinity;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Locale;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.db.PersistInfinity;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.help.Converter;

public class InfinityEditorActivity extends AppCompatActivity {
    private EditText editText_heading;
    private EditText editText_description;
    private TextView textView_state;
    private EditText editText_type;
    private TextView textView_created;
    private TextView textView_updated;
    private TextView textView_id;
    private TextView textView_parent_id;
    private EditText editText_tags;
    private Spinner spinner_state;
    private Spinner spinner_type;
    private ListItem.State item_state = ListItem.State.INDETERMINATE;
    private ListItem.Type item_type = ListItem.Type.DEFAULT;
    private Long parent_id;
    private Long item_id;
    private ListItem item;
    private ListItem parentItem;
    private enum Mode{
        CREATE, EDIT, UNDEFINED;
    }
    private Mode mode = Mode.UNDEFINED;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.infinity_editor_activity);
        Debug.log("InfinityEditorActivity.onCreate()");
        setTitle("List item editor");
        editText_description = findViewById(R.id.editText_infinityEditor_description);
        editText_heading = findViewById(R.id.editText_infinityEditor_heading);
        textView_created = findViewById(R.id.textView_infinityEditor_created);
        textView_id = findViewById(R.id.textView_infinityEditor_id);
        textView_parent_id = findViewById(R.id.textView_infinityEditor_parent_id);
        textView_state = findViewById(R.id.textView_infinityEditor_state);
        textView_updated = findViewById(R.id.textView_infinityEditor_updated);
        editText_tags= findViewById(R.id.editText_infinityEditor_tags);
        spinner_state = findViewById(R.id.spinner_infinityEditor_state);
        spinner_type = findViewById(R.id.spinner_infinityEditor_type);
        initSpinnerType(ListItem.Type.DEFAULT);
        initSpinnerState(ListItem.State.INDETERMINATE);
        //initSpinnerType(ListItem.Type.DEFAULT);
        Intent intent = getIntent();
        if( intent.getBooleanExtra(Constants.INTENT_CREATE_ROOT_ITEM, false)) {
            mode = Mode.CREATE;
            setTitle("create root item");
            createRootItem();
        }
        else if( intent.getBooleanExtra(Constants.INTENT_CREATE_SIBLING,false)){
            parentItem = (ListItem) intent.getSerializableExtra(Constants.INTENT_SERIALIZED_PARENT);
            if(parentItem == null){
                Debug.logError("Infintiy");
            }
            String pid = String.format(Locale.getDefault(), "parent id: %d", parentItem.getID());
            textView_parent_id.setText(pid);
            mode = Mode.CREATE;
            item = new ListItem();
            item.setParentID(parentItem.getID());
        }else if( intent.getBooleanExtra(Constants.INTENT_EDIT_LIST_ITEM, false)){
            setTitle("edit list item");
            mode = Mode.EDIT;
            item = (ListItem) intent.getSerializableExtra(Constants.INTENT_SERIALIZED_LIST_ITEM);
            if( item == null){
                Debug.log("serialized list item null, todo");
            }
            parent_id = item.getParentID();
            item_id = item.getID();
            editListItem(item);
        }
    }

    private void editListItem(ListItem item) {
        Debug.log("InfinityEditorActivity.editListItem() id: " + item.getID());
        editText_description.setText(item.getDescription());
        editText_tags.setText(item.getTags());
        editText_heading.setText(item.getHeading());
        String string_id = String.format("id: %d", item.getID());
        String string_created = String.format("updated; %s", Converter.formatUI(item.getCreated()));
        String string_updated = String.format("created: %s", Converter.formatUI(item.getUpdated()));
        String parent_id = String.format(Locale.getDefault(), "parent id: %s", item.getParentID() == -1? "item is root": item.getParentID().toString());
        textView_id.setText(string_id);
        textView_parent_id.setText(parent_id);
        textView_created.setText(string_created);
        textView_updated.setText(string_updated);
        textView_state.setText(item.getState().toString());
        //TODO type and state

    }

    private void createRootItem() {
        Debug.log("ListItemEditor.createRootItem()");
        item = new ListItem();
        item.setHeading(editText_heading.getText().toString());
        item.setDescription(editText_description.getText().toString());
        item.setTags(editText_tags.getText().toString());
        item.setType(ListItem.Type.DEV);
        item.setState(ListItem.State.TODO);
        item_state = ListItem.State.TODO;
        spinner_state.setSelection(ListItem.State.TODO.ordinal());
    }

    private void initSpinnerState(ListItem.State initial_state) {
        Debug.log("InfinityEditorActivity.initSpinnerState()");
        item_state = initial_state;
        String[] types  = ListItem.State.toArray();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_state.setAdapter(arrayAdapter);
        spinner_state.setSelection(item_state.ordinal());
        spinner_state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item_state = ListItem.State.values()[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initSpinnerType(ListItem.Type initial_type) {
        Debug.log("InfinityEditorActivity.initSpinnerType()");
        item_type = initial_type;
        String[] types  = ListItem.Type.toArray();
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, types);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spinner_type.setAdapter(arrayAdapter);
        spinner_type.setSelection(item_type.ordinal());
        spinner_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                item_type = ListItem.Type.values()[position];
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.infinity_editor, menu);
        return true;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@androidx.annotation.NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.icon_infinityEditor_save:
                saveItem();
                break;
            case R.id.icon_infinityEditor_home:
                startActivity(new Intent(this, InfinityActivity.class));
                break;
            case R.id.icon_infinityEditor_addChild: //new child that is
                newChildItem();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void newChildItem() {
        Debug.log("InfinityEditor.newChildItem()");
        mode = Mode.CREATE;
        setTitle("create child item");
        item = new ListItem();
        item.setParentID(item_id);
        editText_heading.setText("");
        editText_description.setText("");
        editText_tags.setText("");
        String string_pid = String.format("parent id: %d", item.getParentID());
        textView_parent_id.setText(string_pid);
        textView_id.setText("id: n/a");
        textView_updated.setText("updated: n/a");
        String str_update = String.format(Locale.getDefault(), "created: %s", Converter.formatUI(item.getCreated()));
        textView_created.setText(str_update);
    }

    private void saveItem() {
        Debug.log("ListItemEditor.saveItem() mode = " + mode.toString());
        if( mode.equals(Mode.CREATE)) {
            item.setHeading(editText_heading.getText().toString());
            item.setDescription(editText_description.getText().toString());
            item.setTags(editText_tags.getText().toString());
            item.setState(item_state);
            //item.setParentID();
            //TODO:state and type
            item.setType(item_type);
            item = PersistInfinity.add(item, this);
            setGUI(item);
        }else if( mode.equals(Mode.EDIT)){
            try {
                item.setHeading(editText_heading.getText().toString());
                item.setDescription(editText_description.getText().toString());
                item.setTags(editText_tags.getText().toString());
                item.setType(item_type);
                item.setState(item_state);
                item.update();
                PersistInfinity.update(item, this);
                Intent intent = new Intent(this, InfinityActivity.class);
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void setGUI(ListItem item){
        String str_id = String.format(Locale.getDefault(), "id: %d", item.getID());
        textView_id.setText(str_id);
        editText_description.setText(item.getDescription());
        editText_tags.setText(item.getTags());
        editText_heading.setText(item.getHeading());
        String string_id = String.format("id: %d", item.getID());
        String string_created = String.format("updated; %s", Converter.formatUI(item.getCreated()));
        String string_updated = String.format("created: %s", Converter.formatUI(item.getUpdated()));
        String parent_id = String.format(Locale.getDefault(), "parent id: %s", item.getParentID().equals(0)? "item is root": item.getParentID().toString());
        textView_id.setText(string_id);
        textView_parent_id.setText(parent_id);
        textView_created.setText(string_created);
        textView_updated.setText(string_updated);
        textView_state.setText(item.getState().toString());

    }

}
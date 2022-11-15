package se.curtrunebylund.projects.infinity;
//package se.curtrunebylund.projects.res.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.List;

import se.curtrunebylund.projects.Debug;
import se.curtrunebylund.projects.R;
import se.curtrunebylund.projects.activities.SplashActivity;
import se.curtrunebylund.projects.db.PersistInfinity;
import se.curtrunebylund.projects.help.Constants;
import se.curtrunebylund.projects.util.Stack;

public class InfinityActivity extends AppCompatActivity implements ListItemAdapter.Callback{
    private RecyclerView recyclerView;
    private ListItemAdapter adapter;
    private List<ListItem> items;
    private EditText editText_search;
    private TextView textView_parent;
    private ListItem parentItem = null;
    private Stack stack = new Stack();
    //private Long parent_id = 0l;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Debug.log("InfinityActivity.onCreate()");
        setContentView(R.layout.infinity_activity);
        setTitle("infinity");
        recyclerView = findViewById(R.id.recycler_infinity);
        textView_parent = findViewById(R.id.textView_infinityActivity_parent);
        textView_parent.setOnClickListener(view -> {
            Intent intent = new Intent(this, InfinityEditorActivity.class);
            intent.putExtra(Constants.INTENT_EDIT_LIST_ITEM,true);
            intent.putExtra(Constants.INTENT_SERIALIZED_LIST_ITEM, parentItem);
            startActivity(intent);
        });
        editText_search = findViewById(R.id.textView_infinityActivity_search);
        editText_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Debug.log("InfinityActivity editText onClick()");
            }
        });
        Intent intent = getIntent();
        //items = PersistInfinity.getRootItems(this);
        if( parentItem == null) {
            items = PersistInfinity.getChildren(0l, this);
            textView_parent.setText("roots rock reggae");
        }
        initRecyclerView(items);
    }

    private void initRecyclerView(List<ListItem> items) {
        Debug.log("InfinityActivity.initRecyclerView()");
        adapter = new ListItemAdapter(items, this, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.infinity_list_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Debug.log("InfinityActivity.onOptionsSelected()");
        switch(item.getItemId()){
            case R.id.icon_home:
                if( parentItem == null){
                    startActivity(new Intent(this, SplashActivity.class));
                }
                parentItem = stack.pop();
                Debug.log(stack);
                if (parentItem == null){
                    textView_parent.setText("roots rock reggae (pid 0)");
                    items = PersistInfinity.getChildren(0l, this);
                    adapter.setFilteredList(items);
                }else{
                    textView_parent.setText(parentItem.getInfo());
                    items = PersistInfinity.getChildren(parentItem, this);
                    adapter.setFilteredList(items);
                }
                break;
            case R.id.icon_add_root_item://add sibling
                Intent intent = new Intent(this, InfinityEditorActivity.class);
                intent.putExtra(Constants.INTENT_CREATE_SIBLING, true);
                intent.putExtra(Constants.INTENT_SERIALIZED_PARENT, parentItem);
                startActivity(intent);
                break;
            case R.id.infinityList_all_items:
                adapter.setFilteredList(PersistInfinity.getListItems(this));
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemClick(ListItem item) {
        Debug.log("InfinityAdapter.onItemClick(ListItem)");
        Debug.log(item);
        parentItem = item;
        stack.push(item);
        Debug.log(stack);
        List<ListItem> items = PersistInfinity.getChildren(item.getID(), this);
        if( items == null){
            Debug.log("items is null");
        }
        Debug.log("size of items:" + items.size());
        if( items != null && items.size() > 0){
            textView_parent.setText(item.getInfo());
            adapter.setFilteredList(items);
        }else{
            Intent intent = new Intent(this, InfinityEditorActivity.class);
            intent.putExtra(Constants.INTENT_EDIT_LIST_ITEM, true);
            intent.putExtra(Constants.INTENT_SERIALIZED_LIST_ITEM, item);
            startActivity(intent);
        }

    }
}
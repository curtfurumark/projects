package se.curtrunebylund.projects.art;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import se.curtrunebylund.projects.util.Debug;
import se.curtrunebylund.projects.R;


//import java.net.URI;

public class ShareToMeActivity extends AppCompatActivity {
    //private ArtWorkManager artWorkManager;
    private EditText editText_description;
    private TextView textView_url;
    private Button button_save;
    private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_to_me_activity);
        Debug.log("ShareToMeActivity.onCreate()");
        setTitle("share2meActivity");
        //Debug.showMessage(this, "hello i am ShareToMeActvity");
        editText_description = findViewById(R.id.editText_shareAct_description);
        textView_url = findViewById(R.id.textView_shareAct_text);
        button_save = findViewById(R.id.button_shareAct_save);
        button_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save_art_work();
            }
        });
        //artWorkManager = ArtWorkManager.getInstance(this);
        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();
        String info = String.format("action: %s type: %s", action, type);
        Debug.log(info);
        if (Intent.ACTION_SEND.equals(action) && null != type){
            if ( "text/plain".equals(type)){
                handleText(intent);
            }else if( type.startsWith("image")){
                handleImage(intent);
            }else{
                Debug.showMessage(this, "no handler for " + type);
            }
        }else{
            Debug.log("type is null, not equal ACTION_SEND");
        }
    }

    private void handleImage(Intent intent) {
        Debug.log("ShareToMe.handleImage()");
        //Debug.showMessage(this, "got an image");
        ArtWorkManager.uri = uri = intent.getParcelableExtra(Intent.EXTRA_STREAM);
        String path = uri.getPath();
        Debug.log(uri);
        textView_url.setText(uri.getPath());
        Intent intent1 = new Intent(this, ArtworkEditActivity.class);
        intent1.putExtra(PicHelper.INTENT_LOAD_IMAGE_URI, true);
        intent1.putExtra(PicHelper.INTENT_IMAGE_URI, ArtWorkManager.uri);
        startActivity(intent1);
    }

    private void save_art_work() {
        Debug.log("ShareToMeActivity.save_art_work()");
        String url = textView_url.getText().toString();
        String description = editText_description.getText().toString();
        Intent intent = new Intent(this, ArtWorkListActivity.class);
        intent.putExtra(ArtWorkManager.INTENT_NEW_ARTWORK, true);
        intent.putExtra(ArtWorkManager.INTENT_ARTWORK_DESCRIPTON, description);
        intent.putExtra(ArtWorkManager.INTENT_URI, uri);
        startActivity(intent);
    }

    private void handleText(Intent intent) {
        Debug.log("ShareToMeActivity.handleText()");
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if ( null != sharedText){
            textView_url.setText(sharedText);
        }
    }
}

package com.project.major.alumniapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.utils.LoadingDialog;
import com.project.major.alumniapp.utils.StringManipulation;
import com.sdsmdg.tastytoast.TastyToast;

public class EditFeedsActivity extends AppCompatActivity {

    Toolbar toolbar;
    EditText captionText;
    Button updateFeed;
    Button cancel;
    LoadingDialog loadingDialog;
    String caption;
    String tags;
    String node;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_feeds);

        toolbar = findViewById(R.id.toolbar);
        captionText = findViewById(R.id.updateCaption);
        updateFeed = findViewById(R.id.updateFeed);
        cancel = findViewById(R.id.cancelfeed);
        loadingDialog = new LoadingDialog(EditFeedsActivity.this);

        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Update Feed");

        node =getIntent().getStringExtra("node");
        id = getIntent().getStringExtra("id");
        captionText.setText(getIntent().getStringExtra("caption"));

        updateFeed.setOnClickListener(v -> update(node, id));
        cancel.setOnClickListener(v -> finish());

    }

    private void update(String node, String id) {
        loadingDialog.showLoading();
        caption = captionText.getText().toString();
        tags = StringManipulation.getTags(caption);
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("alumni_app").child("Feeds").child(node).child(id);
        reference.child("caption_text").setValue(caption);
        reference.child("tags").setValue(tags).addOnCompleteListener(task -> {
            loadingDialog.hideLoading();
//            startActivity(new Intent(EditFeedsActivity.this, MainActivity.class));
            TastyToast.makeText(EditFeedsActivity.this, "Post Edited", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            finish();
        });
    }
}

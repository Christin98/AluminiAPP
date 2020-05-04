
package com.project.major.alumniapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.utils.LoadingDialog;
import com.project.major.alumniapp.utils.StringManipulation;
import com.sdsmdg.tastytoast.TastyToast;

import java.util.Calendar;

public class EditEventsActivity extends AppCompatActivity {

    EditText eventName;
    EditText details;
    EditText location;
    EditText time;
    EditText date;
    Button updateEvent;
    DatePickerDialog datePickerDialog;
    TimePickerDialog timePickerDialog;
    LoadingDialog loadingDialog;
    Button cancel;
    String node;
    String id;
    String en;
    String det;
    String loc;
    String ti;
    String da;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_events);

        eventName = findViewById(R.id.updateEvent_name);
        details = findViewById(R.id.updateEvent_details);
        location = findViewById(R.id.updateEvent_location);
        time = findViewById(R.id.updateEevent_timing);
        date = findViewById(R.id.updateEvent_date);
        updateEvent = findViewById(R.id.updateEvent);
        cancel = findViewById(R.id.cancelEvent);
        loadingDialog = new LoadingDialog(EditEventsActivity.this);

        node = getIntent().getStringExtra("node");
        id = getIntent().getStringExtra("id");
        en = getIntent().getStringExtra("eventname");
        det = getIntent().getStringExtra("details");
        loc = getIntent().getStringExtra("location");
        ti = getIntent().getStringExtra("time");
        da = getIntent().getStringExtra("date");

        eventName.setText(en);
        details.setText(det);
        location.setText(loc);
        time.setText(ti);
        date.setText(da);

        time.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int min = calendar.get(Calendar.MINUTE);

            timePickerDialog = new TimePickerDialog(EditEventsActivity.this, (view, hourOfDay, minute) -> time.setText(hourOfDay + ":" + minute), hour, min, false);
            timePickerDialog.show();
        });


        date.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            datePickerDialog = new DatePickerDialog(EditEventsActivity.this, (view, year1, month1, dayOfMonth) -> date.setText(dayOfMonth + "/" + (month1 +1) + "/" + year1),year, month, day);
            datePickerDialog.show();
        });

        updateEvent.setOnClickListener(v -> update(node, id));

        cancel.setOnClickListener(v -> finish());
    }

    private void update(String node, String id) {
        loadingDialog.showLoading();
        String en = eventName.getText().toString();
        String det = details.getText().toString();
        String loc = location.getText().toString();
        String ti = time.getText().toString();
        String da = date.getText().toString();
        String tags = StringManipulation.getTags(det);

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("alumni_app").child("Events").child(node).child(id);
        reference.child("event_name").setValue(en);
        reference.child("event_description").setValue(det);
        reference.child("event_location").setValue(loc);
        reference.child("event_time").setValue(ti);
        reference.child("event_date").setValue(da);
        reference.child("tags").setValue(tags).addOnCompleteListener(task -> {
            loadingDialog.hideLoading();
            TastyToast.makeText(EditEventsActivity.this, "Event Edited", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS);
            finish();
        });
    }
}

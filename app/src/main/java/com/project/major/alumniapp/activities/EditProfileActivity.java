package com.project.major.alumniapp.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.project.major.alumniapp.R;
import com.project.major.alumniapp.models.User;
import com.project.major.alumniapp.utils.FileCompressor;
import com.project.major.alumniapp.utils.LoadingDialog;
import com.sdsmdg.tastytoast.TastyToast;
import com.vikktorn.picker.City;
import com.vikktorn.picker.CityPicker;
import com.vikktorn.picker.Country;
import com.vikktorn.picker.CountryPicker;
import com.vikktorn.picker.OnCityPickerListener;
import com.vikktorn.picker.OnCountryPickerListener;
import com.vikktorn.picker.OnStatePickerListener;
import com.vikktorn.picker.State;
import com.vikktorn.picker.StatePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements OnCountryPickerListener, OnStatePickerListener, OnCityPickerListener {

    CircleImageView profilePic;
    ImageButton plusImg;
    ImageButton ImgBtnDone;
    EditText proName;
    EditText proEmail;
    EditText proPhone;
    TextView countrySpinner;
    TextView stateTv;
    TextView stateSpinner;
    TextView cityTv;
    TextView citySpinner;
    Spinner navodaya;
    Spinner navodhyaSpinner;
    Spinner batchSpinner;
    EditText proProfession;
    EditText proOrganization;

    FirebaseAuth auth;
    FirebaseUser firebaseUser;
    DatabaseReference user_DB;
    StorageReference profilePicReference;

    private CountryPicker countryPicker;
    private StatePicker statePicker;
    private CityPicker cityPicker;
    // arrays of state object
    public static List<State> stateObject;
    // arrays of city object
    public static List<City> cityObject;

    public static int countryID, stateID;

    ArrayAdapter<String> arrayAdapter1;
    ArrayAdapter<String> arrayAdapter2;
    ArrayAdapter arrayAdapter3;

    List<String> l1,l2,l3;
    int pos;
    String nav_name;
    String nav_state;
    String batch;
    String createdat;

    LoadingDialog loadingDialog;

    private static final String IMAGE_DIRECTORY = "/AlumniAPP/Profile/Pics";
    private int GALLERY = 1, CAMERA = 2;

    String imgPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        profilePic = findViewById(R.id.imageview_edit_profile);
        plusImg = findViewById(R.id.plus_img_btn);
        ImgBtnDone = findViewById(R.id.img_btn_done);
        proName = findViewById(R.id.edit_profile_name);
        proEmail = findViewById(R.id.edit_profile_email);
        proPhone = findViewById(R.id.edit_profile_phone);
        countrySpinner = findViewById(R.id.edit_country_spinner);
        stateTv = findViewById(R.id.edit_state_tv);
        stateSpinner = findViewById(R.id.edit_state_spinner);
        cityTv = findViewById(R.id.edit_city_tv);
        citySpinner = findViewById(R.id.edit_city_spinner);
        navodaya = findViewById(R.id.edit_navodaya);
        loadingDialog = new LoadingDialog(EditProfileActivity.this);
        navodhyaSpinner = findViewById(R.id.edit_navodhya_spinner);
        batchSpinner = findViewById(R.id.edit_batch_spinner);
        proProfession = findViewById(R.id.edit_profile_profession);
        proOrganization = findViewById(R.id.edit_profile_organization);
        auth = FirebaseAuth.getInstance();
        firebaseUser = auth.getCurrentUser();
        profilePicReference = FirebaseStorage.getInstance().getReference("alumni_app").child("users");
        user_DB = FirebaseDatabase.getInstance().getReference("alumni_app").child("users").child(firebaseUser.getUid());
        stateObject = new ArrayList<>();
        cityObject = new ArrayList<>();

        proName.setText(firebaseUser.getDisplayName());
        proEmail.setText(firebaseUser.getEmail());
        if (firebaseUser.getPhotoUrl() != null){
            Glide.with(this)
                    .load(firebaseUser.getPhotoUrl())
                    .apply(new RequestOptions().override(150, 150)
                    .placeholder(R.drawable.progress_animation)
                    .error(R.drawable.error_img))
                    .into(profilePic);
        }else {
            profilePic.setImageResource(R.drawable.profle_user);
        }

//        proPhone.setText(firebaseUser.getPhoneNumber());

        user_DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                proPhone.setText(user.getPhone());
                proProfession.setText(user.getProfession());
                proOrganization.setText(user.getOrganization());
                user_DB.child("first").setValue("false");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        try {
            getStateJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            getCityJson();
        } catch (JSONException e) {
            e.printStackTrace();
        }


        countryPicker = new CountryPicker.Builder().with(this).listener(this).build();

        setListener();
        setCountryListener();
        setCityListener();

        l1 = new ArrayList<>();
        l1.add("Andaman and Nicobar Islands");
        l1.add("Andra Pradesh");
        l1.add("Arunachal Pradesh");
        l1.add("Assam");
        l1.add("Bihar");
        l1.add("Chandigarh");
        l1.add("Chhattisgarh");
        l1.add("Dadra and Nagar Haveli");
        l1.add("Daman and Diu");
        l1.add("Delhi");
        l1.add("Goa");
        l1.add("Gujarat");
        l1.add("Haryana");
        l1.add("Himachal Pradesh");
        l1.add("Jammu and Kashmir");
        l1.add("Jharkhand");
        l1.add("Karnatka");
        l1.add("Kerala");
        l1.add("Lakshadweep");
        l1.add("Madhya Pradesh");
        l1.add("Maharashtra");
        l1.add("Manipur");
        l1.add("Meghalaya");
        l1.add("Mizoram");
        l1.add("Nagaland");
        l1.add("Orissa");
        l1.add("Puducherry");
        l1.add("Punjab");
        l1.add("Rajasthan");
        l1.add("Sikkim");
        l1.add("Telangana");
        l1.add("Tripura");
        l1.add("Uttar Pradesh");
        l1.add("Uttrakhand");
        l1.add("West Bengal");

        arrayAdapter1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, l1);
        arrayAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        navodaya.setAdapter(arrayAdapter1);

        navodaya.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                nav_state = l1.get(position);
                pos = position;
                add();
            }

            private void add() {
                switch (pos){
                    case 0:
                        l2 = new ArrayList<>();
                        l2.add("JNV Arong(Nicobar)");
                        l2.add("JNV Panchawati(Middle Andaman)");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 1:
                        l2 = new ArrayList<>();
                        l2.add("JNV Ananthapur");
                        l2.add("JNV Chittor");
                        l2.add("JNV Kadapa");
                        l2.add("JNV East Godavari");
                        l2.add("JNV Guntur");
                        l2.add("JNV Krishna");
                        l2.add("JNV Kurnool");
                        l2.add("JNV Nellore");
                        l2.add("JNV Prakasam-I");
                        l2.add("JNV Prakasam-II");
                        l2.add("JNV Srikakulam");
                        l2.add("JNV Vishakhapatnam");
                        l2.add("JNV Vizianagaram");
                        l2.add("JNV West Godavari");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 2:
                        l2 = new ArrayList<>();
                        l2.add("JNV Anjaw");
                        l2.add("JNV Changlang");
                        l2.add("JNV Dibang Valley");
                        l2.add("JNV East Kameng");
                        l2.add("JNV East Siang");
                        l2.add("JNV Khurung Khumey");
                        l2.add("JNV Lower Subansiri");
                        l2.add("JNV Lohit");
                        l2.add("JNV Papumpare");
                        l2.add("JNV Tawang");
                        l2.add("JNV Tirap");
                        l2.add("JNV Upper Subansiri");
                        l2.add("JNV West Kameng");
                        l2.add("JNV West Siang");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 3:
                        l2 = new ArrayList<>();
                        l2.add("JNV Baksa");
                        l2.add("JNV Barpeta");
                        l2.add("JNV Bongaigaon");
                        l2.add("JNV Cachar");
                        l2.add("JNV Chirang");
                        l2.add("JNV Darrang");
                        l2.add("JNV Dhemaji");
                        l2.add("JNV Dhubri");
                        l2.add("JNV Dibrugarh");
                        l2.add("JNV Goalpara");
                        l2.add("JNV Golaghat");
                        l2.add("JNV Hailakandi");
                        l2.add("JNV Jorhat");
                        l2.add("JNV Kamrup");
                        l2.add("JNV Karbianglong");
                        l2.add("JNV Karimganj");
                        l2.add("JNV Kokrajhar");
                        l2.add("JNV Lakhimpur");
                        l2.add("JNV Morigaon");
                        l2.add("JNV Dima Hasao NC Hilla");
                        l2.add("JNV Nagaon");
                        l2.add("JNV Nalbari");
                        l2.add("JNV Sivsagar");
                        l2.add("JNV Sonitpur");
                        l2.add("JNV Tinsukia");
                        l2.add("JNV Udalguri");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 4:
                        l2 = new ArrayList<>();
                        l2.add("JNV Araria");
                        l2.add("JNV Aurangabad");
                        l2.add("JNV Arwal");
                        l2.add("JNV Banka");
                        l2.add("JNV Begusarai");
                        l2.add("JNV Bhagalpur");
                        l2.add("JNV Bhojpur");
                        l2.add("JNV Buxar");
                        l2.add("JNV Dharbhanga");
                        l2.add("JNV East Champaran");
                        l2.add("JNV Gaya");
                        l2.add("JNV Gopalganj");
                        l2.add("JNV Jamui");
                        l2.add("JNV Jehanabad");
                        l2.add("JNV Kaimur");
                        l2.add("JNV Katihar");
                        l2.add("JNV Kagaria");
                        l2.add("JNV Kishanganj");
                        l2.add("JNV Lakhisarai");
                        l2.add("JNV Madhepura");
                        l2.add("JNV Madhubani");
                        l2.add("JNV Munger");
                        l2.add("JNV Muzaffarpur");
                        l2.add("JNV Nalanda");
                        l2.add("JNV Nawada");
                        l2.add("JNV Patna");
                        l2.add("JNV Purnea");
                        l2.add("JNV Rohtas");
                        l2.add("JNV Saharsa");
                        l2.add("JNV Saran");
                        l2.add("JNV Sheikpura");
                        l2.add("JNV Sheohar");
                        l2.add("JNV Sitamarhi");
                        l2.add("JNV Siwan");
                        l2.add("JNV Supaul");
                        l2.add("JNV Vaishali");
                        l2.add("JNV West Champaran");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 5:
                        l2 = new ArrayList<>();
                        l2.add("JNV Chandigarh");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 6:
                        l2 = new ArrayList<>();
                        l2.add("JNV Bastar");
                        l2.add("JNV Bilaspur");
                        l2.add("JNV Dantewada");
                        l2.add("JNV Dhamtari");
                        l2.add("JNV Durg");
                        l2.add("JNV Janjgir Champa");
                        l2.add("JNV Jashpur");
                        l2.add("JNV Kanker");
                        l2.add("JNV Kibirdham");
                        l2.add("JNV Korba");
                        l2.add("JNV Koria");
                        l2.add("JNV Mahasamund");
                        l2.add("JNV RAIGARH");
                        l2.add("JNV RAIPUR");
                        l2.add("JNV Rajandgaon");
                        l2.add("JNV Sukma");
                        l2.add("JNV Surajpur");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 7:
                        l2 = new ArrayList<>();
                        l2.add("JNV Silvassa");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 8:
                        l2 = new ArrayList<>();
                        l2.add("JNV Daman");
                        l2.add("JNV Diu");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 9:
                        l2 = new ArrayList<>();
                        l2.add("JNV Mungeshpur");
                        l2.add("JNV Jaffarour Kalan");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 10:
                        l2 = new ArrayList<>();
                        l2.add("JNV South Goa");
                        l2.add("JNV North Goa");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 11:
                        l2 = new ArrayList<>();
                        l2.add("JNV Amreli");
                        l2.add("JNV Anand");
                        l2.add("JNV Bharuch");
                        l2.add("JNV Bhavnagar");
                        l2.add("JNV Banaskantha");
                        l2.add("JNV Dahod");
                        l2.add("JNV Dang");
                        l2.add("JNV Gandhinagar");
                        l2.add("JNV Jamnagar");
                        l2.add("JNV Gir Somnath");
                        l2.add("JNV Khedha");
                        l2.add("JNV Kutch");
                        l2.add("JNV Mehasana");
                        l2.add("JNV Navsari");
                        l2.add("JNV Narmada");
                        l2.add("JNV Panchmahal");
                        l2.add("JNV Patan");
                        l2.add("JNV Porbandar");
                        l2.add("JNV Rajkot");
                        l2.add("JNV Arvali");
                        l2.add("JNV Tapi");
                        l2.add("JNV Surednranagar");
                        l2.add("JNV Vadodara");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 12:
                        l2 = new ArrayList<>();
                        l2.add("JNV Ambala");
                        l2.add("JNV Bhiwani");
                        l2.add("JNV Fatehbad");
                        l2.add("JNV Faridabad");
                        l2.add("JNV Gurgaon");
                        l2.add("JNV Hissar");
                        l2.add("JNV Jhajjar");
                        l2.add("JNV Jind");
                        l2.add("JNV Kaithal");
                        l2.add("JNV Karnal");
                        l2.add("JNV Kurukshetra");
                        l2.add("JNV Mewat");
                        l2.add("JNV Mohindergarh");
                        l2.add("JNV Panchkula");
                        l2.add("JNV Panipat");
                        l2.add("JNV Rewari");
                        l2.add("JNV Rohtak");
                        l2.add("JNV Sirsa");
                        l2.add("JNV Sonepat");
                        l2.add("JNV Yamunanagar");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 13:
                        l2 = new ArrayList<>();
                        l2.add("JNV Bilaspur");
                        l2.add("JNV Chamba");
                        l2.add("JNV Hamirpur");
                        l2.add("JNV Kangra");
                        l2.add("JNV Kinnaur");
                        l2.add("JNV Kullu");
                        l2.add("JNV Lahaul Spiti");
                        l2.add("JNV Mandi");
                        l2.add("JNV Shimla");
                        l2.add("JNV Sirmour");
                        l2.add("JNV Solan");
                        l2.add("JNV Una");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 14:
                        l2 = new ArrayList<>();
                        l2.add("JNV Anantnag");
                        l2.add("JNV Baramulla");
                        l2.add("JNV Budgam");
                        l2.add("JNV Doda");
                        l2.add("JNV Ganderbal");
                        l2.add("JNV Jammu");
                        l2.add("JNV Kargil");
                        l2.add("JNV Kathua");
                        l2.add("JNV Kulgam");
                        l2.add("JNV Kupwara");
                        l2.add("JNV Leh");
                        l2.add("JNV Poonch");
                        l2.add("JNV Rajouri");
                        l2.add("JNV Reasi");
                        l2.add("JNV Samba");
                        l2.add("JNV Shopian");
                        l2.add("JNV Udhampur");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 15:
                        l2 = new ArrayList<>();
                        l2.add("JNV Bokaro");
                        l2.add("JNV Chatra");
                        l2.add("JNV Deoghar");
                        l2.add("JNV Dhanbad");
                        l2.add("JNV Dumka");
                        l2.add("JNV East Singhbhum");
                        l2.add("JNV Bhojpur");
                        l2.add("JNV Buxar");
                        l2.add("JNV Dharbhanga");
                        l2.add("JNV East Champaran");
                        l2.add("JNV Garhwa");
                        l2.add("JNV Giridih");
                        l2.add("JNV Godda");
                        l2.add("JNV Gumla");
                        l2.add("JNV Hazaribagh");
                        l2.add("JNV Jamtara");
                        l2.add("JNV Kodarma");
                        l2.add("JNV Latehar");
                        l2.add("JNV Lohardaga");
                        l2.add("JNV Pakur");
                        l2.add("JNV Palamu");
                        l2.add("JNV Ranchi");
                        l2.add("JNV Saheganj");
                        l2.add("JNV Saraikela");
                        l2.add("JNV Simdega");
                        l2.add("JNV West Singhbhum");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 16:
                        l2 = new ArrayList<>();
                        l2.add("JNV BagalKot");
                        l2.add("JNV Banglore Rural");
                        l2.add("JNV Banglore Urban");
                        l2.add("JNV Belgaum");
                        l2.add("JNV Bellary");
                        l2.add("JNV Bidar");
                        l2.add("JNV Bijapur");
                        l2.add("JNV Chamarajnagar");
                        l2.add("JNV Chitradurga");
                        l2.add("JNV Davangere");
                        l2.add("JNV Dharwad");
                        l2.add("JNV Gadag");
                        l2.add("JNV Yadagir");
                        l2.add("JNV Gulbarga");
                        l2.add("JNV Hassan");
                        l2.add("JNV Haveri");
                        l2.add("JNV Kodagu");
                        l2.add("JNV Chikkaballapura");
                        l2.add("JNV Koppal");
                        l2.add("JNV Mandya");
                        l2.add("JNV Mysore");
                        l2.add("JNV Raichur");
                        l2.add("JNV Shimoga");
                        l2.add("JNV South Canara");
                        l2.add("JNV Thumkur");
                        l2.add("JNV Udupi");
                        l2.add("JNV Uttara Kannada");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 17:
                        l2 = new ArrayList<>();
                        l2.add("JNV Alleppey");
                        l2.add("JNV Calicut");
                        l2.add("JNV Kannur");
                        l2.add("JNV Ernakulam");
                        l2.add("JNV Idukki");
                        l2.add("JNV Kasargod");
                        l2.add("JNV Kollam");
                        l2.add("JNV Kottayam");
                        l2.add("JNV Malappuram");
                        l2.add("JNV Palakkad");
                        l2.add("JNV Pathanamthitta");
                        l2.add("JNV Trissur");
                        l2.add("JNV Trivandrum");
                        l2.add("JNV Waynad");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 18:
                        l2 = new ArrayList<>();
                        l2.add("JNV Minicoy");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 19:
                        l2 = new ArrayList<>();
                        l2.add("JNV Alirajpur");
                        l2.add("JNV Anuppur");
                        l2.add("JNV Ashoknagar");
                        l2.add("JNV Badwani");
                        l2.add("JNV Balaghat");
                        l2.add("JNV Betul");
                        l2.add("JNV Bhind");
                        l2.add("JNV Bhopal");
                        l2.add("JNV Burhanpur");
                        l2.add("JNV Chhatarpur");
                        l2.add("JNV Chhindwara");
                        l2.add("JNV Damoh");
                        l2.add("JNV Datia");
                        l2.add("JNV Dewas");
                        l2.add("JNV Dhar");
                        l2.add("JNV Dindori");
                        l2.add("JNV Guna");
                        l2.add("JNV Gwalior");
                        l2.add("JNV Harda");
                        l2.add("JNV Hoshangabad");
                        l2.add("JNV Indore");
                        l2.add("JNV Jabalpur");
                        l2.add("JNV Jhabua");
                        l2.add("JNV Katni");
                        l2.add("JNV Khandwa");
                        l2.add("JNV Khargone");
                        l2.add("JNV Mandla");
                        l2.add("JNV Mandsaur");
                        l2.add("JNV Morena");
                        l2.add("JNV Narsinghpur");
                        l2.add("JNV Neemuch");
                        l2.add("JNV Panna");
                        l2.add("JNV Raisen");
                        l2.add("JNV Rajgarh");
                        l2.add("JNV RATLAM");
                        l2.add("JNV Rewa");
                        l2.add("JNV Sagar");
                        l2.add("Satna");
                        l2.add("Seoni");
                        l2.add("Shahdol");
                        l2.add("Shajapur");
                        l2.add("Sheopur");
                        l2.add("Shivpuri");
                        l2.add("Shivpuri");
                        l2.add("Sidhi");
                        l2.add("Tikamgarh");
                        l2.add("Ujjain-I");
                        l2.add("Ujjain-II");
                        l2.add("Umaria");
                        l2.add("Vidisha");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 20:
                        l2 = new ArrayList<>();
                        l2.add("JNV Ahmednagar");
                        l2.add("JNV Akola");
                        l2.add("JNV Amravati");
                        l2.add("JNV Aurangabad");
                        l2.add("JNV Beed");
                        l2.add("JNV Buldana");
                        l2.add("JNV Chandrapur");
                        l2.add("JNV Dhule");
                        l2.add("JNV Gadchiroli");
                        l2.add("JNV Gondia");
                        l2.add("JNV Hingoli");
                        l2.add("JNV Jalgaon");
                        l2.add("JNV Jalna");
                        l2.add("JNV Kolhapur");
                        l2.add("JNV Latur");
                        l2.add("JNV Nanded");
                        l2.add("JNV Nandurbar");
                        l2.add("JNV Nandurbar-2");
                        l2.add("JNV Nashik");
                        l2.add("JNV Osmanabad");
                        l2.add("JNV Pune");
                        l2.add("JNV Parbhani");
                        l2.add("JNV Raigad");
                        l2.add("JNV Ratnagiri");
                        l2.add("JNV Sangli");
                        l2.add("JNV Satara");
                        l2.add("JNV Sindhudurg");
                        l2.add("JNV Solapur");
                        l2.add("JNV Thane");
                        l2.add("JNV Wardha");
                        l2.add("JNV Washim");
                        l2.add("JNV Yavatmal");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 21:
                        l2 = new ArrayList<>();
                        l2.add("JNV Bishnupur");
                        l2.add("JNV Churachandpur");
                        l2.add("JNV Chandel");
                        l2.add("JNV Imphal East");
                        l2.add("JNV Senapati");
                        l2.add("JNV Tamenglong");
                        l2.add("JNV Thoubal");
                        l2.add("JNV Ukhrul");
                        l2.add("JNV Imphal West");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 22:
                        l2 = new ArrayList<>();
                        l2.add("JNV East Garo Hills");
                        l2.add("JNV East Khasi Hills");
                        l2.add("JNV West Jaintia Hills");
                        l2.add("JNV Ri Bhoi");
                        l2.add("JNV South Garo Hills");
                        l2.add("JNV West Garo Hills");
                        l2.add("JNV West Khasi Hills");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 23:
                        l2 = new ArrayList<>();
                        l2.add("JNV Champhai");
                        l2.add("JNV Kolasib");
                        l2.add("JNV Lawngtlai");
                        l2.add("JNV Mamit");
                        l2.add("JNV Serchip");
                        l2.add("JNV Lunglei");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 24:
                        l2 = new ArrayList<>();
                        l2.add("JNV Dimapur");
                        l2.add("JNV Kiphire");
                        l2.add("JNV Kohima");
                        l2.add("JNV Longleng");
                        l2.add("JNV Mokukchung");
                        l2.add("JNV Mon");
                        l2.add("JNV Peren");
                        l2.add("JNV Phek");
                        l2.add("JNV Via Mokukchung");
                        l2.add("JNV Wokha");
                        l2.add("JNV Zunheboto");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 25:
                        l2 = new ArrayList<>();
                        l2.add("JNV Angul");
                        l2.add("JNV Balasore");
                        l2.add("JNV Bargarh");
                        l2.add("JNV Bhadrak");
                        l2.add("JNV Bolangir");
                        l2.add("JNV Boudh");
                        l2.add("JNV Cuttack");
                        l2.add("JNV Deogarh");
                        l2.add("JNV Dhenkanal");
                        l2.add("JNV GAJAPATI");
                        l2.add("JNV Ganjam");
                        l2.add("JNV Jagatsinghpur");
                        l2.add("JNV Jajpur");
                        l2.add("JNV Jharsuguda");
                        l2.add("JNV Kalahandi");
                        l2.add("JNV Kendrapara");
                        l2.add("JNV Keonjhar");
                        l2.add("JNV Khurda");
                        l2.add("JNV Koraput");
                        l2.add("JNV Malkangiri-I");
                        l2.add("JNV Malkangiri-II");
                        l2.add("JNV Mayurbhanj");
                        l2.add("JNV Navarangpur");
                        l2.add("JNV Nayagarh");
                        l2.add("JNV Nuapada");
                        l2.add("JNV Phulbani");
                        l2.add("JNV Puri");
                        l2.add("JNV Rayagada");
                        l2.add("JNV Sambalpur");
                        l2.add("JNV Sonepur");
                        l2.add("JNV Sundargarh");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 26:
                        l2 = new ArrayList<>();
                        l2.add("JNV Karaikal");
                        l2.add("JNV Mahe");
                        l2.add("JNV Puducherry");
                        l2.add("JNV Yanam");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 27:
                        l2 = new ArrayList<>();
                        l2.add("JNV Amritsar-I");
                        l2.add("JNV Amritsar-II");
                        l2.add("JNV Barnala");
                        l2.add("JNV Bathinda");
                        l2.add("JNV Fatehgarh Sahib");
                        l2.add("JNV Faridkot");
                        l2.add("JNV Ferozepur");
                        l2.add("JNV Hoshiarpur");
                        l2.add("JNV Jallandhar");
                        l2.add("JNV Kapurthala");
                        l2.add("JNV Ludhiana");
                        l2.add("JNV Mansa");
                        l2.add("JNV Moga");
                        l2.add("JNV Mohali");
                        l2.add("JNV Muktsar");
                        l2.add("JNV Pathankot");
                        l2.add("JNV Patiala");
                        l2.add("JNV Ropar");
                        l2.add("JNV S.B.S Nagar");
                        l2.add("JNV Sangrur");
                        l2.add("JNV Taran Taran");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 28:
                        l2 = new ArrayList<>();
                        l2.add("JNV Ajmer");
                        l2.add("JNV Alwar");
                        l2.add("JNV Banswara-I");
                        l2.add("JNV Banswara-II");
                        l2.add("JNV Baran");
                        l2.add("JNV Barmer");
                        l2.add("JNV Bharatpur");
                        l2.add("JNV Bikaner");
                        l2.add("JNV Bundi");
                        l2.add("JNV Chittorgarh");
                        l2.add("JNV Churu");
                        l2.add("JNV Dausa");
                        l2.add("JNV Dholpur");
                        l2.add("JNV Dhungarpur");
                        l2.add("JNV Hanumangarh");
                        l2.add("JNV Jaipur");
                        l2.add("JNV Jaiselmer");
                        l2.add("JNV Jalore");
                        l2.add("JNV Jhalawar");
                        l2.add("JNV Jhunjhunu");
                        l2.add("JNV Jhodpur");
                        l2.add("JNV Karauli");
                        l2.add("JNV Kota");
                        l2.add("JNV Nagaur");
                        l2.add("JNV Pali");
                        l2.add("JNV Rajasmand");
                        l2.add("JNV Sawai Madhopur");
                        l2.add("JNV Sikar");
                        l2.add("JNV Sirohi");
                        l2.add("JNV Sri Ganganagar");
                        l2.add("JNV Tonk");
                        l2.add("JNV Udaipur");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 29:
                        l2 = new ArrayList<>();
                        l2.add("JNV East Sikkim");
                        l2.add("JNV North Sikkim");
                        l2.add("JNV Namchi South Sikkim");
                        l2.add("JNV West Sikkim");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 30:
                        l2 = new ArrayList<>();
                        l2.add("JNV Adilabad");
                        l2.add("JNV Karimnagar");
                        l2.add("JNV Khamman-I");
                        l2.add("JNV Khamman-II");
                        l2.add("JNV Mahabubnagar");
                        l2.add("JNV Medak");
                        l2.add("JNV Nalgonda");
                        l2.add("JNV Nizamabad");
                        l2.add("JNV Rangareddy");
                        l2.add("JNV Warangal");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 31:
                        l2 = new ArrayList<>();
                        l2.add("JNV Dhalai");
                        l2.add("JNV North Tripura");
                        l2.add("JNV Gomati");
                        l2.add("JNV Khowai");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 32:
                        l2 = new ArrayList<>();
                        l2.add("JNV Agra");
                        l2.add("JNV Aligarh");
                        l2.add("JNV Allahabad");
                        l2.add("JNV Amebedkar Nagar");
                        l2.add("JNV Auriya");
                        l2.add("JNV Azamgarh");
                        l2.add("JNV Baduan");
                        l2.add("JNV Bagpat");
                        l2.add("JNV Baharaich");
                        l2.add("JNV Ballia");
                        l2.add("JNV Basti");
                        l2.add("JNV Sant Ravidas Nagar");
                        l2.add("JNV Bijnor");
                        l2.add("JNV Bulandsahar");
                        l2.add("JNV Chandauli");
                        l2.add("JNV Chitrakoot");
                        l2.add("JNV Etah");
                        l2.add("JNV Etawah");
                        l2.add("JNV Faizabad");
                        l2.add("JNV Farrukhabad");
                        l2.add("JNV Fatehpur");
                        l2.add("JNV Firzobad");
                        l2.add("JNV G.B. Nagar");
                        l2.add("JNV Ghazipur");
                        l2.add("JNV Ghaziabad");
                        l2.add("JNV Gonda");
                        l2.add("JNV Gorakhpur");
                        l2.add("JNV Hamirpur");
                        l2.add("JNV Hardoi");
                        l2.add("JNV Hathras");
                        l2.add("JNV J.P. Nagar");
                        l2.add("JNV Jalaun");
                        l2.add("JNV Jhansi");
                        l2.add("JNV Kannauj");
                        l2.add("JNV Kanpur Dehat");
                        l2.add("JNV Kanpur Nagar");
                        l2.add("JNV Kaushambi");
                        l2.add("JNV Kushinagar");
                        l2.add("JNV Lakhimpur Kheri");
                        l2.add("JNV Lalitpur");
                        l2.add("JNV Lucknow");
                        l2.add("JNV Maharajganj");
                        l2.add("JNV Mahoba");
                        l2.add("JNV Mainpuri");
                        l2.add("JNV Mathura");
                        l2.add("JNV Mau");
                        l2.add("JNV Mirzapur");
                        l2.add("JNV Moradabad");
                        l2.add("JNV Muzzaffarnagar");
                        l2.add("JNV Pilibhit");
                        l2.add("JNV Pratapgarh");
                        l2.add("JNV Raebareli");
                        l2.add("JNV Saharanpur");
                        l2.add("JNV Sant Kabir Nagar");
                        l2.add("JNV Shajahapur");
                        l2.add("JNV Siddharth Nagar");
                        l2.add("JNV Sitapur");
                        l2.add("JNV Sonebhadra");
                        l2.add("JNV Sultanpur");
                        l2.add("JNV Unnao");
                        l2.add("JNV Varanasi");
                        l2.add("JNV Shrawasti");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 33:
                        l2 = new ArrayList<>();
                        l2.add("JNV Almora");
                        l2.add("JNV Bageshwar");
                        l2.add("JNV Chamoli");
                        l2.add("JNV Champawat");
                        l2.add("JNV Dehradun");
                        l2.add("JNV Haridwar");
                        l2.add("JNV Nainital");
                        l2.add("JNV Pauri");
                        l2.add("JNV Pithrogarh");
                        l2.add("JNV Rudraprayag");
                        l2.add("JNV Tehri Garhwal");
                        l2.add("JNV U.S. Nagar");
                        l2.add("JNV Uttarkashi");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;

                    case 34:
                        l2 = new ArrayList<>();
                        l2.add("JNV Bankura");
                        l2.add("JNV Birbhum");
                        l2.add("JNV Burdhman");
                        l2.add("JNV Darjeeling");
                        l2.add("JNV Howrah");
                        l2.add("JNV Hooghly");
                        l2.add("JNV Jalpaiguri");
                        l2.add("JNV Coochbehar");
                        l2.add("JNV Murshidabad");
                        l2.add("JNV North 24 Parganas");
                        l2.add("JNV Nadia");
                        l2.add("JNV East Midnapur");
                        l2.add("JNV West Medinapur");
                        l2.add("JNV Purulia");
                        l2.add("JNV U. Dinajpur");
                        l2.add("JNV D. Dinajpur");
                        l2.add("JNV South 24 Parganas");
                        arrayAdapter2 = new ArrayAdapter<>(EditProfileActivity.this, android.R.layout.simple_spinner_item, l2);
                        arrayAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        navodhyaSpinner.setAdapter(arrayAdapter2);
                        select();
                        break;
                }
            }

            private void select() {
                navodhyaSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        nav_name = l2.get(position);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        parent.setSelection(0);
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                parent.setSelection(0);
            }
        });

        l3 = new ArrayList<>();
        for (int i = 1990; i <= 2020; i++) {
            l3.add(String.valueOf(i));
        }
        arrayAdapter3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, l3);
        arrayAdapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        batchSpinner.setAdapter(arrayAdapter3);

        batchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                batch = l3.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        plusImg.setOnClickListener(v -> {
            requestMultiplePermissions();
            showPictureDialog();
        });

        ImgBtnDone.setOnClickListener(v -> {
            loadingDialog.showLoading();
            user_DB.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        if (!user.getUser_name().equals(proName.getText().toString())){
                            user_DB.child("user_name").setValue(proName.getText().toString());
                            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(proName.getText().toString())
                                    .build();
                            user_DB.child("search_name").setValue(proName.getText().toString().toLowerCase());
                        } else if(TextUtils.isEmpty(proName.getText().toString())){
                            proName.setError("Enter Your Name.");
                        }

                        if (!user.getEmail().equals(proEmail.getText().toString())){
                            user_DB.child("email").setValue(proEmail.getText().toString());
                            firebaseUser.updateEmail(proEmail.getText().toString());
                        } else if(TextUtils.isEmpty(proEmail.getText().toString())){
                            proEmail.setError("Enter Your Email.");
                        }

                        if (!user.getPhone().equals(proPhone.getText().toString())){
                            user_DB.child("phone").setValue(proPhone.getText().toString());
//                            AuthCredential credential = PhoneAuthProvider.getCredential();
//                            firebaseUser.updatePhoneNumber(proEmail.getText().toString());
                        } else if(TextUtils.isEmpty(proEmail.getText().toString())){
                            proPhone.setError("Enter Your Phone Number.");
                        }

                        if (!user.getCountry().equals(countrySpinner.getText().toString())){
                            user_DB.child("country").setValue(countrySpinner.getText().toString());
                        } else if(TextUtils.isEmpty(countrySpinner.getText().toString())){
                            countrySpinner.setError("Select Your Country.");
                        }

                        if (!user.getState().equals(stateSpinner.getText().toString())){
                            user_DB.child("state").setValue(stateSpinner.getText().toString());
                        } else if(TextUtils.isEmpty(stateSpinner.getText().toString())){
                            stateSpinner.setError("Select Your State.");
                        }

                        if (!user.getCity().equals(citySpinner.getText().toString())){
                            user_DB.child("city").setValue(citySpinner.getText().toString());
                            user_DB.child("search_city").setValue(citySpinner.getText().toString().toLowerCase());
                        } else if(TextUtils.isEmpty(citySpinner.getText().toString())){
                            citySpinner.setError("Select Your City.");
                        }

                        if (!user.getNav_state().equals(nav_state)){
                            user_DB.child("nav_state").setValue(nav_state);
                        }

                        if (!user.getNavodhya().equals(nav_name)){
                            user_DB.child("navodhya").setValue(nav_name);
                        }

                        if (!user.getBatch().equals(batch)){
                            user_DB.child("batch").setValue(batch);
                        }


                        if (!user.getProfession().equals(proProfession.getText().toString())){
                            user_DB.child("profession").setValue(proProfession.getText().toString());
                            user_DB.child("search_profession").setValue(proProfession.getText().toString().toLowerCase());
                        } else if(TextUtils.isEmpty(proProfession.getText().toString())){
                            proProfession.setError("Enter Your Profession.");
                        }


                        if (!user.getOrganization().equals(proOrganization.getText().toString())){
                            user_DB.child("organization").setValue(proOrganization.getText().toString());
                            user_DB.child("search_organization").setValue(proOrganization.getText().toString().toLowerCase());
                        } else if(TextUtils.isEmpty(proOrganization.getText().toString())){
                            proOrganization.setError("Enter Your Organization.");
                        }
                        TastyToast.makeText(EditProfileActivity.this, "Profile Updated", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
                        loadingDialog.hideLoading();
                        Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        });

        user_DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    countrySpinner.setText(user.getCountry());
                    citySpinner.setText(user.getCity());
                    stateSpinner.setText(user.getState());
                    createdat = user.getCreatedat();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showPictureDialog(){
        AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select Action");
        String[] pictureDialogItems = {
                "Select photo from gallery",
                "Capture photo from camera" };
        pictureDialog.setItems(pictureDialogItems,
                (dialog, which) -> {
                    switch (which) {
                        case 0:
                            choosePhotoFromGallary();
                            break;
                        case 1:
                            takePhotoFromCamera();
                            break;
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }

    private void takePhotoFromCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }

//        if(requestCode==1002){
//            try {
//                uri=data.getData();
//                Bitmap bm= MediaStore.Images.Media.getBitmap(getContentResolver(),data.getData());
//                profile_image.setImageBitmap(bm);
//            } catch (IOException e) {
//                e.printStackTrace();
//                TastyToast.makeText(AddEvent.this, ""+e, TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
//            }
//        }
        if (requestCode == GALLERY) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    TastyToast.makeText(EditProfileActivity.this, "Image Saved!", TastyToast.LENGTH_SHORT,TastyToast.INFO).show();
                    profilePic.setImageBitmap(bitmap);
                    upload(path);

                } catch (IOException e) {
                    e.printStackTrace();
                    TastyToast.makeText(EditProfileActivity.this, "Failed!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }

        } else if (requestCode == CAMERA) {
            Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
            profilePic.setImageBitmap(thumbnail);
            imgPath = saveImage(thumbnail);
            upload(imgPath);
            TastyToast.makeText(EditProfileActivity.this, "Image Saved!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
        }
        //the end  onActivityResult
    }

    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File pic_Dirc = new File(
                Environment.getExternalStorageDirectory() + IMAGE_DIRECTORY);
        // have the object build the directory structure, if needed.
        if (!pic_Dirc.exists()) {
            boolean mkdir = pic_Dirc.mkdirs();
            if (!mkdir){
                Log.d("Edit Profile","mkdir failed");
            }
        }

        try {
            File f = new File(pic_Dirc, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            Log.d("Add Profile", "File Saved::---&gt;" + f.getAbsolutePath());

            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
            return "";
        }
    }

    public void upload(String path)
    {
        Log.d("Alumni app",path);
        StorageReference storageReference;
        FileCompressor compressor = new FileCompressor(getApplicationContext());
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setIndeterminate(false);
        progressDialog.setMax(100);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Uploading....");
        progressDialog.show();
        UploadTask uploadTask;
        StorageMetadata metadata = new StorageMetadata.Builder().setContentType("image/jpeg").build();
        storageReference = profilePicReference.child("alumniapp"+firebaseUser.getDisplayName()+System.currentTimeMillis());
        uploadTask = storageReference.putFile(compressor.imageCompressor(path),metadata);
        uploadTask.addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
            String  url = uri1.toString();
            user_DB.child("user_image").setValue(url);
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setPhotoUri(uri1)
                    .build();
            firebaseUser.updateProfile(userProfileChangeRequest);
        })).addOnFailureListener(e -> {
            progressDialog.dismiss();
            TastyToast.makeText(this, "failed", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
        });
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            int currentprogress = (int) progress;
            progressDialog.setProgress(currentprogress);
        });
    }

    private void setListener() {
        stateSpinner.setOnClickListener(v -> statePicker.showDialog(getSupportFragmentManager()));
    }

    private void setCountryListener() {
        countrySpinner.setOnClickListener(v -> countryPicker.showDialog(getSupportFragmentManager()));
    }

    private void setCityListener() {
        citySpinner.setOnClickListener(v -> cityPicker.showDialog(getSupportFragmentManager()));
    }

    @Override
    public void onSelectCity(City city) {
        citySpinner.setText(city.getCityName());
    }

    @Override
    public void onSelectCountry(Country country) {
        countrySpinner.setText(country.getName());
        countryID = country.getCountryId();
        StatePicker.equalStateObject.clear();
        CityPicker.equalCityObject.clear();

        stateTv.setVisibility(View.VISIBLE);
        stateSpinner.setVisibility(View.VISIBLE);
        stateSpinner.setText("REGION");

        for (int i = 0; i < stateObject.size(); i++) {
            statePicker = new StatePicker.Builder().with(this).listener(this).build();
            State stateData = new State();
            if (stateObject.get(i).getCountryId() == countryID) {
                stateData.setStateId(stateObject.get(i).getStateId());
                stateData.setStateName(stateObject.get(i).getStateName());
                stateData.setCountryId(stateObject.get(i).getCountryId());
                stateData.setFlag(country.getFlag());
                StatePicker.equalStateObject.add(stateData);
            }
        }
    }

    @Override
    public void onSelectState(State state) {
        cityTv.setVisibility(View.VISIBLE);
        citySpinner.setVisibility(View.VISIBLE);
        citySpinner.setText("City");

        stateSpinner.setText(state.getStateName());
        stateID = state.getStateId();

        for (int i = 0; i < cityObject.size(); i++) {
            cityPicker = new CityPicker.Builder().with(this).listener(this).build();
            City cityData = new City();
            if (cityObject.get(i).getStateId() == stateID) {
                cityData.setCityId(cityObject.get(i).getCityId());
                cityData.setCityName(cityObject.get(i).getCityName());
                cityData.setStateId(cityObject.get(i).getStateId());
                CityPicker.equalCityObject.add(cityData);
            }
        }
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    public void getStateJson() throws JSONException {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("states.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);
        } catch (IOException e){
            e.printStackTrace();
        }

        JSONObject jsonObject = new JSONObject(json);
        JSONArray events = jsonObject.getJSONArray("states");
        for (int j = 0; j < events.length(); j++) {
            JSONObject cit = events.getJSONObject(j);
            State stateData = new State();

            stateData.setStateId(Integer.parseInt(cit.getString("id")));
            stateData.setStateName(cit.getString("name"));
            stateData.setCountryId(Integer.parseInt(cit.getString("country_id")));
            stateObject.add(stateData);
        }
    }

    public void getCityJson() throws JSONException {
        String json = null;
        try {
            InputStream inputStream = getAssets().open("cities.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            json = new String(buffer, StandardCharsets.UTF_8);

        } catch (IOException e) {
            e.printStackTrace();
        }


        JSONObject jsonObject = new JSONObject(json);
        JSONArray events = jsonObject.getJSONArray("cities");
        for (int j = 0; j < events.length(); j++) {
            JSONObject cit = events.getJSONObject(j);
            City cityData = new City();

            cityData.setCityId(Integer.parseInt(cit.getString("id")));
            cityData.setCityName(cit.getString("name"));
            cityData.setStateId(Integer.parseInt(cit.getString("state_id")));
            cityObject.add(cityData);
        }
    }

    private void  requestMultiplePermissions(){
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        // check if all permissions are granted
                        if (report.areAllPermissionsGranted()) {
                            TastyToast.makeText(getApplicationContext(), "All permissions are granted by user!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
                        }

                        // check for permanent denial of any permission
                        if (report.isAnyPermissionPermanentlyDenied()) {
                            // show alert dialog navigating to Settings
                            startActivityForResult(new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData(Uri.parse("package:"+getPackageName())),0);
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).
                withErrorListener(error -> TastyToast.makeText(getApplicationContext(), "Some Error! ", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show())
                .onSameThread()
                .check();
    }


}

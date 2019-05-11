package com.moshenskyi.bullsheepandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.moshenskyi.bullsheepandroid.pref.UserPrefManager;

public class PetNameActivity extends AppCompatActivity {


    private Button submitBn;
    private EditText petNameEt;

    private TextView googleFitTv;
    private TextView audioTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_name);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        submitBn = findViewById(R.id.loginScreenSubmitBn);
        petNameEt = findViewById(R.id.loginScreenPetNameEt);


        googleFitTv = findViewById(R.id.googleFitTv);
        audioTv = findViewById(R.id.audioTv);

        submitBn.setOnClickListener(v -> {
            String petName = petNameEt.getText().toString();

            UserPrefManager.getInstance().writeHealthPoint(1000);
            UserPrefManager.getInstance().writeMoodPoint(10);

            UserPrefManager.getInstance().writeName(petName);
            Intent intent = new Intent(PetNameActivity.this, MainActivity.class);
            PetNameActivity.this.startActivity(intent);


            Log.i("PET NAME", UserPrefManager.getInstance().getName());
            Log.i("PET HEALTH", String.valueOf(UserPrefManager.getInstance().getHealthPoint()));
            Log.i("PET MOOD", String.valueOf(UserPrefManager.getInstance().getMoodPoint()));
        });


        initBottomSheet();
    }




    private void initBottomSheet() {
        LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
      //  bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
       // bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

// set the peek height
       // bottomSheetBehavior.setPeekHeight(340);

// set hideable or not
        bottomSheetBehavior.setHideable(false);

// set callback for changes

        bottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

            }
        });


        googleFitTv.setOnClickListener(v -> {

        });

        audioTv.setOnClickListener(v -> {

        });
    }


}

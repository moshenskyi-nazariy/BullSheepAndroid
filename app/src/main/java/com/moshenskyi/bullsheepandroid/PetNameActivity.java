package com.moshenskyi.bullsheepandroid;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.moshenskyi.bullsheepandroid.fit.GoogleFitUtil;
import com.moshenskyi.bullsheepandroid.pref.UserPrefManager;

public class PetNameActivity extends AppCompatActivity {

    private static final int REQUEST_OAUTH_REQUEST_CODE = 0x1001;

    private Button submitBn;
    private EditText petNameEt;


    private GoogleFitUtil googleFitUtil;
    private UserPrefManager userPrefManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pet_name);
        userPrefManager = new UserPrefManager();
        googleFitUtil = new GoogleFitUtil(this);
        initGoogleFit();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        submitBn = findViewById(R.id.loginScreenSubmitBn);
        petNameEt = findViewById(R.id.loginScreenPetNameEt);
        submitBn.setOnClickListener(v -> {
            readFitData();
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
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_OAUTH_REQUEST_CODE) {
                googleFitUtil.subscribe();
            }
        }
    }

    private void initGoogleFit() {
        FitnessOptions fitnessOptions =
                FitnessOptions.builder()
                        .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE)
                        .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
                        .addDataType(DataType.TYPE_HEART_POINTS)
                        .addDataType(DataType.TYPE_HEART_RATE_BPM)
                        .build();
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this,
                    REQUEST_OAUTH_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            googleFitUtil.subscribe();
            readFitData();
        }
    }

    private void readFitData() {
        googleFitUtil.readData(dataSet -> {
            int total =
                    dataSet.isEmpty()
                            ? 1
                            : dataSet.getDataPoints().get(0).getValue(Field.FIELD_DURATION).asInt();
            userPrefManager.writeFitHealthPoint(total);
            Toast.makeText(this, String.valueOf(total), Toast.LENGTH_SHORT).show();
        });
    }
}

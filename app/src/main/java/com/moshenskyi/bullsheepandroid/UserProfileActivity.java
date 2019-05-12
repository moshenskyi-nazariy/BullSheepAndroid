package com.moshenskyi.bullsheepandroid;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.moshenskyi.bullsheepandroid.adapter.AnimalsAdapter;

import java.util.Arrays;
import java.util.List;

import static com.moshenskyi.bullsheepandroid.App.getContext;

public class UserProfileActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private EditText parentMailEt;
    private ImageView usersPhoto;
    private TextView currentScoreTv;
    private TextView priceScoreTv;

    private int currentPosition = 0;

    private RecyclerView.Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        initUi();
        initAdapter();
        initRecycler();
    }

    private void initUi() {
        recyclerView = findViewById(R.id.animals_recycler);
        parentMailEt = findViewById(R.id.parent_et);
        usersPhoto = findViewById(R.id.user_image);
        currentScoreTv = findViewById(R.id.score_tv);
        priceScoreTv = findViewById(R.id.price_score_tv);
        usersPhoto.setImageResource(R.drawable.profile_image);


        findViewById(R.id.arrow_left).setOnClickListener(view -> {
            currentPosition = --currentPosition;
            if (currentPosition >= 0) {
                Log.i("SSS", "" + currentPosition);
                recyclerView.getLayoutManager().scrollToPosition(currentPosition);
            } else {
                currentPosition = 2;
                recyclerView.getLayoutManager().scrollToPosition(2);
                Log.i("SSS", "" + currentPosition);
            }

            if (currentPosition == 0) {
                hidePrice();
            } else {
                showPrice();
            }
        });
        findViewById(R.id.arrow_right).setOnClickListener(view -> {
            currentPosition = ++currentPosition;
            if (currentPosition <= 2) {
                recyclerView.getLayoutManager().scrollToPosition(currentPosition);
                Log.i("SSS", "" + currentPosition);
            } else {
                currentPosition = 0;
                recyclerView.getLayoutManager().scrollToPosition(currentPosition);
                Log.i("SSS", "" + currentPosition);
            }
            if (currentPosition == 0) {
                hidePrice();
            } else {
                showPrice();
            }
            priceScoreTv.setText(String.valueOf(currentPosition * 123 + 21));
        });
    }

    private void initAdapter() {
        List<Integer> imageIds = Arrays.asList(R.drawable.cartoon_first,
                R.drawable.cartoon_second,
                R.drawable.cartoon_third);
        adapter = new AnimalsAdapter(imageIds);
    }

    private void initRecycler() {
        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        recyclerView.setLayoutManager(new CustomLinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);
//        recyclerView.getLayoutManager().scrollToPosition();
    }

    private void showPrice() {
        findViewById(R.id.price_score_container).setVisibility(View.VISIBLE);
    }

    private void hidePrice() {
        findViewById(R.id.price_score_container).setVisibility(View.INVISIBLE);
    }
}

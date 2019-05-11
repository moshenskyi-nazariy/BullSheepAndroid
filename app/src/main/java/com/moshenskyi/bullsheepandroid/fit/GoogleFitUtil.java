package com.moshenskyi.bullsheepandroid.fit;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnSuccessListener;

public class GoogleFitUtil {

    private Context context;

    public GoogleFitUtil(Context context) {
        this.context = context;
    }

    public void subscribe() {
        // To create a subscription, invoke the Recording API. As soon as the subscription is
        // active, fitness data will start recording.
        Fitness.getRecordingClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .subscribe(DataType.TYPE_HEART_POINTS)
                .addOnCompleteListener(
                        task -> {
                            if (task.isSuccessful()) {
                                Log.i("GOOGLE","Successfully subscribed!");
                            } else {
                                Log.i("GOOGLE","There was a problem subscribing." + task.getException());
                            }
                        });
    }

    public void readData(OnSuccessListener<DataSet> successListener) {
        Fitness.getHistoryClient(context, GoogleSignIn.getLastSignedInAccount(context))
                .readDailyTotal(DataType.TYPE_HEART_POINTS)
                .addOnSuccessListener(successListener);
    }
}

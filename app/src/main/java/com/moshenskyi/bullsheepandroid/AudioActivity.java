package com.moshenskyi.bullsheepandroid;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.google.ar.core.Anchor;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.reactivex.subjects.Subject;

public class AudioActivity extends AppCompatActivity {

    private static final int VOLUME_MAX = 30;
    private ArFragment arFragment;

    private ModelRenderable modelRenderable;

    private ImageView playBtn;
    private ImageView pauseBtn;
    private SeekBar volumeSeekBar;
    private TextView volumeValue;

    private MediaPlayer player;

    private int items = 0;

    private AudioManager audioManager;

    private Subject<MediaPlayer> musicSubject;
    private Observer<MediaPlayer> observer = new Observer<MediaPlayer>() {
        @Override
        public void onSubscribe(Disposable d) {
            player.setLooping(true); // Set looping
            player.setVolume(VOLUME_MAX, VOLUME_MAX);
            player.start();
        }

        @Override
        public void onNext(MediaPlayer mediaPlayer) {

        }

        @Override
        public void onError(Throwable e) {

        }

        @Override
        public void onComplete() {
            player.pause();
        }
    };
    private SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar
            .OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
            volumeValue.setText("Volume: ");
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        audioManager = (AudioManager) getApplicationContext().getSystemService(Context.AUDIO_SERVICE);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment_audio);

        player = MediaPlayer.create(AudioActivity.this, R.raw.tsawyer);

        musicSubject = PublishSubject.create();

        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if (items++ < 1) {
                placeObject(hitResult.createAnchor());
            }
        });


    }

    private void placeObject(Anchor anchor) {
        ModelRenderable.builder()
                .setSource(this, Uri.parse("gramophone.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    if (AudioActivity.this.modelRenderable == null) {
                        AudioActivity.this.modelRenderable = modelRenderable;
                    } else {
                        AudioActivity.this.modelRenderable = modelRenderable;
                    }
                    placeNodes(anchor, modelRenderable);
                });
    }

    private void placeNodes(Anchor anchor, ModelRenderable renderable) {
        AnchorNode anchorNode = new AnchorNode(anchor);
        anchorNode.setParent(arFragment.getArSceneView().getScene());
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        node.setParent(anchorNode);
        node.setRenderable(renderable);
        node.setLocalRotation(new Quaternion(new Vector3(1f, 0, 0), 0f));

        setMenu(anchorNode, node);
    }

    private void setMenu(AnchorNode anchorNode, TransformableNode transformableNode) {
        ViewRenderable.builder()
                .setView(this, R.layout.music_control)
                .build()
                .thenAccept(viewRenderable -> {
                    playBtn = viewRenderable.getView().findViewById(R.id.play_btn);
                    pauseBtn = viewRenderable.getView().findViewById(R.id.pause_btn);
                    volumeValue = viewRenderable.getView().findViewById(R.id.volume_value);
                    volumeValue.setText("Volume: ");
                    volumeSeekBar = viewRenderable.getView().findViewById(R.id.volume_controls);
                    setupVolumeControl();
                    initClickListeners();

                    TransformableNode musicControlView = new TransformableNode(arFragment
                            .getTransformationSystem());
                    musicControlView.setLocalPosition(new Vector3(0f, transformableNode
                            .getLocalPosition().y + 1f, 0f));
                    musicControlView.setParent(anchorNode);
                    musicControlView.setRenderable(viewRenderable);
                });
    }

    private void setupVolumeControl() {
        volumeSeekBar.getProgressDrawable().setColorFilter(Color.LTGRAY, PorterDuff.Mode.SRC_IN);
        volumeSeekBar.setPadding(10, 0, 10, 0);

        volumeSeekBar.setMax(VOLUME_MAX);
        volumeSeekBar.setOnSeekBarChangeListener(seekBarChangeListener);
        int streamVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volumeSeekBar.setProgress(streamVolume);
    }

    private void subscribe() {
        musicSubject
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void initClickListeners() {
        playBtn.setOnClickListener(view -> {
            playBtn.setVisibility(View.GONE);
            pauseBtn.setVisibility(View.VISIBLE);
            subscribe();
            musicSubject.onNext(player);
        });

        pauseBtn.setOnClickListener(view -> {
            playBtn.setVisibility(View.VISIBLE);
            pauseBtn.setVisibility(View.GONE);
            observer.onComplete();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        observer.onComplete();
    }

}

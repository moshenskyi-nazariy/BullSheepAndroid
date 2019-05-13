package com.moshenskyi.bullsheepandroid;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;
import com.moshenskyi.bullsheepandroid.pref.UserPrefManager;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private ArFragment arFragment;

    private int items = 0;

    private ModelRenderable modelRenderable;

    private TextView findSurfaceTv;

    private ModelAnimator animator;
    private int nextAnimation;

    private LinearLayout llBottomSheet;
//    private BottomSheetBehavior bottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findSurfaceTv = findViewById(R.id.find_surface);

        arFragment = (ArFragment) getSupportFragmentManager().findFragmentById(R.id.ar_fragment);

        // TODO: If more than 1 item should be placed - remove this
        arFragment.setOnTapArPlaneListener((hitResult, plane, motionEvent) -> {
            if (items++ < 1) {

                // hitResult.createAnchor() - method for creating Anchor
                // in place where user tapped
                placeObject(hitResult.createAnchor());
                findSurfaceTv.setVisibility(View.GONE);
                findViewById(R.id.statistics).setVisibility(View.VISIBLE);
                findViewById(R.id.info).setVisibility(View.VISIBLE);
            }
        });

        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            com.google.ar.core.Camera camera = arFragment.getArSceneView().getArFrame().getCamera();
            if (camera.getTrackingState() == TrackingState.TRACKING) {
                arFragment.getPlaneDiscoveryController().hide();
                findSurfaceTv.setText("Tap to place");
            }
//            setPlaneTexture("frame.png");
        });

        TextView moodPoints = findViewById(R.id.statisticMoodPointTv);
        moodPoints.setText(String.valueOf(UserPrefManager.getInstance().getMoodPoint()));

        initBottomSheet();
    }

    private void initBottomSheet() {
        llBottomSheet = findViewById(R.id.bottom_sheet);

        llBottomSheet.setVisibility(View.GONE);

        TextView audioTv = findViewById(R.id.audioTv);
        audioTv.setOnClickListener(v -> {
            items = 0;
            startActivity(new Intent(MainActivity.this, AudioListActivity.class));
        });

        TextView googleTv = findViewById(R.id.googleFitTv);
        googleTv.setOnClickListener(v-> {
            startActivity(new Intent(MainActivity.this, UserProfileActivity.class));
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView moodPoints = findViewById(R.id.statisticMoodPointTv);
        moodPoints.setText(String.valueOf(UserPrefManager.getInstance().getMoodPoint()));
    }

    /**
     * Creates object
     *
     * @param anchor - object, which stores coordinates
     */
    private void placeObject(Anchor anchor) {
        ModelRenderable.builder()
                // sets model from assets
                .setSource(this, Uri.parse("andy_dance.sfb"))
                .build()
                .thenAccept(modelRenderable -> {
                    MainActivity.this.modelRenderable = modelRenderable;

                    placeNodes(anchor, modelRenderable);
                    onPlayAnimation(null);
                });
    }

    private void onPlayAnimation(View unusedView) {
        if (animator == null || !animator.isRunning()) {
            AnimationData data = modelRenderable.getAnimationData(nextAnimation);
            nextAnimation = (nextAnimation + 1) % modelRenderable.getAnimationDataCount();
            animator = new ModelAnimator(data, modelRenderable);
            animator.start();
        }
    }


    /**
     * Places object where user has tapped
     *
     * @param anchor     - object, which stores coordinates
     * @param renderable - our 3d-model
     */
    private void placeNodes(Anchor anchor, ModelRenderable renderable) {
        // Creates node(object which stores our 3d-model), which is attached to coords where user
        // has tapped
        AnchorNode anchorNode = new AnchorNode(anchor);
        // Attach node to fragment
        anchorNode.setParent(arFragment.getArSceneView().getScene());

        // Creates node, which can be resized, moved, rotated
        TransformableNode node = new TransformableNode(arFragment.getTransformationSystem());
        // Wraps anchor node
        node.setParent(anchorNode);
        // Sets view(our 3d-model)
        node.setRenderable(renderable);
        // Some bullshit for rotating the model
        node.setLocalRotation(new Quaternion(new Vector3(1f, 0, 0), 0f));

        setMenu(anchorNode, node);
    }

    /**
     * Shows menu above 3d view({@link com.google.ar.sceneform.rendering.ModelRenderable})
     */
    private void setMenu(AnchorNode anchorNode, TransformableNode transformableNode) {
        // ViewRenderable is used for representing Android views as 2d objects in Sceneform
        ViewRenderable.builder()
                .setView(this, R.layout.some_layout)
                .build()
                .thenAccept(viewRenderable -> {
                    // volumeSeekBar = viewRenderable.getView().findViewById(R.id.volume_controls);
                    // initClickListeners();

                    // Adds ability to rotate, move and resize the node.
                    TransformableNode childNode = new TransformableNode(arFragment
                            .getTransformationSystem());
                    // Appears above higher than ModelRenderable by 0,8f on y-axis.
                    childNode.setLocalPosition(new Vector3(0f, transformableNode
                            .getLocalPosition().y + 0.8f, 0f));
                    // Attaches this ViewRenderable to anchor node, which contains some coordinates.
                    // So, after setting a local position for a node - we set it relatively to
                    // this anchor node.
                    childNode.setParent(anchorNode);
                    // We set here a renderable for our node. It is a ready to use layout, which was
                    // inflated is setView few lines above.
                    childNode.setRenderable(viewRenderable);

                    Observable.timer(3, TimeUnit.SECONDS)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(result -> transformableNode.removeChild(transformableNode),
                                    error -> {
                                    },
                                    () -> {
                                        childNode.setParent(null);
                                        showTasks(anchorNode, transformableNode);
                                    });
                });
    }

    private void showTasks(AnchorNode anchorNode, TransformableNode transformableNode) {
        Node childNode = new Node();
        childNode.setParent(anchorNode);
        childNode.setLocalPosition(new Vector3(0f, transformableNode
                .getLocalPosition().y + 0.8f, 0f));
        ViewRenderable.builder()
                .setView(this, R.layout.info_layout)
                .build()
                .thenAccept(modelRenderable -> {
                    childNode.setRenderable(modelRenderable);
                    ImageView reportView = modelRenderable.getView().findViewById(R.id.report_view);
                    reportView.setOnClickListener(v -> {
                        if (llBottomSheet.getVisibility() == View.GONE) {
                            initBottomSheet();
                            llBottomSheet.setVisibility(View.VISIBLE);
                            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(llBottomSheet,
                                    "alpha", 0, 1);
                            objectAnimator.setDuration(1200);
                            objectAnimator.setInterpolator(new DecelerateInterpolator());
                            objectAnimator.start();
                        } else {
                            ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(llBottomSheet,
                                    "alpha", 1, 0);
                            objectAnimator.setDuration(1200);
                            objectAnimator.setInterpolator(new DecelerateInterpolator());
                            objectAnimator.start();
                            llBottomSheet.setVisibility(View.GONE);
                        }

                    });
                }).exceptionally(throwable -> {
                    Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
                    return null;
                });
    }
}

package com.moshenskyi.bullsheepandroid;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.TrackingState;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.animation.ModelAnimator;
import com.google.ar.sceneform.math.Quaternion;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.AnimationData;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.function.Function;

public class MainActivity extends AppCompatActivity {
    private ArFragment arFragment;

    private int items = 0;

    private ModelRenderable modelRenderable;

    private TextView findSurfaceTv;

    private ModelAnimator animator;
    private int nextAnimation;

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
            }
        });
        arFragment.getArSceneView().getScene().addOnUpdateListener(frameTime -> {
            com.google.ar.core.Camera camera = arFragment.getArSceneView().getArFrame().getCamera();
            if (camera.getTrackingState() == TrackingState.TRACKING) {
                arFragment.getPlaneDiscoveryController().hide();
                findSurfaceTv.setText("Tap to place");
            }
        });
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
                    TransformableNode musicControlView = new TransformableNode(arFragment
                            .getTransformationSystem());
                    // Appears above higher than ModelRenderable by 0,8f on y-axis.
                    musicControlView.setLocalPosition(new Vector3(0f, transformableNode
                            .getLocalPosition().y + 0.8f, 0f));
                    // Attaches this ViewRenderable to anchor node, which contains some coordinates.
                    // So, after setting a local position for a node - we set it relatively to
                    // this anchor node.
                    musicControlView.setParent(anchorNode);
                    // We set here a renderable for our node. It is a ready to use layout, which was
                    // inflated is setView few lines above.
                    musicControlView.setRenderable(viewRenderable);
                });
    }
}

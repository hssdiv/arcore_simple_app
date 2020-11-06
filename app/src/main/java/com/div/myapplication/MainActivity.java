package com.div.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.ar.core.Anchor;
import com.google.ar.core.HitResult;
import com.google.ar.core.Plane;
import com.google.ar.sceneform.AnchorNode;
import com.google.ar.sceneform.Node;
import com.google.ar.sceneform.math.Vector3;
import com.google.ar.sceneform.rendering.ModelRenderable;
import com.google.ar.sceneform.rendering.ViewRenderable;
import com.google.ar.sceneform.ux.ArFragment;
import com.google.ar.sceneform.ux.BaseArFragment;
import com.google.ar.sceneform.ux.TransformableNode;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {
    ArFragment arFragment;

    int ANIMAL_AMOUNT = 12;
    ModelRenderable[] renderableArray = new ModelRenderable[ANIMAL_AMOUNT];
    int[] rawModelsArray;

    View[] iconsArray;
    ImageView bear,cat,cow,dog,elephant,ferret,hippopotamus,horse,koala,lion,reindeer,wolverine;
    int selected = 0;

    String[] namesArray;
    HashMap<Integer, ViewRenderable> namesViewArray = new HashMap<>();
    int currentViewTag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        arFragment = (ArFragment)getSupportFragmentManager()
                .findFragmentById(R.id.sceneform_ux_fragment);

        bear = findViewById(R.id.bear);
        cat = findViewById(R.id.cat);
        cow = findViewById(R.id.cow);
        dog = findViewById(R.id.dog);
        elephant = findViewById(R.id.elephant);
        ferret = findViewById(R.id.ferret);
        hippopotamus = findViewById(R.id.hippopotamus);
        horse = findViewById(R.id.horse);
        koala = findViewById(R.id.koala);
        reindeer = findViewById(R.id.reindeer);
        lion = findViewById(R.id.lion);
        wolverine = findViewById(R.id.wolverine);

        setArrayView();
        setOnClickListeners();
        showBackgroundOnSelectedModel();
        setupModel();


        arFragment.setOnTapArPlaneListener(new BaseArFragment.OnTapArPlaneListener() {
            @Override
            public void onTapPlane(HitResult hitResult, Plane plane, MotionEvent motionEvent) {
                Anchor anchor = hitResult.createAnchor();
                AnchorNode anchorNode = new AnchorNode(anchor);
                anchorNode.setParent(arFragment.getArSceneView().getScene());
                setupViewModel(anchorNode);
            }
        });
    }

    private void setupModel() {
        ModelRenderable.builder()
                .setSource(this, rawModelsArray[selected])
                .build().thenAccept(renderable -> renderableArray[selected] = renderable)
                .exceptionally(throwable -> {
                    Toast.makeText(this, "unable to load bear model", Toast.LENGTH_SHORT)
                    .show();
                    return null;
                });
    }

    private void setupViewModel(AnchorNode anchorNode) {
        ViewRenderable.builder()
                .setView(this, R.layout.animal_name_layout)
                .build()
                .thenAccept(renderable -> {
                    namesViewArray.put(currentViewTag, renderable);
                    createModel(anchorNode);
                });
    }


    private void createModel(AnchorNode anchorNode) {
        TransformableNode modelNode = new TransformableNode(arFragment.getTransformationSystem());
        modelNode.getScaleController().setMaxScale(13f);
        modelNode.getScaleController().setMinScale(5f);
        modelNode.setParent(anchorNode);
        modelNode.setRenderable(renderableArray[selected]);
        modelNode.select();

        Node nameNode = new Node();
        nameNode.setParent(modelNode);
        nameNode.setLocalPosition(new Vector3(0f, 0.12f,0));
        nameNode.setLocalScale(new Vector3(0.3f, 0.3f, 0.3f));
        nameNode.setRenderable(namesViewArray.get(currentViewTag));

        ViewRenderable nameRenderable = namesViewArray.get(currentViewTag);
        if (nameRenderable == null) {
            Log.e("error", "nameRenderable is null");
            return;
        }

        LinearLayout nameLayout = (LinearLayout)nameRenderable.getView();
        TextView nameTextView = nameLayout.findViewById(R.id.animalName);
        nameTextView.setText(namesArray[selected]);
        currentViewTag++;
        nameLayout.setOnClickListener(view -> anchorNode.setParent(null));
    }

    public void setArrayView() {
        iconsArray = new View[]{bear,cat,cow,dog,elephant,ferret,hippopotamus,horse,koala,
                lion,reindeer,wolverine};

        for (int i=0; i < 12; i++){
            iconsArray[i].setTag(i);
        }

        rawModelsArray = new int[]{R.raw.bear, R.raw.cat, R.raw.cow, R.raw.dog, R.raw.elephant,
                R.raw.ferret, R.raw.hippopotamus, R.raw.horse, R.raw.koala_bear, R.raw.lion,
                R.raw.reindeer, R.raw.wolverine};

        namesArray = new String[]{"bear","cat","cow","dog","elephant","ferret","hippopotamus",
                "horse","koala","lion","reindeer","wolverine"};
    }

    private void setOnClickListeners() {
        for (View view : iconsArray) {
            view.setOnClickListener(view1 -> {
                selected = Integer.parseInt(view1.getTag().toString());
                showBackgroundOnSelectedModel();
                setupModel();
            });
        }
    }

    private void showBackgroundOnSelectedModel() {
        for (View view: iconsArray
             ) {
            if (Integer.parseInt(view.getTag().toString()) == selected) {
                view.setBackgroundColor(Color.parseColor("#80333639"));
            } else {
                view.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }
}
package com.project.bottomup.upsa;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class EditActivity extends AppCompatActivity {

    private static final String TAG = "EditActivity";
    String placeName, placeBuilding, placeTel, extraInfo;
    boolean[] toilet = new boolean[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // DocumentActivity에서 넘겨준 값 받기
        Intent intent = getIntent();
        try {
            if (intent == null) {
                throw new Exception();
            }
            placeName = intent.getStringExtra("placeName");
            placeBuilding = intent.getStringExtra("placeBuilding");
            placeTel = intent.getStringExtra("placeTel");
            extraInfo = intent.getStringExtra("extraInfo");
            toilet = intent.getBooleanArrayExtra("toilet");
            Log.i(TAG,"placeName: "+placeName+" placeBuilding: "+placeBuilding+" placeTel "+placeTel+" extraInfo: "+extraInfo);
            Log.i(TAG,"toilet: "+toilet[0]+" "+toilet[1]+" "+toilet[2]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

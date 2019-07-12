package com.example.fruitmanager.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.fruitmanager.R;
import com.example.fruitmanager.models.Fruit;
import com.example.fruitmanager.services.interfaces.FruitServiceInterface;
import com.example.fruitmanager.services.mocks.FruitService;

public class FruitDetails extends AppCompatActivity {

    FruitServiceInterface fruitService = FruitService.getInstance();
    private int fruitId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fruit_details);

        Intent intent = getIntent();
        if (intent == null) finish();
        fruitId = intent.getIntExtra("fruitId", -1);
        populateView();
    }

    @Override
    public void onResume() {
        super.onResume();
        populateView();
    }

    private void populateView() {
        if (fruitId == -1) finish();
        else {
            Fruit fruit = fruitService.getFruit(fruitId);
            if (fruit == null) finish();
            else {
                setTextViewsText(fruit);
            }
        }
    }

    private void setTextViewsText (Fruit fruit) {
        TextView fruitId = findViewById(R.id.tvFruitId);
        TextView fruitName = findViewById(R.id.tvFruitName);
        TextView fruitDescription = findViewById(R.id.tvFruitDescription);

        fruitId.setText(String.format("#%d", fruit.id));
        fruitName.setText(fruit.name);
        fruitDescription.setText(fruit.description);
    }

    public void onDeleteFruitClick(View view) {
        fruitService.deleteFruit(fruitId);
        finish();
    }

    public void onUpdateFruitClick(View view) {
        Intent intent = new Intent(this, FruitUpdate.class);
        intent.putExtra("fruitId", fruitId);

        startActivity(intent);
    }
}

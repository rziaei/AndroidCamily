package edu.murraystate.androidcamilydashboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import edu.murraystate.androidcamilydashboard.R;

public class activity_kitchen extends AppCompatActivity {

    LinearLayout rellay_Calories;
    LinearLayout rellay_GroseryList;
    LinearLayout rellay_FavoriteRestaurant;
    LinearLayout rellay_Cooking;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kitchen);

        rellay_Calories = findViewById(R.id.rellay_Calories);
        rellay_GroseryList = findViewById(R.id.rellay_GroceryList);
        rellay_FavoriteRestaurant = findViewById(R.id.rellay_FavouriteResturants);
        rellay_Cooking = findViewById(R.id.rellay_Cooking);

        rellay_Calories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_kitchen.this, SubActivityKitchenCalories.class);
                startActivity(intent);
            }
        });

        rellay_GroseryList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_kitchen.this, SubActivityKitchenGrocery.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        rellay_FavoriteRestaurant.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_kitchen.this, SubActivityFavoriteRestaurant.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

        rellay_Cooking.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity_kitchen.this, SubActivityFamilyRecipes.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
            }
        });

    }
}
package edu.murraystate.androidcamilydashboard.activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.murraystate.androidcamilydashboard.adapters.CaloriesData;
import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.utils.DateUtil;
import edu.murraystate.androidcamilydashboard.utils.SQLiteHelper;

public class SubActivityKitchenCaloriesEdit extends AppCompatActivity {

    private final String FORMAT_YYYYMMdd = "yyyyMMdd";

    private TextView tvTitle;
    private ImageView btnCancel;
    private ImageView btnSave;
    private EditText etFoodName;
    private EditText etCalorie;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_kitchen_calories_edit);

        tvTitle = findViewById(R.id.tv_title);
        etFoodName = findViewById(R.id.et_food_name);
        etCalorie = findViewById(R.id.et_calorie);
        btnCancel = findViewById(R.id.view_cancel);
        btnSave = findViewById(R.id.view_save);

        Intent intent = getIntent();

        final int idx = intent.getIntExtra("idx", -1);
        final String date = intent.getStringExtra("date");
        final String foodName = intent.getStringExtra("food_name");
        final String calorie = intent.getStringExtra("food_calorie");

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //음식이름, 칼로리 둘중 하나라도 비어있다면 DB작업 안함
                //Do not work on DB if either of the food names or calories are empty
                if (TextUtils.isEmpty(etFoodName.getText().toString())
                        || TextUtils.isEmpty(etCalorie.getText().toString())) {
                    Toast.makeText(SubActivityKitchenCaloriesEdit.this, "Please enter", Toast.LENGTH_SHORT).show();
                    return;
                }

                CaloriesData caloriesData = new CaloriesData();
                caloriesData.setIdx(idx);
                caloriesData.setFoodName(etFoodName.getText().toString());
                caloriesData.setFoodCalories(etCalorie.getText().toString());
                caloriesData.setDate(date);

                if (isAddEntry(idx)) {
                    //추가라면 DB에 삽입을 수행한다.
                    //If added, insert into DB.
                    if (SQLiteHelper.getInstance(SubActivityKitchenCaloriesEdit.this).insertCaloriesData(caloriesData)) {
                        Toast.makeText(SubActivityKitchenCaloriesEdit.this, "Saved", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        Toast.makeText(SubActivityKitchenCaloriesEdit.this, "Save Failed", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    //수정이라면 DB에 갱신을 수행한다.
                    //If it is a modification, perform a renewal to the DB.
                    if (SQLiteHelper.getInstance(SubActivityKitchenCaloriesEdit.this).updateCaloriesData(caloriesData)) {
                        Toast.makeText(SubActivityKitchenCaloriesEdit.this, "Updated", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        Toast.makeText(SubActivityKitchenCaloriesEdit.this, "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        //Date만 전달 받았다면 추가, 아니라면 수정
        //Add if only Date was delivered, or modify if not
        if (isAddEntry(idx)) {
            tvTitle.setText("Add Entry " + DateUtil.convertDateFormat("yyyyMMdd", "yyyy-MM-dd", date));
        }
        else {
            tvTitle.setText("Edit Entry " + DateUtil.convertDateFormat("yyyyMMdd", "yyyy-MM-dd", date));
            etFoodName.setText(foodName);
            etCalorie.setText(calorie);
        }
    }

    private boolean isAddEntry(int idx) {

        if (idx == -1) {
            return true;
        }
        else {
            return false;
        }
    }

}

package edu.murraystate.androidcamilydashboard.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.utils.ImageUtil;

public class SubActivityFamilyRecipesView extends AppCompatActivity {

    private TextView tvBack, tvEdit;
    private TextView tvRecipeName, tvRecipeUrl, tvRecipeDesc;
    private ImageView ivImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sub_family_recipes_view);

        tvBack = findViewById(R.id.tv_back);
        tvEdit = findViewById(R.id.tv_edit);
        ivImage = findViewById(R.id.iv_image);
        tvRecipeName = findViewById(R.id.tv_recipes_title);
        tvRecipeUrl = findViewById(R.id.tv_url);
        tvRecipeDesc = findViewById(R.id.tv_desc);

        Intent intent = getIntent();

        final long idx = intent.getLongExtra("idx", -1);
        final String recipeName = intent.getStringExtra("recipe_name");
        final String recipeUrl = intent.getStringExtra("recipe_url");
        final String recipeDesc = intent.getStringExtra("recipe_desc");

        Bitmap bitmap = ImageUtil.readImageFile(this, idx);
        if (null != bitmap) {
            ivImage.setImageBitmap(bitmap);
        }

        tvRecipeName.setText(recipeName);
        tvRecipeUrl.setText(recipeUrl);
        tvRecipeDesc.setText(recipeDesc);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubActivityFamilyRecipesView.this, SubActivityFamilyRecipesEdit.class);
                intent.putExtra("idx", idx);
                intent.putExtra("recipe_name", recipeName);
                intent.putExtra("recipe_url", recipeUrl);
                intent.putExtra("recipe_desc", recipeDesc);
                startActivityForResult(intent, 0);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            //If the additions and modifications were successful, update the current screen.
            setResult(RESULT_OK);
            finish();
        }
    }
}

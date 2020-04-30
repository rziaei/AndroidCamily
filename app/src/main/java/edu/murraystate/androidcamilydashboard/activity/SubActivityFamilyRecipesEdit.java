package edu.murraystate.androidcamilydashboard.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.github.dhaval2404.imagepicker.ImagePicker;

import edu.murraystate.androidcamilydashboard.adapters.RecipesData;
import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.utils.ImageUtil;
import edu.murraystate.androidcamilydashboard.utils.SQLiteHelper;

public class SubActivityFamilyRecipesEdit extends AppCompatActivity {

    private ImageView ivImage;
    private ConstraintLayout btnImage, btnDelete;
    private TextView tvCancel, tvSave;
    private View viewImageDelete;
    private EditText etName, etUrl, etDesc;

    private Bitmap imageBitmap;
    private Uri imageUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sub_family_recipes_edit);

        tvCancel = findViewById(R.id.tv_cancel);
        tvSave = findViewById(R.id.tv_save);
        ivImage = findViewById(R.id.iv_image);
        btnImage = findViewById(R.id.btn_image);
        etName = findViewById(R.id.et_title);
        etUrl = findViewById(R.id.et_url);
        etDesc = findViewById(R.id.et_desc);
        btnDelete = findViewById(R.id.btn_delete);
        viewImageDelete = findViewById(R.id.view_image_delete);

        Intent intent = getIntent();

        final long idx = intent.getLongExtra("idx", -1);
        final String recipeName = intent.getStringExtra("recipe_name");
        final String recipeUrl = intent.getStringExtra("recipe_url");
        final String recipeDesc = intent.getStringExtra("recipe_desc");

        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        tvSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (TextUtils.isEmpty(etName.getText().toString())
                        || TextUtils.isEmpty(etUrl.getText().toString()) || TextUtils.isEmpty(etDesc.getText().toString())) {
                    Toast.makeText(SubActivityFamilyRecipesEdit.this, "Please enter", Toast.LENGTH_SHORT).show();
                    return;
                }

                RecipesData recipesData = new RecipesData();
                recipesData.setIdx(idx);
                recipesData.setRecipeName(etName.getText().toString());
                recipesData.setRecipeUrl(etUrl.getText().toString());
                recipesData.setRecipeDesc(etDesc.getText().toString());

                if (isAddEntry(idx)) {
                    //추가 add
                    long resultIdx = SQLiteHelper.getInstance(SubActivityFamilyRecipesEdit.this).insertRecipeData(recipesData);

                    if (0 < resultIdx) {
                        ImageUtil.saveImage(SubActivityFamilyRecipesEdit.this, imageBitmap, String.valueOf(resultIdx));
                        Toast.makeText(SubActivityFamilyRecipesEdit.this, "Saved", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        Toast.makeText(SubActivityFamilyRecipesEdit.this, "Save Failed", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    //수정 modification
                    long resultIdx = SQLiteHelper.getInstance(SubActivityFamilyRecipesEdit.this).updateRecipeData(recipesData);

                    if (0 < resultIdx) {

                        if (null == imageUri)
                            ImageUtil.deleteImage(SubActivityFamilyRecipesEdit.this, idx);
                        else
                            ImageUtil.saveImage(SubActivityFamilyRecipesEdit.this, imageBitmap, String.valueOf(idx));

                        Toast.makeText(SubActivityFamilyRecipesEdit.this, "Updated", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }
                    else {
                        Toast.makeText(SubActivityFamilyRecipesEdit.this, "Update Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (SQLiteHelper.getInstance(SubActivityFamilyRecipesEdit.this).deleteRecipeData(idx)) {
                    Toast.makeText(SubActivityFamilyRecipesEdit.this, "Deleted", Toast.LENGTH_SHORT).show();
                    ImageUtil.deleteImage(SubActivityFamilyRecipesEdit.this, idx);
                    setResult(RESULT_OK);
                    finish();
                }
            }
        });

        btnImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.Companion.with(SubActivityFamilyRecipesEdit.this).
                        galleryOnly().start();
            }
        });

        viewImageDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ivImage.setImageResource(R.drawable.image);
                viewImageDelete.setVisibility(View.GONE);
                imageUri = null;
            }
        });


        if (isAddEntry(idx)) {

        }
        else {

            Bitmap bitmap = ImageUtil.readImageFile(this, idx);
            if (null != bitmap) {
                ivImage.setImageBitmap(bitmap);
                viewImageDelete.setVisibility(View.VISIBLE);
            }

            etName.setText(recipeName);
            etUrl.setText(recipeUrl);
            etDesc.setText(recipeDesc);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Uri selectedImageUri = data.getData();

            if (null != selectedImageUri) {
                this.imageUri = selectedImageUri;
                imageBitmap = ImageUtil.resize(this, selectedImageUri, 500);
                ivImage.setImageBitmap(imageBitmap);
                viewImageDelete.setVisibility(View.VISIBLE);
            }
        }
    }

    private boolean isAddEntry(long idx) {

        if (idx == -1) {
            return true;
        }
        else {
            return false;
        }
    }
}

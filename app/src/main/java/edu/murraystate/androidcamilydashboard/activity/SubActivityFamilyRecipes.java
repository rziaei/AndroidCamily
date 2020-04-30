package edu.murraystate.androidcamilydashboard.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

import edu.murraystate.androidcamilydashboard.adapters.RecipesAdapter;
import edu.murraystate.androidcamilydashboard.adapters.RecipesData;
import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.utils.SQLiteHelper;

public class SubActivityFamilyRecipes extends AppCompatActivity {

    private TextView tvEdit;
    private ListView listRecipes;
    private EditText etSearch;
    private RecipesAdapter recipesAdapter;
    private ArrayList<RecipesData> recipesList;
    private ArrayList<RecipesData> recipesSearchList;
    private int selectedPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sub_family_recipes);

        etSearch = findViewById(R.id.et_search);
        listRecipes = findViewById(R.id.list_recipes);
        recipesAdapter = new RecipesAdapter(this);
        listRecipes.setAdapter(recipesAdapter);
        listRecipes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Event when clicking on a list item (move to the modification screen)
                goViewActivity(position);
            }
        });
        listRecipes.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Event when long-click list item (pop-up menu generation)
                selectedPosition = position;
                return false;
            }
        });
        registerForContextMenu(listRecipes);

        tvEdit = findViewById(R.id.tv_edit);
        tvEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SubActivityFamilyRecipes.this, SubActivityFamilyRecipesEdit.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivityForResult(intent, 0);
            }
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                recipesSearch(editable.toString());
            }
        });

        recipesList = SQLiteHelper.getInstance(this).selectRecipesData();
        recipesSearchList = new ArrayList<>();
        recipesSearchList.addAll(recipesList);
        recipesAdapter.setList(recipesSearchList);
    }

    private void recipesSearch(String keyword) {

        // Erase the list and create a new one every time you type a character.
        recipesSearchList.clear();

        // Show all the data when there is no text input.
        if (TextUtils.isEmpty(keyword)) {
            recipesSearchList.addAll(recipesList);
        }
        // 문자 입력을 할때.. when typing.
        else
        {
            // Search all data in the list.
            for(int i = 0;i < recipesList.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                // Returns true if all data in arraylist contains the word entered (charText).
                if (recipesList.get(i).getRecipeName().toLowerCase().contains(keyword))
                {
                    // Add the retrieved data to the list.
                    recipesSearchList.add(recipesList.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        // Since the list data has changed, update the adapter to show the retrieved data on the screen.
        recipesAdapter.notifyDataSetChanged();
    }

    /**
     * 아이템 수정 화면으로 이동
     * Go to item modification screen
     * @param position
     */
    private void goViewActivity(int position) {

        RecipesData recipesData = (RecipesData)recipesAdapter.getItem(position);

        Intent intent = new Intent(SubActivityFamilyRecipes.this, SubActivityFamilyRecipesView.class);
        intent.putExtra("idx", recipesData.getIdx());
        intent.putExtra("recipe_name", recipesData.getRecipeName());
        intent.putExtra("recipe_url", recipesData.getRecipeUrl());
        intent.putExtra("recipe_desc", recipesData.getRecipeDesc());
        startActivityForResult(intent, 0);
    }

    /**
     * 아이템 수정 화면으로 이동
     * Go to item modification screen
     * @param position
     */
    private void goEditActivity(int position) {

        RecipesData recipesData = (RecipesData)recipesAdapter.getItem(position);

        Intent intent = new Intent(SubActivityFamilyRecipes.this, SubActivityFamilyRecipesEdit.class);
        intent.putExtra("idx", recipesData.getIdx());
        intent.putExtra("recipe_name", recipesData.getRecipeName());
        intent.putExtra("recipe_url", recipesData.getRecipeUrl());
        intent.putExtra("recipe_desc", recipesData.getRecipeDesc());
        startActivityForResult(intent, 0);
    }

    /**
     * 리스트 아이템 롱클릭시 팝업 아이템 정의
     * Define pop-up items when long-clicking list items
     * @param menu
     * @param v
     * @param menuInfo
     */
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v.getId() == R.id.list_recipes){
            AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo)menuInfo;
            MenuItem mnu1 = menu.add(0,0,0,"Edit");
            MenuItem mnu2 = menu.add(0,1,1,"Delete");
        }
    }

    /**
     * 팝업 아이템 클릭시 이벤트 정의
     * Define an event when you click a pop-up
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {
            case 0:
                //If you pressed Edit, modify the currently selected item.
                goEditActivity(selectedPosition);
                break;
            case 1:
                //If you pressed Delete, delete the currently selected item.
                RecipesData recipesData = (RecipesData)recipesAdapter.getItem(selectedPosition);
                if (SQLiteHelper.getInstance(this).deleteRecipeData(recipesData.getIdx())) {
                    Toast.makeText(SubActivityFamilyRecipes.this, "Deleted", Toast.LENGTH_SHORT).show();
                    recipesList = SQLiteHelper.getInstance(this).selectRecipesData();
                    recipesSearch(etSearch.getText().toString());
                }
                else {
                    Toast.makeText(SubActivityFamilyRecipes.this, "Delete Failed", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;

        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            //추가 및 수정이 성공이었다면 현재 화면을 갱신한다.
            // If the additions and modifications were successful, update the current screen.
            recipesList = SQLiteHelper.getInstance(this).selectRecipesData();
            recipesSearch(etSearch.getText().toString());
        }
    }
}

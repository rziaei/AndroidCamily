package edu.murraystate.androidcamilydashboard.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import edu.murraystate.androidcamilydashboard.adapters.CaloriesData;
import edu.murraystate.androidcamilydashboard.adapters.RecipesData;
import edu.murraystate.androidcamilydashboard.adapters.RestaurantData;

public class SQLiteHelper extends SQLiteOpenHelper {

    public static SQLiteHelper sqLiteHelper = null;
    public static final String DATABASE_NAME = "CamilyDashboard.db";
    public static final String TABLE_CALORIES = "CaloriesTable";
    public static final String TABLE_RECIPES = "RecipesTable";
    public static final String TABLE_RESTAURANT = "RestaurantTable";
    public static final int DB_VERSION = 1;
    public static final String IDX = "IDX";
    public static final String FOOD_NAME = "food_name";
    public static final String FOOD_CALORIES = "food_calories";
    public static final String DATE = "date";
    public static final String RECIPE_NAME = "recipe_name";
    public static final String RECIPE_URL = "recipe_url";
    public static final String RECIPE_DESC = "recipe_desc";
    public static final String LAT = "lat";
    public static final String LON = "lon";
    public static final String RESTAURANT_NAME = "restaurant_name";
    public static final String RESTAURANT_ADDR = "restaurant_addr";
    public static final String PLACE_ID = "place_id";

    private SQLiteDatabase db;

    public static SQLiteHelper getInstance(Context context){
        if(sqLiteHelper == null){
            sqLiteHelper = new SQLiteHelper(context);
        }

        return sqLiteHelper;
    }

    private SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DB_VERSION);
        db = this.getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + TABLE_CALORIES + " ("
                + IDX + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + FOOD_NAME + " TEXT, "
                + FOOD_CALORIES + " TEXT, "
                + DATE + " TEXT"
                + ")");

        db.execSQL("create table " + TABLE_RECIPES + " ("
                + IDX + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + RECIPE_NAME + " TEXT, "
                + RECIPE_URL + " TEXT, "
                + RECIPE_DESC + " TEXT"
                + ")");

        db.execSQL("create table " + TABLE_RESTAURANT + " ("
                + IDX + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + LAT + " TEXT, "
                + LON + " TEXT, "
                + RESTAURANT_NAME + " TEXT,"
                + RESTAURANT_ADDR + " TEXT"
                + ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CALORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECIPES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RESTAURANT);
        onCreate(db);
    }

    /**
     * 칼로리 데이터를 DB에 삽입한다
     * Insert the calorie data into the DB
     * @param caloriesData
     * @return
     */
    public boolean insertCaloriesData(CaloriesData caloriesData) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FOOD_NAME, caloriesData.getFoodName());
        contentValues.put(FOOD_CALORIES, caloriesData.getFoodCalories());
        contentValues.put(DATE, caloriesData.getDate());

        long result = db.insert(TABLE_CALORIES, null, contentValues);

        if(result > 0)
            return true;
        else
            return false;
    }

    /**
     * 레시피 데이터를 DB에 삽입한다
     * Insert recipe data into DB
     * @param recipeData
     * @return
     */
    public long insertRecipeData(RecipesData recipeData) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(RECIPE_NAME, recipeData.getRecipeName());
        contentValues.put(RECIPE_URL, recipeData.getRecipeUrl());
        contentValues.put(RECIPE_DESC, recipeData.getRecipeDesc());

        long result = db.insert(TABLE_RECIPES, null, contentValues);

        return result;
    }

    /**
     * 음식점 데이터를 DB에 삽입한다
     * Insert restaurant data into DB
     * @param restaurantData
     * @return
     */
    public long insertRestaurantData(RestaurantData restaurantData) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(RESTAURANT_NAME, restaurantData.getRestaurantName());
        contentValues.put(RESTAURANT_ADDR, restaurantData.getRestaurantAddr());
        contentValues.put(LAT, restaurantData.getLat());
        contentValues.put(LON, restaurantData.getLon());

        long result = db.insert(TABLE_RESTAURANT, null, contentValues);

        return result;
    }

    /**
     * 칼로리 데이터를 갱신한다.
     * Update calorie data.
     * @param caloriesData
     * @return
     */
    public boolean updateCaloriesData(CaloriesData caloriesData) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(FOOD_NAME, caloriesData.getFoodName());
        contentValues.put(FOOD_CALORIES, caloriesData.getFoodCalories());

        long result = db.update(TABLE_CALORIES, contentValues,  IDX + " = " + caloriesData.getIdx(), null);

        if (result > 0)
            return true;
        else
            return false;
    }

    /**
     * 레시피 데이터를 갱신한다.
     * Update recipe data.
     * @param recipeData
     * @return
     */
    public long updateRecipeData(RecipesData recipeData) {

        ContentValues contentValues = new ContentValues();
        contentValues.put(RECIPE_NAME, recipeData.getRecipeName());
        contentValues.put(RECIPE_URL, recipeData.getRecipeUrl());
        contentValues.put(RECIPE_DESC, recipeData.getRecipeDesc());

        long result = db.update(TABLE_RECIPES, contentValues,  IDX + " = " + recipeData.getIdx(), null);

        return result;
    }

    /**
     * 원하는 날짜의 칼로리 데이터를 가져온다
     * Get the calorie data for the desired date
     * @param date
     * @return
     */
    public ArrayList<CaloriesData> selectCaloriesData(String date){

        ArrayList<CaloriesData> caloriesDataList = new ArrayList<>();
        String sql = "select * from "+ TABLE_CALORIES + " where " + DATE + " = " + date + ";";
        Cursor cursor = db.rawQuery(sql, null);

        while(cursor.moveToNext()){
            CaloriesData caloriesData = new CaloriesData();
            caloriesData.setIdx(cursor.getInt(cursor.getColumnIndex(IDX)));
            caloriesData.setFoodName(cursor.getString(cursor.getColumnIndex(FOOD_NAME)));
            caloriesData.setFoodCalories(cursor.getString(cursor.getColumnIndex(FOOD_CALORIES)));
            caloriesData.setDate(cursor.getString(cursor.getColumnIndex(DATE)));

            caloriesDataList.add(caloriesData);
        }
        cursor.close();

        return caloriesDataList;
    }

    /**
     * 레시피 데이터를 가져온다
     * Get recipe data
     * @return
     */
    public ArrayList<RecipesData> selectRecipesData(){

        ArrayList<RecipesData> recipesDataList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from "+ TABLE_RECIPES, null);

        while(cursor.moveToNext()){
            RecipesData recipesData = new RecipesData();
            recipesData.setIdx(cursor.getInt(cursor.getColumnIndex(IDX)));
            recipesData.setRecipeName(cursor.getString(cursor.getColumnIndex(RECIPE_NAME)));
            recipesData.setRecipeUrl(cursor.getString(cursor.getColumnIndex(RECIPE_URL)));
            recipesData.setRecipeDesc(cursor.getString(cursor.getColumnIndex(RECIPE_DESC)));

            recipesDataList.add(recipesData);
        }
        cursor.close();

        return recipesDataList;
    }

    /**
     * 레시피 데이터를 가져온다
     * Get recipe data
     * @return
     */
    public ArrayList<RestaurantData> selectRestaurantData(){

        ArrayList<RestaurantData> restaurantList = new ArrayList<>();
        Cursor cursor = db.rawQuery("select * from "+ TABLE_RESTAURANT, null);

        while(cursor.moveToNext()){
            RestaurantData restaurantData = new RestaurantData();
            restaurantData.setIdx(cursor.getInt(cursor.getColumnIndex(IDX)));
            restaurantData.setRestaurantName(cursor.getString(cursor.getColumnIndex(RESTAURANT_NAME)));
            restaurantData.setRestaurantAddr(cursor.getString(cursor.getColumnIndex(RESTAURANT_ADDR)));
            restaurantData.setLat(cursor.getString(cursor.getColumnIndex(LAT)));
            restaurantData.setLon(cursor.getString(cursor.getColumnIndex(LON)));

            restaurantList.add(restaurantData);
        }
        cursor.close();

        return restaurantList;
    }

    /**
     * 칼로리 데이터를 삭제한다.
     * Delete calories data
     * @param idx
     * @return
     */
    public boolean deleteCaloriesData(int idx) {

        long result = db.delete(TABLE_CALORIES, IDX + " = " + idx, null);

        if (result > 0)
            return true;
        else
            return false;
    }

    /**
     * 칼로리 데이터를 삭제한다.
     * Delete calories data
     * @param idx
     * @return
     */
    public boolean deleteRecipeData(long idx) {

        long result = db.delete(TABLE_RECIPES, IDX + " = " + idx, null);

        if (result > 0)
            return true;
        else
            return false;
    }

    /**
     * 음식점 데이터를 삭제한다.
     * delete Restaurant Data
     * @param restName
     * @return
     */
    public boolean deleteRestaurantData(String restName) {

        long result = db.delete(TABLE_RESTAURANT, RESTAURANT_NAME + " = " + "'" + restName + "'", null);

        if (result > 0)
            return true;
        else
            return false;
    }
}
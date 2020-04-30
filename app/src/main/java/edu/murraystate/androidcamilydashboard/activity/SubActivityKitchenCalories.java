package edu.murraystate.androidcamilydashboard.activity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;

import edu.murraystate.androidcamilydashboard.adapters.CaloriesAdapter;
import edu.murraystate.androidcamilydashboard.adapters.CaloriesData;
import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.utils.DateUtil;
import edu.murraystate.androidcamilydashboard.utils.SQLiteHelper;

public class SubActivityKitchenCalories extends AppCompatActivity {

    private final String FORMAT_YYYYMMdd = "yyyyMMdd";

    private TextView tvDate;
    private TextView tvTotalCalorie;
    private ImageView viewCalendar;
    private ImageView btnAdd;
    private ListView listCalories;
    private CaloriesAdapter caloriesAdapter;
    private String selectDate;
    private int selectedPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_kitchen_calories);

        tvDate = findViewById(R.id.tv_date);
        tvTotalCalorie = findViewById(R.id.tv_total_calorie);
        listCalories = findViewById(R.id.list_calories);
        viewCalendar = findViewById(R.id.view_calendar);
        viewCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCalendarDialog();
            }
        });

        btnAdd = findViewById(R.id.view_add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goAddActivity();
            }
        });

        caloriesAdapter = new CaloriesAdapter(this);
        listCalories.setAdapter(caloriesAdapter);
        listCalories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Event when clicking on a list item (move to the modification screen)
                goEditActivity(position);
            }
        });
        listCalories.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long l) {
                //Event when long-click list item (pop-up menu generation)
                selectedPosition = position;
                return false;
            }
        });
        registerForContextMenu(listCalories);

        selectDate = DateUtil.getTodayyyyyMMdd();
        setDate(selectDate);
    }

    /**
     * Go to the Add Item screen
     */
    private void goAddActivity() {
        Intent intent = new Intent(SubActivityKitchenCalories.this, SubActivityKitchenCaloriesEdit.class);
        intent.putExtra("date", selectDate);
        startActivityForResult(intent, 0);
    }

    /**
     * Go to item modification screen
     * @param position
     */
    private void goEditActivity(int position) {

        CaloriesData caloriesData = (CaloriesData)caloriesAdapter.getItem(position);

        Intent intent = new Intent(SubActivityKitchenCalories.this, SubActivityKitchenCaloriesEdit.class);
        intent.putExtra("food_name", caloriesData.getFoodName());
        intent.putExtra("food_calorie", caloriesData.getFoodCalories());
        intent.putExtra("idx", caloriesData.getIdx());
        intent.putExtra("date", caloriesData.getDate());
        startActivityForResult(intent, 0);
    }

    /**
     * Initialize list
     * @param date
     */
    private void setDate(String date) {

        selectDate = date;
        tvDate.setText(DateUtil.convertDateFormat("yyyyMMdd", "(EEE) yyyy-MM-dd", date));
        ArrayList<CaloriesData> caloriesList = SQLiteHelper.getInstance(this).selectCaloriesData(date);

        int totalCalories = 0;

        for (CaloriesData calorieData : caloriesList) {

            int calorie = Integer.parseInt(calorieData.getFoodCalories());
            totalCalories += calorie;
        }

        tvTotalCalorie.setText(String.format("Total Calorie : %d Kcal", totalCalories));
        caloriesAdapter.setList(caloriesList);
    }

    /**
     * Create a calendar pop-up.
     */
    private void showCalendarDialog() {

        Calendar calendar = DateUtil.getCalendar("yyyyMMdd", selectDate);
        DatePickerDialog dialog = new DatePickerDialog(this, R.style.DialogDatePickerTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int date) {
                setDate(year + String.format("%02d", 1 + month) + String.format("%02d", date));
            }
        }, calendar.get(Calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(Calendar.DATE));
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (RESULT_OK == resultCode) {
            //추가 및 수정이 성공이었다면 현재 화면을 갱신한다.
            //If the additions and modifications were successful, update the current screen.
            setDate(selectDate);
        }
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
        if (v.getId() == R.id.list_calories){
            AdapterView.AdapterContextMenuInfo info =(AdapterView.AdapterContextMenuInfo)menuInfo;
            MenuItem mnu1 = menu.add(0,0,0,"Edit");
            MenuItem mnu2 = menu.add(0,1,1,"Delete");
        }
    }

    /**
     * Define an event when you click a pop-up
     * @param item
     * @return
     */
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info=(AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        switch (item.getItemId()) {
            case 0:
                //Edit를 눌렀다면 현재 선택된 아이템을 수정 한다.
                //If pressed Edit, modify the currently selected item.
                goEditActivity(selectedPosition);
                break;
            case 1:
                //Delete를 눌렀다면 현재 선택된 아이템을 삭제 한다.
                //If pressed Delete, delete the currently selected item.
                CaloriesData caloriesData = (CaloriesData)caloriesAdapter.getItem(selectedPosition);
                if (SQLiteHelper.getInstance(this).deleteCaloriesData(caloriesData.getIdx())) {
                    setDate(selectDate);
                    Toast.makeText(SubActivityKitchenCalories.this, "Deleted", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(SubActivityKitchenCalories.this, "Delete Failed", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                break;

        }
        return true;
    }
}

package edu.murraystate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.murraystate.androidcamilydashboard.R;

public class CaloriesAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<CaloriesData> caloriesDataList;

    public CaloriesAdapter(Context context) {
        this.context = context;
        this.caloriesDataList = new ArrayList<>();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public CaloriesAdapter(Context context, ArrayList<CaloriesData> caloriesDataList) {
        this.context = context;
        this.caloriesDataList = caloriesDataList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(ArrayList<CaloriesData> caloriesDataList) {
        this.caloriesDataList = caloriesDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return caloriesDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return caloriesDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        CaloriesViewHolder viewHolder;

        // If no cached views exist, create a new one and create a viewholder
        if(view == null)
        {
            view = inflater.inflate(R.layout.list_calories_item, viewGroup, false);

            viewHolder = new CaloriesViewHolder();
            viewHolder.tvFoodName = view.findViewById(R.id.tv_food_name);
            viewHolder.tvCalories = view.findViewById(R.id.tv_calorie);
            view.setTag(viewHolder);
        }
        // if have cached views, use the saved viewholder
        else
        {
            viewHolder = (CaloriesViewHolder) view.getTag();
        }

        viewHolder.tvFoodName.setText(caloriesDataList.get(position).getFoodName());
        viewHolder.tvCalories.setText(caloriesDataList.get(position).getFoodCalories());

        return view;
    }

    public class CaloriesViewHolder
    {
        public TextView tvFoodName;
        public TextView tvCalories;
    }
}

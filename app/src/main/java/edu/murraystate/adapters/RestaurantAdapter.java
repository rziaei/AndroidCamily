package edu.murraystate.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import edu.murraystate.androidcamilydashboard.R;

public class RestaurantAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<RestaurantData> restaurantList;

    public RestaurantAdapter(Context context) {
        this.context = context;
        this.restaurantList = new ArrayList<>();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(ArrayList<RestaurantData> restaurantList) {
        this.restaurantList = restaurantList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return restaurantList.size();
    }

    @Override
    public Object getItem(int position) {
        return restaurantList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        RestaurantViewHolder viewHolder;

        // If no cached views exist, create a new one and create a viewholder
        if(view == null)
        {
            view = inflater.inflate(R.layout.list_restaurant_item, viewGroup, false);

            viewHolder = new RestaurantViewHolder();
            viewHolder.tvRestName = view.findViewById(R.id.tv_rest_name);
            viewHolder.tvRestAddr = view.findViewById(R.id.tv_rest_addr);
            view.setTag(viewHolder);
        }
        // If have cached views, use the saved viewholder
        else
        {
            viewHolder = (RestaurantViewHolder) view.getTag();
        }

        viewHolder.tvRestName.setText(restaurantList.get(position).getRestaurantName());
        viewHolder.tvRestAddr.setText(restaurantList.get(position).getRestaurantAddr());

        return view;
    }

    public class RestaurantViewHolder
    {
        public TextView tvRestName;
        public TextView tvRestAddr;
    }

}

package edu.murraystate.androidcamilydashboard.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import edu.murraystate.androidcamilydashboard.R;
import edu.murraystate.androidcamilydashboard.utils.ImageUtil;

public class RecipesAdapter extends BaseAdapter {

    private Context context;
    private LayoutInflater inflater;
    private ArrayList<RecipesData> recipesDataList;

    public RecipesAdapter(Context context) {
        this.context = context;
        this.recipesDataList = new ArrayList<>();
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public RecipesAdapter(Context context, ArrayList<RecipesData> caloriesDataList) {
        this.context = context;
        this.recipesDataList = caloriesDataList;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setList(ArrayList<RecipesData> caloriesDataList) {
        this.recipesDataList = caloriesDataList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return recipesDataList.size();
    }

    @Override
    public Object getItem(int position) {
        return recipesDataList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {

        RecipesViewHolder viewHolder;

        // If no cached views exist, create a new one and create a viewholder
        if(view == null)
        {
            view = inflater.inflate(R.layout.list_recipes_item, viewGroup, false);

            viewHolder = new RecipesViewHolder();
            viewHolder.tvRecipesTitle = view.findViewById(R.id.tv_recipes_title);
            viewHolder.ivImage = view.findViewById(R.id.iv_image);

            view.setTag(viewHolder);
        }
        // If you have cached views, use the saved viewholder
        else
        {
            viewHolder = (RecipesViewHolder) view.getTag();
        }
        viewHolder.tvRecipesTitle.setText(recipesDataList.get(position).getRecipeName());

        Bitmap bitmap = ImageUtil.readImageFile(context, recipesDataList.get(position).getIdx());

        if (null == bitmap) {
            viewHolder.ivImage.setImageResource(R.drawable.image);
        }
        else {
            viewHolder.ivImage.setImageBitmap(bitmap);
        }

        return view;
    }

    public class RecipesViewHolder
    {
        public TextView tvRecipesTitle;
        public ImageView ivImage;
    }
}

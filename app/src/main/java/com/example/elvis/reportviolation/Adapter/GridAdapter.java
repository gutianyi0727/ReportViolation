package com.example.elvis.reportviolation.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.elvis.reportviolation.R;
import com.example.elvis.reportviolation.bean.GridViewBean;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2018/4/8.
 */

public class GridAdapter extends BaseAdapter {

    private Context context;
    private int[] images;
    private String[] names;
    private List<GridViewBean> gridViewBeen = new ArrayList<>();


    public GridAdapter(Context context, Boolean isstudent){
        this.context = context;
        if (isstudent){
            images = new int[]{R.drawable.ic_person, R.drawable.ic_report_black,R.drawable.ic_map,R.drawable.ic_history_black_24dp};
            names = new String[]{"个人信息","举报","热力图","历史记录"};
        }else {
            images = new int[]{R.drawable.ic_person,R.drawable.ic_report_black,R.drawable.ic_map};
            names = new String[]{"个人信息","数据","发布课程"};
        }
        for (int i=0;i<images.length;i++){
            GridViewBean gridViewBean = new GridViewBean();
            gridViewBean.setImage(images[i]);
            gridViewBean.setName(names[i]);
            gridViewBeen.add(gridViewBean);
        }
    }

    @Override
    public int getCount() {
        return gridViewBeen.size();
    }

    @Override
    public Object getItem(int position) {
        return gridViewBeen.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_main_grid,null);
            holder.image = (ImageView) convertView.findViewById(R.id.grid_item_image);
            holder.name = (TextView) convertView.findViewById(R.id.grid_item_name);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.image.setImageResource(gridViewBeen.get(position).getImage());
        holder.name.setText(gridViewBeen.get(position).getName());
        return convertView;
    }

    class ViewHolder{
        public ImageView image;
        public TextView name;
    }
}

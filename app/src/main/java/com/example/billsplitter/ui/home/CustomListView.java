package com.example.billsplitter.ui.home;

import android.app.Activity;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.billsplitter.R;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

//class CustomListView
public class CustomListView extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> maintitle;
    private final ArrayList<String> subtitle;
    private final Integer imgId;

    public CustomListView(Activity context, ArrayList<String> maintitle, ArrayList<String> subtitle, Integer imgid){
        super(context, R.layout.custom_list, maintitle);
        this.context = context;
        this.maintitle = maintitle;
        this.subtitle = subtitle;
        this.imgId = imgid;
    }

    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.custom_list,null, true);

        TextView titleText = rowView.findViewById(R.id.name);
        ImageView imageView = rowView.findViewById(R.id.icon);
        TextView subText = rowView.findViewById(R.id.sub_text);

        titleText.setText(StringUtils.abbreviate(maintitle.get(position),30));
        imageView.setImageResource(imgId);

        subText.setText(subtitle.get(position));
        double amount = Double.parseDouble(subtitle.get(position));
        if (amount < 0){
            subText.setTextColor(Color.RED);
        }
        else {

            subText.setTextColor(Color.parseColor("#16c7ad"));
        }


        return rowView;
    }
}

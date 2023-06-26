package com.malawi.dmvicverification;



import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class HistoryAdapter extends ArrayAdapter<HistoryModel> {

    ArrayList<HistoryModel> listOfClaimDetails;
    Context context;

    public HistoryAdapter(@NonNull Context context, ArrayList<HistoryModel> listOfClaimDetails) {
        super(context, R.layout.custom_history_layout, listOfClaimDetails);
        this.listOfClaimDetails = listOfClaimDetails;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        HistoryModel item = listOfClaimDetails.get(position);

        ViewHolder viewHolder;

        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.custom_history_layout, parent, false);
            // Initializing the required variables.
            viewHolder.vehicleNo = convertView.findViewById(R.id.intimationValue);
            viewHolder.chaNum = convertView.findViewById(R.id.reqValue);
            viewHolder.viewDetails = convertView.findViewById(R.id.viewDetails);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            // Setting the model class values to the view.
            viewHolder.vehicleNo.setText(item.getRegNo());
            viewHolder.chaNum.setText(item.getChaNo());

            // OnClick of an item forward to verify claim page.
            viewHolder.viewDetails.setOnClickListener(onClickViewDetails ->{
                try {

                    //redirection of the page to next respective page

                }catch (Exception e){
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public static class ViewHolder {
        // Declaring the variables.
        TextView vehicleNo;
        TextView chaNum;
        ImageView viewDetails;
    }


}

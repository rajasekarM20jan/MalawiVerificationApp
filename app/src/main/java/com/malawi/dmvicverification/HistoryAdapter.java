package com.malawi.dmvicverification;



import android.content.Context;
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
            viewHolder.vehicleNo = convertView.findViewById(R.id.regValue);
            viewHolder.chaNum = convertView.findViewById(R.id.chaValue);
            viewHolder.certNoVal = convertView.findViewById(R.id.certNoVal);
            viewHolder.statusVal = convertView.findViewById(R.id.statusVal);
            // Cache the viewHolder object inside the fresh view
            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        try {
            // Setting the model class values to the view.
            String regNo,chaNo,certNo,status;
            if(item.getRegNo().equals("null")|| item.getRegNo() == null){
                regNo="N/A";
            }else{
                regNo= item.getRegNo();
            }
            if(item.getChaNo().equals("null")|| item.getChaNo() == null){
                chaNo="N/A";
            }else{
                chaNo= item.getChaNo();
            }
            if(item.getCertNo().equals("null")|| item.getCertNo() == null){
                certNo="N/A";
            }else{
                certNo= item.getCertNo();
            }
            if(item.getStatus().equals("null")|| item.getStatus() == null){
                status="N/A";
            }else{
                status= item.getStatus();
            }
            viewHolder.vehicleNo.setText(regNo);
            viewHolder.chaNum.setText(chaNo);
            viewHolder.certNoVal.setText(certNo);
            viewHolder.statusVal.setText(status);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return convertView;
    }

    public static class ViewHolder {
        // Declaring the variables.
        TextView vehicleNo;
        TextView chaNum,certNoVal,statusVal;
    }


}

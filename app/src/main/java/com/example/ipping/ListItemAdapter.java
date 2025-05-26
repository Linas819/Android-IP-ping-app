package com.example.ipping;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;

import java.util.List;

public class ListItemAdapter extends ArrayAdapter<IpListItem> {
    public ListItemAdapter(Context context, int resource, List<IpListItem> ipListItems)
    {
        super(context, resource, ipListItems);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        IpListItem ipListItem = getItem(position);

        if(convertView == null)
        {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ip_server_list_item, parent, false);
        }
        TextView ipTextView = convertView.findViewById(R.id.listItemIp);
        TextView serverNameTextView = convertView.findViewById(R.id.listItemServerName);
        ImageView pingStatusImage = convertView.findViewById(R.id.pingStatus);
        assert ipListItem != null;
        ipTextView.setText(ipListItem.IpAddress);
        serverNameTextView.setText(ipListItem.ServerName);
        pingStatusImage.setImageResource(R.drawable.baseline_loading_24);
        return convertView;
    }
}

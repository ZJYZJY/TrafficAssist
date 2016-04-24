package com.zjy.trafficassist;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by ZJY on 2016/4/20.
 */
public class HistoryListAdapter extends BaseAdapter {

    private Context mContext;
    private ArrayList<AlarmHistory> alarmHistories;
    private LayoutInflater listContainer;

    private TextView text_id;
    private TextView text_username;
    private TextView text_detail;
    private TextView text_isSerious;

    public HistoryListAdapter(Context context, ArrayList<AlarmHistory> alarmHistories) {
        this.mContext = context;
        this.alarmHistories = alarmHistories;
        listContainer = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return alarmHistories.size();
    }

    @Override
    public Object getItem(int position) {
        return alarmHistories.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView == null) {
            convertView = listContainer.inflate(R.layout.history_item, null);
            text_id = (TextView) convertView.findViewById(R.id.text_id);
            text_username = (TextView) convertView.findViewById(R.id.text_username);
            text_detail = (TextView) convertView.findViewById(R.id.text_detail);
            text_isSerious = (TextView) convertView.findViewById(R.id.text_isSerious);
        }
        text_id.setText("ID：" + String.valueOf(alarmHistories.get(position).getId()));
        text_username.setText("用户名：" + alarmHistories.get(position).getUsername());
        text_detail.setText("详情：" + alarmHistories.get(position).getDetail());
        if(alarmHistories.get(position).isSerious())
            text_isSerious.setText("严重？：YES");
        else
            text_isSerious.setText("严重？：No");

        return convertView;
    }
}

package com.zjy.trafficassist.ui;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.zjy.trafficassist.BaseActivity;
import com.zjy.trafficassist.HistoryListAdapter;
import com.zjy.trafficassist.JSONParser;
import com.zjy.trafficassist.R;
import com.zjy.trafficassist.WebService;

import org.json.JSONException;

import java.util.ArrayList;

public class AlarmHistory extends BaseActivity {


    private static boolean FROM_INTERNET = true;

    private ListView historyList;
    private ArrayList<com.zjy.trafficassist.AlarmHistory> local_history;
    private ArrayList<com.zjy.trafficassist.AlarmHistory> internet_history;
    private HistoryListAdapter historyListAdapter;
    private SwipeRefreshLayout RefreshLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        historyList = (ListView) findViewById(R.id.historyList);
        RefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);

        RefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                RefreshLayout.setRefreshing(true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FROM_INTERNET = true;
                        internet_history = getListItem();
                        RefreshLayout.setRefreshing(false);
                        historyListAdapter.notifyDataSetChanged();
                    }
                }, 2000);
            }
        });
        FROM_INTERNET = true;
        internet_history = getListItem();
        //local_history = getListItem();
        /**
         * 创建适配器
         */
//        historyListAdapter = new HistoryListAdapter(this, local_history);
        historyListAdapter = new HistoryListAdapter(this, internet_history);
        historyList.setAdapter(historyListAdapter);
    }

    private ArrayList<com.zjy.trafficassist.AlarmHistory> getListItem() {

        final ArrayList<com.zjy.trafficassist.AlarmHistory> history_item = new ArrayList<>();

        if (FROM_INTERNET) {
            new AsyncTask<Void, Void, ArrayList<com.zjy.trafficassist.AlarmHistory>>() {

                @Override
                protected ArrayList<com.zjy.trafficassist.AlarmHistory> doInBackground(Void... params) {

                    String detail = WebService.DownloadHistory();
                    try {
                        return JSONParser.DownloadHistory(detail);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(final ArrayList<com.zjy.trafficassist.AlarmHistory> ReturnCode) {
                    super.onPostExecute(ReturnCode);
                    if (ReturnCode != null) {
                        for (int i = 0; i < ReturnCode.size(); i++) {
                            history_item.add(ReturnCode.get(i));
                        }
                    } else {
                        Toast.makeText(AlarmHistory.this, "没有历史记录", Toast.LENGTH_SHORT).show();
                    }
                }
            }.execute();
        }
//        com.zjy.trafficassist.AlarmHistory history_item1 = new com.zjy.trafficassist.AlarmHistory(true, "车辆侧面发生刮擦", "郑家烨");
//        com.zjy.trafficassist.AlarmHistory history_item2 = new com.zjy.trafficassist.AlarmHistory(false, "detail", "赵晨曦");
//        com.zjy.trafficassist.AlarmHistory history_item3 = new com.zjy.trafficassist.AlarmHistory(false, "detail", "孙洪萍");
//        com.zjy.trafficassist.AlarmHistory history_item4 = new com.zjy.trafficassist.AlarmHistory(false, "detail", "郝琰");
//        com.zjy.trafficassist.AlarmHistory history_item5 = new com.zjy.trafficassist.AlarmHistory(false, "detail", "用户甲");
//        com.zjy.trafficassist.AlarmHistory history_item6 = new com.zjy.trafficassist.AlarmHistory(false, "detail", "用户乙");
//        com.zjy.trafficassist.AlarmHistory history_item7 = new com.zjy.trafficassist.AlarmHistory(false, "detail", "用户丙");
//        com.zjy.trafficassist.AlarmHistory history_item8 = new com.zjy.trafficassist.AlarmHistory(false, "detail", "用户丁");
//        com.zjy.trafficassist.AlarmHistory history_item9 = new com.zjy.trafficassist.AlarmHistory(false, "detail", "用户A");
//        listItems.add(history_item1);
//        listItems.add(history_item2);
//        listItems.add(history_item3);
//        listItems.add(history_item4);
//        listItems.add(history_item5);
//        listItems.add(history_item6);
//        listItems.add(history_item7);
//        listItems.add(history_item8);
//        listItems.add(history_item9);
        return history_item;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                //NavUtils.navigateUpFromSameTask(this);
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}

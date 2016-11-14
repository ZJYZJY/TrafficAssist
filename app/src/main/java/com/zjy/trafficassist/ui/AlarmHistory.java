package com.zjy.trafficassist.ui;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.Toast;

import com.zjy.trafficassist.BaseActivity;
import com.zjy.trafficassist.DatabaseManager;
import com.zjy.trafficassist.adapter.HistoryListAdapter;
import com.zjy.trafficassist.utils.TransForm;
import com.zjy.trafficassist.R;
import com.zjy.trafficassist.WebService;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

public class AlarmHistory extends BaseActivity {


    private static boolean FROM_INTERNET;

    private RecyclerView historyList;
    private ArrayList<com.zjy.trafficassist.model.AlarmHistory> local_history;
    private ArrayList<com.zjy.trafficassist.model.AlarmHistory> internet_history;
    private HistoryListAdapter historyListAdapter;
    private SwipeRefreshLayout RefreshLayout;
    private DatabaseManager databaseManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_history);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        databaseManager = new DatabaseManager(this);
        RefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshLayout);
        if (RefreshLayout != null) {
            RefreshLayout.setColorSchemeResources(android.R.color.holo_blue_light,
                    android.R.color.holo_red_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_green_light);
        }
        historyList = (RecyclerView) findViewById(R.id.historyList);
        historyList.setLayoutManager(new LinearLayoutManager(this));    //设置LinearLayoutManager
        historyList.setItemAnimator(new DefaultItemAnimator());         //设置ItemAnimator
        historyList.setHasFixedSize(true);                              //设置固定大小

        FROM_INTERNET = true;
//        internet_history = new ArrayList<>();
        internet_history = getListItem();
//        local_history = getListItem();
        /**
         * 创建适配器
         */
//        historyListAdapter = new HistoryListAdapter(this, local_history);
        historyListAdapter = new HistoryListAdapter(this, internet_history);
        historyList.setAdapter(historyListAdapter);

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
                    }
                }, 3000);
            }
        });
    }

    private ArrayList<com.zjy.trafficassist.model.AlarmHistory> getListItem() {

        final ArrayList<com.zjy.trafficassist.model.AlarmHistory> history_item = new ArrayList<>();

        if (FROM_INTERNET) {
            final ProgressDialog mPDialog = new ProgressDialog(AlarmHistory.this);
            mPDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            mPDialog.setMessage(getResources().getString(R.string.now_loading));
            mPDialog.setCancelable(true);
            mPDialog.show();
            new AsyncTask<Void, Void, ArrayList<com.zjy.trafficassist.model.AlarmHistory>>() {

                @Override
                protected ArrayList<com.zjy.trafficassist.model.AlarmHistory> doInBackground(Void... params) {

                    String history_list = WebService.DownloadHistory();
                    try {
                        return TransForm.DownloadHistory(history_list);
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(final ArrayList<com.zjy.trafficassist.model.AlarmHistory> ReturnCode) {
                    super.onPostExecute(ReturnCode);
                    if (ReturnCode != null) {
                        for (int i = 0; i < ReturnCode.size(); i++) {
                            history_item.add(ReturnCode.get(i));
                        }
                        mPDialog.dismiss();
                        historyListAdapter.notifyDataSetChanged();
                    } else {
                        mPDialog.dismiss();
                        Toast.makeText(AlarmHistory.this, "没有历史记录", Toast.LENGTH_SHORT).show();
                    }
//                    mPDialog.dismiss();
                }
            }.execute();
        }
//        databaseManager.SaveHistory(item_history);
        return history_item;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}

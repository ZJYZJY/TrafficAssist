package com.zjy.trafficassist.overlay;

import android.content.Context;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.CoordinatorLayout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.zjy.trafficassist.R;
import com.zjy.trafficassist.model.IssueType;
import com.zjy.trafficassist.model.RoadIssue;
import com.zjy.trafficassist.utils.TransForm;

import java.util.List;

/**
 * com.zjy.trafficassist.overlay
 * Created by 73958 on 2017/6/2.
 */

public class PointOverlay {

    private List<Marker> pointMarkers;
    private List<RoadIssue> mIssues;
    private AMap mAMap;
    private Context mContext;

    public PointOverlay(Context context, AMap aMap, List<RoadIssue> issues) {
        mContext = context;
        mAMap = aMap;
        mIssues = issues;
    }

    public void addToMap() {
        for (int i = 0; i < mIssues.size(); i++) {
            addMarker(new MarkerOptions()
                    .position(mIssues.get(i).getLatLng())
                    .title("road_issue")
                    .snippet(i+"")
                    .anchor(0.5f, 0.5f)
                    .icon(getBitmapDescriptor(mIssues.get(i).getType())));
        }
    }

    /**
     * 给Marker设置图标，并返回更换图标的图片。
     *
     * @return 更换的Marker图片。
     * @since V2.1.0
     */
    private BitmapDescriptor getBitmapDescriptor(IssueType type) {
        int id = 0;
        switch (type){
            case ROAD_JAM:
                id = R.mipmap.road_jam;
                break;
            case ROAD_CLOSE:
                id = R.mipmap.road_close;
                break;
            case ROAD_WATER:
                id = R.mipmap.road_water;
                break;
            case ROAD_CONSTRUCTION:
                id = R.mipmap.road_construction;
                break;
        }
        return BitmapDescriptorFactory.fromResource(id);
    }

    private void addMarker(MarkerOptions options) {
        if (options == null) {
            return;
        }
        Marker marker = mAMap.addMarker(options);
    }

    /**
     * 去掉PointOverlay上所有的Marker。
     *
     * @since V2.1.0
     */
    public void remove() {
        if (pointMarkers != null) {
            for (Marker marker : pointMarkers) {
                marker.remove();
            }
        }
    }

    public void showBSDialog(RoadIssue issue) {
        BottomSheetDialog dialog = new BottomSheetDialog(mContext);
        final View view = LayoutInflater.from(mContext).inflate(R.layout.issue_point_dialog_layout, null);
        ImageView image = (ImageView) view.findViewById(R.id.issue_type_iv);
        TextView type = (TextView) view.findViewById(R.id.bsd_issue_type);
        TextView address = (TextView) view.findViewById(R.id.bsd_issue_address);
        TextView issue_level = (TextView) view.findViewById(R.id.bsd_issue_level);
        TextView issue_detail = (TextView) view.findViewById(R.id.bsd_issue_detail);

        switch (issue.getType()){
            case ROAD_JAM:
                image.setImageResource(R.drawable.ic_issue_road_jam);
                type.setText(R.string.road_jam);
                address.setText(issue.getAddress() + "有" + mContext.getString(R.string.road_jam));
                break;
            case ROAD_CLOSE:
                image.setImageResource(R.drawable.ic_issue_road_close);
                type.setText(R.string.road_close);
                address.setText(issue.getAddress() + "有" + mContext.getString(R.string.road_close));
                break;
            case ROAD_WATER:
                image.setImageResource(R.drawable.ic_issue_road_water);
                type.setText(R.string.road_water);
                address.setText(issue.getAddress() + "有" + mContext.getString(R.string.road_water));
                break;
            case ROAD_CONSTRUCTION:
                image.setImageResource(R.drawable.ic_issue_road_construction);
                type.setText(R.string.road_construction);
                address.setText(issue.getAddress() + "有" + mContext.getString(R.string.road_construction));
                break;
        }
        issue_level.setText("程度：" + issue.getDetail_tag());
        issue_detail.setText("详情：" + issue.getDetail());

        dialog.setContentView(view);
        View parent = (View) view.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        view.measure(0, 0);
        behavior.setPeekHeight(512);
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(params);
        dialog.show();
    }
}

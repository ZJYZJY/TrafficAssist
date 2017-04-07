package com.zjy.trafficassist.ui;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.Circle;
import com.amap.api.maps.model.CircleOptions;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.nearby.NearbyInfo;
import com.amap.api.services.nearby.NearbySearch;
import com.amap.api.services.nearby.NearbySearch.NearbyQuery;
import com.amap.api.services.nearby.NearbySearchFunctionType;
import com.amap.api.services.nearby.NearbySearchResult;
import com.zjy.trafficassist.R;
import com.zjy.trafficassist.UserStatus;
import com.zjy.trafficassist.WebService;
import com.zjy.trafficassist.model.User;
import com.zjy.trafficassist.utils.AutoLogin;
import com.zjy.trafficassist.utils.ConnectIMServer;
import com.zjy.trafficassist.utils.LogUtil;
import com.zjy.trafficassist.utils.SensorEventHelper;
import com.zjy.trafficassist.utils.UserLoginTask;

import java.util.HashMap;
import java.util.Map;

import io.rong.imkit.RongIM;
import io.rong.imlib.model.Conversation;

import static com.zjy.trafficassist.UserStatus.LOGIN_STATUS;
import static com.zjy.trafficassist.UserStatus.EDITOR;
import static com.zjy.trafficassist.UserStatus.first_show;
import static com.zjy.trafficassist.UserStatus.SP;
import static com.zjy.trafficassist.UserStatus.USER;

public class MapActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LocationSource, AMapLocationListener, View.OnClickListener, NearbySearch.NearbyListener {

    /**
     * 定位圈的颜色
     */
    private static final int STROKE_COLOR = Color.argb(180, 3, 145, 255);
    private static final int FILL_COLOR   = Color.argb(10, 0, 0, 180);

    private MapView mapView;
    private AMap aMap;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;
    private Marker mLocMarker;
    private Circle mCircle;
    private LatLng location;

    private SensorEventHelper mSensorHelper;
    private FloatingActionButton fab_post;
    private Button login;
    private NavigationView navigationView;
    private DrawerLayout drawer;
    private LinearLayout unlogin, logined;
    private ImageView display_user_pic;
    private Button display_user_name;

    private long exitTime = 0;
    private boolean mFirstFix = false;
    private Map<String, Boolean> supportConversation = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        fab_post = (FloatingActionButton) findViewById(R.id.fab_post);
        View headerView = navigationView.getHeaderView(0);
        login = (Button) headerView.findViewById(R.id.login_map_aty);
        unlogin = (LinearLayout) headerView.findViewById(R.id.unlogin);
        logined = (LinearLayout) headerView.findViewById(R.id.logined);
        display_user_pic = (ImageView) headerView.findViewById(R.id.display_user_pic);
        display_user_name = (Button) headerView.findViewById(R.id.display_user_name);

        fab_post.setOnClickListener(this);
        login.setOnClickListener(this);

        //地图初始化
        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        Initial();
        //获取附近实例
        NearbySearch PoliceNearbySearch = NearbySearch.getInstance(getApplicationContext());
        //设置附近监听
        PoliceNearbySearch.addNearbyListener(this);

        // 设置支持聊天信息的类型
        supportConversation.put(Conversation.ConversationType.PRIVATE.getName(), false);

        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    /**
     * 初始化地图与方向传感器
     */
    private void Initial() {
        if (aMap == null) {
            aMap = mapView.getMap();
            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
            setUpMap();
        }
        mSensorHelper = new SensorEventHelper(this);
        mSensorHelper.registerSensorListener();
        // 自动登录
        AutoLogin.getInstance().login(this);
    }

    /**
     * 设置定位监听，配置地图默认参数
     */
    private void setUpMap() {
        aMap.setLocationSource(this);// 设置定位监听
        aMap.getUiSettings().setMyLocationButtonEnabled(true);// 设置默认定位按钮可见
        //aMap.getUiSettings().setCompassEnabled(true);//设置指南针按钮可见
        aMap.getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER);
        aMap.setMyLocationEnabled(true);// 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        // 设置定位的类型为定位模式，参见类AMap。
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_FOLLOW);
    }

    @Override
    public void activate(LocationSource.OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //退出时杀死进程
            mLocationOption.setKillProcess(true);
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置是否返回地址信息（默认返回地址信息）
            mLocationOption.setNeedAddress(true);
            //设置定位间隔,单位毫秒,默认为2000ms
            mLocationOption.setInterval(2000);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    private void addCircle(LatLng latlng, double radius) {
        CircleOptions options = new CircleOptions();
        options.strokeWidth(1f);
        options.fillColor(FILL_COLOR);
        options.strokeColor(STROKE_COLOR);
        options.center(latlng);
        options.radius(radius);
        mCircle = aMap.addCircle(options);
    }

    private Marker addMarker(LatLng latlng) {
        if (mLocMarker != null) {
            return null;
        }
		BitmapDescriptor des = BitmapDescriptorFactory.fromResource(R.mipmap.navi_map_gps_locked);
        MarkerOptions options = new MarkerOptions();
        options.icon(des);
        options.anchor(0.5f, 0.5f);
        options.position(latlng);
        mLocMarker = aMap.addMarker(options);
        return mLocMarker;
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation.getErrorCode() == 0) {
                //显示系统小蓝点
                //mListener.onLocationChanged(amapLocation);
                location = new LatLng(amapLocation.getLatitude(), amapLocation.getLongitude());
                //搜索附近的交警信息
                NearbySearchCondition();
                if(UserStatus.USER != null) {
                    UserStatus.USER.setLocation(location);
                }
                if (!mFirstFix) {
                    mFirstFix = true;
                    addCircle(location, amapLocation.getAccuracy());//添加定位精度圆
                    addMarker(location).setFlat(true);//添加定位图标，并贴附在地图上
                    aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15));
                } else {
                    mCircle.setCenter(location);// 改变定位精度圆中心
                    mCircle.setRadius(amapLocation.getAccuracy());// 改变定位精度圆半径
                    mLocMarker.setPosition(location);
                }
                if(mSensorHelper != null)
                    mSensorHelper.setCurrentMarker(mLocMarker);//定位图标旋转
                //mlocationClient.stopLocation(); //停止定位
            } else {
                String errText = "定位失败," + amapLocation.getErrorCode() + ": " + amapLocation.getErrorInfo();
                LogUtil.e("AmapErr", errText);
                Toast.makeText(getApplicationContext(), errText, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.view_change) {
            //设置地图类型
            AlertDialog.Builder builder = new AlertDialog.Builder(MapActivity.this);
            final String[] views = {"标准地图", "卫星地图", "夜间地图"};
            builder.setItems(views, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case 0:
                            aMap.setMapType(AMap.MAP_TYPE_NORMAL);
                            break;
                        case 1:
                            aMap.setMapType(AMap.MAP_TYPE_SATELLITE);
                            break;
                        case 2:
                            aMap.setMapType(AMap.MAP_TYPE_NIGHT);
                            break;
                    }
                }
            }).show();
            return true;
        } else if (id == R.id.rt_traffic) {
            //设置实时交通路况
            if (item.isChecked()) {
                item.setChecked(false);
                aMap.setTrafficEnabled(false);
            } else {
                item.setChecked(true);
                aMap.setTrafficEnabled(true);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        //侧边导航栏按键监听事件
        int id = item.getItemId();
        if(LOGIN_STATUS) {
            if (id == R.id.user_info) {
                startActivity(new Intent(MapActivity.this, UserInfo.class));
            } else if (id == R.id.alarm_history) {
                startActivity(new Intent(MapActivity.this, AlarmHistory.class));
            } else if (id == R.id.nav_refer) {

            }
        }else {
            Toast.makeText(MapActivity.this, "请您先登录", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(MapActivity.this, LoginActivity.class));
        }
        if (id == R.id.nav_setting) {

        } else if (id == R.id.nav_chat) {
            RongIM.getInstance().startConversationList(MapActivity.this, supportConversation);
        } else if(id == R.id.nav_about){
            UserStatus.ClearUserLoginStatus(this);
//            Toast.makeText(MapActivity.this, "数据库有" + (new DatabaseManager(this)).getUserCount()
//                    + "条数据", Toast.LENGTH_SHORT).show();
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onClick(View v) {
        //主界面按钮监听事件
        switch (v.getId()) {
            case R.id.fab_post:
                if(LOGIN_STATUS)
                    startActivity(new Intent(MapActivity.this, PostMessage.class));
                else{
                    Toast.makeText(MapActivity.this, "请您先登录", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(MapActivity.this, LoginActivity.class));
                }
                break;
            case R.id.login_map_aty:
                startActivity(new Intent(MapActivity.this, LoginActivity.class));
                break;
            case R.id.display_user_name:
                startActivity(new Intent(MapActivity.this, UserInfo.class));
                break;
        }
        drawer.closeDrawer(GravityCompat.START);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // 按下键盘上返回按钮出现退出提示
        if(keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN){
            if((System.currentTimeMillis() - exitTime) > 2000){
                Toast.makeText(MapActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    //MapView生命周期
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
        if(USER != null){
            display_user_name.setText(USER.getUsername());
        }
        logined.setVisibility(LOGIN_STATUS ? View.VISIBLE : View.GONE);
        unlogin.setVisibility(LOGIN_STATUS ? View.GONE : View.VISIBLE);
        if (mSensorHelper == null) {
            mSensorHelper = new SensorEventHelper(this);
        }
        mSensorHelper.registerSensorListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mSensorHelper != null) {
            mSensorHelper.unRegisterSensorListener();
            mSensorHelper.setCurrentMarker(null);
            mSensorHelper = null;
        }
        mapView.onPause();
        mFirstFix = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        if(null != mlocationClient){
            mlocationClient.onDestroy();
        }
        NearbySearch.destroy();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    //设置搜索条件
    public void NearbySearchCondition(){
        NearbyQuery query = new NearbyQuery();
        //设置搜索的中心点
        query.setCenterPoint(new LatLonPoint(location.latitude, location.longitude));
        //设置搜索的坐标体系
        query.setCoordType(NearbySearch.AMAP);
        //设置搜索半径
        query.setRadius(5000);
        //设置查询的时间
        query.setTimeRange(10000);
        //设置查询的方式驾车还是距离
        query.setType(NearbySearchFunctionType.DRIVING_DISTANCE_SEARCH);
        //调用异步查询接口
        NearbySearch.getInstance(getApplicationContext())
                .searchNearbyInfoAsyn(query);
    }

    @Override
    public void onUserInfoCleared(int i) {

    }

    @Override
    public void onNearbyInfoSearched(NearbySearchResult nearbySearchResult, int i) {
        //搜索周边附近用户回调处理
        if(i == 1000){
            if (nearbySearchResult != null
                    && nearbySearchResult.getNearbyInfoList() != null
                    && nearbySearchResult.getNearbyInfoList().size() > 0) {
                NearbyInfo nearbyInfo = nearbySearchResult.getNearbyInfoList().get(0);
                LogUtil.e("NearbySearchResult",
                        "周边搜索结果为size " + nearbySearchResult.getNearbyInfoList().size() +
                                " first："+ nearbyInfo.getUserID() + "  " +
                                nearbyInfo.getDistance()+ "  " +
                                nearbyInfo.getDrivingDistance() + "  " +
                                nearbyInfo.getTimeStamp() + "  " +
                                nearbyInfo.getPoint().toString());
            } else {
                LogUtil.e("NearbySearchResult", "周边搜索结果为空");
            }
        }
        else{
            Log.e("NearbySearchResult", "周边搜索出现异常，异常码为："+ i);
        }
    }

    @Override
    public void onNearbyInfoUploaded(int i) {

    }
}

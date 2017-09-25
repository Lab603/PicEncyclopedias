package com.lab603.picencyclopedias;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.view.Window;
import android.view.WindowManager;

import com.aurelhubert.ahbottomnavigation.AHBottomNavigation;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationItem;
import com.aurelhubert.ahbottomnavigation.AHBottomNavigationViewPager;


import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private DemoFragment demoFragment;
    private DemoViewPagerAdapter adapter;
    private ArrayList<AHBottomNavigationItem> bottomNavigationItems = new ArrayList<>();
    private Handler handler = new Handler();

    // UI
    private AHBottomNavigationViewPager viewPager;
    private AHBottomNavigation bottomNavigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
//        ActionBar actionBar = getSupportActionBar();
//        if (actionBar != null) {
//            actionBar.hide();
//        }
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        boolean enabledTranslucentNavigation = getSharedPreferences("shared", Context.MODE_PRIVATE)
                .getBoolean("translucentNavigation", false);
        setTheme(enabledTranslucentNavigation ? R.style.AppTheme_TranslucentNavigation : R.style.AppTheme);
        setContentView(R.layout.activity_main);
        initUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    /**
     * Init UI
     */
    private void initUI() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }

        bottomNavigation = (AHBottomNavigation) findViewById(R.id.bottom_navigation);
        viewPager = (AHBottomNavigationViewPager) findViewById(R.id.view_pager);

        AHBottomNavigationItem item1 = new AHBottomNavigationItem(R.string.tab_1, R.drawable.ic_menu, R.color.color_tab_1);
        AHBottomNavigationItem item2 = new AHBottomNavigationItem(R.string.tab_2, R.drawable.ic_camera, R.color.color_tab_2);
        AHBottomNavigationItem item3 = new AHBottomNavigationItem(R.string.tab_3, R.drawable.ic_face, R.color.color_tab_3);

        bottomNavigationItems.add(item1);
        bottomNavigationItems.add(item2);
        bottomNavigationItems.add(item3);
        // Change colors
        bottomNavigation.setAccentColor(Color.parseColor("#383639"));
        bottomNavigation.setInactiveColor(Color.parseColor("#AEB8C3"));

        bottomNavigation.addItems(bottomNavigationItems);

        bottomNavigation.setTranslucentNavigationEnabled(true);

        bottomNavigation.setOnTabSelectedListener(new AHBottomNavigation.OnTabSelectedListener() {
            @Override
            public boolean onTabSelected(int position, boolean wasSelected) {

                    if (demoFragment == null) {
                        demoFragment = adapter.getMenuFragment();
                    }

                    if (wasSelected) {
                        demoFragment.refresh();
                        return true;
                    }

                    if (demoFragment != null) {
                        demoFragment.willBeHidden();
                    }

                    viewPager.setCurrentItem(position, false);

                    if (demoFragment == null) {
                        return true;
                    }


                demoFragment = adapter.getMenuFragment();
                demoFragment.willBeDisplayed();



                return true;
            }
        });

		/*
        bottomNavigation.setOnNavigationPositionListener(new AHBottomNavigation.OnNavigationPositionListener() {
			@Override public void onPositionChange(int y) {
				Log.d("DemoActivity", "BottomNavigation Position: " + y);
			}
		});
		*/

        viewPager.setOffscreenPageLimit(4);
        adapter = new DemoViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);

        demoFragment = adapter.getMenuFragment();


    }

    /**
     * Reload activity
     */
//    public void reloadCameraActivity() {
//        startActivity(new Intent(this, CameraActivity.class));
//        finish();
//    }


}

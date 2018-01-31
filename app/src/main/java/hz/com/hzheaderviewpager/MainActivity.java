package hz.com.hzheaderviewpager;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;

import java.util.ArrayList;
import java.util.List;

import hz.com.hzheaderviewpager.fragment.GridViewFragment;
import hz.com.hzheaderviewpager.fragment.ListViewFragment;
import hz.com.hzheaderviewpager.fragment.RecyclerViewFragment;
import hz.com.hzheaderviewpager.fragment.ScrollViewFragment;
import hz.com.hzheaderviewpager.fragment.WebViewFragment;
import hz.com.hzheaderviewpager.fragment.base.HeaderViewPagerFragment;
import hz.com.hzheaderviewpager.utils.Utils;
import hz.com.hzheaderviewpager.widget.HeaderViewPager;

public class MainActivity extends BaseActivity {

    public List<HeaderViewPagerFragment> fragments;
    private HeaderViewPager scrollableLayout;
    private SliderLayout mtDefaultSlider;
    private View titleBar_Bg;
    private TextView titleBar_title;
    private View status_bar_fix;
    private View titleBar;
    private long firstTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initTitleBar();
        initViewPager();
        initSlide();
    }

    /**
     * 初始化banner
     */
    private void initSlide() {
        mtDefaultSlider = (SliderLayout) findViewById(R.id.default_slider);

        int[] imgs = {R.mipmap.image1, R.mipmap.image2, R.mipmap.image3, R.mipmap.image4};
        for (int i = 0, ilen = imgs.length; i < ilen; i++){
            DefaultSliderView defaultSliderView = new DefaultSliderView(getApplicationContext());
            defaultSliderView.image(imgs[i]);
            defaultSliderView.setScaleType(BaseSliderView.ScaleType.Fit);
            mtDefaultSlider.addSlider(defaultSliderView);
        }

        mtDefaultSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mtDefaultSlider.setCustomAnimation(new DescriptionAnimation());
        mtDefaultSlider.setPresetTransformer(SliderLayout.Transformer.RotateDown);
        mtDefaultSlider.setDuration(5000);
    }

    /**
     * 初始化viewpager
     */
    private void initViewPager() {
        //内容的fragment
        fragments = new ArrayList<>();
        fragments.add(ScrollViewFragment.newInstance());
        fragments.add(ListViewFragment.newInstance());
        fragments.add(GridViewFragment.newInstance());
        fragments.add(RecyclerViewFragment.newInstance());
        fragments.add(WebViewFragment.newInstance());

        scrollableLayout = (HeaderViewPager) findViewById(R.id.scrollableLayout);

        TabLayout tab_layout = (TabLayout)findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new ContentAdapter(getSupportFragmentManager()));
        tab_layout.setupWithViewPager(viewPager);
        tab_layout.setTabMode(TabLayout.MODE_SCROLLABLE);
        scrollableLayout.setCurrentScrollableContainer(fragments.get(0));
        viewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                scrollableLayout.setCurrentScrollableContainer(fragments.get(position));
            }
        });
        scrollableLayout.setOnScrollListener(new HeaderViewPager.OnScrollListener() {
            @Override
            public void onScroll(int currentY, int maxY) {
                //让头部具有差速动画,如果不需要,可以不用设置
                mtDefaultSlider.setTranslationY(currentY / 2);
                //动态改变标题栏的透明度,注意转化为浮点型
                float alpha = 1.0f * currentY / maxY;
                titleBar_Bg.setAlpha(alpha);
                //注意头部局的颜色也需要改变
                status_bar_fix.setAlpha(alpha);
                titleBar_title.setText("标题");
            }
        });
        viewPager.setCurrentItem(0);
    }

    /**
     * 初始化头部
     */
    private void initTitleBar() {
        titleBar = findViewById(R.id.titleBar);
        titleBar_Bg = titleBar.findViewById(R.id.bg);
        //当状态栏透明后，内容布局会上移，这里使用一个和状态栏高度相同的view来修正内容区域
        status_bar_fix = titleBar.findViewById(R.id.status_bar_fix);
        status_bar_fix.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, Utils.getStatusHeight(this)));
        titleBar_title = (TextView) titleBar.findViewById(R.id.title);
        titleBar_Bg.setAlpha(0);
        status_bar_fix.setAlpha(0);
        titleBar_title.setText("标题");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //当前窗口获取焦点时，才能正真拿到titlebar的高度，此时将需要固定的偏移量设置给scrollableLayout即可
        scrollableLayout.setTopOffset(titleBar.getHeight());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            long secondTime = System.currentTimeMillis();
            if (secondTime - firstTime > 2000) {
                Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
                firstTime = secondTime;
                return true;
            } else {
                System.exit(0);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 内容页的适配器
     */
    private class ContentAdapter extends FragmentPagerAdapter {

        public ContentAdapter(FragmentManager fm) {
            super(fm);
        }

        public String[] titles = new String[]{"ScrollView", "ListView", "GridView", "RecyclerView", "WebView"};

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}

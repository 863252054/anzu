package com.example.anzu.ui.guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.viewpager.widget.ViewPager;

import com.example.anzu.R;
import com.example.anzu.ui.login.LoginActivity;

import java.util.ArrayList;
import java.util.List;

public class GuideActivity extends Activity implements ViewPager.OnPageChangeListener {
    private ViewPager mViewPager;
    //图片资源的数组
    private int[] mImageIdArray;
    //图片的集合
    private List<View> mViewList;
    //放置圆点
    private ViewGroup mViewGroup;
    //实例化圆点View
    private ImageView mImageView;
    private ImageView[] mImageViewArray;
    //最后一页的按钮
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_guide);

        mButton = (Button) findViewById(R.id.vp_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GuideActivity.this, LoginActivity.class));
                finish();
            }
        });
        //加载ViewPager
        initViewpager();
        //加载底部圆点
        initViewPagerTag();

    }

    /**
     * 加载底部圆点
     */
    private void initViewPagerTag() {
        //这里实例化LinearLayout
        mViewGroup = (ViewGroup) findViewById(R.id.vp_tag);
        //根据ViewPager的item数量实例化数组
        mImageViewArray = new ImageView[mViewList.size()];
        //循环新建底部圆点imageview，将生成的imageview保存到数组中
        for (int i = 0; i < mViewList.size(); i++) {
            mImageView = new ImageView(this);
            mImageView.setLayoutParams(new ViewGroup.LayoutParams(80, 80));
            mImageView.setPadding(50, 0, 50, 0);
            mImageViewArray[i] = mImageView;
            //第一个页面需要设为选中状态，这里要使用两张不同的图片（选中与未选中）
            if (i == 0) {
                mImageView.setBackgroundResource(R.drawable.dot_focus);
            } else {
                mImageView.setBackgroundResource(R.drawable.dot);
            }
            //将数组中的imageview加入到viewgroup
            mViewGroup.addView(mImageViewArray[i]);
        }
    }

    /**
     * 加载ViewPager
     */
    private void initViewpager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        //实例化图片资源
        mImageIdArray = new int[]{R.drawable.bg1, R.drawable.bg2, R.drawable.bg3};

        mViewList = new ArrayList<View>();
        //获取一个layout参数，设置为match_parent
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT
                , LinearLayout.LayoutParams.MATCH_PARENT);

        //循环创建view并进入集合
        for (int i = 0; i < mImageIdArray.length; i++) {
            //new imageview并设置全屏和图片资源
            ImageView imageView = new ImageView(this);
            imageView.setLayoutParams(params);
            imageView.setBackgroundResource(mImageIdArray[i]);

            //将imageview加入到View集合中
            mViewList.add(imageView);
        }
        //View集合数据初始化好，setAdapter就可以了
        mViewPager.setAdapter(new GuidePagerAdapter(mViewList));
        //添加ViewPager的滑动监听，注意是.add...以前是setOnPageChangeListener方法，已过时
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }
    //滑动后的监听
    @Override
    public void onPageSelected(int position) {
        //循环设置当前页的标记图
        for (int i = 0; i < mImageViewArray.length; i++) {
            mImageViewArray[position].setBackgroundResource(R.drawable.dot_focus);
            if (position != i) {
                mImageViewArray[i].setBackgroundResource(R.drawable.dot);
            }
        }
        //判断是否最后一页，是则显示button
        if (position == mImageViewArray.length - 1) {
            mButton.setVisibility(View.VISIBLE);
        } else {
            mButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}

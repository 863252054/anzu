package com.example.anzu.ui.msg;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.example.anzu.R;
import com.example.anzu.ui.msg.msgTab.ChatFragment;
import com.example.anzu.ui.msg.msgTab.NoticeFragment;
import com.google.android.material.tabs.TabLayout;

public class MsgFragment extends Fragment {
    private String[] title = {"通知", "消息"};
    private TabLayout tabLayout;
    private ViewPager viewPager;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_msg, container, false);
        tabLayout = (TabLayout) root.findViewById(R.id.msg_tab_layout);
        viewPager = (ViewPager) root.findViewById(R.id.msg_view_pager);
        MyPagerAdapter adapter = new MyPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        return root;
    }
    //tabLayout
    class MyPagerAdapter extends FragmentPagerAdapter {
        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return title[position];
        }

        @Override
        public Fragment getItem(int position) {
            Fragment noticeFragment = new NoticeFragment();
            Fragment chatFragment = new ChatFragment();
            Bundle bundle = new Bundle();
            bundle.putString("title", title[position]);
            if (position == 0) {
                noticeFragment.setArguments(bundle);
                return noticeFragment;
            }
            chatFragment.setArguments(bundle);
            return chatFragment;
        }

        @Override
        public int getCount() {
            return title.length;
        }
    }
}

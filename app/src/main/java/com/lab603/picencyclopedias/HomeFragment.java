package com.lab603.picencyclopedias;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by EdwardZhang on 2017/9/29.
 */

public class HomeFragment extends Fragment {
    private FrameLayout fragmentContainer;

    /**
     * Create a new instance of the fragment
     */
    public static MenuFragment newInstance(int index) {
        MenuFragment fragment = new MenuFragment();
        Bundle b = new Bundle();
        b.putInt("index", index);
        fragment.setArguments(b);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.pk_home, container, false);
        initHomePage(view);
        return view;

    }

    private void initHomePage(View view) {

    }
}

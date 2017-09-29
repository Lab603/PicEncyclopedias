package com.lab603.picencyclopedias;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 *
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

	private ArrayList<Fragment> fragments = new ArrayList<>();
	private MenuFragment menuFragment;

	public ViewPagerAdapter(FragmentManager fm) {
		super(fm);

		fragments.clear();
		fragments.add(MenuFragment.newInstance(0));
		fragments.add(MenuFragment.newInstance(1));
		fragments.add(MenuFragment.newInstance(2));
	}

	@Override
	public Fragment getItem(int position) {
		return fragments.get(position);
	}

	@Override
	public int getCount() {
		return fragments.size();
	}

	@Override
	public void setPrimaryItem(ViewGroup container, int position, Object object) {
		if (getMenuFragment() != object) {
			menuFragment = (MenuFragment) object;
		}
		super.setPrimaryItem(container, position, object);
	}

	/**
	 * Get the current fragment
	 */
	public MenuFragment getMenuFragment() {
		return menuFragment;
	}


}
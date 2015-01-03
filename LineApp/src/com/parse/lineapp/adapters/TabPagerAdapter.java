package com.parse.lineapp.adapters;

import com.parse.lineapp.fragments.HomeFragment;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
public class TabPagerAdapter extends FragmentStatePagerAdapter {
    
	public TabPagerAdapter(FragmentManager fm) {
    super(fm);
    // TODO Auto-generated constructor stub
  }
	
  @Override
  public Fragment getItem(int i) {
    switch (i) {
        case 0:
            return new HomeFragment();
        case 1:
        	return new HomeFragment();
        case 2:
        	return new HomeFragment();
        }
    return null;
  }
  @Override
  public int getCount() {
    // TODO Auto-generated method stub
    return 3; //No of Tabs
  }
    }

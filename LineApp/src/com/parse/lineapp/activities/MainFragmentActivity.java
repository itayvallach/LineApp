package com.parse.lineapp.activities;
import com.parse.lineapp.R;
import com.parse.lineapp.adapters.TabPagerAdapter;

import android.os.Bundle;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class MainFragmentActivity extends FragmentActivity {
  ViewPager Tab;
  TabPagerAdapter TabAdapter;
  ActionBar actionBar;
  
    @SuppressLint("NewApi")
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_fragment_activity);
        TabAdapter = new TabPagerAdapter(getSupportFragmentManager());
        Tab = (ViewPager)findViewById(R.id.pager);
        Tab.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                      actionBar = getActionBar();
                      actionBar.setSelectedNavigationItem(position);                    }
                });
        Tab.setAdapter(TabAdapter);
        actionBar = getActionBar();
//        actionBar.setDisplayShowHomeEnabled(false);
//        actionBar.setDisplayShowTitleEnabled(false);
        //Enable Tabs on Action Bar
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        ActionBar.TabListener tabListener = new ActionBar.TabListener(){
      @Override
      public void onTabReselected(android.app.ActionBar.Tab tab,
          FragmentTransaction ft) {
        // TODO Auto-generated method stub
      }
      @Override
       public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
              Tab.setCurrentItem(tab.getPosition());
          }
      @Override
      public void onTabUnselected(android.app.ActionBar.Tab tab,
          FragmentTransaction ft) {
        // TODO Auto-generated method stub
      }};
      //Add New Tab
      
      actionBar.addTab(actionBar.newTab().setText("אירועים שלי").setTabListener(tabListener), 0, false);
      actionBar.addTab(actionBar.newTab().setText("מפה").setTabListener(tabListener), 1, false);
      actionBar.addTab(actionBar.newTab().setText("ראשי").setTabListener(tabListener), 2, true);
      
    }
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_search:
                
                return true;
            case R.id.action_create_new_event:
            	Intent intent = new Intent(this, NewEventActivity.class);
				startActivity(intent);
                return true;
                
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	MenuInflater inflater = getMenuInflater();
    	inflater.inflate(R.menu.main_activity_actions, menu);
    	return super.onCreateOptionsMenu(menu);
    	
    }
}
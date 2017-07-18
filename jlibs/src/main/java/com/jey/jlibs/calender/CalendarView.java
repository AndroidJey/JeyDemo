package com.jey.jlibs.calender;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.jey.jlibs.utils.TimeUtils;

import java.util.List;

public class CalendarView extends RelativeLayout {

	/**
	 * 和viewpager相关变量
	 * */
	public MonthViewPager viewPager = null;
	public MyPagerAdapter pagerAdapter = null;
	private int currPager = 500;
	private TextView shader;

	/**
	 * 和日历gridview相关变量
	 * */
	private GridView gridView = null;
	public CalendarAdapter adapter = null;
	private GridView currentView = null;
	public List<DateInfo> currList = null;
	public List<DateInfo> list = null;
	public int lastSelected = 0;

	/**
	 * 显示年月
	 * */
	public TextView showYearMonth = null;

	/**
	 * 第一个页面的年月
	 * */
	private int currentYear;
	private int currentMonth;

	public CalendarView(Context context) {
		super(context);
	}

	public CalendarView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CalendarView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	@SuppressLint("NewApi")
	public CalendarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	/**
	 * 初始化view
	 * */
	private void initView() {
		initData();
    	viewPager = new MonthViewPager(getContext());
		addView(viewPager,new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
    	pagerAdapter = new MyPagerAdapter();
    	viewPager.setAdapter(pagerAdapter);
    	viewPager.setCurrentItem(500);
    	viewPager.setPageMargin(0);
//    	showYearMonth = (TextView) findViewById(R.id.main_year_month);
//    	showYearMonth.setText(String.format("%04d年%02d月", currentYear, currentMonth));
    	
    	viewPager.setOnPageChangeListener(new OnPageChangeListener() {
			public void onPageScrollStateChanged(int arg0) {
				if (arg0 == 0) {
//					currentView = (GridView) viewPager.getChildAt(3);
//					if (currentView != null) {
//						adapter = (CalendarAdapter) currentView.getAdapter();
//						currList = adapter.getList();
//						int pos = DateUtils.getDayFlag(currList, lastSelected);
//						adapter.setSelectedPosition(pos);
//						adapter.notifyDataSetInvalidated();
//					}
//					shader.setVisibility(View.GONE);
				}
			}

			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			public void onPageSelected(int position) {
		    	int year = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "year");
		    	int month = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "month");
		    	showYearMonth.setText(String.format("%04d年%02d月", year, month));
				currPager = position;
				shader.setText(showYearMonth.getText());
			}
    	});
    }
    
	/**
	 * 初始化数据
	 * */
    private void initData() {
    	currentYear = TimeUtils.getCurrentYear();
    	currentMonth = TimeUtils.getCurrentMonth();
    	lastSelected = TimeUtils.getCurrentDay();
    	String formatDate = TimeUtils.getFormatDate(currentYear, currentMonth);
    	try {
    		list = TimeUtils.initCalendar(formatDate, currentMonth);
    	} catch (Exception e) {
//    		finish();
    	}
    }
    
    /**
	 * 初始化日历的gridview
	 * */
    private GridView initCalendarView(int position) {
    	int year = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "year");
    	int month = TimeUtils.getTimeByPosition(position, currentYear, currentMonth, "month");
    	String formatDate = TimeUtils.getFormatDate(year, month);
    	try {
    		list = TimeUtils.initCalendar(formatDate, month);
    	} catch (Exception e) {
//    		finish();
    	}
		gridView = new GridView(getContext());
    	adapter = new CalendarAdapter(getContext(), list);
    	if (position == 500) {
    		currList = list;
	    	int pos = DateUtils.getDayFlag(list, lastSelected);
	    	adapter.setSelectedPosition(pos);
    	}
    	gridView.setAdapter(adapter);
    	gridView.setNumColumns(7);
    	gridView.setSelector(new ColorDrawable(Color.TRANSPARENT));
    	gridView.setGravity(Gravity.CENTER);
//    	gridView.setOnItemClickListener(new OnItemClickListenerImpl(adapter, this));
    	return gridView;
    }
    
    /**
	 * viewpager的适配器，从第500页开始，最多支持0-1000页
	 * */
    private class MyPagerAdapter extends PagerAdapter {

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			currentView = (GridView) object;
			adapter = (CalendarAdapter) currentView.getAdapter();
		}

		@Override
		public int getCount() {
			return 1000;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			GridView gv = initCalendarView(position);
			gv.setId(position);
			container.addView(gv);
			return gv;
		}
    }
}

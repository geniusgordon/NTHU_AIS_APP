package com.example.gordon.nthuais.activities.Schedule;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.gordon.nthuais.R;
import com.example.gordon.nthuais.models.Course;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by franktsai on 2015/9/25.
 */
public class SlidingTabsBasicFragment extends Fragment {

    static final String LOG_TAG = "SlidingTabsBasicFragment";
    private ArrayList<HashMap> stuSchedule ;
    private String[] cou_time = new String[]{"1","2","3","4","n","5","6","7","8","9","a","b","c"};
    ListView listView;
    ScheduleListAdapter adapter;


    /**
     * A custom {@link ViewPager} title strip which looks much like Tabs present in Android v4.0 and
     * above, but is designed to give continuous feedback to the user when scrolling.
     */
    private SlidingTabLayout mSlidingTabLayout;

    /**
     * A {@link ViewPager} which will be used in conjunction with the {@link SlidingTabLayout} above.
     */
    private ViewPager mViewPager;



    /**
     * Inflates the {@link View} which will be displayed by this {@link Fragment}, from the app's
     * resources.
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        stuSchedule= (ArrayList<HashMap>)getArguments().getSerializable("stuSCH");


        return inflater.inflate(R.layout.schedule_fragment, container, false);
    }

    // BEGIN_INCLUDE (fragment_onviewcreated)
    /**
     * This is called after the {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)} has finished.
     * Here we can pick out the {@link View}s we need to configure from the content view.
     *
     * We set the {@link ViewPager}'s adapter to be an instance of {@link SamplePagerAdapter}. The
     * {@link SlidingTabLayout} is then given the {@link ViewPager} so that it can populate itself.
     *
     * @param view View created in {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // BEGIN_INCLUDE (setup_viewpager)
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        LayoutInflater inflater = getLayoutInflater(savedInstanceState);

        mViewPager = (ViewPager) view.findViewById(R.id.viewpager);
        mViewPager.setAdapter(new SamplePagerAdapter());


        mSlidingTabLayout = (SlidingTabLayout) view.findViewById(R.id.sliding_tabs);
        mSlidingTabLayout.setViewPager(mViewPager);
        // END_INCLUDE (setup_slidingtablayout)
    }
    // END_INCLUDE (fragment_onviewcreated)

    /**
     * The {@link android.support.v4.view.PagerAdapter} used to display pages in this sample.
     * The individual pages are simple and just display two lines of text. The important section of
     * this class is the {@link #getPageTitle(int)} method which controls what is displayed in the
     * {@link SlidingTabLayout}.
     */
    class SamplePagerAdapter extends PagerAdapter {

        /**
         * @return the number of pages to display
         */



        @Override
        public int getCount() {
            return 6;
        }

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link ViewPager}.
         */
        @Override
        public boolean isViewFromObject(View view, Object o) {
            return o == view;
        }

        // BEGIN_INCLUDE (pageradapter_getpagetitle)
        /**
         * Return the title of the item at {@code position}. This is important as what this method
         * returns is what is displayed in the {@link SlidingTabLayout}.
         * <p>
         * Here we construct one using the position value, but for real application the title should
         * refer to the item's contents.
         */
        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence d = "";
            switch (position){
                case 0: d = "星期一";
                        break;
                case 1:d = "星期二";
                        break;
                case 2:d = "星期三";
                        break;
                case 3:d = "星期四";
                        break;
                case 4:d = "星期五";
                        break;
                case 5:d = "星期六";
                        break;

            }
             return d;
        }
        // END_INCLUDE (pageradapter_getpagetitle)

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = getActivity().getLayoutInflater().inflate(R.layout.schedulepager,
                    container, false);
            // Add the newly created View to the ViewPager
            container.addView(view);

            ArrayList<HashMap> curSchedule = new ArrayList<HashMap>();
            String day = chooseday(position);
            curSchedule = haveCourse(day);
            Integer len = curSchedule.size();

           // Log.d("day",day);

            listView = (ListView) view.findViewById(R.id.schedule_list);
            adapter = new ScheduleListAdapter(mViewPager.getContext(),curSchedule,cou_time,day);
            listView.setAdapter(adapter);



            return view;
        }

        /**
         * Destroy the item from the {@link ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
           // Log.i(LOG_TAG, "destroyItem() [position: " + position + "]");
        }

    }

        private String chooseday(int position){

            String day = null;
           // Log.d("posistion",String.valueOf(position));
            switch (position){
                case 0: day = "M";
                        break;
                case 1: day = "T";
                        break;
                case 2: day = "W";
                        break;
                case 3: day = "R";
                        break;
                case 4: day = "F";
                        break;
                case 5: day = "S";
                        break;
               }
            return day;
        }
    private ArrayList<HashMap>haveCourse(String day){

        ArrayList<HashMap> todayCourse = new ArrayList<HashMap>();
        Integer len = stuSchedule.size();
        int i = 0;
        for (i = 0 ; i < len ; i++){
           // Log.d("Course",stuSchedule.get(i).get("name").toString());
            if(stuSchedule.get(i).get("time").toString().contains(day)){
                todayCourse.add(stuSchedule.get(i));
            }
        }

        return todayCourse;
    }


    class ScheduleListAdapter extends BaseAdapter {
        LayoutInflater mLayoutInflater;

        ArrayList<HashMap> curSch;
        String[] cou_time;
        String day;

        public ScheduleListAdapter(Context context,ArrayList<HashMap> curSchedule, String[]cou_time, String day) {
            mLayoutInflater = LayoutInflater.from(context);
            this.curSch = curSchedule;
            this.cou_time = cou_time;
            this.day = day;
        }

        @Override
        public int getCount() {
            return cou_time.length;
        }

        @Override
        public Object getItem(int position) {
            return cou_time[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //if (convertView == null) {

            View view = mLayoutInflater.inflate(R.layout.schedule_list_layout, null);
            ((TextView)view.findViewById(R.id.time)).setText(cou_time[position]);
            for (int i = 0 ; i < curSch.size(); i++){
                String curTime = day + cou_time[position];
                if(curSch.get(i).get("time").toString().contains(curTime)){
                  //  ((View)view.findViewById(R.id.course_box)).setBackgroundColor(Color.parseColor("#BE27D2"));
                    ((TextView)view.findViewById(R.id.chi_title)).setText(curSch.get(i).get("name").toString());
                    ((TextView)view.findViewById(R.id.classroom)).setText(curSch.get(i).get("classroom").toString());
                    ((TextView)view.findViewById(R.id.teacher)).setText(curSch.get(i).get("teacher").toString());
                   // ((TextView)view.findViewById(R.id.time)).setTextColor(Color.WHITE);
                   // ((TextView)view.findViewById(R.id.chi_title)).setTextColor(Color.WHITE);
                   // ((TextView)view.findViewById(R.id.teacher)).setTextColor(Color.WHITE);
                   // ((TextView)view.findViewById(R.id.classroom)).setTextColor(Color.WHITE);

                }
            }
            //} else {
            //}
            return view;
        }
    }

}


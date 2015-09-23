package com.example.gordon.nthuais;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gordon.nthuais.models.Course;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import me.imid.swipebacklayout.lib.SwipeBackLayout;

public class SearchActivity extends AppCompatSwipeBackActivity {

    String acixstore;
    String query;
    String courseCode;
    int page;
    int total;
    int pageSize;
    boolean searching;

    String searchUrl = "http://nthu-course.cf/search/?q=%s&code=%s&page=%s&size=%s";
    RequestQueue requestQueue;

    SwipeBackLayout mSwipeBackLayout;

    Toolbar toolbar;
    TextView resultMsg;
    ProgressBar searchProgessBar;
    ListView listView;
    SearchResultListAdapter adapter;
    ArrayList<Course> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);
        mSwipeBackLayout.setEdgeSize(500);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        resultMsg = (TextView) findViewById(R.id.resultMsg);
        searchProgessBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        listView = (ListView) findViewById(R.id.resultListView);

        initListView();

        Intent intent = getIntent();
        acixstore = intent.getStringExtra("acixstore");
        query = intent.getStringExtra("query");
        courseCode = intent.getStringExtra("courseCode");

        String title = String.format("關鍵字: %s (%s)", query, courseCode);
        getSupportActionBar().setTitle(title);

        page = 1;
        total = -1;
        pageSize = 20;
        searching = false;

        requestQueue = Volley.newRequestQueue(this);
        search();
    }

    private void initListView() {
        list = new ArrayList<Course>();
        adapter = new SearchResultListAdapter(this, list);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Course course = (Course) parent.getItemAtPosition(position);
                Log.d("Item onClick", course.getNo());
                Intent intent = new Intent(SearchActivity.this, CourseDetailActivity.class);
                intent.putExtra("acixstore", acixstore);
                intent.putExtra("course", course);
                startActivity(intent);
            }
        });
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (firstVisibleItem == totalItemCount - visibleItemCount - 1)
                    search();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_search, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    void startSearch() {
        searchProgessBar.setVisibility(View.VISIBLE);
        searching = true;
    }

    void endSearch() {
        searchProgessBar.setVisibility(View.INVISIBLE);
        searching = false;
    }

    private String getSearchUrl() {
        return String.format(searchUrl, query, courseCode, page, pageSize);
    }

    private void updateListView(JSONObject result) {
        try {
            JSONArray courses = result.getJSONArray("courses");
            for (int i = 0; i < courses.length(); i++) {
                JSONObject courseJSON = courses.getJSONObject(i);
                Course course = new Course(courseJSON);
                list.add(course);
            }
            adapter.notifyDataSetChanged();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void search() {
        if (total != -1 && page > Math.ceil((double)total/pageSize))
            return;
        if (searching)
            return;

        Log.d("Search total", String.valueOf(total));
        Log.d("Search page", String.valueOf(page));
        Log.d("Search pageSize", String.valueOf(pageSize));

        startSearch();
        Log.d("Search Url", getSearchUrl());
        StringRequest request = new StringRequest(getSearchUrl(),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Search Result", response);
                    if (response.equals("TMD")) {
                        resultMsg.setText("搜尋結果過多，請加強搜尋條件。");
                        endSearch();
                        return;
                    }
                    try {
                        JSONObject result = new JSONObject(response);
                        updateListView(result);
                        total = result.getInt("total");
                        page++;
                        if (total == 0)
                            resultMsg.setText("查無結果！請嘗試其他關鍵字。");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    endSearch();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        requestQueue.add(request);
    }

    class SearchResultListAdapter extends BaseAdapter {
        LayoutInflater mLayoutInflater;
        List<Course> courses;

        public SearchResultListAdapter(Context context, List<Course> courses) {
            mLayoutInflater = LayoutInflater.from(context);
            this.courses = courses;
        }

        @Override
        public int getCount() {
            return courses.size();
        }

        @Override
        public Object getItem(int position) {
            return courses.get(position);
        }

        @Override
        public long getItemId(int position) {
            return courses.indexOf(getItem(position));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //if (convertView == null) {
            Course course = (Course) getItem(position);
            View view = mLayoutInflater.inflate(R.layout.search_result_list_layout, null);
            String time = course.getTime();
            time = (time.length() > 4) ? time.substring(0, 4) + "\n" + time.substring(4) : time;
            ((TextView) view.findViewById(R.id.time)).setText(time);
            ((TextView) view.findViewById(R.id.teacher)).setText(course.getTeacher().replace("、", "\n"));
            ((TextView) view.findViewById(R.id.chi_title)).setText(course.getChi_title());
            ((TextView) view.findViewById(R.id.no)).setText(course.getNo());
            //} else {
            //}
            return view;
        }
    }

}

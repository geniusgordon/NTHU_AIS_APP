package com.example.gordon.nthuais;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SearchActivity extends ActionBarActivity {

    String acixstore;
    String query;
    String courseCode;
    int page;

    String searchUrl = "http://nthu-course.cf/search/?q=%s&code=%s&page=%s&size=20";
    String enterAddCourseUrl1 = "https://www.ccxp.nthu.edu.tw/ccxp/COURSE/JH/7/7.1/7.1.3/JH7130011.php";
    String enterAddCourseUrl2 = "https://www.ccxp.nthu.edu.tw/ccxp/COURSE/JH/7/7.1/7.1.3/JH713002.php";
    String addCourseUrl = "https://www.ccxp.nthu.edu.tw/ccxp/COURSE/JH/7/7.1/7.1.3/JH713005.php";
    RequestQueue requestQueue;

    ProgressBar searchProgessBar;
    ListView listView;
    SimpleAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        searchProgessBar = (ProgressBar) findViewById(R.id.searchProgressBar);
        listView = (ListView) findViewById(R.id.resultListView);

        Intent intent = getIntent();
        acixstore = intent.getStringExtra("acixstore");
        query = intent.getStringExtra("query");
        courseCode = intent.getStringExtra("courseCode");
        page = 1;
        requestQueue = Volley.newRequestQueue(this);
        search();
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
    }

    void endSearch() {
        searchProgessBar.setVisibility(View.INVISIBLE);
    }

    private String getSearchUrl() {
        return String.format(searchUrl, query, courseCode, page);
    }

    private void buildListView(String response) {
        try {
            ArrayList<HashMap<String,String>> list = new ArrayList<HashMap<String,String>>();

            JSONObject result = new JSONObject(response);
            JSONArray courses = result.getJSONArray("courses");
            for (int i = 0; i < courses.length(); i++) {
                JSONObject course = courses.getJSONObject(i);
                HashMap<String, String> item = new HashMap<String,String>();
                item.put("chi_title", course.getString("chi_title"));
                item.put("no", course.getString("no"));
                list.add(item);
            }
            adapter = new SimpleAdapter(this,
                    list,
                    android.R.layout.simple_expandable_list_item_2,
                    new String[] { "chi_title", "no" },
                    new int[] { android.R.id.text1, android.R.id.text2 }
                    );

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    final HashMap<String, String> course = (HashMap)parent.getItemAtPosition(position);
                    Log.d("Item onClick", course.get("no"));
                    AlertDialog.Builder dialog = new AlertDialog.Builder(SearchActivity.this);
                    dialog.setTitle("加選");
                    dialog.setMessage("你確定要加選這門課嗎？");
                    dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            enterAddCourse(course.get("no"), 1);
                        }
                    });
                    dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void search() {
        startSearch();
        Log.d("Search Url", getSearchUrl());
        StringRequest request = new StringRequest(getSearchUrl(),
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Search Result", response);
                    buildListView(response);
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

    private void enterAddCourse(final String courseId, final int which) {
        String url = (which == 1) ? enterAddCourseUrl1 : enterAddCourseUrl2+"?ACIXSTORE="+acixstore;
        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("enter add course", String.valueOf(which));
                        Log.d("enter add course", response);
                        if (which == 1)
                            enterAddCourse(courseId, 2);
                        else
                            addCourse(courseId);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("enter add course", String.valueOf(which));
                        if (which == 1)
                            enterAddCourse(courseId, 2);
                        else
                            addCourse(courseId);
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ACIXSTORE", acixstore);
                return params;
            }
        };
        requestQueue.add(request);
    }



    private void addCourse(final String courseId) {
        StringRequest request = new StringRequest(Request.Method.POST, addCourseUrl,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("add course", courseId);
                    Log.d("add course", response);
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ACIXSTORE", acixstore);
                params.put("ckey", courseId);
                params.put("chkbtn", "add");
                params.put("auth_num", "");
                return params;
            }
        };
        requestQueue.add(request);
    }
}

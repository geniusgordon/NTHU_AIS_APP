package com.example.gordon.nthuais;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.TypedArray;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.gordon.nthuais.models.Course;
import com.example.gordon.nthuais.views.ExtendedScrollView;

import java.util.HashMap;
import java.util.Map;

public class CourseDetailActivity extends AppCompatActivity {

    String enterAddCourseUrl1 = "https://www.ccxp.nthu.edu.tw/ccxp/COURSE/JH/7/7.1/7.1.3/JH7130011.php";
    String enterAddCourseUrl2 = "https://www.ccxp.nthu.edu.tw/ccxp/COURSE/JH/7/7.1/7.1.3/JH713002.php";
    String addCourseUrl = "https://www.ccxp.nthu.edu.tw/ccxp/COURSE/JH/7/7.1/7.1.3/JH713005.php";
    RequestQueue requestQueue;

    String acixstore;
    Course course;

    Toolbar toolbar;
    ExtendedScrollView scrollView;
    RelativeLayout titleWrapper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_course_detail);
        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        acixstore = intent.getStringExtra("acixstore");
        course = (Course) intent.getSerializableExtra("course");

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ((TextView) findViewById(R.id.title)).setText(course.getChi_title());

        titleWrapper = (RelativeLayout) findViewById(R.id.toolbarTitleWrapper);
        scrollView = (ExtendedScrollView) findViewById(R.id.scrollView);
        scrollView.setOnScrollViewListener(new ExtendedScrollView.OnScrollViewListener() {
            @Override
            public void onScrollChanged(ExtendedScrollView v, int l, int t, int oldl, int oldt) {
                int actionBarHeight = findViewById(R.id.title).getHeight();

                int ScrollY = scrollView.getScrollY();
                int newY = Math.min(ScrollY, actionBarHeight);
                int height = actionBarHeight*2 - newY;

                Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height);
                titleWrapper.setLayoutParams(layoutParams);
                //Log.d("onScrollChanged", String.valueOf(height));
            }
        });

/*        if (acixstore == null) {
            Toast.makeText(SearchActivity.this, "你尚未登入", Toast.LENGTH_SHORT).show();
            return;
        }
*/
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_course_detail, menu);
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

    private void showAddCourseAlert() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle("加選");
        dialog.setMessage("你確定要加選這門課嗎？");
        dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                enterAddCourse("", 1);
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

package com.example.gordon.nthuais.activities.Schedule;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.example.gordon.nthuais.Big5StringRequest;
import com.example.gordon.nthuais.R;
import com.example.gordon.nthuais.activities.AppCompatSwipeBackActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.imid.swipebacklayout.lib.SwipeBackLayout;


public class ScheduleActivity extends AppCompatSwipeBackActivity {
    Boolean isDataLoaded ;
    String CountTableChild = "body>form>table:nth-child(3)>tbody>tr.word";
    String url = "https://www.ccxp.nthu.edu.tw/ccxp/COURSE/JH/7/7.2/7.2.9/JH729002.php";
    String acixstore = null;
    String stu_id = null;
    RequestQueue requestQueue;
    ArrayList<HashMap> stuSchedule = new ArrayList<HashMap>();
    SwipeBackLayout mSwipeBackLayout;

    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        mSwipeBackLayout = getSwipeBackLayout();
        mSwipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        requestQueue = Volley.newRequestQueue(this);

        Intent intent = getIntent();
        if(intent.getStringExtra("ACIXSTORE")!=null) {
            acixstore = intent.getStringExtra("ACIXSTORE");
            stu_id = intent.getStringExtra("stu_id");
            Log.d("ac", acixstore);
            Log.d("stu", stu_id);
        }
        else {
            Toast.makeText(ScheduleActivity.this,"Please Login First", Toast.LENGTH_SHORT).show();
            finish();

        }
        getSchedule(url);

        //Scrolling Tab


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_schedule, menu);
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
        }else if (id == android.R.id.home) {
            finish();
        }

        return super.onOptionsItemSelected(item);
    }


    private void getSchedule(String url) {

        Big5StringRequest scheRequest = new Big5StringRequest(Request.Method.POST,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Document document = Jsoup.parse(response);
                        Log.d("succeed","");
                        stuSchedule = parseHTML(document);



                        Bundle bundle = new Bundle();
                        bundle.putSerializable("stuSCH",stuSchedule);




                        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                        SlidingTabsBasicFragment fragment = new SlidingTabsBasicFragment();
                        fragment.setArguments(bundle);
                        transaction.replace(R.id.schedule_fragment, fragment);
                        transaction.commit();


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Failed","");
                        error.printStackTrace();
                    }
                }
        ) {
            protected Map<String, String> getParams() throws com.android.volley.AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("ACIXSTORE", acixstore);
                params.put("stu_no", stu_id);
                params.put("semester", "104,10");
                return params;
            };
        };
        requestQueue.add(scheRequest);
    }

    public ArrayList<HashMap>parseHTML(Document document) {
        Integer tr_num = document.select(CountTableChild).size();

        String id = null;
        String name = null;
        String time = null;
        String classroom = null;
        String teacher = null;



        for (Integer i = 2; i < tr_num + 2; i++) {
            HashMap<String, String> tmp_course = new HashMap<String, String>();
            // sel for select
            String sel_id = "body > form > table:nth-child(3) > tbody > tr:nth-child(" + i.toString() + ") > td:nth-child(1) > div";
            String sel_name = "body > form > table:nth-child(3) > tbody > tr:nth-child(" + i.toString() + ") > td:nth-child(2) > div";
            String sel_time = "body > form > table:nth-child(3) > tbody > tr:nth-child(" + i.toString() + ") > td:nth-child(4) > div";
            String sel_classroom = "body > form > table:nth-child(3) > tbody > tr:nth-child(" + i.toString() + ") > td:nth-child(5) > div";
            String sel_teacher = "body > form > table:nth-child(3) > tbody > tr:nth-child(" + i.toString() + ") > td:nth-child(6) > div";

            id = document.select(sel_id).text();
            time = document.select(sel_time).text();
            name = document.select(sel_name).text().split(" ")[0];
            classroom = document.select(sel_classroom).text();
            teacher = document.select(sel_teacher).text().split(" ")[0];

            tmp_course.put("id", id);
            tmp_course.put("time", time);
            tmp_course.put("name", name);
            tmp_course.put("classroom", classroom);
            tmp_course.put("teacher", teacher);

           // Log.d("id", tmp_course.get("id"));
           // Log.d("time", tmp_course.get("time"));
           // Log.d("name", tmp_course.get("name"));
           // Log.d("classroom", tmp_course.get("classroom"));
           // Log.d("teacher", tmp_course.get("teacher"));

            stuSchedule.add(tmp_course);
        }

        return stuSchedule;
    }




}





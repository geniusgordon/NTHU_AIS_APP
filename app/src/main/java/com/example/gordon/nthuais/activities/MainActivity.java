package com.example.gordon.nthuais.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.gordon.nthuais.R;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

public class MainActivity extends AppCompatActivity {

    Drawer drawer;
    AccountHeader accountHeader;
    Toolbar toolbar;

    EditText queryTxt;
    EditText courseCodeTxt;
    Button searchBtn;

    String acixstore;
    String account;
    int LOGIN_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.account_background)
                .build();

        SecondaryDrawerItem loginItem = new SecondaryDrawerItem()
                .withName("Login")
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int i, IDrawerItem iDrawerItem) {
                        login();
                        return true;
                    }
                });

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .addDrawerItems(loginItem)
                .build();

        acixstore = null;

        queryTxt = (EditText) findViewById(R.id.query);
        courseCodeTxt = (EditText) findViewById(R.id.course_code);
        searchBtn = (Button) findViewById(R.id.search_btn);

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search();
            }
        });
    }

    private void login() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivityForResult(intent, LOGIN_REQUEST);
    }

    private void addAccount(String account) {
        accountHeader.addProfiles(
                new ProfileDrawerItem().withName(account).withIcon(R.drawable.account_icon)
        );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == LOGIN_REQUEST) {
            if (resultCode == RESULT_OK) {
                acixstore = data.getStringExtra("acixstore");
                account = data.getStringExtra("account");
                Log.d("MainActivity onResult", acixstore);
                addAccount(account);
                drawer.removeItemByPosition(1);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    private void search() {
        String query = queryTxt.getText().toString();
        String courseCode = courseCodeTxt.getText().toString();
        Intent intent = new Intent(MainActivity.this, SearchActivity.class);
        intent.putExtra("acixstore", acixstore);
        intent.putExtra("query", query);
        intent.putExtra("courseCode", courseCode);
        startActivity(intent);
    }

}

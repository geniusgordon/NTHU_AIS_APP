package com.example.gordon.nthuais;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {

    Button loginBtn;
    EditText usernameTxt;
    EditText passwordTxt;
    EditText captchaTxt;
    ImageView captchaImg;
    ProgressBar captchaProgressBar;
    ProgressBar loginProgressBar;
    RequestQueue requestQueue;
    boolean isImgLoaded;
    String fnstr;

    String baseUrl = "https://www.ccxp.nthu.edu.tw/ccxp/INQUIRE/";
    String loginUrl = "https://www.ccxp.nthu.edu.tw/ccxp/INQUIRE/pre_select_entry.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = (Button) findViewById(R.id.login_btn);
        usernameTxt = (EditText) findViewById(R.id.username);
        passwordTxt = (EditText) findViewById(R.id.password);
        captchaTxt = (EditText) findViewById(R.id.captcha);
        captchaImg = (ImageView) findViewById(R.id.captcha_img);
        captchaProgressBar = (ProgressBar) findViewById(R.id.captchaProgressBar);
        loginProgressBar = (ProgressBar) findViewById(R.id.loginProgressBar);

        isImgLoaded = false;

        requestQueue = Volley.newRequestQueue(this);

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });

        getLoginPage();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    private void startGetImg() {
        captchaImg.setImageBitmap(null);
        captchaProgressBar.setVisibility(View.VISIBLE);
        isImgLoaded = false;
    }

    private void endGetImg() {
        captchaProgressBar.setVisibility(View.INVISIBLE);
        isImgLoaded = true;
    }

    private void getLoginPage() {
        startGetImg();
        StringRequest request = new StringRequest(baseUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d("Login page response", response);
                        Document document = Jsoup.parse(response, baseUrl);

                        String fnstr = document.select("input[name=fnstr]").attr("value");
                        String imgUrl = baseUrl + document.select("img[src^=auth_img]").attr("src");

                        Log.d("Login page fnstr", fnstr);
                        Log.d("Login page imgUrl", imgUrl);

                        LoginActivity.this.fnstr = fnstr;

                        getCaptchaImg(imgUrl);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        requestQueue.add(request);
    }

    private void getCaptchaImg(String url) {
        ImageRequest request = new ImageRequest(url,
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        captchaImg.setImageBitmap(Bitmap.createScaledBitmap(response, response.getWidth() * 3, response.getHeight() * 3, false));
                        endGetImg();
                    }
                }, 0, 0, null,
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        endGetImg();
                        Toast.makeText(LoginActivity.this, "Get Captcha Fail", Toast.LENGTH_SHORT).show();
                    }
                });
        requestQueue.add(request);
    }

    private void startLogin() {
        loginBtn.setEnabled(false);
        loginProgressBar.setVisibility(View.VISIBLE);
    }

    private void endLogin() {
        loginBtn.setEnabled(true);
        loginProgressBar.setVisibility(View.INVISIBLE);
    }

    private void enterMainPage(String path, final String acixstore) {
        StringRequest request = new StringRequest(baseUrl + path,
            new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("enter main page", response);
                    Intent intent = new Intent();
                    intent.putExtra("acixstore", acixstore);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            });
        requestQueue.add(request);
    }

    private void login() {
        if (isImgLoaded) {
            startLogin();
            StringRequest request = new StringRequest(Request.Method.POST, loginUrl,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Login result", response);
                            if (response.contains("script")) {
                                Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                                getLoginPage();
                            } else {
                                Toast.makeText(LoginActivity.this, "Login Success", Toast.LENGTH_SHORT).show();
                                String acixstore = response.split("STORE=")[1].split("&")[0];
                                Log.d("Login Result ACIXSTORE", acixstore);
                                enterMainPage(response.split("url=")[1].split(">")[0], acixstore);
                            }
                            endLogin();
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            endLogin();
                            Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                            getLoginPage();
                        }
                    }
            ) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("account", usernameTxt.getText().toString());
                    params.put("passwd", passwordTxt.getText().toString());
                    params.put("passwd2", captchaTxt.getText().toString());
                    params.put("fnstr", fnstr);
                    return params;
                }
            };
            requestQueue.add(request);
        } else {
            Toast.makeText(this, "Loading Captcha", Toast.LENGTH_SHORT).show();
        }
    }
}

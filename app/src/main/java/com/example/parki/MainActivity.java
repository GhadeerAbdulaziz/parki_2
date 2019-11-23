package com.example.parki;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.parki.models.UserData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button _btnlog;
    EditText emailEditText, passwordEditText;
    TextView _txtsign;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        _btnlog = (Button) findViewById(R.id.textwel);
        emailEditText = (EditText) findViewById(R.id.txtEmail);
        passwordEditText = (EditText) findViewById(R.id.txtPass);
        _txtsign = (TextView) findViewById(R.id.txtsign);
        _btnlog.setOnClickListener(this);
        _txtsign.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        if (view == _txtsign) {
            Intent intent = new Intent(MainActivity.this, register.class);
            startActivity(intent);
        } else if (view == _btnlog) {

            if (TextUtils.isEmpty(emailEditText.getText())) {

                emailEditText.setError(getString(R.string.required_field));

            } else if (TextUtils.isEmpty(passwordEditText.getText())) {

                passwordEditText.setError(getString(R.string.required_field));

            } else {
                //call login api
                loginApi();
            }
        }

    }

    private void loginApi() {

        //progressBar.setVisibility(View.VISIBLE);
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = Constants.LOGIN_URL;
        url = url.replace(" ", "%20");

        Log.e("Login API", url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject apiResult = new JSONObject(response);

                    if (apiResult.has("success")) {

                        Toast.makeText(MainActivity.this, getString(R.string.login_failed), Toast.LENGTH_LONG).show();

                    } else {
                        Log.e("apiResult" , apiResult.toString());
                        UserData userData = new UserData();
                        userData.parseData(apiResult);

                        MyApplication app = ((MyApplication) getApplication());
                        app.setCurrentUser(userData);

                        startActivity(new Intent(MainActivity.this, MapsActivity.class));
                        MainActivity.this.finish();

                    }


                } catch (JSONException e) {
                    //e.printStackTrace();
                    //progressBar.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Server Error", Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, "Network Connection Error", Toast.LENGTH_LONG).show();
                //progressBar.setVisibility(View.GONE);
            }
        }
        ){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("email", emailEditText.getText().toString());
                params.put("password", passwordEditText.getText().toString());

                return params;
            }
        };

        queue.add(stringRequest);

    }
}


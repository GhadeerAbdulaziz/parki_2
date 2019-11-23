package com.example.parki;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;

public class register extends AppCompatActivity implements View.OnClickListener {

    Button _btnreg;
    TextView _txtlog;
    EditText _txtname, _txtemaile, _txtpass, __txtphone, _txtcar, _txtcarnum;

    public static boolean isValidEmailAddress(String email) {
        String ePattern = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$";
        java.util.regex.Pattern p = java.util.regex.Pattern.compile(ePattern);
        java.util.regex.Matcher m = p.matcher(email);
        return m.matches();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        _btnreg = (Button) findViewById(R.id.btnreg);
        _txtname = (EditText) findViewById(R.id.txtname);
        _txtemaile = (EditText) findViewById(R.id.txtemail);
        _txtpass = (EditText) findViewById(R.id.txtpass);
        __txtphone = (EditText) findViewById(R.id.txtphone);
        _txtcar = (EditText) findViewById(R.id.txtcar);
        _txtcarnum = (EditText) findViewById(R.id.txtcarnum);
        _txtlog = (TextView) findViewById(R.id.txtlog);

        _btnreg.setOnClickListener(this);
        _txtlog.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == _txtlog) {
            register.this.finish();
        } else if (view == _btnreg) {
            if (TextUtils.isEmpty(_txtname.getText())) {
                _txtname.setError(getString(R.string.required_field));
            } else if (!isValidEmailAddress(_txtemaile.getText().toString())) {

                _txtemaile.setError(getString(R.string.valid_email));

            } else if (TextUtils.isEmpty(_txtpass.getText())) {

                _txtpass.setError(getString(R.string.required_field));
            } else if ( __txtphone.getText().length()<6 ){
                _txtpass.setError(" كلمة المرور غير صحيحة ادنى حد 6 حروف");

            } else if (TextUtils.isEmpty(__txtphone.getText())) {

                __txtphone.setError(getString(R.string.required_field));

            } else if (TextUtils.isEmpty(_txtcar.getText())) {

                _txtcar.setError(getString(R.string.required_field));

            } else if (TextUtils.isEmpty(_txtcarnum.getText())) {

                _txtcarnum.setError(getString(R.string.required_field));

            } else {

                signUpApi();

            }

        }
    }

    private void signUpApi() {

        //progressBar.setVisibility(View.VISIBLE);

        RequestQueue queue = Volley.newRequestQueue(this);

        String url = Constants.REGISTER_URL;
        url = url.replace(" ", "%20");

        Log.e("sign up API  :", url);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject apiResult = new JSONObject(response);

                    int status = apiResult.getInt("success");

                    if (status == 1) {
                        Toast.makeText(register.this, apiResult.getString("message"), Toast.LENGTH_LONG).show();
                        register.this.finish();
                    } else {
                        Toast.makeText(register.this, apiResult.getString("message"), Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    //e.printStackTrace();
                    //progressBar.setVisibility(View.GONE);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //progressBar.setVisibility(View.GONE);

                Toast.makeText(register.this, "Network Connection Error", Toast.LENGTH_LONG).show();
            }
        }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();

                params.put("name", _txtname.getText().toString());
                params.put("email", _txtemaile.getText().toString());
                params.put("password", _txtpass.getText().toString());
                params.put("mobile", __txtphone.getText().toString());
                params.put("cartype", _txtcar.getText().toString());
                params.put("carnumber", _txtcarnum.getText().toString());

                return params;
            }
        };

        queue.add(stringRequest);

    }


}




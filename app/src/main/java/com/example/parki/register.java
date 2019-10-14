package com.example.parki;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class register extends AppCompatActivity {
    DatabaseHelper db;
    Button _btnreg;
    TextView _txtlog;
    EditText _txtname,_txtemaile,_txtpass,__txtphone,_txtcar,_txtcarnum;
    boolean isNameValid, isEmailValid, isPhoneValid, isPasswordValid,isCarValid,isCarnumValid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        db=new DatabaseHelper(this);
        _btnreg =(Button)findViewById(R.id.btnreg);
        _txtname=(EditText)findViewById(R.id.txtname);
        _txtemaile=(EditText)findViewById(R.id.txtemail);
        _txtpass=(EditText)findViewById(R.id.txtpass);
        __txtphone=(EditText)findViewById(R.id.txtphone);
        _txtcar=(EditText)findViewById(R.id.txtcar);
        _txtcarnum=(EditText)findViewById(R.id.txtcarnum);
        _txtlog=(TextView) findViewById(R.id.txtlog);

        _btnreg.setOnClickListener(new View.OnClickListener() {
            @Override

                public void onClick(View v) {
                    SetValidation();
                }
                /*
                String name=_txtname.getText().toString();
                String email=_txtemaile.getText().toString();
                String pass=_txtpass.getText().toString();
                String phone=__txtphone.getText().toString();
                String car=_txtcar.getText().toString();
                String carnum=_txtcarnum.getText().toString();
                db.addUser(name,email,pass,phone,car,carnum);
                Toast.makeText(getApplicationContext(),"regester successfully",Toast.LENGTH_LONG).show();
                Intent movToLogin=new Intent(register.this,MainActivity.class);
                startActivity(movToLogin);
                /*
                if (val > 0){
                    Toast.makeText(getApplicationContext(),"regester successfully",Toast.LENGTH_LONG).show();
                    Intent movToLogin=new Intent(register.this,MainActivity.class);
                    startActivity(movToLogin);
                }else{
                    Toast.makeText(getApplicationContext(),"Registration Error",Toast.LENGTH_LONG).show();
                }
*/


        });
        _txtlog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent (register.this,MainActivity.class);
                startActivity(intent);
            }


        });
    }
    public void SetValidation() {
        // Check for a valid name.
        if (_txtname.getText().toString().isEmpty()) {
            _txtname.setError(getResources().getString(R.string.name_error));
            isNameValid = false;
        } else  {
            isNameValid = true;
        }

        // Check for a valid email address.
        if (_txtemaile.getText().toString().isEmpty()) {
            _txtemaile.setError(getResources().getString(R.string.email_error));
            isEmailValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(_txtemaile.getText().toString()).matches()) {
            _txtemaile.setError(getResources().getString(R.string.error_invalid_email));
            isEmailValid = false;
        } else  {
            isEmailValid = true;
        }

        // Check for a valid phone number.
        if (__txtphone.getText().toString().isEmpty()) {
            __txtphone.setError(getResources().getString(R.string.phone_error));
            isPhoneValid = false;
        } else  {
            isPhoneValid = true;
        }

        // Check for a valid password.
        if (_txtpass.getText().toString().isEmpty()) {
            _txtpass.setError(getResources().getString(R.string.password_error));
            isPasswordValid = false;
        } else if (_txtpass.getText().length() < 6) {
            _txtpass.setError(getResources().getString(R.string.error_invalid_password));
            isPasswordValid = false;
        } else  {
            isPasswordValid = true;
        }
        if (_txtcar.getText().toString().isEmpty()) {
            _txtcar.setError(getResources().getString(R.string.car_error));
            isCarValid = false;
        } else  {
            isCarValid = true;
        }
        if (_txtcarnum.getText().toString().isEmpty()) {
            _txtcarnum.setError(getResources().getString(R.string.carnum_error));
            isCarnumValid = false;
        } else  {
            isCarnumValid = true;
        }

        if (isNameValid && isEmailValid && isPhoneValid && isPasswordValid) {
            String name=_txtname.getText().toString();
            String email=_txtemaile.getText().toString();
            String pass=_txtpass.getText().toString();
            String phone=__txtphone.getText().toString();
            String car=_txtcar.getText().toString();
            String carnum=_txtcarnum.getText().toString();
            db.addUser(name,email,pass,phone,car,carnum);
            Toast.makeText(getApplicationContext(),"تم التسجيل بنجاح",Toast.LENGTH_LONG).show();
            Intent movToLogin=new Intent(register.this,MainActivity.class);
            startActivity(movToLogin);

        }

    }
    }




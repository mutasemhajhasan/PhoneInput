package me.mutasem.phoneinputsample;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.QuickContactBadge;
import android.widget.Toast;

import me.mutasem.phoneinput.PhoneEditText;
import me.mutasem.phoneinput.PhoneInputLayout;

public class MainActivity extends AppCompatActivity {
    PhoneEditText phoneEditText;
    PhoneInputLayout phoneInputLayout;
    private Button submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        phoneEditText = findViewById(R.id.phoneEditText);
        phoneInputLayout = findViewById(R.id.phoneInputLayout);
        phoneEditText.setDefaultCountry("SY");
        phoneInputLayout.setDefaultCountry("SY");
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, phoneEditText.getPhoneNumber() + "," + phoneInputLayout.getPhoneNumber(), Toast.LENGTH_LONG).show();
            }
        });
    }
}

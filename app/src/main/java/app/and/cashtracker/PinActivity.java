package app.and.cashtracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.and.cashtracker.system.Settings;

public class PinActivity extends AppCompatActivity {

    private EditText mPin;
    private ImageView mFingerprint;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        settings = new Settings(this);
        initializeView();
        initializeListeners();
    }

    private void initializeView(){
        mPin = findViewById(R.id.pin_pin);
        mFingerprint = findViewById(R.id.pin_fingerprint);
        mFingerprint.setVisibility(View.GONE);
    }

    private void initializeListeners(){
        mPin.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(editable.toString().length()==4)
                    if(editable.toString().equals(settings.getPin())){
                        Toast.makeText(getApplicationContext(), "Пин-код принят!",Toast.LENGTH_SHORT).show();
                        PinActivity.this.finish();
                    } else {
                        Toast toast = new Toast(PinActivity.this);
                        ImageView handalf = new ImageView(PinActivity.this);
                        handalf.setImageResource(R.drawable.handalf);
                        handalf.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        toast.setView(handalf);
                        toast.setDuration(Toast.LENGTH_SHORT);
                        toast.show();
                    }
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent home = new Intent(Intent.ACTION_MAIN);
        home.addCategory(Intent.CATEGORY_HOME);
        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(home);
    }
}

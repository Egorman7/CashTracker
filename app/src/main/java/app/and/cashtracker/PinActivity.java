package app.and.cashtracker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PinActivity extends AppCompatActivity {

    private EditText mPin;
    private ImageView mFingerprint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pin);

        initializeView();
        initializeListeners();
    }

    private void initializeView(){
        mPin = findViewById(R.id.pin_pin);
        mFingerprint = findViewById(R.id.pin_fingerprint);
    }

    private void initializeListeners(){
        mPin.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(textView.getText().length()==4){
                    Toast.makeText(getApplicationContext(), mPin.getText().toString(),Toast.LENGTH_SHORT).show();
                    PinActivity.this.finish();
                }
                return false;
            }
        });
    }
}

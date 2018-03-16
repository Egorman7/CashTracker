package app.and.cashtracker;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import app.and.cashtracker.calculator.Calculator;

public class CalculatorActivity extends AppCompatActivity {

    //private TextView[] digits;
    private Calculator calculator;
    private TextView mInput;
    private ImageView mOk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        calculator=new Calculator();
        initializeView();
        //mOk.setEnabled(false);
        double startValue = getIntent().getDoubleExtra("value",0);
        mInput.setText(String.valueOf(startValue));
        if(startValue!=0){
            String s = String.valueOf(startValue);
            for(int i=0; i<s.length(); i++) calculator.addSymbol(s.charAt(i));
        }
        Intent intent = new Intent();
        intent.putExtra("result", String.valueOf(startValue));
        setResult(2, intent);
    }

    private void initializeView(){
        mInput = findViewById(R.id.calculator_field);
        mOk = findViewById(R.id.calculator_ok);
        TextView[] digitsAndOps = {
                findViewById(R.id.calculator_1),
                findViewById(R.id.calculator_2),
                findViewById(R.id.calculator_3),
                findViewById(R.id.calculator_4),
                findViewById(R.id.calculator_5),
                findViewById(R.id.calculator_6),
                findViewById(R.id.calculator_7),
                findViewById(R.id.calculator_8),
                findViewById(R.id.calculator_9),
                findViewById(R.id.calculator_0),
                findViewById(R.id.calculator_sum),
                findViewById(R.id.calculator_sub),
                findViewById(R.id.calculator_div),
                findViewById(R.id.calculator_mul),
                findViewById(R.id.calculator_eq),
                findViewById(R.id.calculator_dot)
        };
        for(TextView digit : digitsAndOps){
            digit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    char sym = ((TextView)view).getText().charAt(0);
                    //if(sym=='=') mOk.setEnabled(true);
                    String res = calculator.addSymbol(sym);
                    if(res.equals("null")) {
                        Toast.makeText(CalculatorActivity.this, "Ошибка в выражении!", Toast.LENGTH_LONG).show();
                        calculator.erase();
                        mInput.setText("0");
                        //mOk.setEnabled(false);
                    } else mInput.setText(res);
                }
            });
        }
        findViewById(R.id.calculator_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mInput.setText(calculator.addSymbol('d'));
            }
        });
        findViewById(R.id.calculator_c).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calculator.erase();
                mInput.setText("0");
            }
        });
        findViewById(R.id.calculator_ok).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(calculator.isSolved()){
                    Intent intent = new Intent();
                    intent.putExtra("result", mInput.getText().toString());
                    setResult(2, intent);
                    finish();
                } else {
                    String res = calculator.addSymbol('=');
                    if(calculator.isSolved()){
                        Intent intent = new Intent();
                        intent.putExtra("result", res);
                        setResult(2, intent);
                        finish();
                    } else {
                        Toast.makeText(CalculatorActivity.this, "Ошибка в выражении!", Toast.LENGTH_LONG).show();
                        calculator.erase();
                        mInput.setText("0");
                        //mOk.setEnabled(false);
                        finish();
                    }
                }
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
    }
}

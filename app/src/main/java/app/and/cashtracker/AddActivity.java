package app.and.cashtracker;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.Spinner;

import java.util.Calendar;

import app.and.cashtracker.database.DBHelper;
import app.and.cashtracker.models.RecordModel;

public class AddActivity extends AppCompatActivity {

    private Spinner mCategorySpinner;
    private ImageButton mCategorySettingsButton;
    private Button mButton;
    private TextInputEditText mValue, mDate;
    private RadioButton mRadioInc, mRadioOut;
    private boolean isIncome, isEdit, isWasIncome;
    private double oldValue;
    private int id;
    private ArrayAdapter<String> mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isIncome = false;

        initializeView();
        initializeData();
        initializeListeners();

        double val;
        String date, cat;
        isEdit = getIntent().getBooleanExtra("edit",false);
        if(isEdit){
            val=getIntent().getDoubleExtra("value",0);
            date=getIntent().getStringExtra("date");
            cat=getIntent().getStringExtra("cat");
            id=getIntent().getIntExtra("id",0);
            mButton.setText("Изменить");
            mValue.setText(String.valueOf(val));
            mDate.setText(date);
            oldValue = val;
            isWasIncome = getIntent().getBooleanExtra("income",false);
            if(isWasIncome){
                isIncome = true;
                mRadioInc.setChecked(true);
            }
            mCategorySpinner.setSelection(mAdapter.getPosition(cat));
        }
    }

    void setUpCategoriesSpinner(){
        String[] data = DBHelper.getCategories(DBHelper.getInstance(this),isIncome);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item ,data);
        mAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        mCategorySpinner.setAdapter(mAdapter);
        mCategorySpinner.setSelection(0);
    }

    // set up view
    private void initializeView(){
        mCategorySpinner =  findViewById(R.id.add_category_spinner);
        mValue = findViewById(R.id.add_input_value);
        mDate = findViewById(R.id.add_input_date);
        mRadioInc = findViewById(R.id.add_radio_income);
        mRadioOut = findViewById(R.id.add_radio_outcome);
        mCategorySettingsButton = findViewById(R.id.add_category_config_button);
        mButton = findViewById(R.id.add_button);
    }
    private void initializeData(){
        mCategorySpinner.setPrompt("Категория");
        setUpCategoriesSpinner();
        mDate.setText(DBHelper.SDF.format(Calendar.getInstance().getTime()));
    }
    private void initializeListeners(){
        mCategorySettingsButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddActivity.this, CategoryActivity.class);
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(getResources().getColor(R.color.colorPrimary));
                Bundle bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view,bitmap,0,0).toBundle();
                //startActivityForResult(intent, 1, bundle);
                // error
                startActivityForResult(intent, 1, bundle);
            }
        });
        mRadioInc.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                isIncome=b;
                setUpCategoriesSpinner();
            }
        });
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mValue.getText().toString().length()==0 || mDate.getText().toString().length()==0){
                    return;
                }
                try {
                    if (!isEdit && DBHelper.addRecord(DBHelper.getInstance(AddActivity.this), new RecordModel(Double.valueOf(mValue.getText().toString()),
                            DBHelper.SDF.parse(mDate.getText().toString()),
                            mCategorySpinner.getSelectedItem().toString(), mRadioInc.isChecked()
                    ), isIncome)){
                        if(DBHelper.addBalance(DBHelper.getInstance(AddActivity.this),Double.valueOf(
                                (mRadioInc.isChecked() ? "" : "-")+
                                        mValue.getText().toString())))
                            AddActivity.this.finish();
                    }
                    else {
                        if(DBHelper.updateRecord(DBHelper.getInstance(AddActivity.this),new RecordModel(
                                Double.valueOf(mValue.getText().toString()),
                                DBHelper.SDF.parse(mDate.getText().toString()),
                                mCategorySpinner.getSelectedItem().toString(),
                                mRadioInc.isChecked(), id
                        ))){
                            if(DBHelper.addBalance(DBHelper.getInstance(AddActivity.this),Double.valueOf((isWasIncome ? "-" : "") +oldValue)) &&
                                    DBHelper.addBalance(DBHelper.getInstance(AddActivity.this),
                                            Double.valueOf((mRadioInc.isChecked() ? "" : "-") + mValue.getText().toString()))){
                                AddActivity.this.finish();
                            }
                        }
                    }
                } catch (Exception ex){ ex.printStackTrace();}
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        setUpCategoriesSpinner();
    }

    @Override
    public void finish() {
        setResult(1);
        super.finish();
        overridePendingTransition(0, R.anim.activity_add_zoom_out);
    }


}

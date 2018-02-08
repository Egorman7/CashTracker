package app.and.cashtracker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.razerdp.widget.animatedpieview.AnimatedPieView;
import com.razerdp.widget.animatedpieview.AnimatedPieViewConfig;
import com.razerdp.widget.animatedpieview.callback.OnPieSelectListener;
import com.razerdp.widget.animatedpieview.data.IPieInfo;
import com.razerdp.widget.animatedpieview.data.SimplePieInfo;

import java.util.Calendar;
import java.util.Map;

import app.and.cashtracker.database.DBHelper;

public class DiagramActivity extends AppCompatActivity {
    private AnimatedPieView mChart;
    private ImageButton mButton;
    private TextView mTextView;
    private boolean isIncome;
    private AnimatedPieViewConfig configOutcome, configIncome;
    private String dateEnd, dateStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isIncome = false;
        dateEnd = DBHelper.SDF.format(Calendar.getInstance().getTime());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,-14);
        dateStart = DBHelper.SDF.format(cal.getTime());

        initializeView();
        initializeOutcomeConfig();
        initializeIncomeConfig();
        initializeData();
        initializeListeners();

    }

    private void initializeView(){
        mTextView = findViewById(R.id.diagram_info);
        mChart = findViewById(R.id.diagram_pie);
        mButton = findViewById(R.id.diagram_renew);
    }
    private void initializeIncomeConfig(){
        Map<String, Float> dataMap = DBHelper.getValuesForCategoriesByDates(DBHelper.getInstance(this),dateStart,dateEnd,true);
        configIncome = new AnimatedPieViewConfig();
        String[] keys2 = dataMap.keySet().toArray(new String[dataMap.size()]);
        for (int i=0; i<keys2.length; i++){
            if(dataMap.get(keys2[i])>0){
                configIncome.addData(new SimplePieInfo(dataMap.get(keys2[i]),DBHelper.getCategoryColor(DBHelper.getInstance(this),keys2[i]),keys2[i]));
            }
        }
        configIncome.setStartAngle(-90);
        configIncome.setTextSize(32);
        configIncome.setTextMarginLine(32);
        configIncome.setDirectText(false);
        configIncome.setFocusAlphaType(AnimatedPieViewConfig.FOCUS_WITH_ALPHA, 150);
        configIncome.setDuration(2000);
        configIncome.setOnPieSelectListener(new OnPieSelectListener<IPieInfo>() {
            @Override
            public void onSelectPie(@NonNull IPieInfo pieInfo, boolean isScaleUp) {
                if(isScaleUp) Toast.makeText(getApplicationContext(),pieInfo.getDesc() + ": " + pieInfo.getValue() + " UAH", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initializeOutcomeConfig(){
        Map<String, Float> dataMap = DBHelper.getValuesForCategoriesByDates(DBHelper.getInstance(this),dateStart,dateEnd,false);
        configOutcome = new AnimatedPieViewConfig();
        String[] keys = dataMap.keySet().toArray(new String[dataMap.size()]);
        for(int i=0; i<keys.length; i++){
            if(dataMap.get(keys[i])>0)
                configOutcome.addData(new SimplePieInfo(dataMap.get(keys[i]),DBHelper.getCategoryColor(DBHelper.getInstance(this),keys[i]),keys[i]));
        }
        configOutcome.setStartAngle(-90);
        configOutcome.setTextSize(32);
        configOutcome.setTextMarginLine(32);
        //config.setDrawStrokeOnly(false);
        configOutcome.setDirectText(false);
        configOutcome.setFocusAlphaType(AnimatedPieViewConfig.FOCUS_WITH_ALPHA, 150);
        configOutcome.setDuration(2000);
        configOutcome.setOnPieSelectListener(new OnPieSelectListener<IPieInfo>() {
            @Override
            public void onSelectPie(@NonNull IPieInfo pieInfo, boolean isScaleUp) {
                if(isScaleUp) Toast.makeText(getApplicationContext(),pieInfo.getDesc() + ": " + pieInfo.getValue() + " UAH", Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void initializeData(){
        mChart.applyConfig(configOutcome);
        mChart.start();
        mTextView.setText("Расходы (" + dateStart + " - " + dateEnd+")");
    }
    private void initializeListeners(){
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isIncome = !isIncome;
                if(isIncome){
                    mTextView.setText("Доходы (" + dateStart + " - " + dateEnd+")");
                    mChart.applyConfig(configIncome);
                } else {
                    mTextView.setText("Расходы (" + dateStart + " - " + dateEnd+")");
                    mChart.applyConfig(configOutcome);
                }
                mChart.start();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.activity_journal_zoom_out);
    }
}

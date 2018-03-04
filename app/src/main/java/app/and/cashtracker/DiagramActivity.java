package app.and.cashtracker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import app.and.cashtracker.database.DBHelper;

public class DiagramActivity extends AppCompatActivity {
    private AnimatedPieView mChartOutcome, mChartIncome;
    private CardView mCard;
    private ImageButton mButton, mCardButton;
    private TextView mTextView, mSumInfo, mCardText;
    private boolean isIncome, isCardShowing;
    private AnimatedPieViewConfig configOutcome, configIncome;
    private String dateEnd, dateStart, selectedCat;
    private double valueInc, valueOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isIncome = false; isCardShowing = false;
        dateEnd = DBHelper.SDF.format(Calendar.getInstance().getTime());
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,-14);
        dateStart = DBHelper.SDF.format(cal.getTime());

        valueInc = valueOut = 0.0;

        initializeView();
        initializeOutcomeConfig();
        initializeIncomeConfig();
        initializeData();
        initializeListeners();

    }

    private void initializeView(){
        mTextView = findViewById(R.id.diagram_info);
        mSumInfo = findViewById(R.id.diagram_sum_info);
        mChartOutcome = findViewById(R.id.diagram_pie_outcome);
        mChartIncome = findViewById(R.id.diagram_pie_income);
        mButton = findViewById(R.id.diagram_renew);
        mCard = findViewById(R.id.diagram_info_card);
        mCardText = findViewById(R.id.diagram_info_card_text);
        mCardButton = findViewById(R.id.diagram_info_card_button);
    }
    private void initializeIncomeConfig(){
       // Map<String, Float> dataMap = DBHelper.getValuesForCategoriesByDates(DBHelper.getInstance(this),dateStart,dateEnd,true);
        Map<String, Float> dataMap = DBHelper.getValuesForCat(DBHelper.getInstance(this),dateStart,dateEnd,true);
        configIncome = new AnimatedPieViewConfig();
        String[] keys = dataMap.keySet().toArray(new String[dataMap.size()]);
        for (int i=0; i<keys.length; i++){
            if(dataMap.get(keys[i])>0){
                configIncome.addData(new SimplePieInfo(dataMap.get(keys[i]),DBHelper.getCategoryColor(DBHelper.getInstance(this),keys[i]),keys[i]));
                valueInc += dataMap.get(keys[i]);
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
                //if(isScaleUp) Toast.makeText(getApplicationContext(),pieInfo.getDesc() + ": " + pieInfo.getValue() + " UAH", Toast.LENGTH_SHORT).show();
                if(isScaleUp){
                    selectedCat = pieInfo.getDesc()+": " + new DecimalFormat("#.00 UAH",DecimalFormatSymbols.getInstance(Locale.US)).format(pieInfo.getValue());
                    showCard();
                } else hideCard();
            }
        });
    }
    private void initializeOutcomeConfig(){
        //Map<String, Float> dataMap = DBHelper.getValuesForCategoriesByDates(DBHelper.getInstance(this),dateStart,dateEnd,false);
        Map<String, Float> dataMap = DBHelper.getValuesForCat(DBHelper.getInstance(this),dateStart,dateEnd,false);
        configOutcome = new AnimatedPieViewConfig();
        String[] keys = dataMap.keySet().toArray(new String[dataMap.size()]);
        for(int i=0; i<keys.length; i++){
            if(dataMap.get(keys[i])>0)
                configOutcome.addData(new SimplePieInfo(dataMap.get(keys[i]),DBHelper.getCategoryColor(DBHelper.getInstance(this),keys[i]),keys[i]));
                valueOut += dataMap.get(keys[i]);
        }
        configOutcome.setStartAngle(-90);
        configOutcome.setTextSize(32);
        configOutcome.setTextMarginLine(32);
        configOutcome.setDirectText(false);
        configOutcome.setFocusAlphaType(AnimatedPieViewConfig.FOCUS_WITH_ALPHA, 150);
        configOutcome.setDuration(2000);
        configOutcome.setOnPieSelectListener(new OnPieSelectListener<IPieInfo>() {
            @Override
            public void onSelectPie(@NonNull IPieInfo pieInfo, boolean isScaleUp) {
                //if(isScaleUp) Toast.makeText(getApplicationContext(),pieInfo.getDesc() + ": " + pieInfo.getValue() + " UAH", Toast.LENGTH_SHORT).show();
                if(isScaleUp){
                    selectedCat = pieInfo.getDesc()+": " + new DecimalFormat("#.00 UAH",DecimalFormatSymbols.getInstance(Locale.US)).format(pieInfo.getValue());
                    showCard();
                } else hideCard();
            }
        });
    }
    private void initializeData(){
        mChartOutcome.applyConfig(configOutcome);
        mChartOutcome.start();
        mChartIncome.applyConfig(configIncome);
        mTextView.setText("Расходы (" + dateStart + " - " + dateEnd+")");
        DecimalFormat df = new DecimalFormat("#.00 UAH", DecimalFormatSymbols.getInstance(Locale.US));
        mSumInfo.setText(df.format(valueOut));
        mSumInfo.setAlpha(0f);
        mSumInfo.animate().alpha(1f).setDuration(1200).setListener(null);
    }
    private void initializeListeners(){
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isIncome = !isIncome;
                if(isIncome){
                    mTextView.setText("Доходы (" + dateStart + " - " + dateEnd+")");
                    //mChartOutcome.applyConfig(configIncome);
                    mChartOutcome.setVisibility(View.GONE);
                    mChartIncome.setVisibility(View.VISIBLE);
                    mChartIncome.start();
                    fadeInOutSumLable(valueInc);
                } else {
                    mTextView.setText("Расходы (" + dateStart + " - " + dateEnd+")");
                    mChartIncome.setVisibility(View.GONE);
                    mChartOutcome.setVisibility(View.VISIBLE);
                    mChartOutcome.start();
                    fadeInOutSumLable(valueOut);
                    //mChartOutcome.applyConfig(configOutcome);
                }
                //mChartOutcome.start();
            }
        });
    }

    private void fadeInOutSumLable(final double value){
        final DecimalFormat df = new DecimalFormat("#.00 UAH", DecimalFormatSymbols.getInstance(Locale.US));
        mSumInfo.animate()
                .alpha(0f)
                .setDuration(600)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mSumInfo.setText(df.format(value));
                        mSumInfo.animate()
                                .alpha(1f)
                                .setDuration(600)
                                .setListener(null);
                    }
                });
    }

    private void showCard(){
        if(isCardShowing){
            updateCard();
        } else{
            isCardShowing=true;
            updateCard();
            mCard.setVisibility(View.VISIBLE);
            mCard.setTranslationY(300);
            mCard.animate()
                    .translationY(0f)
                    .setDuration(600)
                    .setListener(null);
        }
    }
    private void hideCard(){
        if(isCardShowing){
            mCard.animate()
                    .translationY(mCard.getHeight())
                    .setDuration(600)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isCardShowing=false;
                        }
                    });
        }
    }

    private void updateCard(){
        mCardText.setText(selectedCat);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0,R.anim.activity_journal_zoom_out);
    }
}

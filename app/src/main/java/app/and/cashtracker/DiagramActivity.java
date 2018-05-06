package app.and.cashtracker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
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
import app.and.cashtracker.database.Data;
import app.and.cashtracker.system.Settings;

public class DiagramActivity extends AppCompatActivity {
    private AnimatedPieView mChartOutcome, mChartIncome;
    private CardView mCard;
    private ImageButton mCardButton;
    private TextView mTextView, mSumInfo, mCardText;
    private Button mDateStart, mDateEnd;
    private boolean isIncome, isCardShowing;
    private AnimatedPieViewConfig configOutcome, configIncome;
    private String dateEnd, dateStart, selectedCat, selectedValue;
    private double valueInc, valueOut;
    private Settings settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isIncome = false; isCardShowing = false;
        dateEnd = getIntent().getStringExtra(MainActivity.DATE_END);
        dateStart = getIntent().getStringExtra(MainActivity.DATE_START);

        valueInc = valueOut = 0.0;
        settings = new Settings(this);

        initializeView();
        initializeOutcomeConfig(dateStart, dateEnd);
        initializeIncomeConfig(dateStart, dateEnd);
        initializeData();
        initializeListeners();

    }

    private void initializeView(){
        mTextView = findViewById(R.id.diagram_info);
        mSumInfo = findViewById(R.id.diagram_sum_info);
        mChartOutcome = findViewById(R.id.diagram_pie_outcome);
        mChartIncome = findViewById(R.id.diagram_pie_income);
        mCard = findViewById(R.id.diagram_info_card);
        mCardText = findViewById(R.id.diagram_info_card_text);
        mCardButton = findViewById(R.id.diagram_info_card_button);
        mDateStart = findViewById(R.id.diagram_date_start);
        mDateEnd = findViewById(R.id.diagram_date_end);
    }
    private void initializeIncomeConfig(String dateStart, String dateEnd){
       // Map<String, Float> dataMap = DBHelper.getValuesForCategoriesByDates(DBHelper.getInstance(this),dateStart,dateEnd,true);
        Map<String, Float> dataMap = DBHelper.getValuesForCat(DBHelper.getInstance(this),dateStart,dateEnd,true);
        configIncome = new AnimatedPieViewConfig();
        String[] keys = dataMap.keySet().toArray(new String[dataMap.size()]);
        for (int i=0; i<keys.length; i++){
            if(dataMap.get(keys[i])>0){
                configIncome.addData(new SimplePieInfo(dataMap.get(keys[i]),DBHelper.getCategoryColor(DBHelper.getInstance(this),keys[i], true),keys[i]));
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
                if(isScaleUp){
                    selectedCat = pieInfo.getDesc();
                    selectedValue = ": " + new DecimalFormat("#.00 "+settings.getCurrency(),DecimalFormatSymbols.getInstance(Locale.US)).format(pieInfo.getValue());
                    showCard();
                } else hideCard();
            }
        });
    }
    private void initializeOutcomeConfig(String dateStart, String dateEnd){
        //Map<String, Float> dataMap = DBHelper.getValuesForCategoriesByDates(DBHelper.getInstance(this),dateStart,dateEnd,false);
        Map<String, Float> dataMap = DBHelper.getValuesForCat(DBHelper.getInstance(this),dateStart,dateEnd,false);
        configOutcome = new AnimatedPieViewConfig();
        String[] keys = dataMap.keySet().toArray(new String[dataMap.size()]);
        for(int i=0; i<keys.length; i++){
            if(dataMap.get(keys[i])>0)
                configOutcome.addData(new SimplePieInfo(dataMap.get(keys[i]),DBHelper.getCategoryColor(DBHelper.getInstance(this),keys[i], false),keys[i]));
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
                if(isScaleUp){
                    selectedCat = pieInfo.getDesc();
                    selectedValue = ": " + new DecimalFormat("#.00 "+settings.getCurrency(),DecimalFormatSymbols.getInstance(Locale.US)).format(pieInfo.getValue());
                    showCard();
                } else hideCard();
            }
        });
    }
    private void initializeData(){
        mChartOutcome.applyConfig(configOutcome);
        mChartOutcome.start();
        mChartIncome.applyConfig(configIncome);
        mTextView.setText("Расходы"); //(" + dateStart + " - " + dateEnd+")");
        DecimalFormat df = new DecimalFormat("#.00 "+settings.getCurrency(), DecimalFormatSymbols.getInstance(Locale.US));
        mSumInfo.setText(df.format(valueOut));
        mSumInfo.setAlpha(0f);
        mSumInfo.animate().alpha(1f).setDuration(1200).setListener(null);
        mCard.setVisibility(View.VISIBLE);
        mCard.setTranslationY(300);
        mDateStart.setText(dateStart); mDateEnd.setText(dateEnd);
    }
    private void initializeListeners(){
        mCardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DiagramActivity.this, MoreActivity.class);
                intent.putExtra("cat",selectedCat);
                intent.putExtra("dateStart",dateStart);
                intent.putExtra("dateEnd",dateEnd);
                intent.putExtra("income",isIncome);
                startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(DiagramActivity.this,mCardText,"titlemore").toBundle());
            }
        });
        mDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Data.getSDF().parse(dateStart));
                    new DatePickerDialog(DiagramActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            Calendar c = Calendar.getInstance();
                            c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            updateDiagramData(Data.getSDF().format(c.getTime()), dateEnd);
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                } catch (Exception ex) {ex.printStackTrace();}
            }
        });
        mDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Data.getSDF().parse(dateEnd));
                    new DatePickerDialog(DiagramActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            Calendar c = Calendar.getInstance();
                            c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            updateDiagramData(dateStart, Data.getSDF().format(c.getTime()));
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                } catch (Exception ex) {ex.printStackTrace();}
            }
        });
    }
    private void updateDiagramData(String dateStart, String dateEnd){
        valueInc = valueOut = 0.0;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        initializeIncomeConfig(dateStart, dateEnd);
        initializeOutcomeConfig(dateStart, dateEnd);
        mChartIncome.applyConfig(configIncome);
        mChartOutcome.applyConfig(configOutcome);
        mDateStart.setText(dateStart); mDateEnd.setText(dateEnd);
        if(isIncome) {
            mChartIncome.start();
            fadeInOutSumLable(valueInc);
        } else {
            mChartOutcome.start();
            fadeInOutSumLable(valueOut);
        }
    }
    private void fadeInOutSumLable(final double value){
        final DecimalFormat df = new DecimalFormat("#.00 "+settings.getCurrency(), DecimalFormatSymbols.getInstance(Locale.US));
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
            mCard.animate()
                    .translationY(0f)
                    .setDuration(600)
                    .setListener(null);
        }
    }
    private void hideCard(){
        mCard.animate()
                .translationY(mCard.getHeight())
                .setDuration(600)
                .setListener(null);
        isCardShowing=false;
    }
    private void updateCard(){
        mCardText.animate()
                .setDuration(100)
                .alpha(0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mCardText.setText(selectedCat+selectedValue);
                        mCardText.animate()
                                .setDuration(100)
                                .alpha(1)
                                .setListener(null);
                    }
                });
    }
    private void onChangeItemClick(){
        if (isCardShowing) hideCard();
        isIncome = !isIncome;
        if(isIncome){
            mTextView.setText("Доходы"); //(" + dateStart + " - " + dateEnd+")");
            mChartOutcome.setVisibility(View.GONE);
            mChartIncome.setVisibility(View.VISIBLE);
            mChartIncome.start();
            fadeInOutSumLable(valueInc);
        } else {
            mTextView.setText("Расходы");
            mChartIncome.setVisibility(View.GONE);
            mChartOutcome.setVisibility(View.VISIBLE);
            mChartOutcome.start();
            fadeInOutSumLable(valueOut);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.diagram_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        View view = findViewById(itemId);
        switch (itemId){
            case R.id.diagram_menu_change_item:
                if(item.getIcon() instanceof Animatable) ((Animatable)item.getIcon()).start();
                onChangeItemClick();
                break;
        }
        return true;
    }
}

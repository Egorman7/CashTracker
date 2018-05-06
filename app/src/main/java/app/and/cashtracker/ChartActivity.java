package app.and.cashtracker;

import android.app.DatePickerDialog;
import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

import app.and.cashtracker.adapters.ChartListAdapter;
import app.and.cashtracker.database.DBHelper;
import app.and.cashtracker.database.Data;

public class ChartActivity extends AppCompatActivity {

    private RecyclerView mRecycler;
    private ChartListAdapter mAdapter;
    private Button mDateStart, mDateEnd;

    private boolean isIncome;
    private String dateStart, dateEnd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeView();
        initializeData();
        initializeListeners();
    }

    private void initializeView(){
        mDateStart = findViewById(R.id.chart_date_start);
        mDateEnd = findViewById(R.id.chart_date_end);
        mRecycler = findViewById(R.id.chart_recycler);
    }
    private void initializeData(){
        isIncome = false;
        dateEnd = getIntent().getStringExtra(MainActivity.DATE_END);
        dateStart = getIntent().getStringExtra(MainActivity.DATE_START);
        LinearLayoutManager llm = new LinearLayoutManager(this); llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(llm);
        setUpList();
    }

    private void initializeListeners(){
        mDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Data.getSDF().parse(dateStart));
                    new DatePickerDialog(ChartActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            Calendar c = Calendar.getInstance();
                            c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            dateStart = Data.getSDF().format(c.getTime());
                            setUpList();
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
                    new DatePickerDialog(ChartActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            Calendar c = Calendar.getInstance();
                            c.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            dateEnd = Data.getSDF().format(c.getTime());
                            setUpList();
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                } catch (Exception ex) {ex.printStackTrace();}
            }
        });
    }

    private void setUpList(){
        mDateStart.setText(dateStart); mDateEnd.setText(dateEnd);
        String[] categories = DBHelper.getCategories(DBHelper.getInstance(this), isIncome);
        ArrayList<String> cats = new ArrayList<>(Arrays.asList(categories));
        cats.add(0, ChartListAdapter.ALL_DATA);
        mAdapter = new ChartListAdapter(cats, this, dateStart, dateEnd, isIncome);
        mRecycler.setAdapter(mAdapter);
        for(int i=0; i<mAdapter.toDelete.size(); i++){
            mAdapter.getData().remove(mAdapter.toDelete.get(i));
            mAdapter.notifyItemRemoved(mAdapter.toDelete.get(i));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.diagram_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.diagram_menu_change_item){
            isIncome=!isIncome;
            setUpList();
            if(item.getIcon() instanceof Animatable) ((Animatable)item.getIcon()).start();
            return true;
        }
        return false;
    }
}

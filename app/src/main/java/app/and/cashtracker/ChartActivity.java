package app.and.cashtracker;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.github.mikephil.charting.charts.LineChart;

import java.util.ArrayList;
import java.util.Arrays;

import app.and.cashtracker.adapters.ChartListAdapter;
import app.and.cashtracker.database.DBHelper;
import app.and.cashtracker.database.Data;

public class ChartActivity extends AppCompatActivity {

    private LineChart mMainChart;
    private RecyclerView mRecycler;
    private ChartListAdapter mAdapter;

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
    }

    private void initializeView(){
        //mMainChart = findViewById(R.id.chart_main_chart);
        mRecycler = findViewById(R.id.chart_recycler);
    }
    private void initializeData(){
        isIncome = false;
        dateStart = Data.getCurrentDateSub(14,0);
        dateEnd = Data.getCurrentDate();
        setUpList();
//        String[] categories = DBHelper.getCategories(DBHelper.getInstance(this), isIncome);
//        final ArrayList<String> dates = Data.getDatesByPeriod(dateStart,dateEnd);
//        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
//        Log.d("CHARTING...", "Searching in categories...");
//        for(int i=0; i<categories.length; i++) {
//            Log.d("CHARTING...", "Category [" + categories[i] + "]");
//            ArrayList<Entry> entries = new ArrayList<>();
//            Map<String, Double> data = DBHelper.getDataForChart(this, categories[i], dateStart, dateEnd, isIncome);
//            Log.d("CHARTING...", "Data from DB got!");
//            for(String date : data.keySet()){
//                entries.add(new Entry((float)Data.posDateInDates(date, dates), data.get(date).floatValue()));
//            }
//            Log.d("CHARTING...", "Entries filled! Lenght: " + entries.size());
//            if(entries.size()==0) continue;
//            Collections.sort(entries, new EntryXComparator());
//            LineDataSet dataSet = new LineDataSet(entries, categories[i]);
//            Log.d("CHARTING...", "LineDataSet created!");
//            dataSet.setDrawIcons(false);
//            dataSet.setColor(DBHelper.getCategoryColor(DBHelper.getInstance(getApplicationContext()), categories[i], isIncome));
////            dataSet.setCircleColor(Color.BLACK);
////            dataSet.setLineWidth(1f);
////            dataSet.setCircleRadius(2f);
////            dataSet.setDrawCircleHole(false);
////            dataSet.setValueTextSize(9f);
////            dataSet.setDrawFilled(true);
////            dataSet.setFormLineWidth(1f);
////            dataSet.setFormLineDashEffect(new DashPathEffect(new float[]{10f, 5f}, 0f));
////            dataSet.setFormSize(15.f);
////            dataSet.setFillColor(Color.BLACK);
//            Log.d("CHARTING...", "Color set!");
//            dataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
//            dataSets.add(dataSet);
//            Log.d("CHARTING...", "Data added to DataSets!");
//        }
//        IAxisValueFormatter formatter = new IAxisValueFormatter() {
//            @Override
//            public String getFormattedValue(float value, AxisBase axis) {
//                if(value>=0 && value< dates.size()) return dates.get((int)value);
//                return "NULL";
//            }
//        };
//        Log.d("CHARTING...", "Formatter sut up!");
//        mMainChart.getXAxis().setGranularity(1f);
//        mMainChart.getXAxis().setValueFormatter(formatter);
//        Log.d("CHARTING...", "Formatter applied!");
//        mMainChart.setData(new LineData(dataSets));
//        Log.d("CHARTING...", "Data set!");
//        mMainChart.invalidate();
//        Log.d("CHARTING...", "Invalidating...");
    }

    private void setUpList(){
        String[] categories = DBHelper.getCategories(DBHelper.getInstance(this), isIncome);
        ArrayList<String> cats = new ArrayList<>(Arrays.asList(categories));
        cats.add(0, ChartListAdapter.ALL_DATA);
        mAdapter = new ChartListAdapter(cats, this, dateStart, dateEnd, isIncome);
        LinearLayoutManager llm = new LinearLayoutManager(this); llm.setOrientation(LinearLayoutManager.VERTICAL);
        mRecycler.setLayoutManager(llm);
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

package app.and.cashtracker.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.EntryXComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import app.and.cashtracker.R;
import app.and.cashtracker.database.DBHelper;
import app.and.cashtracker.database.Data;

public class ChartListAdapter extends RecyclerView.Adapter<ChartListAdapter.ViewHolder> {
    public static final String ALL_DATA = "all";
    private ArrayList<String> data;
    private Context context;
    private String dateStart, dateEnd;
    private boolean income;
    public ArrayList<Integer> toDelete;

    public ChartListAdapter(ArrayList<String> data, Context context, String dateStart, String dateEnd, boolean income) {
        this.data = data;
        this.context = context;
        this.dateStart = dateStart;
        this.dateEnd = dateEnd;
        this.income = income;
        toDelete = new ArrayList<>();
    }

    public ArrayList<String> getData() {
        return data;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.chart_list_item,parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Log.d("CHART_ADAPTER", data.get(position) + " at [" + position + "]");
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        final ArrayList<String> dates = Data.getDatesByPeriod(dateStart, dateEnd);
        if(data.get(position).equals(ALL_DATA)){
            ArrayList<Entry> entriesInc = new ArrayList<>();
            Map<String, Double> incData = DBHelper.getAllData(context, dateStart, dateEnd, true);
            for (String date : incData.keySet()){
                entriesInc.add(new Entry(Data.posDateInDates(date, dates), incData.get(date).floatValue()));
            }
            if(entriesInc.size()!=0){
                Collections.sort(entriesInc, new EntryXComparator());
                LineDataSet incSet = new LineDataSet(entriesInc, "Доход");
                incSet.setColor(ContextCompat.getColor(context,R.color.incomeColor));
                dataSets.add(incSet);
            }

            ArrayList<Entry> entriesOut = new ArrayList<>();
            Map<String, Double> outData = DBHelper.getAllData(context, dateStart, dateEnd, false);
            for (String date : outData.keySet()){
                entriesOut.add(new Entry(Data.posDateInDates(date, dates), outData.get(date).floatValue()));
            }
            if(entriesOut.size()!=0){
                Collections.sort(entriesOut, new EntryXComparator());
                LineDataSet outSet = new LineDataSet(entriesOut, "Расход");
                outSet.setColor(ContextCompat.getColor(context,R.color.outcomeColor));
                dataSets.add(outSet);
            }
        } else {
            Map<String, Double> chartData = DBHelper.getDataForChart(context, data.get(position), dateStart, dateEnd, income);
            ArrayList<Entry> entries = new ArrayList<>();
            for(String date : chartData.keySet()){
                entries.add(new Entry(Data.posDateInDates(date, dates), chartData.get(date).floatValue()));
            }
            if(entries.size()!=0){
                Collections.sort(entries, new EntryXComparator());
                LineDataSet dataSet = new LineDataSet(entries, data.get(position));
                dataSet.setColor(DBHelper.getCategoryColor(DBHelper.getInstance(context), data.get(position), income));
                dataSets.add(dataSet);
            }
        }
        if(dataSets.size()==0){
            toDelete.add(position);
            return;
        }
        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                if(value>=0 && value< dates.size()) return dates.get((int)value);
                return "NULL";
            }
        };
        holder.chart.getXAxis().setGranularity(1f);
        holder.chart.getXAxis().setValueFormatter(formatter);
        holder.chart.setData(new LineData(dataSets));
        holder.chart.invalidate();
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public LineChart chart;
        public ViewHolder(View itemView) {
            super(itemView);
            chart = itemView.findViewById(R.id.chart);
        }
    }
}

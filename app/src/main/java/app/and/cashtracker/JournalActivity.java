package app.and.cashtracker;

import android.app.ActivityOptions;
import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;

import java.util.Calendar;
import java.util.Date;

import app.and.cashtracker.adapters.RecordsListCursorAdapter;
import app.and.cashtracker.database.DBHelper;
import app.and.cashtracker.database.Data;

public class JournalActivity extends AppCompatActivity {
    private Button mDateStartButton, mDateEndButton;
    private ListView mListView;
    private RecordsListCursorAdapter mAdapter;
    private String dateStart, dateEnd;
    private BroadcastReceiver mReciever;
    private CardView mDescHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeView();
        initializeData();
        initializeListeners();


        mReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mListView.refreshDrawableState();
//                mListView.setVisibility(View.GONE);
//                mListView.setVisibility(View.VISIBLE);
            }
        };
        registerReceiver(mReciever, new IntentFilter("UPDATE_DATA"));
    }

    private void initializeView(){
        mDateStartButton = findViewById(R.id.journal_date_start_button);
        mDateEndButton = findViewById(R.id.journal_date_end_button);
        mListView = findViewById(R.id.journal_list_view);
        mDescHolder = findViewById(R.id.desc_holder_card);
    }
    private void initializeData(){
        dateEnd = getIntent().getStringExtra(MainActivity.DATE_END);
        dateStart = getIntent().getStringExtra(MainActivity.DATE_START);
        mDateStartButton.setText(dateStart);
        mDateEndButton.setText(dateEnd);
        mAdapter = new RecordsListCursorAdapter(this,DBHelper.getRecordsCursorByDates(DBHelper.getInstance(this),
                dateStart, dateEnd), dateStart, dateEnd, findViewById(R.id.desc_holder_card));
        mListView.setAdapter(mAdapter);
        mDescHolder.setVisibility(View.GONE);
    }
    private void initializeListeners(){
        mDateStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Data.getSDF().parse(dateStart));
                    new DatePickerDialog(JournalActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            Date date = cal.getTime();
                            dateStart = Data.getSDF().format(date);
                            mDateStartButton.setText(dateStart);
                            updateAdapter();
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                } catch (Exception ex) {ex.printStackTrace();}
            }
        });
        mDateEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(Data.getSDF().parse(dateEnd));
                    new DatePickerDialog(JournalActivity.this, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                            Calendar cal = Calendar.getInstance();
                            cal.set(datePicker.getYear(), datePicker.getMonth(), datePicker.getDayOfMonth());
                            Date date = cal.getTime();
                            dateEnd = Data.getSDF().format(date);
                            mDateEndButton.setText(dateEnd);
                            updateAdapter();
                        }
                    }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
                } catch (Exception ex) {ex.printStackTrace();}
            }
        });
    }

    private void updateAdapter(){
        mAdapter.swapCursor(DBHelper.getRecordsCursorByDates(DBHelper.getInstance(this),
                dateStart, dateEnd));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        updateAdapter();
    }

    @Override
    public void finish() {
        super.finish();
        unregisterReceiver(mReciever);
        setResult(1);
    }
}

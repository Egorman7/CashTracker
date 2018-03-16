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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.Calendar;
import java.util.Date;

import app.and.cashtracker.adapters.RecordsListCursorAdapter;
import app.and.cashtracker.database.DBHelper;

public class JournalActivity extends AppCompatActivity {
    private Button mDateStartButton, mDateEndButton;
    private ListView mListView;
    private RecordsListCursorAdapter mAdapter;
    private Date dateStart, dateEnd;
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
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR,-14);
        dateStart = cal.getTime();
        dateEnd = Calendar.getInstance().getTime();
        mDateStartButton.setText(DBHelper.SDF.format(dateStart));
        mDateEndButton.setText(DBHelper.SDF.format(dateEnd));
        mAdapter = new RecordsListCursorAdapter(this,DBHelper.getRecordsCursorByDates(DBHelper.getInstance(this),
                DBHelper.SDF.format(dateStart), DBHelper.SDF.format(dateEnd)), DBHelper.SDF.format(dateStart),DBHelper.SDF.format(dateEnd), findViewById(R.id.desc_holder_card));
        mListView.setAdapter(mAdapter);
        mDescHolder.setVisibility(View.GONE);
    }
    private void initializeListeners(){
        mDateStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(JournalActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                        Date date = cal.getTime();
                        dateStart =date;
                        mDateStartButton.setText(DBHelper.SDF.format(date));
                        updateAdapter();
                    }
                }, Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
        mDateEndButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog dpd = new DatePickerDialog(JournalActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                        Calendar cal = Calendar.getInstance();
                        cal.set(datePicker.getYear(),datePicker.getMonth(),datePicker.getDayOfMonth());
                        Date date = cal.getTime();
                        dateEnd =date;
                        mDateEndButton.setText(DBHelper.SDF.format(date));
                        updateAdapter();
                    }
                }, Calendar.getInstance().get(Calendar.YEAR),Calendar.getInstance().get(Calendar.MONTH),Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
                dpd.show();
            }
        });
    }

    private void updateAdapter(){
        mAdapter.swapCursor(DBHelper.getRecordsCursorByDates(DBHelper.getInstance(this),
                DBHelper.SDF.format(dateStart), DBHelper.SDF.format(dateEnd)));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.journal_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId=item.getItemId();
        View view = findViewById(itemId);
        switch (itemId){
            case R.id.journal_menu_diagram_item:
                onDiagramItemClick(view);
                break;
        }
        return true;
    }

    private void onDiagramItemClick(View view) {
        Intent intent = new Intent(JournalActivity.this, DiagramActivity.class);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(getResources().getColor(R.color.colorPrimary));
        Bundle bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
        startActivity(intent, bundle);
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
        overridePendingTransition(0, R.anim.activity_journal_zoom_out);
    }
}

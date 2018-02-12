package app.and.cashtracker;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.Calendar;

import app.and.cashtracker.adapters.RecordsListCursorAdapter;
import app.and.cashtracker.database.DBHelper;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private RecordsListCursorAdapter mAdapter;
    private TextView mDates, mIncome, mOutcome, mValue;
    private CardView mCard;

    private String startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //deleteDatabase(DBHelper.DB_NAME);

        initializeView();
        initializeData();
        initializeListeners();

        BroadcastReceiver mReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateInfoCard();
            }
        };
        registerReceiver(mReciever, new IntentFilter("UPDATE_DATA"));
    }

    private void initializeView() {
        mDates = findViewById(R.id.main_dates_info);
        mIncome = findViewById(R.id.main_income);
        mOutcome = findViewById(R.id.main_outcome);
        mValue = findViewById(R.id.main_balance);
        mListView = findViewById(R.id.main_list_view);
        mCard = findViewById(R.id.main_card);
    }

    private void initializeData() {
        endDate = DBHelper.SDF.format(Calendar.getInstance().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -14);
        startDate = DBHelper.SDF.format(calendar.getTime());
        mDates.setText(startDate + "  -  " + endDate);

        mIncome.setText(String.valueOf(DBHelper.getValuesSumForDates(DBHelper.getInstance(this), startDate, endDate, true)));
        mOutcome.setText("-" + String.valueOf(DBHelper.getValuesSumForDates(DBHelper.getInstance(this), startDate, endDate, false)));
        mValue.setText(String.valueOf(DBHelper.getBalance(DBHelper.getInstance(this))));

        mAdapter = new RecordsListCursorAdapter(this, DBHelper.getRecordsCursorByDates(DBHelper.getInstance(this), startDate, endDate), startDate, endDate);
        mListView.setAdapter(mAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemid = item.getItemId();
        View view = findViewById(itemid);
        switch (itemid) {
            case R.id.main_add_item:
                onAddSelected(view);
                break;
            case R.id.main_journal_item:
                onJournalSelected(view);
                break;
        }
        return true;
    }

    @SuppressLint("RestrictedApi")
    private void onJournalSelected(View view) {
        Intent intent = new Intent(MainActivity.this, JournalActivity.class);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(getResources().getColor(R.color.colorPrimary));
        Bundle bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
        startActivityForResult(intent, 1, bundle);
    }

    @SuppressLint("RestrictedApi")
    private void onAddSelected(View view) {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        intent.putExtra("edit", false);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(getResources().getColor(R.color.colorPrimary));
        Bundle bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
        startActivityForResult(intent, 1, bundle);
    }
    private void initializeListeners(){
        mCard.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Обновление")
                        .setMessage("Обновить данные?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(DBHelper.updateCurrentValue(DBHelper.getInstance(MainActivity.this))){
                                    updateInfoCard();
                                }
                                dialogInterface.dismiss();
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        })
                        .show();
                return true;
            }
        });
    }

    private void updateInfoCard() {
        mValue.setText(String.valueOf(DBHelper.getBalance(DBHelper.getInstance(this))));
        mIncome.setText(String.valueOf(DBHelper.getValuesSumForDates(DBHelper.getInstance(this), startDate, endDate, true)));
        mOutcome.setText("-" + String.valueOf(DBHelper.getValuesSumForDates(DBHelper.getInstance(this), startDate, endDate, false)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.swapCursor(DBHelper.getRecordsCursorByDates(DBHelper.getInstance(this), startDate, endDate));
        updateInfoCard();
    }
}

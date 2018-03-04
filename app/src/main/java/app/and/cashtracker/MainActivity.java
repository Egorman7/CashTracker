package app.and.cashtracker;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.util.Pair;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import app.and.cashtracker.adapters.RecordsListCursorAdapter;
import app.and.cashtracker.database.DBHelper;

public class MainActivity extends AppCompatActivity {

    private ImageButton mJournalButton, mAddButton;
    private ListView mListView;
    private RecordsListCursorAdapter mAdapter;
    private TextView mDates, mIncome, mOutcome, mValue;
    private CardView mCard;

    private String startDate, endDate;
    private BroadcastReceiver mReciever;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //deleteDatabase(DBHelper.DB_NAME);

        initializeView();
        initializeData();
        initializeListeners();

        mReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateInfoCard();
                //mAdapter.swapCursor(DBHelper.getRecordsCursorByDates(DBHelper.getInstance(MainActivity.this),startDate,endDate));
            }
        };
        registerReceiver(mReciever,new IntentFilter("UPDATE_DATA"));
    }

    private void initializeView(){
        mJournalButton = findViewById(R.id.main_journal_button);
        mAddButton = findViewById(R.id.main_add_button);
        mDates = findViewById(R.id.main_dates_info);
        mIncome = findViewById(R.id.main_income);
        mOutcome = findViewById(R.id.main_outcome);
        mValue = findViewById(R.id.main_balance);
        mListView = findViewById(R.id.main_list_view);
        mCard = findViewById(R.id.main_card);
    }
    private void initializeData(){
        endDate = DBHelper.SDF.format(Calendar.getInstance().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -14);
        startDate = DBHelper.SDF.format(calendar.getTime());
        mDates.setText(startDate + "  -  " + endDate);

        /*mIncome.setText(String.valueOf(DBHelper.getValuesSumForDates(DBHelper.getInstance(this),startDate, endDate, true)));
        mOutcome.setText("-"+String.valueOf(DBHelper.getValuesSumForDates(DBHelper.getInstance(this),startDate, endDate, false)));
        mValue.setText(String.valueOf(DBHelper.getBalance(DBHelper.getInstance(this))));*/
        updateInfoCard();

        mAdapter = new RecordsListCursorAdapter(this, DBHelper.getRecordsCursorByDates(DBHelper.getInstance(this),startDate, endDate),startDate,endDate);
        mListView.setAdapter(mAdapter);
    }
    private void initializeListeners(){
        mJournalButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, JournalActivity.class);
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(getResources().getColor(R.color.colorPrimary));
                Bundle bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view,bitmap,0,0).toBundle();
                startActivityForResult(intent,1,bundle);
            }
        });
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                intent.putExtra("edit",false);
                Bitmap bitmap = Bitmap.createBitmap(view.getWidth(),view.getHeight(),Bitmap.Config.ARGB_8888);
                bitmap.eraseColor(getResources().getColor(R.color.colorPrimary));
                Bundle bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view,bitmap,0,0).toBundle();
                startActivityForResult(intent,1,bundle);
            }
        });
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

    private void updateInfoCard(){
        DecimalFormat df = new DecimalFormat("#.00 UAH", DecimalFormatSymbols.getInstance(Locale.US));
        mValue.setText(df.format(DBHelper.getBalance(DBHelper.getInstance(this))));
        mIncome.setText(df.format(DBHelper.getValuesSumForDates(DBHelper.getInstance(this),startDate, endDate, true)));
        mOutcome.setText("-"+df.format(DBHelper.getValuesSumForDates(DBHelper.getInstance(this),startDate, endDate, false)));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mAdapter.swapCursor(DBHelper.getRecordsCursorByDates(DBHelper.getInstance(this),startDate,endDate));
        updateInfoCard();
    }

    @Override
    public void finish() {
        unregisterReceiver(mReciever);
        super.finish();
    }
}

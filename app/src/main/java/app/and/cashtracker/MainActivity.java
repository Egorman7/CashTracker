package app.and.cashtracker;

import android.annotation.SuppressLint;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.transition.Explode;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.hotmail.or_dvir.easysettings.pojos.EasySettings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import app.and.cashtracker.adapters.RecordsListCursorAdapter;
import app.and.cashtracker.database.DBHelper;
import app.and.cashtracker.database.Data;
import app.and.cashtracker.system.Settings;
import app.and.cashtracker.system.service.NotificationService;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private RecordsListCursorAdapter mAdapter;
    private TextView mIncome, mOutcome, mValue;
    private TextView mDateStart, mDateEnd;
    private CardView mCard, mDescHolder;
    private NavigationView mNavigation;
    private ActionBarDrawerToggle mDrawerToogle;
    private DrawerLayout mDrawerLayout;

    private String dateStart, dateEnd;
    private BroadcastReceiver mReciever;

    private Settings settings;

    private static final int REQUEST_ADD=1, REQUEST_JOURNAL=2, REQUEST_PIN=3, REQUEST_SETTINGS =4;
    public static final String DATE_START = "dateStart", DATE_END = "dateEnd";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_CONTENT_TRANSITIONS);
        getWindow().setExitTransition(new Explode());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        settings = new Settings(this);
        //EasySettings.retrieveSettingsSharedPrefs(this).edit().putBoolean(Settings.KEY_PIN,false).apply();
//        Intent service = new Intent(this, NotificationService.class);
//        stopService(service);
//        startService(service);

        //deleteDatabase(DBHelper.DB_NAME);

        SharedPreferences preferences = EasySettings.retrieveSettingsSharedPrefs(this);
        if(preferences.getBoolean(Settings.KEY_PIN, false)){
            startActivityForResult(new Intent(this,PinActivity.class),REQUEST_PIN);
        }

        initializeView();
        initializeData();
        initializeListeners();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateInfoCard();
                //mAdapter.swapCursor(DBHelper.getRecordsCursorByDates(DBHelper.getInstance(MainActivity.this),dateStart,endDate));
            }
        };
        registerReceiver(mReciever,new IntentFilter("UPDATE_DATA"));
    }

    private void initializeView(){
        mDateStart = findViewById(R.id.main_date_start);
        mDateEnd = findViewById(R.id.main_date_end);
        mIncome = findViewById(R.id.main_income);
        mOutcome = findViewById(R.id.main_outcome);
        mValue = findViewById(R.id.main_balance);
        mListView = findViewById(R.id.main_list_view);
        mCard = findViewById(R.id.main_card);
        mDescHolder = findViewById(R.id.desc_holder_card);
        mNavigation = findViewById(R.id.main_navigation);
        mDrawerLayout = findViewById(R.id.main_drawer_layout);
        mDrawerToogle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_closer);
    }
    private void initializeData(){
        dateEnd = Data.getCurrentDate();
        dateStart = Data.getDateMinusPeriod(settings.getPeriod());
        mDateStart.setText(dateStart); mDateEnd.setText(dateEnd);

        updateInfoCard();

        mDescHolder.setVisibility(View.GONE);

        mAdapter = new RecordsListCursorAdapter(this, DBHelper.getRecordsCursorByDates(DBHelper.getInstance(this), dateStart, dateEnd), dateStart,dateEnd, mDescHolder);
        mListView.setAdapter(mAdapter);
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
        mDrawerLayout.setDrawerListener(mDrawerToogle);
        mDrawerToogle.syncState();
        mNavigation.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch (item.getItemId()){
                    case R.id.main_drawer_item_1:
                        intent = new Intent(MainActivity.this, JournalActivity.class);
                        intent.putExtra(DATE_START, dateStart);
                        intent.putExtra(DATE_END, dateEnd);
                        startActivityForResult(intent,REQUEST_JOURNAL);
                        break;
                    case R.id.main_drawer_item_2:
                        intent = new Intent(MainActivity.this, DiagramActivity.class);
                        intent.putExtra(DATE_START, dateStart);
                        intent.putExtra(DATE_END, dateEnd);
                        startActivity(intent);
                        break;
                    case R.id.main_drawer_item_3:
                        intent = new Intent(MainActivity.this, ChartActivity.class);
                        intent.putExtra(DATE_START, dateStart);
                        intent.putExtra(DATE_END, dateEnd);
                        startActivity(intent);
                        break;
                    case R.id.main_drawer_item_4:
                        intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivityForResult(intent, REQUEST_SETTINGS);
                        break;
                }
                mDrawerLayout.closeDrawers();
                return true;
            }
        });
    }

    private void updateInfoCard(){
        DecimalFormat df = new DecimalFormat("#.00 "+settings.getCurrency(), DecimalFormatSymbols.getInstance(Locale.US));
        mValue.setText(df.format(DBHelper.getBalance(DBHelper.getInstance(this))));
        mIncome.setText(df.format(DBHelper.getValuesSumForDates(DBHelper.getInstance(this), dateStart, dateEnd, true)));
        mOutcome.setText("-"+df.format(DBHelper.getValuesSumForDates(DBHelper.getInstance(this), dateStart, dateEnd, false)));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToogle.onOptionsItemSelected(item)) return true;
        int itemId=item.getItemId();
        View view = findViewById(itemId);
        switch (itemId){
            case R.id.main_menu_add_item:
                onAddItemClick(view);
                break;
        }
        return true;
    }

    @SuppressLint("RestrictedApi")
    private void onAddItemClick(View view) {
        Intent intent = new Intent(MainActivity.this, AddActivity.class);
        intent.putExtra("edit", false);
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        bitmap.eraseColor(getResources().getColor(R.color.colorPrimary));
        Bundle bundle = ActivityOptions.makeThumbnailScaleUpAnimation(view, bitmap, 0, 0).toBundle();
        startActivityForResult(intent, REQUEST_ADD, bundle);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_SETTINGS){
            dateEnd = Data.getCurrentDate();
            dateStart = Data.getDateMinusPeriod(settings.getPeriod());
            mDateStart.setText(dateStart); mDateEnd.setText(dateEnd);
        }
        if(requestCode==REQUEST_ADD || requestCode==REQUEST_JOURNAL || requestCode == REQUEST_SETTINGS){
            mAdapter.swapCursor(DBHelper.getRecordsCursorByDates(DBHelper.getInstance(this), dateStart,dateEnd));
            updateInfoCard();
        }
    }

    @Override
    public void finish() {
        unregisterReceiver(mReciever);
        super.finish();
    }
}

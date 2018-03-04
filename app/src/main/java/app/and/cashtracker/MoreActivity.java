package app.and.cashtracker;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import app.and.cashtracker.adapters.MoreListCursorAdapter;
import app.and.cashtracker.database.DBHelper;

public class MoreActivity extends AppCompatActivity {

    private TextView mTitle;
    private ListView mList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeView();
        initializeData();
    }

    private void initializeView(){
        mTitle = findViewById(R.id.more_title);
        mList = findViewById(R.id.more_list);
    }
    private void initializeData(){
        mTitle.setText(getIntent().getStringExtra("cat"));
        mList.setAdapter(new MoreListCursorAdapter(this, DBHelper.getMoreCursor(DBHelper.getInstance(this),
                getIntent().getStringExtra("dateStart"),
                getIntent().getStringExtra("dateEnd"),
                getIntent().getBooleanExtra("income",false),
                getIntent().getStringExtra("cat")),
                getIntent().getBooleanExtra("income",false)));
    }

}

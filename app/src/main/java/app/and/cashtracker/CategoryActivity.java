package app.and.cashtracker;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.github.naz013.colorslider.ColorSlider;

import app.and.cashtracker.adapters.CategoryListCursorAdapter;
import app.and.cashtracker.database.DBHelper;

public class CategoryActivity extends AppCompatActivity {

    private CategoryListCursorAdapter mAdapter;
    private ImageButton mRenewButton;
    private ListView mListView;
    private boolean isIncomeCats;
    private FloatingActionButton mFab;

    private int[] mColors = {
            Color.RED, Color.YELLOW, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.WHITE, Color.GRAY, Color.BLACK
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        isIncomeCats = false;

        initializeView();
        initializeData();
        initializeListeners();

    }

    private void initializeView(){
        mFab = findViewById(R.id.fab);
        mListView = findViewById(R.id.category_list);
        mRenewButton = findViewById(R.id.category_renew);
    }
    private void initializeData(){
        mAdapter = new CategoryListCursorAdapter(this,DBHelper.getCategoriesCursor(DBHelper.getInstance(this),isIncomeCats),isIncomeCats);
        mListView.setAdapter(mAdapter);
    }
    private void initializeListeners(){
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v = LayoutInflater.from(CategoryActivity.this).inflate(R.layout.category_create_dialog, null);
                final TextInputEditText input = v.findViewById(R.id.cat_create_input);
                final ImageView image = v.findViewById(R.id.cat_create_image);
                final ColorSlider slider = v.findViewById(R.id.cat_create_color_slider);
                slider.setGradient(mColors,255);
                slider.setListener(new ColorSlider.OnColorSelectedListener() {
                    @Override
                    public void onColorChanged(int pos, int color) {
                        Bitmap bm = Bitmap.createBitmap(image.getWidth(),image.getHeight(), Bitmap.Config.ARGB_8888);
                        bm.eraseColor(color);
                        image.setImageBitmap(bm);
                    }
                });
                Bitmap bm = Bitmap.createBitmap(20,20, Bitmap.Config.ARGB_8888);
                bm.eraseColor(slider.getSelectedColor());
                image.setScaleType(ImageView.ScaleType.FIT_XY);
                image.setImageBitmap(bm);
                new AlertDialog.Builder(CategoryActivity.this)
                        .setTitle("Создать категорию")
                        .setView(v)
                        .setPositiveButton("Создать", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(input.getText().toString().isEmpty()){
                                    Toast.makeText(getApplicationContext(), "Заполните поле!", Toast.LENGTH_LONG).show();
                                    dialogInterface.dismiss();
                                    CategoryActivity.this.setResult(1);
                                    return;
                                }
                                mAdapter.addCategory(input.getText().toString(),slider.getSelectedColor(),DBHelper.getInstance(CategoryActivity.this),isIncomeCats);
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();

            }
        });
        mRenewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isIncomeCats = !isIncomeCats;
                mAdapter = new CategoryListCursorAdapter(CategoryActivity.this,DBHelper.getCategoriesCursor(DBHelper.getInstance(CategoryActivity.this),isIncomeCats),isIncomeCats);
                mListView.setAdapter(mAdapter);
            }
        });
    }

}

package app.and.cashtracker.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import app.and.cashtracker.R;
import app.and.cashtracker.database.DBHelper;

/**
 * Created by Egorman on 04.02.2018.
 */

public class CategoryListCursorAdapter extends CursorAdapter {

    private boolean isIncome;

    public CategoryListCursorAdapter(Context context, Cursor cursor, boolean isIncome){
        super(context,cursor,0);
        this.isIncome = isIncome;
    }

    public void addCategory(String name, int color, DBHelper dbHelper, boolean income){
        if(DBHelper.addCategory(dbHelper,name,color, income)) swapCursor(DBHelper.getCategoriesCursor(dbHelper, income));
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.category_list_item, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {
        TextView catName = view.findViewById(R.id.category_list_item_text);
        final String name = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.CAT_NAME));
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CAT_ID));
        int color = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CAT_COLOR));
        final boolean income = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.CAT_INCOME)) > 0;
        catName.setText(name);
        ImageView imageView = view.findViewById(R.id.category_list_item_image);
        Bitmap bm = Bitmap.createBitmap(32,32, Bitmap.Config.ARGB_8888);
        bm.eraseColor(color);
        imageView.setImageBitmap(bm);
        ImageButton delButt = view.findViewById(R.id.category_list_item_delete);
        if(id==1 || id==5) delButt.setVisibility(View.GONE);
        else
        delButt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new AlertDialog.Builder(context)
                        .setTitle("Удалить")
                        .setMessage("Удалить категорию "+name+"?")
                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(DBHelper.removeCategoryById(DBHelper.getInstance(context),id, income))
                                    //notifyDataSetChanged();
                                    swapCursor(DBHelper.getCategoriesCursor(DBHelper.getInstance(context),isIncome));
                            }
                        })
                        .setNegativeButton("Нет", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        }).show();
            }
        });
    }
}

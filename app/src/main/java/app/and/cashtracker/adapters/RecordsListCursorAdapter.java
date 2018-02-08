package app.and.cashtracker.adapters;

import android.app.Activity;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import app.and.cashtracker.AddActivity;
import app.and.cashtracker.R;
import app.and.cashtracker.database.DBHelper;

/**
 * Created by Egorman on 05.02.2018.
 */

public class RecordsListCursorAdapter extends CursorAdapter {

    private String dateStart, dateEnd;
    public RecordsListCursorAdapter(Context context, Cursor cursor, String dateStart, String dateEnd){
        super(context,cursor,0);
        this.dateStart=dateStart;
        this.dateEnd=dateEnd;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.info_card, viewGroup, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        TextView dateView = view.findViewById(R.id.info_card_date);
        TextView valueView = view.findViewById(R.id.info_card_money);
        TextView catView = view.findViewById(R.id.info_card_category);
        final CardView cardView = view.findViewById(R.id.info_card);
        final int id = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.REC_ID));
        final double val = cursor.getDouble(cursor.getColumnIndexOrThrow(DBHelper.REC_VALUE));
        final String value = String.valueOf(val),
                date = cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.REC_DATE)),
                cat = DBHelper.getCategoryById(DBHelper.getInstance(context),cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.REC_CAT)));
        final boolean income = cursor.getInt(cursor.getColumnIndexOrThrow(DBHelper.REC_INCOME)) > 0;
        cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {
                PopupMenu menu = new PopupMenu(view.getContext(),view);
                menu.inflate(R.menu.info_card_menu);
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        switch (menuItem.getItemId()){
                            case R.id.info_card_menu_1:
                                Intent intent = new Intent(context, AddActivity.class);
                                intent.putExtra("edit", true);
                                intent.putExtra("value", val);
                                intent.putExtra("cat", cat);
                                intent.putExtra("date",date);
                                intent.putExtra("id",id);
                                intent.putExtra("income",income);
                                Bitmap bitmap = Bitmap.createBitmap(cardView.getWidth()/5,cardView.getHeight(),Bitmap.Config.ARGB_8888);
                                bitmap.eraseColor(context.getResources().getColor(R.color.colorPrimary));
                                Bundle bundle = ActivityOptions.makeThumbnailScaleUpAnimation(cardView,bitmap,cardView.getWidth()/2,cardView.getHeight()/2).toBundle();
                                ((Activity)context).startActivityForResult(intent,1, bundle);
                                return true;
                            case R.id.info_card_menu_2:
                                new AlertDialog.Builder(view.getContext())
                                        .setTitle("Удалить")
                                        .setMessage("Удалить запись?")
                                        .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {
                                                if(DBHelper.removeRecordById(DBHelper.getInstance(context),id))
                                                {
                                                    swapCursor(DBHelper.getRecordsCursorByDates(DBHelper.getInstance(context),dateStart,dateEnd));
                                                    Intent intent1 = new Intent("UPDATE_DATA");
                                                    context.sendBroadcast(intent1);
                                                }
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
                        return false;
                    }
                });
                menu.show();
                return true;
            }
        });
        dateView.setText(date);
        catView.setText(cat);
        if(!income){
            valueView.setText("-" + value);
            valueView.setTextColor(ContextCompat.getColor(context,R.color.outcomeColor));
        } else {
            valueView.setText(value);
            valueView.setTextColor(ContextCompat.getColor(context,R.color.incomeColor));
        }
    }
}

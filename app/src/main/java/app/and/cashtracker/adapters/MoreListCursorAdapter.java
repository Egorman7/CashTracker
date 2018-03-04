package app.and.cashtracker.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import app.and.cashtracker.R;
import app.and.cashtracker.database.DBHelper;

/**
 * Created by Egorman on 04.03.2018.
 */

public class MoreListCursorAdapter extends CursorAdapter {
    private boolean income;
    public MoreListCursorAdapter(Context context, Cursor cursor, boolean income){
        super(context,cursor,0);
        this.income=income;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        return LayoutInflater.from(context).inflate(R.layout.more_list_item,viewGroup,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView dateView = view.findViewById(R.id.more_list_date);
        TextView valueView = view.findViewById(R.id.more_list_value);
        dateView.setText(cursor.getString(cursor.getColumnIndexOrThrow(DBHelper.REC_DATE)));
        valueView.setText((income ? "" : "-")+new DecimalFormat("#.00 UAH", DecimalFormatSymbols.getInstance(Locale.US)).format(cursor.getDouble(cursor.getColumnIndexOrThrow("sum"))));
        valueView.setTextColor(ContextCompat.getColor(context,income ? R.color.incomeColor : R.color.outcomeColor));
    }
}

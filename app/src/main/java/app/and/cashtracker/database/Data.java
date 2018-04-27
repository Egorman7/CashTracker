package app.and.cashtracker.database;

import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

// в этом классе плюшки для подсчета календаря и прочей шняги
public class Data {
    private final static String[] dayNames = {"","Воскресенье", "Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота"};
    public static final String[] PERIOD = {"1 неделя", "2 недели", "Месяц"};
    private static SimpleDateFormat SDF;
    public static SimpleDateFormat getSDF(){
        if(SDF==null) SDF = new SimpleDateFormat(DBHelper.DATE_FORMAT);
        return SDF;
    }
    public static String getCurrentDate(){
        return getSDF().format(Calendar.getInstance().getTime());
    }
    public static String getCurrentDateSub(int days, int months){
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, -days);
        calendar.add(Calendar.MONTH, -months);
        return getSDF().format(calendar.getTime());
    }
    public static String getDayName(String date){
        try {
            Date currentDate = getSDF().parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            return dayNames[calendar.get(Calendar.DAY_OF_WEEK)];
        } catch (Exception ex) {ex.printStackTrace();}
        return "";
    }
    public static int[] getDaysMonthsPeriod(String value){
        int[] array = new int[2];
        if(value.equals(PERIOD[0])) {array[0]=7; array[1]=0;}
        else if(value.equals(PERIOD[1])) {array[0]=14; array[1]=0;}
        else if(value.equals(PERIOD[2])) {array[0]=0; array[1]=1;}
        return array;
    }
    public static ArrayList<String> getDatesByPeriod(String dateStart, String dateEnd){
        try {
            Log.d("DATES", "GET DATES");
            ArrayList<String> dates = new ArrayList<>();
            Calendar from = Calendar.getInstance();
            from.setTime(getSDF().parse(dateStart));
            Calendar to = Calendar.getInstance();
            to.setTime(getSDF().parse(dateEnd));
            while (!isDatesSimilarEq(from, to)){
                dates.add(getSDF().format(from.getTime()));
                from.add(Calendar.DAY_OF_YEAR,1);
                //Log.d("DATES", getSDF().format(from.getTime()));
            }
            return dates;
            //String[] datesArray = new String[dates.size()];
            //dates.toArray(datesArray);
            //Log.d("dates", datesArray.length+" " +datesArray[0]);
            //return datesArray;
        } catch (Exception ex) {ex.printStackTrace();}
        return null;
    }
    private static boolean isDatesSimilarEq(Calendar c1, Calendar c2){
        Calendar c = (Calendar)c2.clone();
        c.add(Calendar.DAY_OF_YEAR,1);
        //Log.d("SIMILAR", c1.get(Calendar.YEAR)+"-"+c1.get(Calendar.MONTH)+"-"+c1.get(Calendar.DAY_OF_MONTH)+" -- "+c2.get(Calendar.YEAR)+"-"+c2.get(Calendar.MONTH)+"-"+c2.get(Calendar.DAY_OF_MONTH));
        return (c1.get(Calendar.YEAR)==c.get(Calendar.YEAR) && c1.get(Calendar.MONTH)==c.get(Calendar.MONTH) && c1.get(Calendar.DAY_OF_MONTH)==c.get(Calendar.DAY_OF_MONTH));
    }
    public static int posDateInDates(String date, ArrayList<String> dates){
        for(int i=0; i<dates.size(); i++){
            if(date.equals(dates.get(i))) return i;
        }
        return -1;
    }
}

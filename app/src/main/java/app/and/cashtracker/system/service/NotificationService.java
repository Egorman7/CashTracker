package app.and.cashtracker.system.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.hotmail.or_dvir.easysettings.pojos.EasySettings;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Calendar;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import app.and.cashtracker.R;
import app.and.cashtracker.database.DBHelper;
import app.and.cashtracker.database.Data;
import app.and.cashtracker.system.Settings;
import br.com.goncalves.pugnotification.notification.PugNotification;

public class NotificationService extends Service {
    private static final int UPDATE_PERIOD = 1 * 10 * 1000; // min / sec / ms
    private static final String NOTIFICATION_CHANNEL = "CashTrackerNotify";
    private static final int NOTIFICATION_ID = 1;
    private Timer timer;
    private TimerTask task = new TimerTask() {
        @Override
        public void run() {
            Settings settings = new Settings(NotificationService.this);
            if(settings.getNotifications()){
                String start = Data.getCurrentDate();
                String end = Data.getDateMinusPeriod(settings.getPeriod());
                DecimalFormat df = new DecimalFormat("#.00 "+settings.getCurrency(), DecimalFormatSymbols.getInstance(Locale.US));
                String balance = df.format(DBHelper.getBalance(DBHelper.getInstance(getApplicationContext())));
                String income = df.format(DBHelper.getValuesSumForDates(DBHelper.getInstance(getApplicationContext()),start, end, true));
                String outcome = df.format(DBHelper.getValuesSumForDates(DBHelper.getInstance(getApplicationContext()),start, end, false));
                PugNotification.with(getApplicationContext())
                        .load()
                        .title("Cash Tracker")
                        .message("А вы уже добавили записи за сегодня?")
                        .bigTextStyle("Баланс: " + balance + "\nДоход: " + income + "\nРасход: " + outcome)
                        .smallIcon(R.drawable.ic_launcher_foreground)
                        .largeIcon(R.drawable.ic_launcher_foreground)
                        .flags(Notification.DEFAULT_ALL)
                        .simple()
                        .build();
//                Notification notification = new Notification.Builder(NotificationService.this)
//                        .setContentTitle("Cash Tracker")
//                        .setContentTitle("А вы уже добавили записи за сегодня?")
//                        .setContentText("Баланс: " + balance + "\nДоход: " + income + "\nРасход: " + outcome)
//                        .setSmallIcon(R.drawable.ic_launcher_foreground)
//                        .setDefaults(Notification.DEFAULT_ALL)
//                        .setAutoCancel(true)
//                        //.setLargeIcon(R.drawable.ic_launcher_foreground)
//                        .build();
//                //NotificationManager nm = (NotificationManager)getSystemService(Service.NOTIFICATION_SERVICE);
//                //nm.notify(NOTIFICATION_ID, notification);
//                startForeground(NOTIFICATION_ID, notification);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        timer = new Timer();
        Toast.makeText(this, "Create service!", Toast.LENGTH_SHORT).show();

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        timer.schedule(task, UPDATE_PERIOD, UPDATE_PERIOD);
        Toast.makeText(this, "Start service!", Toast.LENGTH_SHORT).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Destroy service!", Toast.LENGTH_SHORT).show();
        timer.cancel();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}

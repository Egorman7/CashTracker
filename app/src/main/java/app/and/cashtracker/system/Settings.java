package app.and.cashtracker.system;

import android.content.Context;

import com.hotmail.or_dvir.easysettings.pojos.BasicSettingsObject;
import com.hotmail.or_dvir.easysettings.pojos.EasySettings;
import com.hotmail.or_dvir.easysettings.pojos.SettingsObject;
import com.hotmail.or_dvir.easysettings.pojos.SwitchSettingsObject;
import com.hotmail.or_dvir.easysettings_dialogs.pojos.EditTextSettingsObject;
import com.hotmail.or_dvir.easysettings_dialogs.pojos.ListSettingsObject;

import java.util.ArrayList;
import java.util.Arrays;

import app.and.cashtracker.database.DBHelper;
import app.and.cashtracker.database.Data;

public class Settings {
    public static final String KEY_CURRENCY = "currencyKey", KEY_PERIOD = "periodKey", KEY_NOTIFICATIONS = "notifyKey", KEY_PIN = "pinKey", KEY_FABADD = "fabKey";

    private ArrayList<SettingsObject> settings;
    private ArrayList<String> periods;
    private Context context;

    public Settings(Context context){
        this.context = context;
        periods = new ArrayList<>(Arrays.asList(Data.PERIOD));
        createSettings();
    }

    private void createSettings(){
        settings = new ArrayList<>();
        settings.add(new EditTextSettingsObject.Builder(KEY_CURRENCY, "Название валюты", "UAH", "OK")
                .setUseValueAsPrefillText().setDialogTitle("Введите валюту").setUseValueAsSummary().build());
        settings.add(new ListSettingsObject.Builder(KEY_PERIOD, "Период","Месяц", periods, "OK")
                .setUseValueAsSummary().setDialogTitle("Период").build());
        settings.add(new SwitchSettingsObject.Builder(KEY_PIN, "Пин-код", false)
                .setSummary("Пин-код для защиты приложения").build());
//        settings.add(new SwitchSettingsObject.Builder(KEY_NOTIFICATIONS, "Опевещения", false)
//                .setSummary("Оповещения в конце каждой недели").build());
//        settings.add(new SwitchSettingsObject.Builder(KEY_FABADD, "Кнопка \'+\'", false)
//                .setOnText("Кнопка внизу справа").setOffText("Кнопка вверху в меню").setUseValueAsSummary().build());
        EasySettings.initializeSettings(context, settings);
    }

    public String getCurrency(){
        return EasySettings.retrieveSettingsSharedPrefs(context).getString(KEY_CURRENCY,"$");
    }

    public String getPin() {
        return DBHelper.getPin(context);
    }

    public boolean getNotifications(){
        return EasySettings.retrieveSettingsSharedPrefs(context).getBoolean(KEY_NOTIFICATIONS, false);
    }

    public boolean getAddButton(){
        return EasySettings.retrieveSettingsSharedPrefs(context).getBoolean(KEY_FABADD,false);
    }

    public int[] getPeriod(){
        return Data.getDaysMonthsPeriod(EasySettings.retrieveSettingsSharedPrefs(context).getString(KEY_PERIOD, "Месяц"));
    }

    public ArrayList<SettingsObject> getSettingsList() {
        return settings;
    }
}

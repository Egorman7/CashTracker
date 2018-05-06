package app.and.cashtracker;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.hotmail.or_dvir.easysettings.events.SwitchSettingsClickEvent;
import com.hotmail.or_dvir.easysettings.pojos.CheckBoxSettingsObject;
import com.hotmail.or_dvir.easysettings.pojos.EasySettings;
import com.hotmail.or_dvir.easysettings.pojos.SettingsObject;
import com.hotmail.or_dvir.easysettings.pojos.SwitchSettingsObject;
import com.hotmail.or_dvir.easysettings_dialogs.pojos.DialogSettingsObject;
import com.hotmail.or_dvir.easysettings_dialogs.pojos.EditTextSettingsObject;
import com.hotmail.or_dvir.easysettings_dialogs.pojos.ListSettingsObject;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;

import app.and.cashtracker.database.DBHelper;
import app.and.cashtracker.system.Settings;

public class SettingsActivity extends AppCompatActivity {

    private Settings settings;
    private LinearLayout container;
    private boolean pinDefault;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        container = findViewById(R.id.settings_main_layout);

        settings = new Settings(this);

        EasySettings.inflateSettingsLayout(this, container, settings.getSettingsList());
        SwitchSettingsClickEvent event = new SwitchSettingsClickEvent((SwitchSettingsObject)EasySettings.findSettingsObject(Settings.KEY_PIN, settings.getSettingsList()));
        pinDefault = EasySettings.retrieveSettingsSharedPrefs(this).getBoolean(Settings.KEY_PIN,false);
        EventBus.getDefault().post(event);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onPinChecked(final SwitchSettingsClickEvent event){
        if(event.getClickedSettingsObj().getKey().equals(Settings.KEY_PIN) && pinDefault!=EasySettings.retrieveSettingsSharedPrefs(this).getBoolean(Settings.KEY_PIN, false)){
            boolean flag = EasySettings.retrieveSettingsSharedPrefs(this).getBoolean(Settings.KEY_PIN, false);
            if(flag){
                View view = LayoutInflater.from(this).inflate(R.layout.dialog_create_pin, null);
                final EditText pin1 = view.findViewById(R.id.dialog_pin_1);
                final EditText pin2 = view.findViewById(R.id.dialog_pin_2);
                new AlertDialog.Builder(this)
                    .setView(view)
                    .setTitle("Придумайте пин-код")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            if(pin1.getText().toString().length()<4) {dismissPinSwitch(event); Toast.makeText(getApplicationContext(), "Введите 4 цифры!", Toast.LENGTH_SHORT).show(); return;}
                            if(pin1.getText().toString().equals(pin2.getText().toString())){
                                if(DBHelper.setPin(SettingsActivity.this, pin1.getText().toString())){
                                    Toast.makeText(getApplicationContext(), "Пин-код установлен!", Toast.LENGTH_SHORT).show();
                                    pinDefault=!pinDefault;
                                } else {Toast.makeText(getApplicationContext(), "Ошибка в работе БД!", Toast.LENGTH_SHORT).show(); dismissPinSwitch(event);}
                            } else {Toast.makeText(getApplicationContext(), "Пин-коды не совпадают!", Toast.LENGTH_SHORT).show(); dismissPinSwitch(event);}
                        }
                    })
                    .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dismissPinSwitch(event);
                        }
                    }).setCancelable(false).show();
            } else {
                View view = LayoutInflater.from(SettingsActivity.this).inflate(R.layout.dialog_remove_pin, null);
                final EditText pin = view.findViewById(R.id.dialog_repin);
                new AlertDialog.Builder(this)
                        .setView(view)
                        .setTitle("Придумайте пин-код")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(pin.getText().toString().equals(settings.getPin())){
                                    Toast.makeText(getApplicationContext(), "Пин-код убран!", Toast.LENGTH_SHORT).show();
                                    pinDefault=!pinDefault;
                                } else {Toast.makeText(getApplicationContext(), "Неверный пин!", Toast.LENGTH_SHORT).show(); dismissPinSwitch(event);}
                            }
                        })
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dismissPinSwitch(event);
                            }
                        }).setCancelable(false).show();
            }
        }
    }

    private void dismissPinSwitch(SwitchSettingsClickEvent event){
        EasySettings.retrieveSettingsSharedPrefs(SettingsActivity.this).edit().putBoolean(Settings.KEY_PIN,pinDefault).apply();
        ((Switch)container.findViewById(event.getClickedSettingsObj().getCompoundButtonId())).setChecked(pinDefault);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }
}

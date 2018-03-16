package app.and.cashtracker.models;

import java.util.Date;

/**
 * Created by Egorman on 01.02.2018.
 */

public class RecordModel {
    double value;
    Date date;
    String category;
    String desc;
    boolean income;
    int id;

    public RecordModel(double value, Date date, String category, boolean income, String desc) {
        this.value = value;
        this.date = date;
        this.category = category;
        this.income = income;
        this.desc=desc;
    }

    public RecordModel(double value, Date date, String category, boolean income, String desc, int id) {
        this.value = value;
        this.date = date;
        this.category = category;
        this.income = income;
        this.id = id;
        this.desc=desc;
    }

    public int getId() {
        return id;
    }

    public double getValue() {
        return value;
    }

    public Date getDate() {
        return date;
    }

    public boolean isIncome() {
        return income;
    }

    public String getCategory() {
        return category;
    }

    public String getDesc() {
        return desc;
    }
}

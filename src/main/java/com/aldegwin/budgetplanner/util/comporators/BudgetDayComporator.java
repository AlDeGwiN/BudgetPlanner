package com.aldegwin.budgetplanner.util.comporators;

import com.aldegwin.budgetplanner.model.BudgetDay;

import java.time.LocalDate;
import java.util.Comparator;

public class BudgetDayComporator implements Comparator<BudgetDay> {
    @Override
    public int compare(BudgetDay bd1, BudgetDay bd2) {
        LocalDate bd1Date = bd1.getDayDate();
        LocalDate bd2Date = bd2.getDayDate();
        if (bd1Date.isAfter(bd2Date))
            return 1;
        else if (bd1Date.isBefore(bd2Date))
            return -1;
        return 0;
    }
}

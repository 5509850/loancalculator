package pw.lena.loancalculator;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import java.util.ArrayList;

/**
 * Created by Aliaksandr_Harbunou on 6/13/2017.
 */

public class Prefs extends PreferenceActivity
{
    private static final String OPT_LOAN = "loan" ;
    private static final Integer OPT_LOAN_DEF = 0;

    private static final String OPT_STATUS = "status" ;
    private static final String OPT_STATUS_DEF = "Loan";

    private static final String OPT_TERM = "term" ;
    private static final int OPT_TERM_DEF = 0;

    private static final String OPT_INTEREST = "interest" ;
    private static final int OPT_INTEREST_DEF = 0;


    private static final String OPT_TOTAL = "eventotal" ;
    private static final int OPT_TOTAL_DEF = 1;

    private static final String OPT_PRINCIPAL = "evenproncipal" ;
    private static final int OPT_PRINCIPAL_DEF = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
//        final ArrayList<CheckBoxPreference> alViewMode = new ArrayList<>();
//
//        Preference.OnPreferenceClickListener listener = new Preference.OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//                for (CheckBoxPreference cbp : alViewMode) {
//                    if (!cbp.getKey().equals(preference.getKey()) && cbp.isChecked()) {
//                        cbp.setChecked(false);
//                    }
//                    else if (cbp.getKey().equals(preference.getKey()) && !cbp.isChecked()) {
//                        cbp.setChecked(true);
//                    }
//                }
//                return false;
//            }
//        };
//
//        CheckBoxPreference eventotal = (CheckBoxPreference) getPreferenceManager().findPreference("eventotal");
//        eventotal.setOnPreferenceClickListener(listener);
//
//        CheckBoxPreference evenproncipal = (CheckBoxPreference) getPreferenceManager().findPreference("evenproncipal");
//        evenproncipal.setOnPreferenceClickListener(listener);
//
//        alViewMode.add(eventotal);
//        alViewMode.add(evenproncipal);
    }

    public static Integer getLoan(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(OPT_LOAN, OPT_LOAN_DEF);
    }

    public static void setLoan(Context context, Integer loan)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(OPT_LOAN, loan);
        editor.commit();
    }

    public static Integer getTerm(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(OPT_TERM, OPT_TERM_DEF);
    }

    public static void setTerm(Context context, Integer term)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putInt(OPT_TERM, term);
        editor.commit();
    }


    public static Integer getInterest(Context context)
    {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getInt(OPT_INTEREST, OPT_INTEREST_DEF);
    }

    public static void setInterest(Context context, float interest)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putFloat(OPT_INTEREST, interest);
        editor.commit();
    }


    public static MainActivity.Status getStatus(Context context)
    {
        String status = PreferenceManager.getDefaultSharedPreferences(context)
                .getString(OPT_STATUS, OPT_STATUS_DEF);
        for (MainActivity.Status b : MainActivity.Status.values()) {
            if (b.name().equals(status)) {
                return b;
            }
        }
        return null;
    }



    public static void setStatus(Context context, String status)
    {
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(OPT_STATUS, status);
        editor.commit();
    }


}
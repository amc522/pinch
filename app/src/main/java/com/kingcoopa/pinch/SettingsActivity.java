package com.kingcoopa.pinch;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.util.AttributeSet;
import android.view.Menu;
import android.view.MenuItem;

import java.text.NumberFormat;

public class SettingsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FragmentTransaction fragTransaction = getFragmentManager().beginTransaction();
        fragTransaction.replace(R.id.settings_generalPref, new GeneralPreferencesFragment(), "General");
        fragTransaction.replace(R.id.settings_transactionPref, new TransactionPreferencesFragment(), "Transactions");
        fragTransaction.commit();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_settings, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }

    static public class GeneralPreferencesFragment extends PreferenceFragment {
        SharedPreferences.OnSharedPreferenceChangeListener _prefChangedListener;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_general);

            _prefChangedListener = new SharedPreferences.OnSharedPreferenceChangeListener() {
                @Override
                public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                    buildSummary(sharedPreferences, key);
                }
            };

            getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(_prefChangedListener);

            buildSummaries();
        }

        private void buildSummaries() {
            buildSummary(getPreferenceManager().getSharedPreferences(), "pref_key_general_monthly_limit");
        }

        private void buildSummary(SharedPreferences sharedPreferences, String key) {
            if(!key.equals("pref_key_general_monthly_limit")) {
                return;
            }

            boolean contains = sharedPreferences.contains("pref_key_general_monthly_limit");
            double defaultLimit = 750.0;

            if(contains) {
                try {
                    String prefStr = sharedPreferences.getString("pref_key_general_monthly_limit", "750.0");
                    defaultLimit = Double.parseDouble(prefStr);
                } catch (NumberFormatException ex) {
                    sharedPreferences.edit().putString("pref_key_general_monthly_limit", Double.toString(defaultLimit));
                }
            } else {
                sharedPreferences.edit().putString("pref_key_general_monthly_limit", Double.toString(defaultLimit));
            }

            NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance();

            StringBuilder builder = new StringBuilder();
            builder.append("Default monthly limit is ");
            builder.append(currencyFormatter.format(defaultLimit));
            getPreferenceManager().findPreference("pref_key_general_monthly_limit").setSummary(builder.toString());
        }
    }

    static public class TransactionPreferencesFragment extends PreferenceFragment {
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.pref_transactions);
        }
    }
}

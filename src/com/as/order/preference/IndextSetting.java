package com.as.order.preference;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.as.order.R;

public class IndextSetting extends PreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener{

	private static final String TAG = "IndexSetting";
	private Preference initialSaWarecode;
	private EditTextPreference ftpUrl;
	private EditTextPreference reportUrl;
	private EditTextPreference saIndentUploadTime;
	
	SharedPreferences shp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.index_setting);
		
		initialSaWarecode = (Preference) findPreference("init_sawarecode");
		ftpUrl = (EditTextPreference) findPreference("ftp_url");
		reportUrl = (EditTextPreference) findPreference("report_url");
		saIndentUploadTime = (EditTextPreference) findPreference("saindent_upload_time");
		
		initialSaWarecode.setOnPreferenceClickListener(this);
		ftpUrl.setOnPreferenceChangeListener(this);
		ftpUrl.setOnPreferenceClickListener(this);
		reportUrl.setOnPreferenceChangeListener(this);
		reportUrl.setOnPreferenceClickListener(this);
		saIndentUploadTime.setOnPreferenceChangeListener(this);
		saIndentUploadTime.setOnPreferenceClickListener(this);
		
		shp = PreferenceManager.getDefaultSharedPreferences(this);
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if(preference == initialSaWarecode) {
			
		}
		return false;
	}
	@Override
	public boolean onPreferenceClick(Preference preference) {
		return false;
	}
}

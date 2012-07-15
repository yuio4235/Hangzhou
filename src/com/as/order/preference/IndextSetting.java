package com.as.order.preference;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.as.db.provider.AsProvider;
import com.as.order.R;
import com.as.ui.utils.AlertUtils;

public class IndextSetting extends PreferenceActivity implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener{

	private static final String TAG = "IndexSetting";
	private Preference initialSaWarecode;
	private EditTextPreference ftpUrl;
	private EditTextPreference reportUrl;
	private EditTextPreference saIndentUploadTime;
	private EditTextPreference ftpUserName;
	private EditTextPreference ftpPassWord;
	private EditTextPreference regUrl;
	private Preference initSystem;
	
	SharedPreferences shp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.index_setting);
		
		initialSaWarecode = (Preference) findPreference("init_sawarecode");
		ftpUrl = (EditTextPreference) findPreference("ftp_url");
		reportUrl = (EditTextPreference) findPreference("report_url");
		saIndentUploadTime = (EditTextPreference) findPreference("saindent_upload_time");
		
		ftpUserName = (EditTextPreference) findPreference("ftp_username");
		ftpPassWord = (EditTextPreference) findPreference("ftp_password");
		
		regUrl = (EditTextPreference) findPreference("reg_url");
		
		initSystem = (Preference) findPreference("init_sawarecode");
		
		initialSaWarecode.setOnPreferenceClickListener(this);
		ftpUrl.setOnPreferenceChangeListener(this);
		ftpUrl.setOnPreferenceClickListener(this);
		reportUrl.setOnPreferenceChangeListener(this);
		reportUrl.setOnPreferenceClickListener(this);
		saIndentUploadTime.setOnPreferenceChangeListener(this);
		saIndentUploadTime.setOnPreferenceClickListener(this);
		
		ftpUserName.setOnPreferenceChangeListener(this);
		ftpPassWord.setOnPreferenceChangeListener(this);
		
		regUrl.setOnPreferenceChangeListener(this);
		
		shp = PreferenceManager.getDefaultSharedPreferences(this);
		
		initSystem.setOnPreferenceClickListener(this);
	}
	@Override
	public boolean onPreferenceChange(Preference preference, Object newValue) {
		Editor et = shp.edit();
		if(preference == initialSaWarecode) {
			
		}
		
		if(preference == reportUrl) {
			et.putString(reportUrl.getKey(), (String) newValue);
			et.commit();
			return true;
		}
		
		if(preference == ftpUrl) {
			et.putString(ftpUrl.getKey(), (String) newValue);
			et.commit();
			return true;
		}
		
		if(preference == ftpUserName) {
			et.putString(ftpUserName.getKey(), (String)newValue);
			et.commit();
			return true;
		}
		
		if(preference == ftpPassWord) {
			et.putString(ftpPassWord.getKey(), (String)newValue);
			et.commit();
			return true;
		}
		
		if(preference == regUrl) {
			et.putString("reg_url", (String)newValue);
			et.commit();
			return true;
		}
		
		
		
		if(preference == saIndentUploadTime) {
			et.putString(saIndentUploadTime.getKey(), (String) newValue);
			et.commit();
			return true;
		}
		return false;
	}
	@Override
	public boolean onPreferenceClick(Preference preference) {
		if(preference == initSystem) {
			AlertDialog.Builder initBuilder = new AlertDialog.Builder(IndextSetting.this);
			initBuilder.setIcon(R.drawable.logo);
			initBuilder.setTitle("提示");
			initBuilder.setMessage("系统初始化将丢失所有数据,确定初始化吗?");
			initBuilder.setPositiveButton("确定", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SQLiteDatabase db = AsProvider.getWriteableDatabase(IndextSetting.this);
					if( db != null) {
						try {
							AsProvider.systemInitial(db, IndextSetting.this);
							AlertUtils.toastMsg(IndextSetting.this, "系统已经初始化");
						} finally {
							db.close();
						}
					}
					dialog.dismiss();
				}
			});
			
			initBuilder.setNegativeButton("取消", new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		return false;
	}
}

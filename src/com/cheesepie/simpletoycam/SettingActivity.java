package com.cheesepie.simpletoycam;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import com.cheesepie.simpletoycam.R;

public class SettingActivity extends PreferenceActivity {

	/* (non-Javadoc)
	 * @see android.preference.PreferenceActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
	}

}

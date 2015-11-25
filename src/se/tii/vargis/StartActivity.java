package se.tii.vargis;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class StartActivity extends Activity {
	
	private SharedPreferences settings;

	public void onCreate(Bundle savedState) {
		super.onCreate(savedState);
		setContentView(R.layout.activity_start);
		settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		
		LinearLayout startLayout = (LinearLayout) findViewById(R.id.startLayout);
		
		Resources res = getResources();
		String[] languageOptions = res.getStringArray(R.array.preference_language_options);
		
		String prefLang = settings.getString(getString(R.string.preference_language), "");
		
		for (String language : languageOptions) {
			Button b = new Button(this);
			b.setText(language);
			if(language.equals(prefLang))
			{
				b.setBackgroundColor(Color.BLUE);
				b.setTextColor(Color.WHITE);
			}
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			b.setLayoutParams(params);
			startLayout.addView(b);
			b.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					storePreferenceString(getString(R.string.preference_language), ""+((Button)v).getText());
					Intent i = new Intent(StartActivity.this, MainActivity.class);
					startActivity(i);
				}
			});
		}
	}
	
	private void storePreferenceInt(String tag, int value) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt(tag, value);
		editor.commit();
	}
	private void storePreferenceString(String tag, String value) {
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(tag, value);
		editor.commit();
	}
}

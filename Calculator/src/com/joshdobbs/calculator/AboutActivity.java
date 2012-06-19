package com.joshdobbs.calculator;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class AboutActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_layout);
		((TextView) findViewById(R.id.aboutText)).setMovementMethod(LinkMovementMethod.getInstance());
		((TextView) findViewById(R.id.aboutText)).setText(Html.fromHtml(getResources().getString(R.string.about_text)));
	}
	
	public void okHandler(View v){
		finish();
	}
}

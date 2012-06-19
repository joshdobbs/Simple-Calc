package com.joshdobbs.calculator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import de.congrace.exp4j.Calculable;
import de.congrace.exp4j.ExpressionBuilder;
import de.congrace.exp4j.UnknownFunctionException;
import de.congrace.exp4j.UnparsableExpressionException;

public class CalculatorActivity extends Activity {
	private TextView mNumberDisplay;

	// private static Resources sResources;
	// key used to store and retrieve last value dispayed in shared preferences
	final private String LAST_VALUE_DISPLAYED = "last value displayed";

	private Resources mResources;
	
	private String MULTIPLY;
	private String DEVIDE;
	private String SUBTRACT;
	private String ADD;
	private String EQUALS;
	private String CLEAR;
	private String CLEAR_ENTRY;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		initVars();

	}

	private void initVars(){
		mNumberDisplay = (TextView) findViewById(R.id.number_display);
		Typeface tf = Typeface.createFromAsset(getAssets(),
				"fonts/pocket_calculator_ot.otf");
		mNumberDisplay.setTypeface(tf);

		mResources = getResources();
		MULTIPLY = mResources.getString(R.string.multiply);
		DEVIDE = mResources.getString(R.string.devide);
		ADD = mResources.getString(R.string.plus);
		SUBTRACT = mResources.getString(R.string.minus);
		EQUALS = mResources.getString(R.string.equal);
		CLEAR = mResources.getString(R.string.clear);
		CLEAR_ENTRY = mResources.getString(R.string.ce);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		// ------get sharedPreferences

		final SharedPreferences pref = this.getSharedPreferences("preferences",
				Context.MODE_PRIVATE);
		mNumberDisplay.setText(pref.getString(LAST_VALUE_DISPLAYED, ""));
	}

	@Override
	protected void onPause() {
		super.onPause();
		
		// Instantiate a shared preferences object
		final SharedPreferences pref = this.getSharedPreferences("preferences", Context.MODE_PRIVATE);
		
		//save the current display value in shared preferences
		pref.edit().putString(LAST_VALUE_DISPLAYED, mNumberDisplay.getText().toString()).commit();
	}

	/**
	 * Hanle all button clicks
	 * 
	 * @param v
	 */
	public void clickHandler(View v) {
		
		// get the value of the button that was pressed
		String value = ((Button) v).getText().toString();

		// get the value of the number display
		String displayValue = mNumberDisplay.getText().toString();

		// if the number display is empty don't allow operators to be entered
		if (displayValue.length() == 0) {
			if (MULTIPLY.equals(value) || DEVIDE.equals(value) || ADD.equals(value) || SUBTRACT.equals(value)) {
				// exit function without adding anything to the number display
				return;
			}
		}

		// if the previous value entered is an operator we don't want to add a
		// second operator
		if (MULTIPLY.equals(value) || DEVIDE.equals(value) || ADD.equals(value) || SUBTRACT.equals(value)) {
			// load prevValue with the last number in the number display
			String prevValue = displayValue.substring(displayValue.length() - 1);
			
			// don't allow more than one math operator 
			if (MULTIPLY.equals(prevValue) || DEVIDE.equals(prevValue) || ADD.equals(prevValue) || SUBTRACT.equals(prevValue)) {
				return;
			}

		}

		// Add the number or operator to the number display
		if (!EQUALS.equals(value) && !value.equals(mResources.getString(R.string.ce))) {
			mNumberDisplay.setText(displayValue + value);
		}

		if (EQUALS.equals(value) && mNumberDisplay.getText().length() > 0) {
			// add them up
			mNumberDisplay.setText(calculate(displayValue));
		} else if (CLEAR.equals(value)) {
			// clear the number display
			mNumberDisplay.setText("");
		} else if (CLEAR_ENTRY.equals(value) && displayValue.length() >= 1) {
			// remove the last entry from the number display
			mNumberDisplay.setText(displayValue.subSequence(0, displayValue.length() - 1));
		}
		
	}
	
	/**
	 * function that returns the proper math operator
	 * @param value
	 * @return
	 */
	private String convertOperators(String value){
		
		value = value.replace(MULTIPLY, "*");
		
		value = value.replace(DEVIDE, "/");
		return value;
	}

	private String calculate(String value) {
		Calculable calc;
		
		//replace the division symbol and the multiply symbol with  / and *
		
		value = convertOperators(value);
		
		try {
			Log.d("Value ", value);
			calc = new ExpressionBuilder(value).build();

			return calc.calculate() + "";
		
		} catch (UnknownFunctionException e) {
			e.printStackTrace();
			return "UNKNOWN FUNCTION";
		} catch (UnparsableExpressionException e) {
			e.printStackTrace();
			return "UNPARSEABLE";

		} 
	}

	/**
	 * Creates and starts an intent to Display the about screen
	 * @param v
	 */
	public void aboutHandler(View v) {
		// Declare an intent
		final Intent intent = new Intent();
		
		// Specify the avtivity to be started
		intent.setClass(getApplicationContext(), AboutActivity.class);
		
		// Start the activity
		startActivity(intent);
	}

	/**
	 * Creates and start an intent to launch the Apps page on Google Play 
	 * @param v
	 */
	public void rateHandler(View v) {
		
		// Create an intent to launch the App Page
		
		// View actions are most commonly used to send an email, navigate to a webpage, launch contacts, etc
		final Intent MyIntent = new Intent(Intent.ACTION_VIEW,
				Uri.parse("market://details?id=com.joshdobbs.calculator"));
		
		// Start the activity
		startActivity(MyIntent);
	}

	/**
	 * creates and starts an intent to share the app via twitter, email, facebook, google+, etc
	 * @param v
	 */
	public void shareHandler(View v) {
		// create an intent to share a link to this app
		Intent sendIntent = new Intent(Intent.ACTION_SEND);
		
		// set the type to text/plain to give the user the most choices of how they want to share
		// setting the type to text/html will limit the number of apps usable to share.
		sendIntent.setType("text/plain");

		// add the subject of the message to the intent
		sendIntent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
		
		// add the body of the message to the intent
		sendIntent.putExtra(Intent.EXTRA_TEXT, getResources().getString(R.string.share_text));
		
		// start the intent that will launch the app chooser
		this.startActivity(Intent.createChooser(sendIntent, "Share"));
	}

}
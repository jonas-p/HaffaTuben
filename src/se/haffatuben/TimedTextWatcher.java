package se.haffatuben;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;

/**
 * TimedTextWatcher provides a way to watch a text field
 * for delayed changes. exceededTimeSinceLastChange fires
 * when the text has not been touched for delayMillis milliseconds.
 * Default delay time is 1000ms if no delay time is provided
 * when initializing.
 * 
 * @author jonas
 *
 */
public abstract class TimedTextWatcher implements TextWatcher {
	public abstract void exceededTimeSinceLastChange(CharSequence s);
	
	protected Handler handler;
	protected Runnable runnable;
	protected long delayMillis;
	
	private CharSequence text;
	
	/**
	 * TimedTextWatcher constructor. Initializes the TimedTextWatcher
	 * with 1000ms delay time.
	 */
	public TimedTextWatcher() {
		this(1000);
	}
	
	/**
	 * TimedTextWatcher constructor
	 * 
	 * @param delayMillis delay time
	 */
	public TimedTextWatcher(long delayMillis) {
		handler = new Handler();
		runnable = new Runnable() {
			@Override
			public void run() {
				exceededTimeSinceLastChange(text);
			}
		};
		this.delayMillis = delayMillis;
	}
	
	/**
	 * Abstract method from TextWatcher. Removes any old timers
	 * and posts a new runnable to the handler with delayMillis delay time.
	 * Fired whenever text is changed from TextWatcher.
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		text = s;
		handler.removeCallbacks(runnable);
		handler.postDelayed(runnable, delayMillis);
	}
	
	/**
	 * Abstract method from TextWatcher. Currently it does nothing.
	 */
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// Do nothing
	}
	
	/**
	 * Abstract method from TextWatcher. Currently it does nothing.
	 */
	@Override
	public void afterTextChanged(Editable s) {
		// Do nothing
	}
}
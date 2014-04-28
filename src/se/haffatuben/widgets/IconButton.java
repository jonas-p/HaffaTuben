package se.haffatuben.widgets;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

/**
 * IconButton represents a button with icons contained in "icons.ttf"
 * asset. The typeface is always set to be using icons.ttf in the
 * setTypeface method.
 * 
 * @author jonas
 *
 */
public class IconButton extends Button {
	/**
	 * Public constructor
	 */
	public IconButton(Context context) {
		super(context);
	}
	
	/**
	 * Public constructor
	 */
	public IconButton(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	/**
	 * Public constructor
	 */
	public IconButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}
	
	/**
	 * Sets the typeface to "icons.ttf"
	 */
	public void setTypeface(Typeface tf) {
		Typeface font = Typeface.createFromAsset(getContext().getAssets(), "icons.ttf");
		super.setTypeface(font);
	}
}

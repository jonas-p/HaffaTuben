package se.haffatuben;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

/**
 * DialogFragment for adding routes to the application. The
 * user specifies Station A and Station B.
 * 
 * TODO: Create Route object and save to storage
 * TODO: Autocomplete against SL API
 * 
 * @author jonas
 *
 */
public class AddRouteDialogFragment extends DialogFragment {	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Inflate view
		LayoutInflater inflater = getActivity().getLayoutInflater();
		final View view = inflater.inflate(R.layout.fragment_addroutedialog, null);
		
		// Create dialog
		Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle(R.string.button_add_route);
		dialog.setView(view);
		dialog.setCancelable(true);
		
		// Cancel/OK buttons
		dialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO: Create route object and save to storage
						Log.d(getString(R.string.app_name), AddRouteDialogFragment.class.getName() + ": Positive button not yet implemented!");
					}
				});
		dialog.setNegativeButton(android.R.string.cancel,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});
		
		return dialog.create();
	}
}

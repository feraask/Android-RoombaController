package com.cs424.roombacontroller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RoombaControl extends Activity {

	private TCPHandler tcpHandler = null;
	private String ipAddr;
	private String connectionError;
	private TextView connectedField;
	private int i = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_roomba_control);
		// Show the Up button in the action bar.
		setupActionBar();
		
		//Disable buttons on the display
		disableButtons();
		
		//Get the ip address passed from the main activity
		Intent intent = getIntent();
		ipAddr = intent.getStringExtra(MainActivity.IP_ADDRESS);
		connectionError = "Connection to " + ipAddr + " Failed!\nPlease Enter a Valid IP Address\n";
		//Setup the Connection status and ACK text view
		connectedField = (TextView) findViewById(R.id.ConnectedIP);

		
		//Initialize the TCPHandler and create the client thread
		tcpHandler = new TCPHandler(ipAddr);
		new Thread(tcpHandler).start();
		
		//Wait until the connection is complete and enable command buttons upon success
		//	otherwise show an error message if the connection was unsuccessful
		while(tcpHandler.connectionComplete() == false)
		{}
		
		//Set the connection status text to green for success and show the content
		//	or show an error and end the activity
		boolean connected = tcpHandler.connectionSuccessful();
		connectedField.setTextColor(0xFF000000);
		if (connected == true)
		{
			connectedField.setText("Connected To: " + ipAddr);
			connectedField.setBackgroundColor(0xFF00FF00);
			enableButtons();
		}
		else
		{
			showError(connectionError);
		}	
	}
	
	public void sendCommand(View view)
	{
		String ack;
		//Based off which button is pressed, send the command
		switch(view.getId())
		{
			case R.id.UpButton:
				ack = tcpHandler.sendData("Forward");
				break;
			case R.id.LeftButton:
				ack = tcpHandler.sendData("Left");
				break;
			case R.id.RightButton:
				ack = tcpHandler.sendData("Right");
				break;
			case R.id.DownButton:
				ack = tcpHandler.sendData("Back");
				break;
			case R.id.StopButton:
				ack = tcpHandler.sendData("Stop");
				break;
			default:
				ack = "UNKNOWN";
				break;
		}
		if(tcpHandler.connectionSuccessful() == false)
			showError(connectionError + "ACK = " + ack);
		else if (view.getId() == R.id.StopButton)
			this.finish();
		else 
		{
			connectedField.setText("RECEIVED: " + ack + "(" + i + ")");
			i++;
		}
	}
	
	private void enableButtons()
	{
		Button button = (Button) findViewById(R.id.UpButton);
		button.setEnabled(true);
		
		button = (Button) findViewById(R.id.LeftButton);
		button.setEnabled(true);
		
		button = (Button) findViewById(R.id.RightButton);
		button.setEnabled(true);
		
		button = (Button) findViewById(R.id.DownButton);
		button.setEnabled(true);
		
		button = (Button) findViewById(R.id.StopButton);
		button.setEnabled(true);		
	}
	
	private void disableButtons()
	{
		Button button = (Button) findViewById(R.id.UpButton);
		button.setEnabled(false);
		
		button = (Button) findViewById(R.id.LeftButton);
		button.setEnabled(false);
		
		button = (Button) findViewById(R.id.RightButton);
		button.setEnabled(false);
		
		button = (Button) findViewById(R.id.DownButton);
		button.setEnabled(false);
		
		button = (Button) findViewById(R.id.StopButton);
		button.setEnabled(false);
	}
	
	//Create an error message popup for the user
	private void showError(String errorMsg)
	{
		AlertDialog.Builder popupBuilder = new AlertDialog.Builder(this);
		TextView connectionFailed = new TextView(this);
		connectionFailed.setText(errorMsg);
		connectionFailed.setGravity(Gravity.CENTER_HORIZONTAL);
		popupBuilder.setCancelable(true);
		popupBuilder.setTitle("ERROR");
		popupBuilder.setInverseBackgroundForced(true);
		popupBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
			  @Override
			  public void onClick(DialogInterface dialog, int which) {
			    finish();
			  }
			});
		popupBuilder.setOnCancelListener(new DialogInterface.OnCancelListener() {         
		    @Override
		    public void onCancel(DialogInterface dialog) {
		        finish();
		    }
		});
		popupBuilder.setView(connectionFailed);
		popupBuilder.create().show();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.roomba_control, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This ID represents the Home or Up button. In the case of this
			// activity, the Up button is shown. Use NavUtils to allow users
			// to navigate up one level in the application structure. For
			// more details, see the Navigation pattern on Android Design:
			//
			// http://developer.android.com/design/patterns/navigation.html#up-vs-back
			//
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}

package com.cs424.roombacontroller;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;

public class MainActivity extends Activity {

	public final static String IP_ADDRESS = "com.cs424.roombacontroller.IP_ADDRESS";
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
	/*
	 * Called when the connect button is pressed
	 * Launches the controller activity and connect to the TCP Server passed by the user
    */
    public void connectButtonPress(View view)
    {
    	Intent intent = new Intent(this, RoombaControl.class);
    	EditText ipTextField = (EditText) findViewById(R.id.IPAddrField);
    	String ipAddr = ipTextField.getText().toString();
    	intent.putExtra(IP_ADDRESS, ipAddr);
    	startActivity(intent);
    }
    
}

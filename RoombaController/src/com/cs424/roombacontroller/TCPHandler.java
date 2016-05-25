package com.cs424.roombacontroller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import android.app.Activity;
import android.os.NetworkOnMainThreadException;

public class TCPHandler extends Activity implements Runnable{
	
	public Socket socket;
	
	private static final int PORT = 50000;
	private static String serverIP;
	private boolean connectionComplete;
	private boolean connected;
	private PrintWriter output;
	private ReceiveThread ackThread;
	
	class ReceiveThread implements Runnable
	{
		private Socket receiveSocket;
		private BufferedReader input;
		public String data;

		public ReceiveThread(Socket serverSocket)
		{
			this.receiveSocket = serverSocket;
			try {
				this.input = new BufferedReader(new 
						InputStreamReader(receiveSocket.getInputStream()));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		public void run()
		{
			while(!Thread.currentThread().isInterrupted())
			{
				try {
					this.data = this.input.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		public String readData()
		{			
			String received = this.data;
			this.data = null;
			
			return received;
		}
		
	}
	
	//Constructor which initializes the server IP address and connection status variables
	public TCPHandler(String ipaddr)
	{
		serverIP = ipaddr;
		connected = false;
		connectionComplete = false;
	}
	
	/*Run when we create the client thread
	 * attempts to connect to the server based off the IP address
	 * and initialize the Socket
	 */
	public void run()
	{
		try
		{
			InetAddress serverAddr = InetAddress.getByName(serverIP);
			socket = new Socket(serverAddr, PORT);
			
			output = new PrintWriter(new BufferedWriter(
					new OutputStreamWriter(socket.getOutputStream())),
					true);
			
			ackThread = new ReceiveThread(socket);
			new Thread(ackThread).start();
			
			connected = true;
		}
		catch (UnknownHostException e1)
		{
			e1.printStackTrace();
			
		}
		catch (IOException e1) 
		{
			e1.printStackTrace();
		}
		finally
		{
			connectionComplete = true;
		}
	}
	
	//Check if the the thread is done connecting
	public boolean connectionComplete()
	{
		return connectionComplete;
	}
	
	//Check if the connection was/is successful
	public boolean connectionSuccessful()
	{
		return connected;
	}
	
	/*Send data, called by the Robot Controller movement button presses
	 * also waits for an acknowledgment/response to be received and returns it to the caller
	 */
	public String sendData(String data)
	{
		try
		{
			connected = false;
			output.println(data);
						
			connected = true;
			return ackThread.readData();
		}
//		catch (UnknownHostException e) 
//		{
//			e.printStackTrace();
//			return "UNKNOWN HOST EXCEPTION";
//		}
//		catch (IOException e) 
//		{
//			e.printStackTrace();
//			return "IOEXCEPTION";
//		}
		catch (NetworkOnMainThreadException e)
		{
			e.printStackTrace();
			return "NETWORK ON MAIN THREAD EXCEPTION";
		}
		catch (Exception e) 
		{
			e.printStackTrace();
			return "OTHER EXCEPTION";
		}
		
	}
}

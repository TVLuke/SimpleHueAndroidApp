package com.example.simplehueapp;

import java.util.Collection;
import java.util.List;

import de.jaetzold.philips.hue.ColorHelper;
import de.jaetzold.philips.hue.HueBridge;
import de.jaetzold.philips.hue.HueLightBulb;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity 
{

	static List<HueBridge> bridges;
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		WifiManager wm = (WifiManager)getSystemService(Context.WIFI_SERVICE);
	    WifiManager.MulticastLock multicastLock = wm.createMulticastLock("multicastLock"); 
	    multicastLock.setReferenceCounted(true);
	    multicastLock.acquire();
	        
		Button button1 = (Button) findViewById(R.id.button1);
		button1.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) 
			{
				discoverAndAuthenticate();				
			}
			
		});
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) 
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	 public static void discoverAndAuthenticate() 
	 {
		 	new Thread(new Runnable()
		 	{
		 		public void run()
		 		{
		 			bridges = HueBridge.discover();
				    for(HueBridge bridge : bridges) 
				    {
				    	Log.d("HUE", "Found " + bridge);
				        // You may need a better scheme to store your username that to just hardcode it.
				        // suggestion: Save a mapping from HueBridge.getUDN() to HueBridge.getUsername() somewhere.
				        bridge.setUsername("552627b33010930f275b72ab1c7be258");
				        if(!bridge.authenticate(false)) 
				        {
				        	Log.d("HUE", "Press the button on your Hue bridge in the next 30 seconds to grant access.");
				            if(bridge.authenticate(true)) 
				            {
				            	Log.d("HUE", "Access granted. username: " + bridge.getUsername());
				    			Collection<HueLightBulb> lights = (Collection<HueLightBulb>) bridge.getLights();
				    			Log.d("HUE", "Available LightBulbs: "+lights.size());
				    			for (HueLightBulb bulb : lights) {
				    				Log.d("HUE", bulb.toString());
				    				bulb.setBrightness(255);
				    				bulb.setHue(0);
				    			}
				    			System.out.println("");
				            } 
				            else 
				            {
				            	Log.d("HUE", "Authentication failed.");
				            }
				        } 
				        else 
				        {
				        	Log.d("HUE", "Already granted access. username: " + bridge.getUsername());
			    			Collection<HueLightBulb> lights = (Collection<HueLightBulb>) bridge.getLights();
			    			Log.d("HUE", "Available LightBulbs: "+lights.size());
			    			for (HueLightBulb bulb : lights) {
			    				Log.d("HUE", bulb.toString());
			    				bulb.setBrightness(ColorHelper.convertRGB2Hue("255255255").get("bri"));
			    				bulb.setHue(ColorHelper.convertRGB2Hue("255255255").get("hue"));
			    				bulb.setSaturation(ColorHelper.convertRGB2Hue("255255255").get("sat"));
			    			}
			    			System.out.println("");
				        }
				    }
		 		}
		 	}).start();	    
	}
}

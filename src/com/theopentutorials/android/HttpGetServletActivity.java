package com.theopentutorials.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.JSONValue;

import com.theopentutorials.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class HttpGetServletActivity extends Activity {
	private static String tag = "HttpGetServletActivity";
	Button buttonSend, buttonLight, buttonAcc, buttonUp, buttonGet;
	TextView outputText;
	TextView stepCounter, counterText;
	static EditText nameId;
	private boolean color = false;
	//private View view;
	private long lastUpdate;
	private static int counter = 0;
	static String theString= "";

	// @Yong - your web.xml defines the URLs that your servlets are registered at. This
	// url is not listed there! I've commented this line out and fixed it 

	public static final String URL = "http://10.0.2.2:8094/HelloWorldServlet";
	//public static final String URL = "http://192.168.1.1/first";
	//MediaPlayer beepSong = MediaPlayer.create(HttpGetServletActivity.this, R.raw.beep);
	
	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		findViewsById();
		
		
		
		lastUpdate = System.currentTimeMillis();
		// counterText.setText("why wont it work");
		SensorManager sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		Sensor AccSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		
		
		if (AccSensor == null) {
			Toast.makeText(HttpGetServletActivity.this,
					"No Light Sensor! quit-", Toast.LENGTH_LONG).show();
		} else {
			Log.e(tag, " accelSensor");
			//counterText.setText("caccelSensor");
			sensorManager.registerListener(accSensorEventListener, AccSensor,
					SensorManager.SENSOR_DELAY_NORMAL);
		}
		
		buttonAcc.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
			//	beepSong.start();
				// TODO Auto-generated method stub
				startActivity(new Intent(HttpGetServletActivity.this, AccActivity.class));


			}
		});
		// button.setOnClickListener(this);
		buttonSend.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//	beepSong.start();
				if(!validate())
				    Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
				counterText.setText(String.valueOf(counter));
				Log.d("name", nameId.getText().toString());
				GetXMLTask task = new GetXMLTask();
				task.execute(new String[] { URL });


			}
		});
		buttonLight.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
			//	beepSong.start();
				startActivity(new Intent(HttpGetServletActivity.this, LightActivity.class));


			}
		});
		buttonGet.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				outputText.setText(theString);
				
			}
		});

	}



	SensorEventListener accSensorEventListener = new SensorEventListener() {

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				//counterText.setText("accel onsensor");
				Log.e(tag, " accel onsensor");
				getAccelerometer(event);
			}
		}

		private void getAccelerometer(SensorEvent event) {
			//Log.e(tag, " accel getaccel");
			float[] values = event.values;
			// Movement
			float x = values[0];
			float y = values[1];
			float z = values[2];

			float accelationSquareRoot = (x * x + y * y + z * z)
					/ (SensorManager.GRAVITY_EARTH * SensorManager.GRAVITY_EARTH);
			long actualTime = System.currentTimeMillis();
			if (accelationSquareRoot >= 2) //
			{
				//Log.e(tag, " accel squareroot");
				if (actualTime - lastUpdate < 200) {
					return;
				}
				//Log.e(tag, " accel countertext");
				lastUpdate = actualTime;
				// Toast.makeText(this, "Device was shuffed",
				// Toast.LENGTH_SHORT)
				// .show();
				//counterText.setText("counter");
				if (color) {
					//view.setBackgroundColor(Color.GREEN);
					counter = counter + 1;
						//counterText.setText(counter);

				} else {
					//view.setBackgroundColor(Color.RED);
					//counterText.setText("no no counter");
				}
				color = !color;
			}
		}

	};

	private void findViewsById() {
		buttonSend = (Button) findViewById(R.id.button);
		buttonLight = (Button) findViewById(R.id.lightButton);
		buttonAcc = (Button) findViewById(R.id.accButton);
		buttonGet = (Button) findViewById(R.id.getButton);
		outputText = (TextView) findViewById(R.id.outputTxt);
		counterText = (TextView) findViewById(R.id.counterOutput);
		nameId = (EditText) findViewById(R.id.name);

	}

	// public void onClick(View view) {
	// GetXMLTask task = new GetXMLTask();
	// task.execute(new String[] { URL });
	// }

	public class GetXMLTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... urls) {
			return POST(urls[0]);
		}

		@Override
		protected void onPostExecute(String output) {
			  Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
		}
	}
	
	 public static String POST(String url){
	        InputStream inputStream = null;
	        String result = "";
	        try {
	 
	            // 1. create HttpClient
	            HttpClient httpclient = new DefaultHttpClient();
	 
	            // make POST request to the given URL
	            HttpPost httpPost = new HttpPost(url);
	 
	            String json = "";
	 
	            // build jsonObject

				
	            JSONObject jsonObject = new JSONObject();
	            Log.d("name ",nameId.getText().toString());
	            Log.d("Counter ",String.valueOf(counter));
	            
	            String forName =nameId.getText().toString();
	            String forCounter =String.valueOf(counter);
	            
	            jsonObject.put("name", forName);
	            jsonObject.put("counter",  forCounter);
	          
	 
	            // convert JSONObject to JSON to String
	            json = jsonObject.toString();
	            Log.d(tag, "Sending json to server:" + json);
	 
	 
	            //  set json to StringEntity
	            StringEntity se = new StringEntity(json);
	 
	            // set httpPost Entity
	            httpPost.setEntity(se);
	 
	            // Set some headers to inform server about the type of the content   
	            httpPost.setHeader("Accept", "application/json");
	            httpPost.setHeader("Content-type", "application/json");
	 
	            // Execute POST request to the given URL
	            HttpResponse httpResponse = httpclient.execute(httpPost);
	 
	            // receive response as inputStream
	            inputStream = httpResponse.getEntity().getContent();
	 
	            //  convert inputstream to string
	            if(inputStream != null)
	                result = convertInputStreamToString(inputStream);
	            else
	                result = "Did not work!";
	            	
	        } catch (Exception e) {
	            Log.d(tag, e.getLocalizedMessage());
	        }
	        //outputText.setText(result);
	        // 11. return result
	        theString =result;
	        return result;
	    }
	 
	 private boolean validate(){
	        if(nameId.getText().toString().trim().equals(""))
	            return false;
	        else if(String.valueOf(counter).trim().equals("0"))
	            return false;
	        else
	            return true;   
	 }
	 private static String convertInputStreamToString(InputStream inputStream) throws IOException{
	        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
	        String line = "";
	        String result = "";
	        while((line = bufferedReader.readLine()) != null)
	            result += line;
	 
	        inputStream.close();
	        return result;
	 
	    }   


}
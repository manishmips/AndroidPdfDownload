package com.example.manish_assignment1;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.support.v7.app.ActionBarActivity;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

public class PdfDownloadMainActivity extends Activity {

	// Progress Dialog
	private AlertDialog alertDialog;
	public static final int progress_bar_type = 0;
	private Button downloadButton;
	LinearLayout urlView,progressLayout;
	ProgressBar progressBar;
	private EditText fileUrlEditText;

	// File url to download
	private static String fileUrl="http://brahminno.com/doc/Brahmastra_Innovations.pdf";
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    Intent intent = new Intent(this, Splash.class);
		setContentView(R.layout.activity_pdf_download_main);
		
		downloadButton=(Button) findViewById(R.id.pdf_download_button);
		fileUrlEditText=(EditText) findViewById(R.id.file_uri);
		urlView=(LinearLayout) findViewById(R.id.url_view);
		progressLayout=(LinearLayout) findViewById(R.id.downloading_view);
		progressBar=(ProgressBar) findViewById(R.id.progressbar);
		downloadButton.setOnClickListener( new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				/*
				 * Going to download file from URL provided
				 */
				clickButtonToDownloadFile();
				
			}
		});
	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy); 
	   

	}

	private void clickButtonToDownloadFile() {
		fileUrl=fileUrlEditText.getText().toString();
		if(isNetworkAvailable()){
			progressLayout.setVisibility(View.VISIBLE);
			urlView.setVisibility(View.GONE);
			new DownloadFileFromURL().execute(fileUrl);
		}
		else{
			Toast.makeText(getApplicationContext(), "Check Network Connection", Toast.LENGTH_SHORT).show();
    	}
			
	}

	/**
	 * Showing Dialog
	 * */


	/**
	 * Background Async Task to download file
	 * */
	class DownloadFileFromURL extends AsyncTask<String, String, String> {

	    /**
	     * Before starting background thread Show Progress Bar Dialog
	     * */
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
	        progressBar.setProgress(0);
	    }
		/**
	     * Downloading file in background thread
	     * */
	    @Override
	    protected String doInBackground(String... f_url) {
	        int count;
	        Exception exp;
	        try {
	            URL url = new URL(f_url[0]);
	            URLConnection conection = url.openConnection();
	          //  conection.connect();

	            // this will be useful so that you can show a tipical 0-100%
	            // progress bar
	            int lenghtOfFile = conection.getContentLength();

	            // download the file
	            InputStream input = new BufferedInputStream(url.openStream(),
	                    8192);

	            // Output stream
	            OutputStream output = new FileOutputStream(Environment
	                    .getExternalStorageDirectory().toString()+"/myFile.pdf"
	                    );

	            byte data[] = new byte[1024];

	            long total = 0;

	            while ((count = input.read(data)) != -1) {
	                total += count;
	                // publishing the progress....
	                // After this onProgressUpdate will be called
	                publishProgress("" + (int) ((total * 100) / lenghtOfFile));

	                // writing data to file
	                output.write(data, 0, count);
	            }

	            // flushing output
	            output.flush();

	            // closing streams
	            output.close();
	            input.close();

	        } catch (Exception e) {
	            return e.getMessage();
	        }

	        return f_url[0];
	    }

	    /**
	     * Updating progress bar
	     * */
	    protected void onProgressUpdate(String... progress) {
	        // setting progress percentage
	    	progressBar.setProgress(Integer.parseInt(progress[0]));
	    }

	    /**
	     * After completing background task Dismiss the progress dialog
	     * **/
	    @Override
	    protected void onPostExecute(String error_message) {
	        // dismiss the dialog after the file was downloaded
	    	if(progressBar.getProgress()<100){
	    		if(error_message!=null)
					Toast.makeText(getApplicationContext(), "Please enter valid Url", Toast.LENGTH_SHORT).show();
		    	else
				Toast.makeText(getApplicationContext(), "Download Failled", Toast.LENGTH_SHORT).show();
	    	}
	    	else {
			showPdf();
	    	}
			urlView.setVisibility(View.VISIBLE);
			progressLayout.setVisibility(View.GONE);

	    }
		private void showPdf() {
			AlertDialog.Builder builder=new Builder(PdfDownloadMainActivity.this);
			// Create the AlertDialog
			builder.setTitle("Pdf Downloader");
			builder.setMessage("Download Completed");
			builder.setPositiveButton("View", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					alertDialog.dismiss();
	                File file = new File(Environment
		                    .getExternalStorageDirectory().toString()+"/myFile.pdf");

	                if (file.exists()) {
	                    Uri path = Uri.fromFile(file);
	                    Intent intent = new Intent(Intent.ACTION_VIEW);
	                    intent.setDataAndType(path, "application/pdf");
	                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

	                    try {
	                        startActivity(intent);
	                    } 
	                    catch (ActivityNotFoundException e) {
	                    	final String appPackageName = "com.adobe.reader"; // getPackageName() from Context or Activity object
	                    	try {
	                    	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
	                    	} catch (android.content.ActivityNotFoundException anfe) {
	                    	    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id=" + appPackageName)));
	                    	}
	                    }
	                }
				}
			});
			alertDialog = builder.create();
			alertDialog.show();
			
		}

	}
	protected boolean isNetworkAvailable(){
		ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
		
		 if ( conMgr.getNetworkInfo(0).getState() == NetworkInfo.State.DISCONNECTED 
		    || conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.DISCONNECTED 
		    || conMgr.getNetworkInfo(1).getState() == NetworkInfo.State.SUSPENDED) {
			return false;
		    // notify user you are not online
		}
		return true;
	}
}
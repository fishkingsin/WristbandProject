package com.idthk.wristband.ui;

import java.io.IOException;
import java.io.InputStream;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;

public class MyWebView extends Activity {
//	try {
//		InputStream is=getActivity().getAssets().open("test.xml");
//		
//	} catch (IOException e) {
//		// TODO Auto-generated catch block
//		e.printStackTrace();
//	}
	
	private WebView webView;
	 
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.webview);
 
		webView = (WebView) findViewById(R.id.webView1);
//		webView.getSettings().setJavaScriptEnabled(true);
//		webView.loadUrl("file:///android_asset/manual.pdf");
//		webView.loadUrl("http://www.cran.r-project.org/doc/manuals/R-intro.pdf");
		
//		WebView webview = (WebView) findViewById(R.id.webview);
		Uri path = Uri.parse("android.resource://"+ getPackageName() +"/raw/manual.pdf");
//		Uri path = Uri.parse("android.resource://" + getPackageName() + "/"+ R.raw.manual);
		
		webView.getSettings().setJavaScriptEnabled(true); 
		String pdf = path.getPath();
		webView.loadUrl("http://docs.google.com/gview?embedded=true&url=" + pdf);
 
	}
}

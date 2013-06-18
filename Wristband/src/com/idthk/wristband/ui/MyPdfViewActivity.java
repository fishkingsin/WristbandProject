package com.idthk.wristband.ui;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class MyPdfViewActivity extends Activity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.pdfview_layout);
    CopyReadAssets();
//    File file = new File("android_assest/manual.pdf");
//    		Log.d("myTag", "" + file.isFile());
//    		
//    WebView mWebView=new WebView(MyPdfViewActivity.this);
//    mWebView.getSettings().setJavaScriptEnabled(true);
//    mWebView.getSettings().setPluginsEnabled(true);
//    mWebView.loadUrl("file://" + file.getAbsolutePath());
//    setContentView(mWebView);
	
  }
  private void CopyReadAssets()
  {
      AssetManager assetManager = getAssets();

      InputStream in = null;
      OutputStream out = null;
      File file = new File(getFilesDir(), "manual.pdf");
      try
      {
          in = assetManager.open("manual.pdf");
          out = openFileOutput(file.getName(), Context.MODE_WORLD_READABLE);

          copyFile(in, out);
          in.close();
          in = null;
          out.flush();
          out.close();
          out = null;
      } catch (Exception e)
      {
          Log.e("tag", e.getMessage());
      }

      Intent intent = new Intent(Intent.ACTION_VIEW);
      Uri uri = Uri.parse("file://" + getFilesDir() + "/manual.pdf");
      Log.v("TAG",uri.getPath());
      intent.setDataAndType(
    		  uri,
              "application/pdf");
      

      startActivity(intent);
  }

  private void copyFile(InputStream in, OutputStream out) throws IOException
  {
      byte[] buffer = new byte[1024];
      int read;
      while ((read = in.read(buffer)) != -1)
      {
          out.write(buffer, 0, read);
      }
  }
}
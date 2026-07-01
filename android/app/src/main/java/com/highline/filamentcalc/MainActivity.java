package com.highline.filamentcalc;

import android.os.Bundle;
import android.webkit.DownloadListener;
import android.webkit.JavascriptInterface;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;
import androidx.core.content.FileProvider;
import com.getcapacitor.BridgeActivity;
import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends BridgeActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Register a native bridge for sharing files
        if (getBridge() != null && getBridge().getWebView() != null) {
            getBridge().getWebView().addJavascriptInterface(new WebAppInterface(), "AndroidShare");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getBridge() != null && getBridge().getWebView() != null) {
            getBridge().getWebView().setDownloadListener(new DownloadListener() {
                @Override
                public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                    try {
                        Intent i = new Intent(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

    // Native Interface for JavaScript to call
    public class WebAppInterface {
        @JavascriptInterface
        public void shareFile(String content, String fileName) {
            try {
                // 1. Create the file in the app's cache directory
                File cachePath = new File(getCacheDir(), "exports");
                cachePath.mkdirs();
                File newFile = new File(cachePath, fileName);
                
                FileOutputStream stream = new FileOutputStream(newFile);
                stream.write(content.getBytes());
                stream.close();

                // 2. Get a URI for the file using FileProvider
                Uri contentUri = FileProvider.getUriForFile(MainActivity.this, getPackageName() + ".fileprovider", newFile);

                if (contentUri != null) {
                    // 3. Create the share intent
                    Intent shareIntent = new Intent();
                    shareIntent.setAction(Intent.ACTION_SEND);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    shareIntent.setDataAndType(contentUri, getContentResolver().getType(contentUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
                    shareIntent.setType("text/csv");
                    
                    // 4. Start the share activity
                    startActivity(Intent.createChooser(shareIntent, "Save or Share Export"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(MainActivity.this, "Export Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}

package uk.co.tekkies.readings.downloadmp3;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;

import uk.co.tekkies.readings.R;
import uk.co.tekkies.readings.util.Analytics;

public class DownloadMp3Activity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_mp3);
        WebView webView = (WebView) findViewById(R.id.webView);
        webView.loadUrl("http://server.firefighters.org/kjv.asp");
        findViewById(R.id.buttonDownload).setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_download_mp3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.buttonDownload:
                doDownloadZip();
                break;
        }
    }

    private void doDownloadZip() {
        Analytics.UIClick(this, "download-mp3");
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://server.firefighters.org/kjv/kjv.zip")));
    }
}

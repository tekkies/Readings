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

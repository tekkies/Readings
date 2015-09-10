package uk.co.tekkies.readings.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.widget.Toast;

import java.util.List;

import uk.co.tekkies.readings.R;

public class AppInstaller {
    public static final String MARKET_URL_BASE = "market://details?id=";
    private final Context context;
    private final String packageId;
    private final String packageName;

    public AppInstaller(Context context, String packageId, String packageName) {
        this.context = context;
        this.packageId = packageId;
        this.packageName = packageName;
    }

    public boolean isAppInstalled() {
        boolean installed = false;
        PackageManager pm = context.getPackageManager();
        try {
            pm.getPackageInfo(packageId, PackageManager.GET_ACTIVITIES);
            installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            //NOP
        }
        return installed;
    }

    public void askUserToInstallApp() {
        Analytics.UIClick(context, "request-install-" + packageId);
        new AlertDialog.Builder(context)
            .setTitle(String.format(context.getString(R.string.install_package), packageName))
            .setMessage(String.format(context.getString(R.string.please_install_package), packageName))
            .setCancelable(true)
            .setPositiveButton(R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            installApp();
                        }
                    })
            .create()
            .show();
    }

    private boolean installApp() {
        Analytics.UIClick(context, "install-"+packageId);
        boolean installed = false;
        Uri marketUri = Uri.parse(MARKET_URL_BASE +packageId);
        Intent marketIntent = new Intent(Intent.ACTION_VIEW).setData(marketUri);
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(marketIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0)
            context.startActivity(marketIntent);
        else {
            Toast.makeText(context, R.string.sorry_no_market_installed, Toast.LENGTH_LONG).show();
        }
        return installed;
    }


}

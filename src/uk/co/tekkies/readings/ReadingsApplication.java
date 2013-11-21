/*
Copyright 2013 Andrew Joiner

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
 */

package uk.co.tekkies.readings;

import java.util.List;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

public class ReadingsApplication extends Application {

    private static ReadingsApplication application;
    public static Boolean mp3Installed = false;

    public ReadingsApplication() {
        super();
        application = this;
    }

    public void onCreate() {
    }

    public static void checkForMP3(Context context) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setType("vnd.android.cursor.item/vnd.uk.co.tekkies.mp3bible.passage");
        // Look for plugin in packages
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> list = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.size() > 0) {
            mp3Installed = true;
        } else {
            mp3Installed = false;
        }
    };

    public ReadingsApplication getApplication() {
        return application;
    }
}

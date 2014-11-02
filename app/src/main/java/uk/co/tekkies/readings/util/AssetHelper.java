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

package uk.co.tekkies.readings.util;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

/** Assets are split into licensed and private.  If a private assets exists, it is used.  
 * If no private version exists, then a licensed sample assets are used instead.  
 */
public class AssetHelper {

    /**
     * If private asset exists, load that, otherwise fall back to open source
     * assets.
     * 
     * @param context
     * @param upgradePath
     * @return
     * @throws IOException
     */
    public static InputStream openAsset(Context context, String relativePath) throws IOException {
        InputStream inputStream;
        try {
            inputStream = context.getAssets().open("private/" + relativePath);
        } catch (IOException e) {
            inputStream = context.getAssets().open("licensed/" + relativePath);
        }
        return inputStream;
    }

}

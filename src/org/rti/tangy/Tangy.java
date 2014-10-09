/**
 *     Copyright 2011 Couchbase, Inc.
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.rti.tangy;

import java.io.IOException;

import android.content.ServiceConnection;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;

import com.couchbase.android.CouchbaseMobile;
import com.couchbase.android.ICouchbaseDelegate;
import com.phonegap.DroidGap;

public class Tangy extends DroidGap
{
    public static final String TAG = "Tangy";

    private CouchbaseMobile couchbaseMobile;
    private ServiceConnection couchbaseService;
    private String couchappDatabase;

    protected String calcTangerineURL(String host, int port) {
        return "http://" + host + ":" + port + "/tangerine/_design/ojai/index.html";
    }

    protected void couchbaseStarted(String host, int port) {

    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        // show the splash screen
        // NOTE: Callback won't show the splash until we try to load a URL
        //       so we start a load, with a wait time we should never exceed
        // super.setIntegerProperty("splashscreen", R.drawable.splash);
        // loadUrl( "file:///android_asset/www/error.html", 120000 );

        // increase the default timeout
        super.setIntegerProperty( "loadUrlTimeoutValue", 60000 );

        couchbaseMobile = new CouchbaseMobile( getBaseContext(), couchCallbackHandler );

        try {
            couchbaseMobile.installDatabase("tangerine.couch");
            couchbaseMobile.installDatabase("mmlp.couch");
        } catch (IOException e) {
            Log.e(TAG, "Error installing database", e);
        }

        // start couchbase
        couchbaseService = couchbaseMobile.startCouchbase();

        listFiles("/assets");

    }

    /**
     * Returns a list of .json files to load.
     */
    private void listFiles(String dirFrom) {
        Resources res = getResources(); //if you are in an activity
        AssetManager am = res.getAssets();
        String fileList[] = {};
        try {
            fileList = am.list(dirFrom);
        } catch (Exception e) {
            Log.e(TAG, "Couldn't fetch file list"); 
        }
        if (fileList != null)
        {   
            for ( int i = 0;i<fileList.length;i++)
            {
                Log.d(TAG, fileList[i]); 
            }
        }
    }

    /**
     * Clean up the Couchbase service
     */
    @Override
    public void onDestroy() {
        if(couchbaseService != null) {
            unbindService(couchbaseService);
        }
        super.onDestroy();
    }

    /**
     * Implementation of the ICouchbaseDelegat inerface
     */
    private final ICouchbaseDelegate couchCallbackHandler = new ICouchbaseDelegate() {

        /**
         * Once Couchbase has started, load the couchapp, or the instructions if no couchapp is present
         */
        @Override
        public void couchbaseStarted(String host, int port) {

            //stop the load that we started to display the splash screen
            cancelLoadUrl();
            
            Log.d(TAG, "Cancelling load, loading couchapp");

            //loadUrl( "file:///android_asset/www/index.html" );
            String url = calcTangerineURL(host, port);
            Log.d(TAG, "url: " + url);
            Tangy.this.loadUrl(url);

            Tangy.this.couchbaseStarted(host, port);
        }

        @Override
        public void exit(String error) {}
    };
}




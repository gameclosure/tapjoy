package com.tealeaf.plugin.plugins;
import java.util.Map;
import org.json.JSONObject;
import org.json.JSONArray;
import org.json.JSONException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import com.tealeaf.EventQueue;
import com.tealeaf.TeaLeaf;
import com.tealeaf.logger;
import android.content.pm.PackageManager;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import java.util.HashMap;

import com.tealeaf.plugin.IPlugin;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;

import com.tealeaf.event.*;

import com.tapjoy.TapjoyConnect;
import com.tapjoy.TapjoyConstants;
import com.tapjoy.TapjoyFullScreenAdNotifier;
import com.tapjoy.TapjoyLog;

public class TapjoyPlugin implements IPlugin {
	Context _ctx;
	HashMap<String, String> manifestKeyMap = new HashMap<String,String>();
	boolean launchedOfferWall;

	public class tapjoyOfferClose extends com.tealeaf.event.Event {

		public tapjoyOfferClose() {
			super("tapjoyOfferClose");
		}

	}

	public class tapjoyAdAvailable extends com.tealeaf.event.Event {

		public tapjoyAdAvailable() {
			super("tapjoyAdAvailable");
		}
	}

	public class tapjoyAdNotAvailable extends com.tealeaf.event.Event {

		public tapjoyAdNotAvailable() {
			super("tapjoyAdNotAvailable");
		}
	}

	public class tapjoyAdNotifier implements TapjoyFullScreenAdNotifier {

		public void getFullScreenAdResponse() {
			EventQueue.pushEvent(new tapjoyAdAvailable());
		}

		public void getFullScreenAdResponseFailed(int error) {
			EventQueue.pushEvent(new tapjoyAdNotAvailable());
		}
	}

	public TapjoyPlugin() {
	}

	public void onCreateApplication(Context applicationContext) {
		_ctx = applicationContext;
	}

	public void onCreate(Activity activity, Bundle savedInstanceState) {
		PackageManager manager = activity.getBaseContext().getPackageManager();
		String[] keys = {"tapjoyAppID", "tapjoySecretKey"};
		try {
			Bundle meta = manager.getApplicationInfo(activity.getApplicationContext().getPackageName(),
					PackageManager.GET_META_DATA).metaData;
			for (String k : keys) {
				if (meta.containsKey(k)) {
					manifestKeyMap.put(k, meta.get(k).toString());
				}
			}
		} catch (Exception e) {
			logger.log("Exception while loading manifest keys:", e);
		}

		String tapJoyAppID = manifestKeyMap.get("tapjoyAppID");
		String tapJoySecretKey = manifestKeyMap.get("tapjoySecretKey");

		logger.log("{tapjoy} Installing for appID:", tapJoyAppID);

		// Enables logging to the console.
		//TapjoyLog.enableLogging(true);

		// Connect with the Tapjoy server.
		TapjoyConnect.requestTapjoyConnect(_ctx, tapJoyAppID, tapJoySecretKey);
	}

	public void setUserID(String jsonData) {
		try {
			JSONObject jsonObject = new JSONObject(jsonData);
			String userID = jsonObject.getString("userID");
			logger.log("{tapjoy} Setting userid : "+userID);
			TapjoyConnect.getTapjoyConnectInstance().setUserID(userID);
		} catch (Exception e) {
			logger.log("{tapjoy} WARNING: Failure in setUserID:", e);
			e.printStackTrace();
		}
	}

	public void showOffers(String jsonData) {
		launchedOfferWall = true;
		TapjoyConnect.getTapjoyConnectInstance().showOffers();
	}

	public void getFullScreenAd(String jsonData) {
		TapjoyConnect.getTapjoyConnectInstance().getFullScreenAd(new tapjoyAdNotifier());
	}

	public void showFullScreenAd(String jsonData) {
		launchedOfferWall = true;
		TapjoyConnect.getTapjoyConnectInstance().showFullScreenAd();
	}

	public void onResume() {
		logger.log("{tapjoy} onResume");
		if (launchedOfferWall) {
			logger.log("{tapjoy} Offer closed");
			launchedOfferWall = false;
			EventQueue.pushEvent(new tapjoyOfferClose());
		}
	}

	public void onStart() {
	}

	public void onPause() {
	}

	public void onStop() {
	}

	public void onDestroy() {
	}

	public void onNewIntent(Intent intent) {
	}

	public void setInstallReferrer(String referrer) {
	}

	public void onActivityResult(Integer request, Integer result, Intent data) {
	}

	public void logError(String error) {
	}

	public boolean consumeOnBackPressed() {
		return true;
	}

	public void onBackPressed() {
	}
}

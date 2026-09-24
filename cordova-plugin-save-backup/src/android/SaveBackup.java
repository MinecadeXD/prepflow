package com.minecade.prepflow.savebackup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.content.Context;
import java.io.OutputStream;
import java.io.IOException;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class SaveBackup extends CordovaPlugin {
    private static final int CREATE_DOCUMENT_REQUEST = 48127;
    private CallbackContext pendingCallback;
    private String pendingJson;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        if (!"create".equals(action)) {
            return false;
        }

        pendingCallback = callbackContext;
        pendingJson = args.getString(0);
        String fileName = args.optString(1, "prepflow_backup.json");

        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/json");
        intent.putExtra(Intent.EXTRA_TITLE, fileName);
        cordova.getActivity().startActivityForResult(intent, CREATE_DOCUMENT_REQUEST);
        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode != CREATE_DOCUMENT_REQUEST || pendingCallback == null) {
            return;
        }

        CallbackContext callback = pendingCallback;
        String json = pendingJson;
        pendingCallback = null;
        pendingJson = null;

        if (resultCode != Activity.RESULT_OK || intent == null || intent.getData() == null) {
            callback.error("Save cancelled");
            return;
        }

        Uri uri = intent.getData();
        try {
            OutputStream output = cordova.getActivity().getContentResolver().openOutputStream(uri);
            if (output == null) {
                callback.error("Could not open selected save location");
                return;
            }
            output.write(json.getBytes("UTF-8"));
            output.flush();
            output.close();
            callback.success("Backup saved");
        } catch (Exception e) {
            callback.error("Could not save backup: " + e.getMessage());
        }
    }
}

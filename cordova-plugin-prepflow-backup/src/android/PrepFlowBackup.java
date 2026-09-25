package com.minecade.prepflow.backup;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;

public class PrepFlowBackup extends CordovaPlugin {
    private static final int REQUEST_CREATE = 4101;
    private static final int REQUEST_OPEN = 4102;

    private CallbackContext pendingCallback;
    private String pendingBackupJson;

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) {
        if ("exportBackup".equals(action)) {
            String json = args.optString(0, "");
            String filename = args.optString(1, "PrepFlow_Backup.json");
            if (json.isEmpty()) {
                callbackContext.error("empty_backup");
                return true;
            }

            pendingCallback = callbackContext;
            pendingBackupJson = json;

            Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("application/json");
            intent.putExtra(Intent.EXTRA_TITLE, filename);
            
            // CRITICAL FIX 1: Register this plugin instance to capture the intent callback result
            cordova.setActivityResultCallback(this);
            
            cordova.getActivity().startActivityForResult(intent, REQUEST_CREATE);
            return true;
        }

        if ("importBackup".equals(action)) {
            pendingCallback = callbackContext;

            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setType("application/json");
            
            // CRITICAL FIX 2: Register this plugin instance to capture the intent callback result
            cordova.setActivityResultCallback(this);
            
            cordova.getActivity().startActivityForResult(intent, REQUEST_OPEN);
            return true;
        }

        return false;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        CallbackContext callback = pendingCallback;
        pendingCallback = null;

        if (callback == null) return;

        if (resultCode != Activity.RESULT_OK || intent == null || intent.getData() == null) {
            pendingBackupJson = null;
            callback.error("cancelled");
            return;
        }

        Uri uri = intent.getData();

        if (requestCode == REQUEST_CREATE) {
            try {
                OutputStream output = cordova.getActivity().getContentResolver().openOutputStream(uri);
                if (output == null) {
                    pendingBackupJson = null;
                    callback.error("open_output_failed");
                    return;
                }

                output.write(pendingBackupJson.getBytes(StandardCharsets.UTF_8));
                output.flush();
                output.close();
                pendingBackupJson = null;
                callback.success();
            } catch (Exception e) {
                pendingBackupJson = null;
                callback.error("write_failed");
            }
            return;
        }

        if (requestCode == REQUEST_OPEN) {
            try {
                InputStream input = cordova.getActivity().getContentResolver().openInputStream(uri);
                if (input == null) {
                    callback.error("open_input_failed");
                    return;
                }

                ByteArrayOutputStream buffer = new ByteArrayOutputStream();
                byte[] chunk = new byte[8192];
                int count;
                while ((count = input.read(chunk)) != -1) {
                    buffer.write(chunk, 0, count);
                }
                input.close();

                callback.success(buffer.toString("UTF-8"));
            } catch (Exception e) {
                callback.error("read_failed");
            }
        }
    }

    @Override
    public void onReset() {
        pendingCallback = null;
        pendingBackupJson = null;
        super.onReset();
    }
}

package org.cuberite.android.helpers;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.TextUtils;
import android.text.format.Formatter;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import org.cuberite.android.services.CuberiteService;

import static android.content.Context.WIFI_SERVICE;

public class CuberiteHelper {
    private static StringBuilder consoleOutput = new StringBuilder();

    public static void addConsoleOutput(Context context, String string) {
        StringBuilder logLine = new StringBuilder();
        String[] text = string.split("\\n");

        for (String line : text) {
            String curText = TextUtils.htmlEncode(line);

            if (curText.toLowerCase().startsWith("log: ")) {
                curText = curText.replaceFirst("(?i)log: ", "");
            } else if (curText.toLowerCase().startsWith("info:")) {
                curText = curText.replaceFirst("(?i)info: ", "");
                curText = "<font color= \"#FFA500\">" + curText + "</font>";
            } else if (curText.toLowerCase().startsWith("warning: ")) {
                curText = curText.replaceFirst("(?i)warning: ", "");
                curText = "<font color= \"#FF0000\">" + curText + "</font>";
            } else if (curText.toLowerCase().startsWith("error: ")) {
                curText = curText.replaceFirst("(?i)error: ", "");
                curText = "<font color=\"#8B0000\">" + curText + "</font>";
            }

            if (consoleOutput.length() == 0) {
                logLine.append(curText);
            } else {
                logLine.append("<br>").append(curText);
            }
        }
        consoleOutput.append(logLine);
        Intent intent = new Intent("updateLog");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    public static String getConsoleOutput() {
        return consoleOutput.toString();
    }

    public static void resetConsoleOutput() {
        consoleOutput = new StringBuilder();
    }

    public static String getIpAddress(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();

        if (ip == 0) {
            return "127.0.0.1";
        } else {
            return Formatter.formatIpAddress(ip);
        }
    }

    public static boolean isCuberiteRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (CuberiteService.class.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
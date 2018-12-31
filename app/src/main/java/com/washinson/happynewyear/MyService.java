package com.washinson.happynewyear;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import com.vk.sdk.VKAccessToken;
import com.vk.sdk.VKSdk;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MyService extends Service {
    // Binder given to clients
    private final IBinder mBinder = new LocalBinder();
    SharedPreferences sharedPreferences;
    String TAG = "VK";

    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferences = getSharedPreferences(TAG, Context.MODE_PRIVATE);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    getPostId();
                    ooo_YES();
                }
            }
        }).start();
    }

    void getPostId() {
        int id;
        while(true) {
            id = sharedPreferences.getInt("post_id", -1);
            if (id == -1) {
                VKParameters parameters = new VKParameters();
                //parameters.put("owner_id", "-" + VKAccessToken.currentToken().userId); //TODO: check it
                parameters.put("message", "Я вступаю в ряды обратного отсчета)");
                VKRequest request = VKApi.wall().post(parameters);
                request.executeWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        try {
                            int id = response.json.getJSONObject("response").getInt("post_id");
                            editor.putInt("post_id", id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        editor.apply();
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                    }
                });
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else break;
            Log.d("123", "post_id is " + id);
        }
    }

    void ooo_YES() {
        while(true) {
            int id = sharedPreferences.getInt("post_id", -1);
            if (id == -1) return;
            VKParameters parameters = new VKParameters();
            parameters.put("post_id", id);
            DateFormat df = new SimpleDateFormat("MMM dd kk:mm:ss yyyy", Locale.ENGLISH);
            long time = -1;
            try {
                Date dateNE = df.parse("Jan 01 00:00:00 2019");
                Date dateCur = new Date();
                time = dateNE.getTime() - dateCur.getTime();
                if(time <= 0) {
                    parameters.put("message", "Новый Гоооооод!!!! 2019))");
                } else {
                    long h = time / 3600000;
                    long m = time / 60000 % 60;
                    parameters.put("message", "До нового года осталось " + h + " часов(а) и " + m + " минут(ы) :)");
                }
                VKRequest request = VKApi.wall().edit(parameters);
                final boolean[] res = {false};
                request.executeSyncWithListener(new VKRequest.VKRequestListener() {
                    @Override
                    public void onComplete(VKResponse response) {
                        super.onComplete(response);
                        try {
                            int suc = response.json.getInt("response");
                            res[0] = suc == 1;
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(VKError error) {
                        super.onError(error);
                    }
                });
                //if(!res[0]) {
                //    SharedPreferences.Editor editor = sharedPreferences.edit();
                //    editor.remove("post_id");
                //    editor.apply();
                //    return;
                //}
            } catch (ParseException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(2*60000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public class LocalBinder extends Binder {
        MyService getService() {
            // Return this instance of LocalService so clients can call public methods
            return MyService.this;
        }
    }
}

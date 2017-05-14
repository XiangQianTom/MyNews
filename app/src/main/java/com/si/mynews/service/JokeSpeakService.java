package com.si.mynews.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.si.mynews.IMySpeakInterface;
import com.si.mynews.SpeakTaskCallback;
import com.si.mynews.iflytek.SpeechSynthesizerTask;

public class JokeSpeakService extends Service {

    private static final String TAG = JokeSpeakService.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    private IMySpeakInterface.Stub myBinder = new IMySpeakInterface.Stub() {

        @Override
        public void speakWords(String words, SpeakTaskCallback callback) throws RemoteException {
            SpeechSynthesizerTask.getInstance().setSpeakEndListener(callback);
            SpeechSynthesizerTask.getInstance().startSynthesizer(words);
        }

        @Override
        public void stopWords() throws RemoteException {
            SpeechSynthesizerTask.getInstance().stopSynthesizer();
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}

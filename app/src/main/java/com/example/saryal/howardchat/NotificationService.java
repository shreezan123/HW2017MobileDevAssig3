package com.example.saryal.howardchat;

import android.app.Service;

/**
 * Created by saryal on 8/8/17.
 */

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class NotificationService extends Service {

    @Nullable
    @Override
    public IBinder onBind(Intent intent){
        return null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i("SHR", "service started");
        setupDatabase();
        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("SHR", "service stopped");
    }


    private void setupDatabase() {
        DatabaseReference databaseRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messagesRef = databaseRef.child("messages");
        Query lastArticleQuery = messagesRef.limitToLast(1);

        lastArticleQuery.addValueEventListener(new ValueEventListener() {
            boolean mDidInitialLoad = false;
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (mDidInitialLoad == false) {
                    // This is the first load...we want to ignore it.
                    mDidInitialLoad = true;
                    return;
                }
                DataSnapshot messagesSnapShot = dataSnapshot.getChildren().iterator().next();
                Messages messages = new Messages(messagesSnapShot);
                if (!messages.getmUsername().equals(FirebaseAuth.getInstance().getCurrentUser().getDisplayName())) {
                    showNotification(messages);
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void showNotification(Messages messagesinst){
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), intent, 0);

        android.app.Notification n = new NotificationCompat.Builder(this)
                .setContentTitle("New Message")
                .setContentText(" " + messagesinst.getmUsername() + " : " + messagesinst.getmContent())
                .setSmallIcon(android.R.drawable.btn_dropdown)
                .setContentIntent(pIntent)
                .setAutoCancel(true)
                .build();
        notificationManager.notify(0, n);
    }
}



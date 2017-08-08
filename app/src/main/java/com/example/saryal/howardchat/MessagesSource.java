package com.example.saryal.howardchat;

/**
 * Created by saryal on 8/7/17.
 */
import android.content.Context;
import android.provider.ContactsContract;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesSource {

    public interface MessagesListener {
        void onMessagesReceived(List<Messages> messagesList);
    }
    private static MessagesSource sMessagesSource;

    private Context mContext;

    public static MessagesSource get(Context context) {
        if (sMessagesSource == null) {
            sMessagesSource = new MessagesSource(context);
        }
        return sMessagesSource;
    }
    private MessagesSource(Context context) {
        mContext = context;
    }
    // Firebase methods for you to implement.
    public void getMessages(final MessagesListener messagesListener){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messagesRef = databaseReference.child("messages");
        Query last50MessagesQuery = messagesRef.limitToLast(50);
        last50MessagesQuery.addValueEventListener(new ValueEventListener() {
            @Override

            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> children = dataSnapshot.getChildren();
                List<Messages> messages = new ArrayList<>();
                for (DataSnapshot childMessageSnapshot : children) { //fires oldest to newest
                    Messages message = new Messages(childMessageSnapshot);
                    if (message.getmContent() != null && message.getmUsername() != null) {
                        messages.add(message);
                    }
                }
                messagesListener.onMessagesReceived(messages);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });
    }

    public void sendMessages(Messages messages) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference messagesRef = databaseReference.child("messages");
        DatabaseReference newMessagesRef = messagesRef.push();
        Map<String,Object> messagesValMap = new HashMap<>();
        messagesValMap.put("fromUserName",messages.getmUsername());
        messagesValMap.put("fromUserId",messages.getmUserid());
        messagesValMap.put("content", messages.getmContent());
        newMessagesRef.setValue(messagesValMap);
    }
}

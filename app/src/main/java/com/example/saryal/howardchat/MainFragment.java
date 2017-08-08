package com.example.saryal.howardchat;

import android.support.v4.app.Fragment;

/**
 * Created by saryal on 8/7/17.
 */

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class MainFragment extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstance) {
        //inflate the view
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        final ListView messageList = v.findViewById(R.id.list_content);
        final EditText edittext = v.findViewById(R.id.edit_text);

        MessagesSource.get(getContext()).getMessages(new MessagesSource.MessagesListener() {
            @Override
            public void onMessagesReceived(List<Messages> messagesList) {
                MessageArrayAdapter adapter = new MessageArrayAdapter(getContext(), messagesList);
                messageList.setAdapter(adapter);
                // Whenever we set a new adapter (which usually scrolls to the top of the contents), scroll to the bottom of the contents.
                messageList.setSelection(adapter.getCount() - 1);
            }
        });
        Button sendbutton = v.findViewById(R.id.send_button);
        sendbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if (user == null) {
                    Toast.makeText(getContext(), "Can't send text, not logged in", Toast.LENGTH_SHORT);
                    return;
                }
                Messages messages= new Messages(user.getDisplayName(),user.getUid(),edittext.getText().toString());
                MessagesSource.get(getContext()).sendMessages(messages);
                edittext.setText("");
            }
        });
        return v;
    }

    private class MessageArrayAdapter extends BaseAdapter{
        protected Context mContext;
        protected List<Messages> mMessagesList;
        protected LayoutInflater mLayoutInflater;

        public MessageArrayAdapter(Context context, List<Messages> messagesList) {
            mContext = context;
            mMessagesList = messagesList;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

@Override
public int getCount() {
        return mMessagesList.size();
        }
@Override
public Object getItem(int position) {
        return mMessagesList.get(position);
        }

@Override
public long getItemId(int position) {
        return position;
        }

@Override
public View getView(int position, View convertView, ViewGroup parent) {
final Messages message = mMessagesList.get(position);
        View rowView = mLayoutInflater.inflate(R.layout.list_view_items, parent, false);
        TextView username = rowView.findViewById(R.id.user_name);
        username.setText(message.getmUsername());

        TextView content = rowView.findViewById(R.id.content);
        content.setText(message.getmContent());
        return rowView;
        }
    }
}
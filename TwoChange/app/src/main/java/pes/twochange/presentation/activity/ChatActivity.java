package pes.twochange.presentation.activity;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseListAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;

import pes.twochange.R;
import pes.twochange.domain.model.Chat;
import pes.twochange.domain.model.Message;
import pes.twochange.services.NotificationSender;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivitiy";
    private String userSenderUid;
    private String userReciverUid;
    private DatabaseReference mFirebaseChatRefSender;
    private DatabaseReference mFirebaseChatRefReciver;
    FloatingActionButton sendBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //Coger chat pasado como extra en el intent
        Chat chat = (Chat) getIntent().getExtras().getSerializable("chat");
        /*
        //crear chat
        chat = new Chat(userSenderUid, userReciverUid);*/

        userSenderUid = chat.getMessageSender();
        userReciverUid = chat.getMessageReciver();
        Log.d(TAG, userReciverUid);
        Log.d(TAG, userSenderUid);

        //Suscribirse al topic para recibir notificaciones de chat
        FirebaseMessaging.getInstance()
                .subscribeToTopic(userReciverUid);


        //Firebase database
        FirebaseDatabase mFirebaseDatabase = FirebaseDatabase.getInstance();
        //Referencia al chat
        mFirebaseChatRefSender = mFirebaseDatabase.getReference().child("chats").child(userSenderUid).child(userReciverUid);
        mFirebaseChatRefReciver = mFirebaseDatabase.getReference().child("chats").child(userReciverUid).child(userSenderUid);


        sendBtn = (FloatingActionButton)findViewById(R.id.sender_btn);
        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                EditText messageInput = (EditText) findViewById(R.id.message_input);
                String content = messageInput.getText().toString();
                content = content.trim();
                if (!content.isEmpty()) {
                    mFirebaseChatRefSender.push().setValue(new Message(content, userSenderUid, userReciverUid));
                    mFirebaseChatRefReciver.push().setValue(new Message(content, userSenderUid, userReciverUid));

                    NotificationSender n = new NotificationSender();
                    n.sendNotification(userSenderUid);

                    messageInput.setText("");
                }
            }
        });


        displayChatMessage();
    }

    private void displayChatMessage() {

        ListView messagesList = (ListView)findViewById(R.id.messages_list);
        FirebaseListAdapter<Message> adapter = new FirebaseListAdapter<Message>(this, Message.class, R.layout.message, mFirebaseChatRefSender) {
            @Override
            protected void populateView(View v, Message model, int position) {

                TextView messageContent, messageSender, messageTime;
                messageContent = (TextView) v.findViewById(R.id.message_content);
                messageSender = (TextView) v.findViewById(R.id.message_sender);
                messageTime = (TextView) v.findViewById(R.id.message_time);

                LinearLayout layoutMessageContent = (LinearLayout) v.findViewById(R.id.layout_message_content);
                RelativeLayout.LayoutParams rl = (RelativeLayout.LayoutParams) layoutMessageContent.getLayoutParams();
                if (model.getMessageSender().equals(userSenderUid)) {
                    layoutMessageContent.setBackgroundResource(R.drawable.ic_send_message);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT,0);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                } else {
                    layoutMessageContent.setBackgroundResource(R.drawable.ic_recive_message);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,0);
                    rl.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                }

                messageContent.setText(model.getMessageContent());
                messageSender.setText(model.getMessageSender());
                messageTime.setText(DateFormat.format("dd-MM-yyyy (HH:mm)", model.getMessageTime()));
            }
        };

        messagesList.setAdapter(adapter);
    }
}

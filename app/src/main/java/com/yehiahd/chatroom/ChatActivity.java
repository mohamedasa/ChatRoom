package com.yehiahd.chatroom;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ChatActivity extends AppCompatActivity {

    @BindView(R.id.recycler_chat)
    RecyclerView recyclerChat;
    @BindView(R.id.message_ET)
    EditText messageET;
    @BindView(R.id.send_msg_fab)
    FloatingActionButton sendMsgFab;


    private String url;
    private String name;


    private ChatAdapter adapter;
    private RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        url = preferences.getString("url", "");
        name = preferences.getString("name", "Yehia");

        adapter = new ChatAdapter(this, new ArrayList<Message>());
        mLayoutManager = new LinearLayoutManager(this);

        recyclerChat.setLayoutManager(mLayoutManager);
        recyclerChat.setAdapter(adapter);

        initRecycler();


    }

    private void initRecycler() {

        FirebaseDatabase.getInstance().getReference()
                .child("chatRoom")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        List<Message> list = new ArrayList<>();

                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                            Message message = snapshot.getValue(Message.class);
                            list.add(message);
                        }
                        adapter.updateList(list);
                        recyclerChat.scrollToPosition(list.size() - 1);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    @OnClick(R.id.send_msg_fab)
    public void sendMessage() {
        String msg = messageET.getText().toString();

        if (!msg.equals("")) {

            Message message = new Message()
                    .setName(name)
                    .setImgUrl(url)
                    .setMsg(msg)
                    .setDate(String.valueOf(Calendar.getInstance().getTimeInMillis()));

            String key = FirebaseDatabase.getInstance().getReference()
                    .child("chatRoom").push().getKey();

            FirebaseDatabase.getInstance().getReference()
                    .child("chatRoom")
                    .child(key)
                    .setValue(message)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                messageET.setText("");
                            }
                        }
                    });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_sign_out, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(ChatActivity.this, LoginActivity.class));
                finish();
                return true;
        }

        return false;
    }
}

package com.lineargs.chatservice.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lineargs.chatservice.R;
import com.lineargs.chatservice.adapters.MessageAdapter;
import com.lineargs.chatservice.model.ChatMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends BaseTopActivity {

    private static final int RC_SIGN_IN = 222;
    public static final String DUMMY = "dummy";

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.messageEditText)
    EditText messageEditText;
    @BindView(R.id.sendFab)
    FloatingActionButton sendFab;
    @BindView(R.id.messageListView)
    ListView messageListView;

    private MessageAdapter messageAdapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ChildEventListener childEventListener;

    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setups
        setupActionBar();
        setupNavDrawer();
        //Bind views
        ButterKnife.bind(this);
        //Initialise message ListView and it's adapter
        List<ChatMessage> chatMessages = new ArrayList<>();
        messageAdapter = new MessageAdapter(this, R.layout.item_message, chatMessages);
        messageListView.setAdapter(messageAdapter);
        username = DUMMY;
        //Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance();
        //Firebase Database
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference().child("messages");
        /* Disabling FAB in onCreate() to avoid sending empty message, either this
         * or an if...else statement in sendFab onClick()
         */
        sendFab.setEnabled(false);

        //Enable sendFab when there is text to send
        messageEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().length() > 0) {
                    sendFab.setEnabled(true);
                } else {
                    sendFab.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //Firebase AuthUI StateListener
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    onSignedInInitialize(firebaseUser.getDisplayName());
                } else {
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.EmailBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    /**
     * FloatingActionButton onClick()
     */
    @OnClick(R.id.sendFab)
    public void sendMessage() {
        ChatMessage chatMessage = new ChatMessage(messageEditText.getText().toString(), username, null);
        databaseReference.push().setValue(chatMessage);
        messageEditText.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        setDrawerSelectedItem(R.id.nav_home);
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        detachDatabaseReadListener();
        messageAdapter.clear();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, getString(R.string.sign_in_successful), Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, getString(R.string.sign_in_cancelled), Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    /**
     * Used to initialize the UI afer sign in
     *
     * @param username The username
     */
    private void onSignedInInitialize(String username) {
        this.username = username;
        attachDatabaseReadListener();
    }

    /**
     * Used to breakdown the UI after user signs out
     */
    private void onSignedOutCleanUp() {
        username = DUMMY;
        messageAdapter.clear();
        detachDatabaseReadListener();
    }

    private void attachDatabaseReadListener() {
        if (childEventListener == null) {
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    messageAdapter.add(chatMessage);
                }

                @Override
                public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                }

                @Override
                public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            };
            databaseReference.addChildEventListener(childEventListener);
        }
    }

    private void detachDatabaseReadListener() {
        if (childEventListener != null) {
            databaseReference.removeEventListener(childEventListener);
        }
    }
}

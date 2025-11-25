package com.example.journal;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class log extends AppCompatActivity {

    private LinearLayout messagesContainer;
    private EditText messageInput;
    private Button sendButton;
    private ScrollView scrollView;

    private ChatbotAPI chatbot;

    FirebaseFirestore db;
    String currentUser;  // logged in username

    CollectionReference chatRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        messagesContainer = findViewById(R.id.messagesContainer);
        messageInput = findViewById(R.id.messageInput);
        sendButton = findViewById(R.id.sendButton);
        scrollView = findViewById(R.id.chatScrollView);

        db = FirebaseFirestore.getInstance();

        // Get logged in username from SharedPreferences
        currentUser = getSharedPreferences("UserData", MODE_PRIVATE)
                .getString("loggedUsername", "UnknownUser");

        // Firestore path: chats/{username}/messages
        chatRef = db.collection("chats")
                .document(currentUser)
                .collection("messages");

        chatbot = new ChatbotAPI(BuildConfig.HF_TOKEN);

        ImageButton back = findViewById(R.id.backButton);
        back.setOnClickListener(v -> finish());

        // Load previous messages
        loadChatHistory();

        sendButton.setOnClickListener(v -> {
            String userMessage = messageInput.getText().toString().trim();

            if (!userMessage.isEmpty()) {
                addLabeledMessage(currentUser, userMessage, true);
                saveMessage(currentUser, userMessage, true);

                messageInput.setText("");

                chatbot.sendMessage(userMessage, new ChatbotAPI.ChatCallback() {
                    @Override
                    public void onSuccess(String reply) {
                        runOnUiThread(() -> {
                            addLabeledMessage("Juna", reply, false);
                            saveMessage("Juna", reply, false);
                        });
                    }

                    @Override
                    public void onError(String errorMessage) {
                        runOnUiThread(() -> {
                            addLabeledMessage("Juna", "AI Error: " + errorMessage, false);
                            saveMessage("Juna", "AI Error: " + errorMessage, false);
                        });
                    }
                });
            }
        });

        // Only send greeting if chat is empty
        chatRef.get().addOnSuccessListener(snap -> {
            if (snap.isEmpty()) {
                String greeting = "Hi! I'm here to listen. How are you feeling today?";
                addLabeledMessage("Juna", greeting, false);
                saveMessage("Juna", greeting, false);
            }
        });
    }

    private void loadChatHistory() {
        chatRef
                .orderBy("timestamp")
                .get()
                .addOnSuccessListener(query -> {
                    messagesContainer.removeAllViews();

                    for (QueryDocumentSnapshot doc : query) {
                        String sender = doc.getString("sender");
                        String message = doc.getString("message");
                        boolean isUser = doc.getBoolean("isUser");

                        addLabeledMessage(sender, message, isUser);
                    }

                    scrollToBottom();
                });
    }

    private void addLabeledMessage(String name, String text, boolean isUser) {

        TextView label = new TextView(this);
        label.setText(name);
        label.setTextSize(12);
        label.setPadding(10, 10, 10, 2);
        label.setTypeface(null, Typeface.BOLD);

        // DIFFERENT COLORS FOR USER VS JUNA
        if (isUser) {
            // User name: white
            label.setTextColor(Color.parseColor("#FFFFFF"));
        } else {
            // Juna name: light teal (good contrast on purple)
            label.setTextColor(Color.parseColor("#A8FFF4"));
        }

        LinearLayout.LayoutParams labelParams =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        labelParams.gravity = isUser ? Gravity.END : Gravity.START;
        label.setLayoutParams(labelParams);

        messagesContainer.addView(label);

        addMessageBubble(text, isUser);

        scrollToBottom();
    }


    // Original bubble creation
    private void addMessageBubble(String text, boolean isUser) {
        TextView bubble = new TextView(this);
        bubble.setText(text);
        bubble.setTextSize(16);
        bubble.setPadding(20, 15, 20, 15);

        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT);

        if (isUser) {
            params.gravity = Gravity.END;
            bubble.setBackgroundResource(R.drawable.bubble_user);
            bubble.setTextColor(Color.WHITE);
        } else {
            params.gravity = Gravity.START;
            bubble.setBackgroundResource(R.drawable.bubble_bot);
            bubble.setTextColor(Color.BLACK);
        }

        bubble.setLayoutParams(params);
        messagesContainer.addView(bubble);
    }

    private void saveMessage(String sender, String message, boolean isUser) {
        chatRef.add(new ChatMessage(sender, message, System.currentTimeMillis(), isUser));
    }

    private void scrollToBottom() {
        scrollView.post(() -> scrollView.fullScroll(ScrollView.FOCUS_DOWN));
    }
}

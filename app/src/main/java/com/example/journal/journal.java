package com.example.journal;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class journal extends AppCompatActivity {

    EditText editTitle, editNote;
    Button submitButton;

    DatabaseReference journalRef;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal);

        editTitle = findViewById(R.id.editTitle);
        editNote = findViewById(R.id.editNote);
        submitButton = findViewById(R.id.submitButton);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Store journal entries inside this user’s node
            journalRef = FirebaseDatabase.getInstance()
                    .getReference("journals")
                    .child("users")
                    .child(userId);
        }

        submitButton.setOnClickListener(v -> saveJournal());
    }

    private void saveJournal() {
        String title = editTitle.getText().toString().trim();
        String note = editNote.getText().toString().trim();

        if (title.isEmpty() || note.isEmpty()) {
            Toast.makeText(this, "Please fill in both fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (journalRef != null) {
            // ✅ Use your hidden token from BuildConfig
            HuggingFaceAPI hfAPI = new HuggingFaceAPI(BuildConfig.HF_TOKEN);

            hfAPI.analyzeEmotion(note, new HuggingFaceAPI.EmotionCallback() {
                @Override
                public void onSuccess(String mood) {
                    String journalId = journalRef.push().getKey();
                    if (journalId != null) {
                        JournalEntry entry = new JournalEntry(title, note, mood);
                        journalRef.child(journalId).setValue(entry)
                                .addOnSuccessListener(unused -> runOnUiThread(() -> {
                                    EmojiUtils.MoodDisplay moodDisplay = EmojiUtils.moodToDisplay(mood);
                                    Toast.makeText(journal.this,
                                            "Mood: " + moodDisplay.textWithEmoji,
                                            Toast.LENGTH_SHORT).show();
                                    editTitle.setText("");
                                    editNote.setText("");
                                }))
                                .addOnFailureListener(e -> runOnUiThread(() ->
                                        Toast.makeText(journal.this,
                                                "Error: " + e.getMessage(),
                                                Toast.LENGTH_SHORT).show()));
                    }
                }

                @Override
                public void onError(Exception e) {
                    runOnUiThread(() -> {
                        EmojiUtils.MoodDisplay moodDisplay = EmojiUtils.moodToDisplay("neutral");
                        Toast.makeText(journal.this,
                                "Mood: " + moodDisplay.textWithEmoji,
                                Toast.LENGTH_SHORT).show();
                    });

                    String journalId = journalRef.push().getKey();
                    if (journalId != null) {
                        JournalEntry entry = new JournalEntry(title, note, "neutral");
                        journalRef.child(journalId).setValue(entry);
                    }
                }
            });
        }
    }
}

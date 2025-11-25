package com.example.journal;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;

public class JournalViewActivity extends AppCompatActivity {

    private TextView titleView, noteView, moodView;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_view);

        // Bind views
        titleView = findViewById(R.id.viewTitle);
        noteView = findViewById(R.id.viewNote);
        moodView = findViewById(R.id.viewMood);
        backButton = findViewById(R.id.backButton);

        // Receive data from intent
        String title = getIntent().getStringExtra("title");
        String note = getIntent().getStringExtra("note");
        String mood = getIntent().getStringExtra("mood");

        // Apply title & note
        titleView.setText(title);
        noteView.setText(note);

        // Convert mood → emoji + text + color
        EmojiUtils.MoodDisplay moodDisplay = EmojiUtils.moodToDisplay(mood);

        // Set mood text + emoji
        moodView.setText("Mood: " + moodDisplay.textWithEmoji);

        // Apply mood color
        moodView.setTextColor(moodDisplay.color);

        // Back press
        backButton.setOnClickListener(v -> finish());
    }
}

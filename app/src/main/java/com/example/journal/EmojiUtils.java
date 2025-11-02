package com.example.journal;

import android.graphics.Color;

public class EmojiUtils {
    public static class MoodDisplay {
        public final String textWithEmoji;
        public final int color;

        public MoodDisplay(String textWithEmoji, int color) {
            this.textWithEmoji = textWithEmoji;
            this.color = color;
        }
    }

    public static MoodDisplay moodToDisplay(String mood) {
        switch (mood.toLowerCase()) {
            case "joy":
            case "happy":
            case "love":
                return new MoodDisplay("Joy 😊", Color.parseColor("#4CAF50")); // Green
            case "anger":
            case "annoyed":
                return new MoodDisplay("Anger 😡", Color.parseColor("#F44336")); // Red
            case "sadness":
            case "sad":
                return new MoodDisplay("Sadness 😢", Color.parseColor("#2196F3")); // Blue
            case "fear":
                return new MoodDisplay("Fear 😨", Color.parseColor("#9C27B0")); // Purple
            case "surprise":
                return new MoodDisplay("Surprise 😲", Color.parseColor("#FFC107")); // Yellow
            default:
                return new MoodDisplay("Neutral 😐", Color.parseColor("#9E9E9E")); // Gray
        }
    }
}
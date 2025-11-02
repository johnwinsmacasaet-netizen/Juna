package com.example.journal;

public class JournalEntry {
    private String title;
    private String note;
    private String mood; // NEW field

    // 🔹 Empty constructor required for Firebase
    public JournalEntry() {
    }

    // 🔹 Constructor for creating a new entry without mood
    public JournalEntry(String title, String note) {
        this.title = title;
        this.note = note;
        this.mood = "neutral"; // default if API not used
    }

    // 🔹 Constructor with mood (for API usage)
    public JournalEntry(String title, String note, String mood) {
        this.title = title;
        this.note = note;
        this.mood = mood;
    }

    // 🔹 Getters and setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getMood() {
        return mood;
    }

    public void setMood(String mood) {
        this.mood = mood;
    }
}

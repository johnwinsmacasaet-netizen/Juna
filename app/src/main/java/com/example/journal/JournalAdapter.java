package com.example.journal;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private List<JournalEntry> journalList;

    public JournalAdapter(List<JournalEntry> journalList) {
        this.journalList = journalList;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_journal, parent, false);
        return new JournalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        JournalEntry entry = journalList.get(position);
        holder.title.setText(entry.getTitle());

        // Show only short preview of note
        String preview = entry.getNote();
        if (preview.length() > 50) {
            preview = preview.substring(0, 50) + "...";
        }
        holder.note.setText(preview);

        // Mood formatting
        String mood = capitalizeFirst(entry.getMood());
        String emoji = getEmojiForMood(mood);

        holder.mood.setText(mood + " " + emoji);
        holder.mood.setTextColor(getColorForMood(mood));
    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    static class JournalViewHolder extends RecyclerView.ViewHolder {
        TextView title, note, mood;

        JournalViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.journalTitle);
            note = itemView.findViewById(R.id.journalNote);
            mood = itemView.findViewById(R.id.journalMood);
        }
    }

    // Helper method to map mood → emoji
    private String getEmojiForMood(String mood) {
        if (mood == null) return "😐"; // default neutral

        switch (mood.toLowerCase()) {
            case "joy":
            case "happy":
            case "happiness":
                return "😄";
            case "sad":
            case "sadness":
                return "😢";
            case "anger":
            case "angry":
                return "😡";
            case "fear":
                return "😨";
            case "love":
                return "❤️";
            case "surprise":
                return "😲";
            case "neutral":
                return "😐";
            default:
                return "🙂"; // fallback
        }
    }

    // Helper method to map mood → color
    private int getColorForMood(String mood) {
        if (mood == null) return Color.GRAY;

        switch (mood.toLowerCase()) {
            case "joy":
            case "happy":
            case "happiness":
                return Color.parseColor("#4CAF50"); // green
            case "sad":
            case "sadness":
                return Color.parseColor("#2196F3"); // blue
            case "anger":
            case "angry":
                return Color.parseColor("#F44336"); // red
            case "fear":
                return Color.parseColor("#9C27B0"); // purple
            case "love":
                return Color.parseColor("#E91E63"); // pink
            case "surprise":
                return Color.parseColor("#FF9800"); // orange
            case "neutral":
                return Color.parseColor("#9E9E9E"); // gray
            default:
                return Color.BLACK; // fallback
        }
    }

    // Helper method to capitalize first letter
    private String capitalizeFirst(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
}

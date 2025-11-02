package com.example.journal;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class HuggingFaceAPI {
    private static final String TAG = "HuggingFaceAPI";
    private static final String API_URL =
            "https://api-inference.huggingface.co/models/j-hartmann/emotion-english-distilroberta-base";

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    public HuggingFaceAPI(String apiKey) {
        this.apiKey = apiKey;
    }

    // Callback interface
    public interface EmotionCallback {
        void onSuccess(String mood);
        void onError(Exception e);
    }

    // 🔹 Analyze text emotion
    public void analyzeEmotion(String text, EmotionCallback callback) {
        try {
            JSONObject json = new JSONObject();
            json.put("inputs", text);

            RequestBody body = RequestBody.create(json.toString(), JSON);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey) // Token should be raw (hf_xxx)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Log.e(TAG, "API request failed", e);
                    callback.onError(e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if (!response.isSuccessful()) {
                        Log.e(TAG, "Unexpected response: " + response);
                        callback.onError(new IOException("Unexpected code " + response));
                        return;
                    }

                    try {
                        String responseBody = response.body().string();
                        Log.d(TAG, "API raw response: " + responseBody);

                        JSONArray outer = new JSONArray(responseBody);
                        JSONArray emotions = outer.getJSONArray(0);

                        // Pick the top emotion
                        String bestEmotion = "neutral";
                        double bestScore = 0;

                        for (int i = 0; i < emotions.length(); i++) {
                            JSONObject obj = emotions.getJSONObject(i);
                            String label = obj.getString("label");
                            double score = obj.getDouble("score");
                            if (score > bestScore) {
                                bestScore = score;
                                bestEmotion = label;
                            }
                        }

                        callback.onSuccess(bestEmotion);

                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing API response", e);
                        callback.onError(e);
                    }
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error creating request", e);
            callback.onError(e);
        }
    }
}

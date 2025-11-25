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

public class ChatbotAPI {

    private static final String TAG = "ChatbotAPI";

    private static final String API_URL =
            "https://router.huggingface.co/v1/chat/completions";

    private static final MediaType JSON_TYPE =
            MediaType.get("application/json; charset=utf-8");

    private final OkHttpClient client = new OkHttpClient();
    private final String apiKey;

    public interface ChatCallback {
        void onSuccess(String reply);
        void onError(String errorMessage);
    }

    public ChatbotAPI(String apiKey) {
        this.apiKey = apiKey;
    }

    public void sendMessage(String userMessage, ChatCallback callback) {
        try {

            /* SYSTEM PROMPT (unchanged, only cutoff rule added) */
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content",
                    "You are a concise, emotionally aware chat assistant. " +
                            "Keep replies very short — usually 1–2 sentences. " +
                            "Always answer directly and specifically to what the user just said. " +
                            "Stay on-topic and avoid generic lists, steps, or explanations unless the user asks. " +
                            "Show brief empathy when the user is stressed or upset, but keep it short. " +
                            "Never give long-form advice or multi-step instructions unless requested. " +
                            "Make sure each reply fully finishes its thought — no cutoffs. " +
                            "Keep the tone natural, warm, and human-like."
            );




            JSONObject userMsg = new JSONObject();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);

            JSONArray messages = new JSONArray();
            messages.put(systemMsg);
            messages.put(userMsg);

            JSONObject root = new JSONObject();
            root.put("model", "Qwen/Qwen1.5-1.8B-Chat:featherless-ai");
            root.put("messages", messages);

            /* CUT-OFF FIX — allow slightly more space */
            root.put("max_tokens", 120);   // previously 60 – doubled to prevent cutoff
            root.put("temperature", 0.4);

            RequestBody body = RequestBody.create(root.toString(), JSON_TYPE);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {

                @Override
                public void onFailure(Call call, IOException e) {
                    callback.onError("Connection failed: " + e.getMessage());
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {

                    if (!response.isSuccessful()) {
                        callback.onError("HTTP Error: " + response.code());
                        return;
                    }

                    try {
                        String responseBody = response.body().string();
                        Log.d(TAG, "Raw Response: " + responseBody);

                        JSONObject obj = new JSONObject(responseBody);
                        JSONArray choices = obj.getJSONArray("choices");

                        JSONObject messageObj = choices.getJSONObject(0)
                                .getJSONObject("message");

                        String reply = messageObj.getString("content").trim();

                        /* ----------------------------
                           CUTOFF DETECTION & PATCHING
                           ---------------------------- */
                        if (looksCutOff(reply)) {
                            fetchCompletion(reply, callback);
                            return;
                        }

                        callback.onSuccess(reply);

                    } catch (Exception e) {
                        callback.onError("Parsing Error: " + e.getMessage());
                    }
                }
            });

        } catch (Exception e) {
            callback.onError("Error creating request: " + e.getMessage());
        }
    }

    /* Detect cut-off endings */
    private boolean looksCutOff(String text) {
        return !text.endsWith(".")
                && !text.endsWith("!")
                && !text.endsWith("?")
                && text.length() > 20;
    }

    /* Ask model to finish the incomplete sentence */
    private void fetchCompletion(String partial, ChatCallback callback) {

        try {
            JSONObject systemMsg = new JSONObject();
            systemMsg.put("role", "system");
            systemMsg.put("content",
                    "The following reply was cut off. Finish the thought in one short sentence only."
            );

            JSONObject assistantMsg = new JSONObject();
            assistantMsg.put("role", "assistant");
            assistantMsg.put("content", partial);

            JSONArray msgs = new JSONArray();
            msgs.put(systemMsg);
            msgs.put(assistantMsg);

            JSONObject root = new JSONObject();
            root.put("model", "Qwen/Qwen1.5-1.8B-Chat:featherless-ai");
            root.put("messages", msgs);
            root.put("max_tokens", 40);

            RequestBody body = RequestBody.create(root.toString(), JSON_TYPE);

            Request request = new Request.Builder()
                    .url(API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call c, IOException e) {
                    callback.onSuccess(partial); // fallback: show original
                }

                @Override
                public void onResponse(Call c, Response r) throws IOException {
                    try {
                        String rb = r.body().string();
                        JSONObject j = new JSONObject(rb);
                        String fix = j.getJSONArray("choices")
                                .getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content")
                                .trim();

                        callback.onSuccess(partial + " " + fix);

                    } catch (Exception ex) {
                        callback.onSuccess(partial);
                    }
                }
            });

        } catch (Exception e) {
            callback.onSuccess(partial);
        }
    }
}

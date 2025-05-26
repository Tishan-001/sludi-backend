package com.sludi.sludi.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;

import java.nio.charset.StandardCharsets;

public class JsonUtil {
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public static String prettyJson(String json) {
        return gson.toJson(JsonParser.parseString(json));
    }

    public static String prettyJson(byte[] jsonBytes) {
        return prettyJson(new String(jsonBytes, StandardCharsets.UTF_8));
    }
}

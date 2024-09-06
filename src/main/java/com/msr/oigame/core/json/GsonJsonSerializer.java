package com.msr.oigame.core.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
public class GsonJsonSerializer implements JsonSerializer {

    private Gson gson;

    public GsonJsonSerializer(GsonBuilder gsonBuilder) {
        this.gson = gsonBuilder.create();
    }

    @Override
    public String serialize(Object data) {
        return getGson().toJson(data);
    }

    public Gson getGson() {
        if (this.gson == null) {
            this.gson = new Gson();
        }
        return this.gson;
    }
}

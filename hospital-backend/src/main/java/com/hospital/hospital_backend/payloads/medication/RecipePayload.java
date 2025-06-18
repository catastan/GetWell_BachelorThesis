package com.hospital.hospital_backend.payloads.medication;

public class RecipePayload {
    private String content;

    public RecipePayload(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}


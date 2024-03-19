package dev.potato.npcgpt.utilities.enumerations.configurations;

public enum ConfigKeys {
    OPEN_AI_API_TOKEN("open-ai-api-token");

    public final String VALUE;

    ConfigKeys(String VALUE) {
        this.VALUE = VALUE;
    }
}
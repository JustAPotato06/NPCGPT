package dev.potato.npcgpt.utilities;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;

public class LanguageUtilities {
    public static TextComponent PREFIX = LegacyComponentSerializer.legacy('&').deserialize("&b&l[NPC GPT] &r");
    public static TextComponent INCORRECT_SPAWNNPC_USAGE = LegacyComponentSerializer.legacy('&').deserialize("&cIncorrect usage! Example: /spawnnpc [name] [role]");
}
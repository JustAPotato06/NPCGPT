package dev.potato.npcgpt.traits;

import com.theokanning.openai.completion.CompletionChoice;
import com.theokanning.openai.completion.CompletionRequest;
import com.theokanning.openai.service.OpenAiService;
import dev.potato.npcgpt.NPCGPT;
import dev.potato.npcgpt.utilities.LanguageUtilities;
import dev.potato.npcgpt.utilities.enumerations.configurations.ConfigKeys;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitName;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

import java.time.Duration;
import java.util.List;

@TraitName("conversation-trait")
public class ConversationTrait extends Trait {
    private final NPCGPT plugin = NPCGPT.getPlugin();
    private String name;
    private String role;
    private Player targetPlayer;
    private StringBuilder conversation;
    private final String CONVERSATION_STARTER;

    public ConversationTrait() {
        super("conversation-trait");
        CONVERSATION_STARTER = "";
    }

    public ConversationTrait(String name, String role) {
        super("conversation-trait");
        this.name = name;
        this.role = role;
        CONVERSATION_STARTER = "The following is a conversation with an AI who represents a " + this.role.toLowerCase() + " NPC character in the game Minecraft. " +
                "The AI should limit their knowledge to the world of Minecraft and being a " + this.role.toLowerCase() + " and should not stray even if asked about something else. " +
                "Play this " + this.role.toLowerCase() + " role without mentioning that you are in Minecraft. To you, Minecraft is real life. " +
                "Your name is " + this.name + ".\n" +
                "\n" +
                "Human: Hello, who are you?\n" +
                "\n" +
                "AI:";
    }

    public String getRole() {
        return role;
    }

    public Player getTargetPlayer() {
        return targetPlayer;
    }

    public StringBuilder getConversation() {
        return conversation;
    }

    @EventHandler
    public void onNPCRightClick(NPCRightClickEvent e) {
        if (e.getNPC() != npc) return;

        Player player = e.getClicker();

        if (targetPlayer != null) {
            if (targetPlayer.equals(player)) {
                player.sendMessage(
                        LanguageUtilities.PREFIX.append(
                                Component.text("You are already speaking to this NPC!", NamedTextColor.RED)
                        )
                );
            } else {
                if (npc.getEntity().getLocation().distance(targetPlayer.getLocation()) > 20) {
                    targetPlayer.sendMessage(
                            LanguageUtilities.PREFIX.append(
                                    Component.text("A conversation you were having with an NPC has ended because you walked away.", NamedTextColor.RED)
                            )
                    );
                    startConversation(player);
                } else {
                    player.sendMessage(
                            LanguageUtilities.PREFIX.append(
                                    Component.text("This NPC is currently in a conversation with another player.", NamedTextColor.RED)
                            )
                    );
                }
            }
        } else {
            startConversation(player);
        }
    }

    private void startConversation(Player player) {
        targetPlayer = player;
        conversation = new StringBuilder(CONVERSATION_STARTER);
        getResponse(player, "");
    }

    public void stopConversation() {
        if (targetPlayer == null) return;

        targetPlayer.sendMessage(
                LanguageUtilities.PREFIX.append(
                        Component.text("A conversation you were having with an NPC has ended.", NamedTextColor.RED)
                )
        );

        this.targetPlayer = null;
        conversation = new StringBuilder();
    }

    public void addMessage(String message) {
        conversation.append("\n\nHuman:").append(message).append("\n\nAI:");
    }

    public void getResponse(Player player, String playerMessage) {
        player.sendActionBar(
                Component.text("Thinking...", NamedTextColor.GRAY)
        );
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HARP, 1, 1);

        String token = plugin.getConfig().getString(ConfigKeys.OPEN_AI_API_TOKEN.VALUE);
        OpenAiService service = new OpenAiService(token, Duration.ZERO);
        CompletionRequest request = CompletionRequest.builder()
                .model("gpt-3.5-turbo-instruct")
                .prompt(conversation.toString())
                .temperature(0.50)
                .maxTokens(150)
                .topP(1.0)
                .frequencyPenalty(0.0)
                .presencePenalty(0.6)
                .stop(List.of("Human:", "AI:"))
                .build();
        List<CompletionChoice> choices = service.createCompletion(request).getChoices();
        String response = choices.get(0).getText();

        conversation.append(response);

        TextComponent playerPrefix = LegacyComponentSerializer.legacy('&').deserialize("&e&l[YOU] &r");
        TextComponent npcPrefix = LegacyComponentSerializer.legacy('&').deserialize("&6&l[" + getNPC().getName() + "] &r");

        if (playerMessage != null && !playerMessage.equalsIgnoreCase("")) {
            player.sendMessage(playerPrefix.append(Component.text(playerMessage, NamedTextColor.WHITE)));
        }

        player.sendMessage(npcPrefix.append(Component.text(response.strip(), NamedTextColor.WHITE)));
    }
}
package CatBotJava;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.util.Objects;

public class ConvertCommand extends ListenerAdapter {

    private final CurrencyService currencyService;

    public ConvertCommand(CurrencyService currencyService) {
        this.currencyService = currencyService;
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        if (!event.getName().equals("convert")) return;

        long nominal = Objects.requireNonNull(event.getOption("nominal")).getAsLong();
        String source = Objects.requireNonNull(event.getOption("source")).getAsString();
        String target = Objects.requireNonNull(event.getOption("target")).getAsString();

        // Discord requires a response within 3s; defer since we make an HTTP call.
        event.deferReply().queue();

        currencyService.getRate(source, target)
                .thenAccept(rate -> {
                    String message = rate
                            .map(r -> {
                                return String.format("%d %s = %.2f %s", nominal, source, nominal * r, target);
                            })
                            .orElse("Invalid Currency Code");
                    event.getHook().sendMessage(message).queue();
                })
                .exceptionally(ex -> {
                    ex.printStackTrace();
                    event.getHook().sendMessage("Failed to fetch exchange rate.").queue();
                    return null;
                });
    }
}
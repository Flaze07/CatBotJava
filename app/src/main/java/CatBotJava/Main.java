package CatBotJava;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.JDABuilder;

import java.util.EnumSet;

public class Main {

    public static void main(String[] args) throws InterruptedException {
        String token = System.getenv("DISCORD_TOKEN");
        if (token == null || token.isBlank()) {
            System.err.println("Set the DISCORD_TOKEN environment variable.");
            System.exit(1);
        }

        JDA jda = JDABuilder.createLight(token, EnumSet.of(
                        GatewayIntent.GUILD_MESSAGES,
                        GatewayIntent.GUILD_MESSAGE_REACTIONS))
                .addEventListeners(new ConvertCommand(new CurrencyService()))
                .build();

        jda.awaitReady();

        jda.upsertCommand(
                Commands.slash("convert", "Convert Currency")
                        .addOptions(
                                new OptionData(OptionType.INTEGER, "nominal", "Amount to convert", true)
                                        .setMinValue(0),
                                new OptionData(OptionType.STRING, "source", "Source currency code (e.g. USD)", true),
                                new OptionData(OptionType.STRING, "target", "Target currency code (e.g. JPY)", true))
        ).queue();
    }
}
package com.hei.isc;

import com.hei.isc.bot.ISCBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * The entry point of the Telegram bot
 * @author Pierre-André Mudry
 * @version 1.0
 */
public final class App {
    private App() {
    }

    /**
     * Skeleton code for BOT lab made for EPTM
     *
     * @param args The arguments of the program.
     */
    public static void main(String[] args) {
        Utils.logger.info("Starting the bot - Version " + Utils.version);

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new ISCBot());
        } catch (Exception e) {
            Utils.logger.error(e.toString());
        }
    }
}

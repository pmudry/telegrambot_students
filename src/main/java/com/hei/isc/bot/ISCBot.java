package com.hei.isc.bot;

import com.hei.isc.Utils;
import com.hei.isc.imageProcessing.ImagePipeline;
import com.hei.isc.imageProcessing.data.Image;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendSticker;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * A simple bot facade for the ISC EPTM demo<br>
 * Using API from Rubenlagus
 *
 * @see <a href="https://github.com/rubenlagus/TelegramBots">The Rubenlagus API</a>
 * @author Pierre-André Mudry, Marc Pignat
 * @version 1.0
 */

public class ISCBot extends TelegramLongPollingBot {

    public final static String BOT_TOKEN = "INSERT YOUR TOKEN HERE";
    public final static String BOT_USER = "A super bot for doing stuff during the ISC visit day";

    public ISCBot() {
        setMenu();
    }

    @Override
    public String getBotUsername() {
        return BOT_USER;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    /**
     * Called when we receive a message. The {@link Update} received can
     * have several things embedded, ranging from messages, stickers, audio... It also
     * has callbacks information when buttons are pressed.
     *
     * @param update The update from the server
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage()) {

            Message msg = update.getMessage();

            // TODO Complete here the message parsing
        }
    }

    /**
     * Generic function to send text messages. The messages can include
     * html content that will be parsed correctly
     *
     * @param chatId The chat id
     * @param txt    The text to be sent, optionally with HTML tags
     */
    private void sendTextMessage(Long chatId, String txt) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(txt);

        // HTML can be embedded in replies if required
        message.setParseMode(ParseMode.HTML);

        try {
            Utils.logger.info("Replying " + message.getText());
            execute(message); // Call this method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    /**
     * Sends a Sticker (which is simply a webp file) that handles transparency correctly
     *
     * @param chatID   The chat to send the message to
     * @param filePath The path of the file
     */
    private void sendSticker(Long chatID, String filePath) {
        SendSticker sticker_msg = new SendSticker();
        sticker_msg.setChatId(chatID.toString());

        try {
            sticker_msg.setSticker(new InputFile(new File(filePath)));
            execute(sticker_msg);
        } catch (Exception ex) {
            Utils.logger.error(ex.getLocalizedMessage());
        }
    }

    /**
     * Called when we got a Photo in message
     *
     * @param update The chat {@link Update} message
     * @return The {@link File} to be read
     */
    private File getPhotoFile(Update update) {
        // When receiving a photo, you usually get different sizes of it
        List<PhotoSize> photos = update.getMessage().getPhoto();

        // We fetch the biggest photo
        PhotoSize p = photos.stream()
            .max(Comparator.comparing(PhotoSize::getFileSize)).orElse(null);

        if (p == null) return null;

        try {
            String path = p.getFilePath();

            // If we don't have the file's path yet, get it
            if (path == null) {
                GetFile getFileMethod = new GetFile();
                getFileMethod.setFileId(p.getFileId());
                // We execute the method using AbsSender::execute method.
                org.telegram.telegrambots.meta.api.objects.File file = execute(getFileMethod);
                // We now have the file_path
                path = file.getFilePath();
                return downloadFile(path);
            }

        } catch (Exception ex) {
            Utils.logger.error(ex.getLocalizedMessage());
        }

        return null; // Just in case, should not happen
    }

    /**
     * Sends an image to a chatID
     *
     * @param chatId The chatId for the chat
     * @param im     The {@link Image} we made
     */
    private void sendPhotoMsg(Long chatId, Image im) {
        try {
            ByteArrayOutputStream buff = new ByteArrayOutputStream();
            im.dumpToStream(buff);

            SendPhoto sendPhotoRequest = new SendPhoto();
            sendPhotoRequest.setChatId(chatId.toString());
            sendPhotoRequest.setPhoto(new InputFile(new ByteArrayInputStream(buff.toByteArray()), "out.png"));

            execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Utils.logger.error(ex.getLocalizedMessage());
        }
    }

    /**
     * Sends an image to a chatID
     *
     * @param chatId The chatId for the chat
     * @param URL    A String URL for the image to send (located on the web for instance)
     */
    private void sendPhotoMsg(Long chatId, String URL) {
        try {
            SendPhoto sendPhotoRequest = new SendPhoto();
            sendPhotoRequest.setChatId(chatId.toString());
            sendPhotoRequest.setPhoto(new InputFile(URL));
            execute(sendPhotoRequest);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        } catch (Exception ex) {
            Utils.logger.error(ex.getLocalizedMessage());
        }
    }

    /**
     * Sets the menu that appears automatically with the available commands
     * for every chats that the bot serves
     */
    public void setMenu() {
        List<BotCommand> commandsList = new ArrayList<>();
        commandsList.add(new BotCommand("help", "Display commands"));

        try {
            // Send the message, with a menu for all languages
            execute(new SetMyCommands(commandsList, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

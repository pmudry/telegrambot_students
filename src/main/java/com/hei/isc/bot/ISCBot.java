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
 * A simple bot facade for the ISC EPTM demo
 * Using API from https://github.com/rubenlagus/TelegramBots
 *
 * @author Pierre-André Mudry, Marc Pignat
 * @version 1.0
 */

public class ISCBot extends TelegramLongPollingBot {

    public final static String BOT_TOKEN = "INSERT YOUR TOKEN HERE";
    public final static String BOT_USER = "A super bot for doing stuff during the ISC visit day";

    HashMap<Long, Image> images = new HashMap<>();

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

            // We check if the update has a message and the message has text
            if (update.getMessage().hasText())
                handleTxtMsg(update);

            if (update.getMessage().hasPhoto())
                handlePhotoMsg(update);

            /*
             * Stickers are WEBP files and every file of this type is treated as such
             */
            if (update.getMessage().hasSticker()) {
                sendTextMessage(update.getMessage().getChatId(), "What a nice sticker \uD83D\uDC4D");
            }

            /*
             * Stuff which is not a WEBP file or a photo
             */
            if (update.getMessage().hasDocument()) {
                sendTextMessage(update.getMessage().getChatId(), "Try to drop the picture directly, I can't handle files \uD83D\uDE22");
            }
        }

        if (update.hasCallbackQuery()) {
            handleUpdateQuery(update);
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
     * Gives the user some help
     *
     * @param chatId The chat id
     */
    private void sendUserHelpMessage(Long chatId) {
        sendTextMessage(chatId, "I am your bot and I can do several things, try typing for instance:\n\n" +
            "/cat Display random cat facts \uD83D\uDC08\n" +
            "/hangman Play a hangman game \uD83C\uDFB2 \n" +
            "/btc Get Bitcoin ₿ past history\n" +
            "/help To get this help\n\n" +
            "You can also <b>send me photos</b> \uD83D\uDDBC and we will do something with them!");
    }

    /**
     * Sends a welcome message when starting the chat
     *
     * @param chatId The chat id
     */
    private void sendWelcomeMsg(Long chatId) {
        sendTextMessage(chatId, "Welcome here ! \uD83C\uDF89");
        sendSticker(chatId, "resources/isc_logo_256.webp");
        sendUserHelpMessage(chatId);
    }

    /**
     * Handles buttons when pressed under an image
     *
     * @param u The {@link Update} event we got
     */
    private void handleUpdateQuery(Update u) {
        CallbackQuery q = u.getCallbackQuery();
        String what = q.getData();
        Long id = q.getMessage().getChatId();
        Image im = images.get(id);

        // Get a default image if there is none
        if (im == null) {
            im = new Image("resources/astronaut.png");
        }

        // Work on a copy of the image
        Image out = new Image(im.w, im.h);

        // Handle the treatment
        switch (what) {
            // TODO Adding MEME generator would be nice but requires a different strategy as we need a text afterwards
            case "logo":
                final String logoPath = "resources/isc_logo_128.png";
                out = ImagePipeline.
                    create(im).
                    embedLogo(ImagePipeline.create(logoPath).getImage(), 0.9, 0.1)
                    .getImage();
                break;

            case "bw":
                out = ImagePipeline.
                    create(im).
                    bw().
                    getImage();
                break;

            case "threshold":
                out = ImagePipeline.
                    create(im).
                    threshold(127).
                    getImage();
                break;

            case "blur":
                out = ImagePipeline.
                    create(im).
                    blur(10).
                    getImage();
                break;

            case "sepia":
                out = ImagePipeline.
                    create(im).
                    sepia().
                    getImage();
                break;
        }

        // Send the image back
        sendPhotoMsg(id, out);

        // End the callback properly
        try {
            execute(new AnswerCallbackQuery(q.getId()));
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
     * Handles textual messages incoming from user in chat
     *
     * @param update The chat {@link Update} message
     */
    private void handleTxtMsg(Update update) {
        Utils.logger.info("Got text message");
        Long id = update.getMessage().getChatId();
        String text = update.getMessage().getText();

        if (text.charAt(0) == '/') {
            switch (text) {
                case "/start":
                    sendWelcomeMsg(id);
                    return;
                case "/btc":
                    Utils.logger.info("Sending BTC image");
                    sendPhotoMsg(id, "https://inf1.begincoding.net/wp-content/uploads/markus-spiske-L2cxSuKWbpo-unsplash.png");
                    return;
                case "/help":
                    sendUserHelpMessage(id);
                    return;
                default:
                    sendTextMessage(id, "This command is not available, sorry \uD83D\uDE14");
                    return;
            }
        }

        // Otherwise, reply the given text, with some decor
        SendMessage message = new SendMessage(); // Create a SendMessage object with mandatory fields
        message.setChatId(id.toString());
        message.enableHtml(true);
        message.setText("<b>" +
            update.getMessage().
                getText() + "</b> to you " + update.getMessage().
            getFrom().
            getFirstName());
        try {
            Utils.logger.info("Replying " + message.getText());
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    /**
     * Called to handle photos when incoming
     * From https://github.com/rubenlagus/TelegramBots/wiki/FAQ#how_to_get_picture
     *
     * @param update The {@link Update} event
     */
    private void handlePhotoMsg(Update update) {
        Utils.logger.info("A picture is coming !");

        SendMessage message = new SendMessage();
        message.setChatId(update.getMessage().getChatId().toString());
        message.setText("😅 Wouah... you sent me a picture. Let's choose what to do with it!");

        // First row
        InlineKeyboardButton b1 = new InlineKeyboardButton();
        b1.setText("Add logo");
        b1.setCallbackData("logo");

        InlineKeyboardButton b2 = new InlineKeyboardButton();
        b2.setText("Black and white");
        b2.setCallbackData("bw");

        InlineKeyboardButton b3 = new InlineKeyboardButton();
        b3.setText("Blur it");
        b3.setCallbackData("blur");

        List<InlineKeyboardButton> row1 = new ArrayList<>();
        row1.add(b1);
        row1.add(b2);
        row1.add(b3);

        List<InlineKeyboardButton> row2 = new ArrayList<>();

        // Second row
        InlineKeyboardButton b4 = new InlineKeyboardButton();
        b4.setText("Threshold");
        b4.setCallbackData("threshold");

        InlineKeyboardButton b5 = new InlineKeyboardButton();
        b5.setText("Sepia");
        b5.setCallbackData("sepia");
        row2.add(b4);
        row2.add(b5);

        List<List<InlineKeyboardButton>> k = new ArrayList<>();
        k.add(row1);
        k.add(row2);

        InlineKeyboardMarkup m = new InlineKeyboardMarkup();
        m.setKeyboard(k);
        message.setReplyMarkup(m);

        try {
            Utils.logger.info("Replying " + message.getText());
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

        // TODO comment this please, the use of the hashmap is not clear
        File photoFile = getPhotoFile(update);
        images.put(update.getMessage().getChatId(), new Image(photoFile));
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
            // Send the message
            execute(new SetMyCommands(commandsList, new BotCommandScopeDefault(), "fr"));
            execute(new SetMyCommands(commandsList, new BotCommandScopeDefault(), "en"));
            execute(new SetMyCommands(commandsList, new BotCommandScopeDefault(), "de"));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

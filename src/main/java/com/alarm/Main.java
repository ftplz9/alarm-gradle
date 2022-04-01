package com.alarm;

import it.tdlight.client.*;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class Main {
    private static SimpleTelegramClient client;
    private static final long rawoochatId = -1001744749792L;
    public static void main(String[] args) throws CantLoadLibrary, InterruptedException {

        Init.start();

        APIToken apiToken = APIToken.example();

        var settings = TDLibSettings.create(apiToken);

        var sessionPath = Paths.get("example-tdlight-session");
        settings.setDatabaseDirectoryPath(sessionPath.resolve("data"));
        settings.setDownloadedFilesDirectoryPath(sessionPath.resolve("downloads"));

        client = new SimpleTelegramClient(settings);

        var authenticationData = AuthenticationData.qrCode();

        client.addUpdateHandler(TdApi.UpdateAuthorizationState.class, Main::onUpdateAuthorizationState);

        client.addUpdateHandler(TdApi.UpdateNewMessage.class, Main::onUpdateNewMessage);

        client.start(authenticationData);

        client.waitForExit();
    }



    private static void onUpdateNewMessage(TdApi.UpdateNewMessage update) {
        var messageContent = update.message.content;

        String message;
        if (messageContent instanceof TdApi.MessageText messageText) {
            message = messageText.text.text;
        } else {
            message = String.format("(%s)", messageContent.getClass().getSimpleName());
        }

        client.send(new TdApi.GetChat(update.message.chatId), chatIdResult -> {
            TdApi.Chat chat = chatIdResult.get();
            String chatName = chat.title;
            long chatId = chat.id;

            if (chatId == -1001766138888L) {
                System.out.println(message + " " + chatId);
                List<String> cities = Arrays.asList("м_Київ", "Вінницька_область", "Дніпропетровська_область", "Рівненська_область");
                cities.forEach(c -> {
                    if (message.contains(c)) {
                        sendMessage(rawoochatId, message);
                    }
                });
            }
        });
    }

    private static void sendMessage(long chatId, String message) {
        TdApi.InlineKeyboardButton[] row = {new TdApi.InlineKeyboardButton("https://telegram.org?1", new TdApi.InlineKeyboardButtonTypeUrl()), new TdApi.InlineKeyboardButton("https://telegram.org?2", new TdApi.InlineKeyboardButtonTypeUrl()), new TdApi.InlineKeyboardButton("https://telegram.org?3", new TdApi.InlineKeyboardButtonTypeUrl())};
        TdApi.ReplyMarkup replyMarkup = new TdApi.ReplyMarkupInlineKeyboard(new TdApi.InlineKeyboardButton[][]{row, row, row});

        TdApi.InputMessageContent content = new TdApi.InputMessageText(new TdApi.FormattedText(message, null), false, true);
        client.send(new TdApi.SendMessage(chatId, 0, 0, null, replyMarkup, content), res -> {

        });
    }

    private static void onUpdateAuthorizationState(TdApi.UpdateAuthorizationState update) {
        var authorizationState = update.authorizationState;
        if (authorizationState instanceof TdApi.AuthorizationStateReady) {
            System.out.println("Logged in");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosing) {
            System.out.println("Closing");
        } else if (authorizationState instanceof TdApi.AuthorizationStateClosed) {
            System.out.println("Closed");
        } else if (authorizationState instanceof TdApi.AuthorizationStateLoggingOut) {
            System.out.println("Logging out");
        }
    }
}
//
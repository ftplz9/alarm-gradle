package com.alarm;

import com.coreoz.wisp.Scheduler;
import com.coreoz.wisp.schedule.Schedules;
import it.tdlight.client.*;
import it.tdlight.common.Init;
import it.tdlight.common.utils.CantLoadLibrary;
import it.tdlight.jni.TdApi;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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

        Scheduler scheduler = new Scheduler();

        scheduler.schedule(
                () -> sendMessage(rawoochatId, "10:00 - Всем доброе утро, утренняя перекличка"),
                Schedules.executeAt("10:00")
        );

        scheduler.schedule(
                () -> sendMessage(rawoochatId, "20:00 - Вечерняя перекличка. Всем хорошего вечера и спокойной ночи"),
                Schedules.executeAt("20:00")
        );

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
            long chatId = chat.id;

            if (chatId == rawoochatId && message.equalsIgnoreCase("/status")) {
                sendMessage(rawoochatId, "OK");
            }

            if (chatId == -1001766138888L) {
                List<String> cities = Arrays.asList("м_Київ", "Вінницька_область",
                        "Дніпропетровська_область", "Рівненська_область", "Київська_область");

                cities.forEach(c -> {
                    if (message.contains(c)) {
                        switch (c) {
                            case "м_Київ":
                                String str = message.concat("\n").concat("@Perv1t1n")
                                        .concat(" ").concat("@alexman03")
                                        .concat(" ").concat("@ftplz");
                                sendMessage(rawoochatId, str);
                                break;
                            default:
                                sendMessage(rawoochatId, message);
                        }

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
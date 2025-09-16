package me.marensovich.backend.telegramBot;


import lombok.Getter;
import me.marensovich.backend.telegramBot.command.CommandManager;
import me.marensovich.backend.telegramBot.update.UpdateHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collections;

@Component
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    @Getter private final TelegramClient telegramClient;
    @Getter private static Bot instance;
    @Autowired @Getter private CommandManager commandManager;
    @Autowired private UpdateHandler updateHandler;

    public Bot() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
        instance = this;
    }

    @Override
    public String getBotToken() {
        return System.getenv("BOT_TOKEN");
    }

    @Override
    public LongPollingUpdateConsumer getUpdatesConsumer() {
        return this;
    }

    @Override
    public void consume(Update update) {
        try {
            updateHandler.handleUpdate(update);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
        if (update.hasMessage() && update.getMessage().hasText()) {
            KeyboardButton webAppButton = KeyboardButton
                    .builder()
                    .text("Открыть игру 🎮")
                    .webApp(new WebAppInfo("https://abcd1234.ngrok-free.app"))
                    .build();

            KeyboardRow row = new KeyboardRow();
            row.add(webAppButton);

            ReplyKeyboardMarkup keyboard = ReplyKeyboardMarkup
                    .builder()
                    .keyboard(Collections.singletonList(row))
                    .resizeKeyboard(true)
                    .build();

            SendMessage msg = SendMessage.builder()
                    .chatId(update.getMessage().getChatId())
                    .text("Выбери действие:")
                    .replyMarkup(keyboard)
                    .build();

            try {
                telegramClient.execute(msg);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }
    }
    @AfterBotRegistration
    public void afterRegistration(BotSession botSession) {
        System.out.println("Registered bot running state is: " + botSession.isRunning());
    }


    public ReplyKeyboardRemove removeKeyboard(){
        ReplyKeyboardRemove keyboardRemove = ReplyKeyboardRemove.builder()
            .removeKeyboard(true)
            .selective(false)
            .build();
        return keyboardRemove;
    }

    public void sendErrorMessage(Long chatId, String text) {
        Bot.getInstance().showBotAction(chatId, ActionType.TYPING);
        try {
            SendMessage message = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
            telegramClient.execute(message);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void showBotAction(Long chatId, ActionType actionType) {
        SendChatAction chatAction = SendChatAction.builder()
            .chatId(String.valueOf(chatId))
            .action(actionType.toString())
            .build();

        try {
            telegramClient.execute(chatAction);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

}

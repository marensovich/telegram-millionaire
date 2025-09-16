package me.marensovich.bot.telegramBot;


import lombok.Getter;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.BotSession;
import org.telegram.telegrambots.longpolling.interfaces.LongPollingUpdateConsumer;
import org.telegram.telegrambots.longpolling.starter.AfterBotRegistration;
import org.telegram.telegrambots.longpolling.starter.SpringLongPollingBot;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.api.objects.webapp.WebAppInfo;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.Collections;

@Component
public class Bot implements SpringLongPollingBot, LongPollingSingleThreadUpdateConsumer {
    @Getter private final TelegramClient telegramClient;

    public Bot() {
        telegramClient = new OkHttpTelegramClient(getBotToken());
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

}

package me.marensovich.backend.telegramBot.update;

import me.marensovich.backend.services.UserService;
import me.marensovich.backend.telegramBot.Bot;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class UpdateHandler {

    @Autowired
    UserService userService;

    public void handleUpdate(Update update) throws TelegramApiException {

        if (update.hasMessage() || update.hasCallbackQuery()) {
            if (update.hasMessage()) {
                if (userService.checkAllUserExist(update.getMessage().getFrom().getId())) {
                    if (!Bot.getInstance().getCommandManager().hasActiveCommand(update.getMessage().getFrom().getId())) {
                        if (update.getMessage().hasText()) {
                            if (update.getMessage().getText().startsWith("/")) {
                                if (!Bot.getInstance().getCommandManager().executeCommand(update)) {
                                    String text = "Команда не распознана, проверьте правильность написания команды. \n\n" +
                                            "Команды с доп. параметрами указаны отдельной графой в информации. Подробнее в /help.";
                                    SendMessage message = SendMessage.builder()
                                            .chatId(update.getMessage().getChatId().toString())
                                            .text(text)
                                            .build();
                                    try {
                                        Bot.getInstance().getTelegramClient().execute(message);
                                    } catch (TelegramApiException e) {
                                        throw new RuntimeException(e);
                                    }
                                    return;
                                }
                            }
                        }
                    } else {
                        Bot.getInstance().getCommandManager().executeCommand(update);
                        return;
                    }
                } else {
                    userService.addUser(update.getMessage().getFrom().getId());
                }
            }
            if (update.hasCallbackQuery()) {
                if (userService.checkAllUserExist(update.getCallbackQuery().getFrom().getId())) {
                    boolean handled = Bot.getInstance().getCallbackHandler().handleCallback(update);
                    if (!handled) {
                        SendMessage errorMsg = SendMessage.builder()
                                .chatId(update.getCallbackQuery().getMessage().getChatId().toString())
                                .text("Действие не распознано, попробуйте ещё раз")
                                .build();
                        try {
                            Bot.getInstance().getTelegramClient().execute(errorMsg);
                        } catch (TelegramApiException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else {
                    userService.addUser(update.getCallbackQuery().getFrom().getId());
                }
            }
        }
    }
}
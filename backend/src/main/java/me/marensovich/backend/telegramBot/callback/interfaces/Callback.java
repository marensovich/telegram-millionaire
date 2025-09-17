package me.marensovich.backend.telegramBot.callback.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Callback {
    String getCallbackData();
    void handle(Update update);
}

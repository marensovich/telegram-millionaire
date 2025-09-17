package me.marensovich.backend.telegramBot.callback.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface PrefixCallback {
    String getPrefixCallbackData();
    void handle(Update update);
}

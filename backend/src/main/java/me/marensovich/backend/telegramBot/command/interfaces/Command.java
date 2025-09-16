package me.marensovich.backend.telegramBot.command.interfaces;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface Command {
    String getName();
    void execute(Update update);
    boolean isAdminRequired();
}

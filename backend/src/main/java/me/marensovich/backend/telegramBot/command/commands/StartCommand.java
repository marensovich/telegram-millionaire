package me.marensovich.backend.telegramBot.command.commands;

import me.marensovich.backend.telegramBot.command.interfaces.Command;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class StartCommand implements Command {
    @Override
    public String getName() {
        return "/start";
    }

    @Override
    public void execute(Update update) {

    }

    @Override
    public boolean isAdminRequired() {
        return false;
    }
}

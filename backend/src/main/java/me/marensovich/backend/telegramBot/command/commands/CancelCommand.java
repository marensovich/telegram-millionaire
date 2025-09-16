package me.marensovich.backend.telegramBot.command.commands;

import me.marensovich.backend.telegramBot.Bot;
import me.marensovich.backend.telegramBot.command.interfaces.Command;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ActionType;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CancelCommand implements Command {
    @Override
    public String getName() {
        return "/cancel";
    }

    @Override
    public void execute(Update update) {
        if (Bot.getInstance().getCommandManager().hasActiveCommand(update.getMessage().getFrom().getId())){
            Bot.getInstance().getCommandManager().unsetActiveCommand(update.getMessage().getFrom().getId());
            Bot.getInstance().showBotAction(update.getMessage().getFrom().getId(), ActionType.TYPING);
            SendMessage msg = SendMessage.builder()
                .chatId(update.getMessage().getChatId().toString())
                .replyMarkup(Bot.getInstance().removeKeyboard())
                .text("Активная команда была удалена.")
                .build();
            try {
                Bot.getInstance().getTelegramClient().execute(msg);
            } catch (TelegramApiException e) {
                Bot.getInstance().sendErrorMessage(update.getMessage().getChatId(), "⚠️ Ошибка при работе бота, обратитесь к администратору");
                Bot.getInstance().getCommandManager().unsetActiveCommand(update.getMessage().getChatId());
                throw new RuntimeException(e);
            }
            return;
        }
        Bot.getInstance().showBotAction(update.getMessage().getFrom().getId(), ActionType.TYPING);
        SendMessage msg = SendMessage.builder()
            .chatId(update.getMessage().getChatId().toString())
            .text("❌ Нет активных команд")
            .build();
        try {
            Bot.getInstance().getTelegramClient().execute(msg);
        } catch (TelegramApiException e) {
            Bot.getInstance().sendErrorMessage(update.getMessage().getChatId(), "⚠️ Ошибка при работе бота, обратитесь к администратору");
            Bot.getInstance().getCommandManager().unsetActiveCommand(update.getMessage().getChatId());
            throw new RuntimeException(e);
        }
    }

    @Override
    public boolean isAdminRequired() {
        return false;
    }
}

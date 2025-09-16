package me.marensovich.backend.telegramBot.command;

import lombok.extern.slf4j.Slf4j;
import me.marensovich.backend.telegramBot.Bot;
import me.marensovich.backend.telegramBot.command.commands.CancelCommand;
import me.marensovich.backend.telegramBot.command.interfaces.Command;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class CommandManager {
    private final Map<String, Command> commands = new HashMap<>();
    private final Map<Long, Command> activeCommands = new HashMap<>();


    @Autowired
    public CommandManager(List<Command> commandList) {
        commandList.forEach(this::registerCommand);
    }

    private void registerCommand(Command command) {
        commands.put(command.getName().toLowerCase(), command);
    }


    public boolean executeCommand(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return false;
        }
        if (hasActiveCommand(update.getMessage().getFrom().getId())){
            Command activeCommand = activeCommands.get(update.getMessage().getFrom().getId());
            if (update.hasMessage() && update.getMessage().hasText()){
                if (update.getMessage().getText().startsWith("/start")){
                    new CancelCommand().execute(update);
                    return true;
                }

                sendActiveCommandMessage(update.getMessage().getChatId(), activeCommand.getName());
                return true;
            }
        }

        if (update.getMessage().hasText()){
            String messageText = update.getMessage().getText().trim();
            String[] parts = messageText.split(" ");
            String commandKey = parts[0];

            if (commands.containsKey(commandKey)) {
                commands.get(commandKey).execute(update);
                return true;
            }
        }
        return false;
    }

    private String extractCommand(String text) {
        if (text.contains("@")) {
            text = text.substring(0, text.indexOf("@"));
        }
        return text.trim().toLowerCase();
    }

    private void sendActiveCommandMessage(Long chatId, String commandName) {
        String reply = """
        Бот обрабатывает отправленную вами команду %command%
        
        В случае если это вы хотите прекратить выполнение команды - отправьте /cancel
        """;
        sendMessage(chatId, reply.replace("%command%", commandName));
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage msg = SendMessage
                .builder()
                .chatId(chatId.toString())
                .text(text)
                .build();
        try {
            Bot.getInstance().getTelegramClient().execute(msg);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void setActiveCommand(Long userId, Command command) {
        log.debug("Активная команда " + command.getName() + " закреплена за пользователем " + userId);
        activeCommands.put(userId, command);
    }

    public void unsetActiveCommand(Long userId) {
        log.debug("Активная команда пользователя " + userId + " была очищена");
        activeCommands.remove(userId);
    }

    public boolean hasActiveCommand(Long userId) {
        return activeCommands.containsKey(userId);
    }

    public Command getActiveCommand(Long userId) {
        return activeCommands.get(userId);
    }

}

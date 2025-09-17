package me.marensovich.backend.telegramBot.callback;

import me.marensovich.backend.telegramBot.callback.interfaces.Callback;
import me.marensovich.backend.telegramBot.callback.interfaces.PrefixCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class CallbackHandler {

    private final Map<String, Callback> handlers = new HashMap<>();

    private final Map<String, PrefixCallback> prefixHandlers = new HashMap<>();

    @Autowired
    public CallbackHandler(List<Callback> handlers, List<PrefixCallback> prefixHandlers) {
        handlers.forEach(this::registerHandler);
        prefixHandlers.forEach(this::registerPrefixHandler);
    }

    private void registerHandler(Callback handler) {
        handlers.put(handler.getCallbackData().toLowerCase(), handler);
    }

    private void registerPrefixHandler(PrefixCallback handler) {
        prefixHandlers.put(handler.getPrefixCallbackData().toLowerCase(), handler);
    }

    public boolean handleCallback(Update update){
        if (!update.hasCallbackQuery()) {
            return false;
        }
        String callbackData = update.getCallbackQuery().getData();

        Callback handler = handlers.get(callbackData);
        if (handler != null) {
            handler.handle(update);
            return true;
        }


        for (Map.Entry<String, PrefixCallback> entry : prefixHandlers.entrySet()) {
            if (callbackData.startsWith(entry.getKey())) {
                entry.getValue().handle(update);
                return true;
            }
        }
        return false;
    }
}
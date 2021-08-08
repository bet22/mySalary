package ru.bet;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class Bot extends TelegramLongPollingBot {
    private final SalaryRepo salaryRepo;

    @Value("${telegram.bot.token}")
    private String botToken;

    @Value("${telegram.bot.name}")
    private String botName;

    @Override
    public void onUpdateReceived(@Nonnull Update update) {
        //Если есть сообщение и оно из нашего чата
        if (update.hasMessage()) {
            val message = update.getMessage();
            if (message.getChatId() == 886843243 && message.hasText()) {
                Date date = new Date((long) message.getDate() * 1000);
                try {
                    if (message.getText().startsWith("$")) {
                        val substring = message.getText().substring(1);
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setText(substring);
                        sendMessage.setChatId(message.getChatId().toString());
                        Salary salary = new Salary(substring, message.getDate());
                        salaryRepo.save(salary);
                        Iterable<Salary> all = salaryRepo.findAll();
                        ArrayList<String> objects = new ArrayList<>();
                        all.forEach(s -> {
                            objects.add(s.getSalary());
                        });
                        sendMessage.setText(String.join(", " , objects));
                        execute(sendMessage);
                    }
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public String getBotUsername() {
        return botName;
    }

    @Override
    public String getBotToken() {
        return botToken;
    }
}

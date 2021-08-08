package ru.bet;

import lombok.RequiredArgsConstructor;
import lombok.val;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.Nonnull;
import java.time.Instant;
import java.time.ZoneId;
import java.util.ArrayList;

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
                try {
                    val text = message.getText();
                    //Если символ $ - значит мы добавляем число в дб
                    if (text.startsWith("$")) {
                        val substring = text.substring(1);
                        SendMessage sendMessage = addSendMessage(message, "add " + substring);
                        Salary salary = new Salary(substring, message.getDate());
                        salaryRepo.save(salary);
                        execute(sendMessage);
                    }
                    //Считываем из дб данные
                    if (text.startsWith("#")) {
                        val substring = text.substring(1);
                        val salaries = new ArrayList<Salary>();
                        val all = salaryRepo.findAll();
                        all.forEach(s -> {
                            val localDate = Instant.ofEpochSecond(s.getDate()).atZone(ZoneId.systemDefault()).toLocalDate();
                            if (localDate.getMonthValue() == Integer.parseInt(substring))
                                salaries.add(s);
                        });
                        var sum = 0;
                        for (Salary salary : salaries) {
                            sum += Integer.parseInt(salary.getSalary());
                        }
                        SendMessage sendMessage = addSendMessage(message, Integer.toString(sum));
                        execute(sendMessage);
                    }
                } catch (TelegramApiException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Nonnull
    private SendMessage addSendMessage(Message message, String substring) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(substring);
        sendMessage.setChatId(message.getChatId().toString());
        return sendMessage;
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

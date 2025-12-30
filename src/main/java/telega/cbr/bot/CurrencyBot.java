package telega.cbr.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class CurrencyBot extends TelegramLongPollingBot {

    @Override
    public String getBotUsername() {
        return "FinancialCourses_bot";
    }

    @Override
    public String getBotToken() {
        return "8328020813:AAGwFbLPolaScZAymi13xp9Y9HaFaRtX2-8";
    }

    @Override
    public void onUpdateReceived(org.telegram.telegrambots.meta.api.objects.Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();

            try {
                handleUserMessage(chatId, text);
            } catch (Exception e) {
                sendTextMessage(chatId, "Произошла ошибка. Начните с /start");
                UserSession.clear(chatId);
                e.printStackTrace();
            }
        }
    }

    private void handleUserMessage(Long chatId, String text) {
        DialogState state = UserSession.getState(chatId);

        switch (state) {
            case START:
                if (text.equals("/start")) {
                    sendTextMessage(chatId, "Хотите увидеть все валюты или одну?\nВыберите: *Все* или *Одну*", true);
                    UserSession.setState(chatId, DialogState.CHOSE_CURRENCY_TYPE);
                } else {
                    sendTextMessage(chatId, "Выберите команду для запуска.");
                }
                break;

            case CHOSE_CURRENCY_TYPE:
                if (text.equalsIgnoreCase("все")) {
                    sendTextMessage(chatId, "Введите дату в формате ДД.ММ.ГГГГ (например, 02.03.2024):");
                    UserSession.setState(chatId, DialogState.CHOSE_DATE);
                } else if (text.equalsIgnoreCase("одну")) {
                    sendTextMessage(chatId, "Введите код валюты (например, USD, EUR, GBP):");
                    UserSession.setState(chatId, DialogState.CHOSE_CURRENCY_CODE);
                } else {
                    sendTextMessage(chatId, "Пожалуйста, выберите: *Все* или *Одну*", true);
                }
                break;

            case CHOSE_CURRENCY_CODE:
                String code = text.trim().toUpperCase();
                if (code.matches("[A-Z]{3}")) {
                    UserSession.setCurrencyCode(chatId, code);
                    sendTextMessage(chatId, "Введите дату в формате ДД.ММ.ГГГГ:");
                    UserSession.setState(chatId, DialogState.CHOSE_DATE);
                } else {
                    sendTextMessage(chatId, "Неверный код валюты. Введите 3 заглавные буквы (например, USD):");
                }
                break;

            case CHOSE_DATE:
                try {
                    // Поддерживаем формат ДД.ММ.ГГГГ
                    LocalDate date = LocalDate.parse(text, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    UserSession.setDate(chatId, date);

                    // Формируем и отправляем результат
                    sendResult(chatId);
                    UserSession.clear(chatId); // завершаем диалог
                } catch (DateTimeParseException e) {
                    sendTextMessage(chatId, "Неверный формат даты. Пример: 02.03.2024");
                }
                break;
        }
    }

    private void sendResult(Long chatId) {
        LocalDate date = UserSession.getDate(chatId);
        String currencyCode = UserSession.getCurrencyCode(chatId);

        try {
            String xml = CbrClient.fetchCurrencyXml(date); // ← реализуем ниже
            ValCurs valCurs = CurrencyParser.parseFromXml(xml);

            String message;
            if (currencyCode != null) {
                // Одна валюта
                Valute valute = valCurs.getValutes().stream()
                        .filter(v -> v.getCharCode().equals(currencyCode))
                        .findFirst()
                        .orElse(null);

                if (valute != null) {
                    double rate = Double.parseDouble(valute.getValue().replace(',', '.'));
                    message = String.format(
                            "%s (%s): %.2f RUB",
                            valute.getName(),
                            valute.getCharCode(),
                            rate
                    );
                } else {
                    message = "Валюта " + currencyCode + " не найдена на указанную дату.";
                }
            } else {
                // Все валюты
                message = CurrencyParser.formatRatesAsMessage(valCurs);
            }

            sendTextMessage(chatId, message);
        } catch (Exception e) {
            sendTextMessage(chatId, "Не удалось получить курсы на " + date + ". Попробуйте позже.");
            e.printStackTrace();
        }
    }

    private void sendTextMessage(Long chatId, String text) {
        sendTextMessage(chatId, text, false);
    }

    private void sendTextMessage(Long chatId, String text, boolean markdown) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        if (markdown) {
            sendMessage.enableMarkdown(true);
        }
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

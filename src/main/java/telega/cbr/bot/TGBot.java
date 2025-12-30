package telega.cbr.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import static telega.cbr.bot.DialogState.*;

public class TGBot extends TelegramLongPollingBot {
    private static volatile long requestCount = 0;

    @Override
    public void onUpdateReceived(org.telegram.telegrambots.meta.api.objects.Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            String text = message.getText();
            long currentCount = ++requestCount;
            if (currentCount % 10 == 0) {
                System.out.println("📊 Обработано " + currentCount + " запросов от пользователей");
            }
            try {
                handleUserMessage(chatId, text);
            } catch (Exception e) {
                sendTextMessage(chatId, "Произошла ошибка. Выберите команду снова.");
                UserSession.clear(chatId);
                e.printStackTrace();
            }
        }
    }

    private void handleUserMessage(Long chatId, String text) {
        if (text.equals("/listbreeds")) {
            sendTextMessage(chatId, "affenpinscher\n" + "african\n" + "airedale\n" + "akita\n" + "appenzeller\n" + "australian\n" +
                    "bakharwal\n" + "basenji\n" + "beagle\n" + "bluetick\n" + "borzoi\n" + "bouvier\n" + "boxer\n" + "brabancon\n" +
                    "briard\n" + "buhund\n" + "bulldog\n" + "bullterrier\n" + "cattledog\n" + "cavapoo\n" + "chihuahua\n" + "chippiparai\n" +
                    "chow\n" + "clumber\n" + "cockapoo\n" + "collie\n" + "coonhound\n" + "corgi\n" + "cotondetulear\n" + "dachshund\n" +
                    "dalmatian\n" + "dane\n" + "danish\n" + "deerhound\n" + "dhole\n" + "dingo\n" + "doberman\n" + "elkhound\n" + "entlebucher\n" +
                    "eskimo\n" + "finnish\n" + "frise\n" + "gaddi\n" + "german\n" + "greyhound\n" + "groenendael\n" + "havanese\n" + "hound\n" +
                    "husky\n" + "keeshond\n" + "kelpie\n" + "kombai\n" + "komondor\n" + "kuvasz\n" + "labradoodle\n" + "labrador\n" + "leonberg\n" +
                    "lhasa\n" + "malamute\n" + "malinois\n" + "maltese\n" + "mastiff\n" + "mexicanhairless\n" + "mix\n" + "mountain\n" +
                    "mudhol\n" + "newfoundland\n" + "otterhound\n" + "ovcharka\n" + "papillon\n" + "pariah\n" + "pekinese\n" + "pembroke\n" +
                    "pinscher\n" + "pitbull\n" + "pointer\n" + "pomeranian\n" + "poodle\n" + "pug\n" + "puggle\n" + "pyrenees\n" + "rajapalayam\n" +
                    "redbone\n" + "retriever\n" + "ridgeback\n" + "rottweiler\n" + "rough\n" + "saluki\n" + "samoyed\n" + "schipperke\n" +
                    "schnauzer\n" + "segugio\n" + "setter\n" + "sharpei\n" + "sheepdog\n" + "shiba\n" + "shihtzu\n" + "spaniel\n" + "spitz\n" +
                    "springer\n" + "stbernard\n" + "terrier\n" + "tervuren\n" + "vizsla\n" + "waterdog\n" + "weimaraner\n" + "whippet\n" + "wolfhound");
            return;
        } else if (text.equals("/dogbreed")) {
            sendTextMessage(chatId, "Введите название породы (пример: husky, hound, bulldog):");
            UserSession.setState(chatId, DialogState.AWAITING_DOG_BREED);
            return;
        } else if (text.equals("/currencies")) {
            sendAllCurrenciesList(chatId);
            return;
        }
        DialogState state = UserSession.getState(chatId);
        switch (state) {
            case START:
                if (text.equals("/start")) {
                    sendTextMessage(chatId, "Хотите увидеть все валюты или одну?\nОтветьте: *Все* или *Одну*", true);
                    UserSession.setState(chatId, DialogState.CHOSE_CURRENCY_TYPE);
                } else {
                    sendTextMessage(chatId, "Напишите /start для начала.");
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
                    sendTextMessage(chatId, "Пожалуйста, напишите: *Все* или *Одну*", true);
                }
                break;

            case CHOSE_CURRENCY_CODE:
                String code = text.trim().toUpperCase();
                if (code.matches("[A-Z]{3}")) {
                    UserSession.setCurrencyCode(chatId, code);
                    sendTextMessage(chatId, "Введите дату в формате ДД.ММ.ГГГГ:");
                    UserSession.setState(chatId, DialogState.CHOSE_DATE);
                } else {
                    sendTextMessage(chatId, "Неверный код валюты. Пример: USD");
                }
                break;

            case CHOSE_DATE:
                try {
                    LocalDate date = LocalDate.parse(text, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                    UserSession.setDate(chatId, date);
                    sendResult(chatId);
                } catch (DateTimeParseException e) {
                    sendTextMessage(chatId, "Неверный формат даты. Пример: 02.03.2024");
                }
                break;
            case AWAITING_DOG_BREED:
                String breed = text.trim().toLowerCase();
                try {
                    String imageUrl = LogicDogBot.urlGet(breed);
                    sendTextMessage(chatId, imageUrl);
                } catch (Exception e) {
                    sendTextMessage(chatId, "Не удалось найти породу '" + breed + "'.\nПопробуйте из списка: /listbreeds");
                }
                UserSession.clear(chatId); // завершаем диалог
                break;
            default:
                sendTextMessage(chatId, LogicDogBot.urlGet(text));
        }
    }


    private void sendResult(Long chatId) {
        LocalDate date = UserSession.getDate(chatId);
        String currencyCode = UserSession.getCurrencyCode(chatId);

        try {
            String xml = CbrClient.fetchCurrencyXml(date);
            ValCurs valCurs = CurrencyParser.parseFromXml(xml);

            String message;
            if (currencyCode != null) {
                var valute = valCurs.getValutes().stream()
                        .filter(v -> v.getCharCode().equals(currencyCode))
                        .findFirst()
                        .orElse(null);

                if (valute != null) {
                    double rate = Double.parseDouble(valute.getValue().replace(',', '.'));
                    message = String.format("%s (%s): %.2f RUB", valute.getName(), valute.getCharCode(), rate);
                } else {
                    message = "Валюта " + currencyCode + " не найдена.";
                }
            } else {
                message = CurrencyParser.formatRatesAsMessage(valCurs);
            }

            sendTextMessage(chatId, message);
            UserSession.clear(chatId);

        } catch (Exception e) {
            sendTextMessage(chatId, "❌ Не удалось получить курсы на " + date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) +
                    ".\n• Убедитесь, что дата — рабочий день\n• Попробуйте позже");
            UserSession.clear(chatId);
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

    @Override
    public String getBotUsername() {
        return "FinancialCourses_bot";
    }

    @Override
    public String getBotToken() {
        return "8328020813:AAGwFbLPolaScZAymi13xp9Y9HaFaRtX2-8";
    }

    private void sendAllCurrenciesList(Long chatId) {
        try {
            LocalDate today = LocalDate.now();
            String xml = CbrClient.fetchCurrencyXml(today);
            ValCurs valCurs = CurrencyParser.parseFromXml(xml);

            if (valCurs.getValutes() == null || valCurs.getValutes().isEmpty()) {
                LocalDate yesterday = today.minusDays(1);
                xml = CbrClient.fetchCurrencyXml(yesterday);
                valCurs = CurrencyParser.parseFromXml(xml);
            }

            if (valCurs.getValutes() == null) {
                sendTextMessage(chatId, "Не удалось загрузить список валют. Попробуйте позже.");
                return;
            }

            StringBuilder sb = new StringBuilder("Доступные валюты:\n\n");
            for (Valute valute : valCurs.getValutes()) {
                sb.append(valute.getCharCode())
                        .append(" — ")
                        .append(valute.getName())
                        .append("\n");
            }

            sendTextMessage(chatId, sb.toString().trim());

        } catch (Exception e) {
            sendTextMessage(chatId, "❌ Ошибка при получении списка валют.");
            e.printStackTrace();
        }
    }
}
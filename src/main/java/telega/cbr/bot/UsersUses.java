import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import telega.cbr.bot.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class UsersUses {
//    public static void main(String[] args) throws IOException {
//
//        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
//        System.out.println("Курс каких валют Вы хотитe посмотреть? ");
//        System.out.println("1. Курс USD/RUR");
//        System.out.println("2. Курс всех валют к ₽");
//        int firstValue = Integer.parseInt(reader.readLine());
//        if (firstValue == 1) {
//            System.out.println("Введите за какую дату необходимо отобразить курс валюты:");
//            System.out.print("В формате DD/MM/YYYY:");
//            String date = reader.readLine();
//            String usd = "https://cbr.ru/scripts/XML_dynamic.asp?date_req1=01/01/2012&date_req2=" + date + "&VAL_NM_RQ=R01235";
//            String result = xml(usd);
//            int start = result.lastIndexOf("<Value>");
//            int end = result.lastIndexOf("</Value>");
//            String resShow = result.substring(start + 7, end - 2);
//            System.out.println("Курс доллара за этот день составил " + resShow + "₽");
//        } else if (firstValue == 2) {
//            System.out.println("Введите желаемую дату в формате DD/MM/YYYY:");
//            String date = reader.readLine();
//            String allValute = "http://www.cbr.ru/scripts/XML_daily.asp?date_req=" + date;
//            String result = xml(allValute);
//            int start = result.lastIndexOf("<Name>");
//            int end = result.lastIndexOf("</Name>");
//            String resShow = result.substring(start + 6, end);
//            System.out.println(resShow);
//        } else System.out.println("Введено некорректное значение. Повторите вызов программы");
//
//
//    }


    public static String xml(String url) throws IOException {
        StringBuilder res = new StringBuilder();
        String line;
        URLConnection urlConn = new URL(url).openConnection();
        try
                (BufferedReader br = new BufferedReader(new InputStreamReader(urlConn.getInputStream()))) {
            while ((line = br.readLine()) != null) res.append(line);
        }
        return res.toString();
    }
}


package telega.cbr.bot;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CbrClient {

    public static String fetchCurrencyXml(LocalDate date) throws IOException {
        // Формат даты в URL: dd/MM/yyyy
        String formattedDate = date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        String urlString = "https://www.cbr.ru/scripts/XML_daily.asp?date_req=" + formattedDate;

        URL url = new URL(urlString);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Java Currency Bot");

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(conn.getInputStream(), "windows-1251"))) {
            return reader.lines().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append).toString();
        }
    }
}
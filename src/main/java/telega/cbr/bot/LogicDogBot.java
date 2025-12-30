package telega.cbr.bot;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;

import java.io.IOException;
import java.util.Scanner;

public class LogicDogBot {
    public static String urlGet(String breed) {

        Scanner scanner = new Scanner(System.in);
        String url = "https://dog.ceo/api/breed/" + breed.toLowerCase() + "/images/random";

        ObjectMapper mapper = new ObjectMapper();
        try {
            CloseableHttpClient client = HttpClients.createDefault();
            HttpGet request = new HttpGet(url);
            CloseableHttpResponse response = client.execute(request);
            Json answer = mapper.readValue(response.getEntity().getContent(), Json.class);
            String imageUrl = answer.message;
            if (answer.status.equals("success")) {
                return imageUrl;
            } else return "Попробуй другое название породы";
        } catch (IOException e) {
            return "В данный момент сервер не отвечает";
        }


    }
}


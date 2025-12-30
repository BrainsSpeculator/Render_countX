package telega.cbr.bot;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;

import java.io.StringReader;
import java.util.List;
import java.util.Locale;

public class CurrencyParser {

    public static ValCurs parseFromXml(String xml) throws Exception {
        JAXBContext context = JAXBContext.newInstance(ValCurs.class);
        Unmarshaller unmarshaller = context.createUnmarshaller();
        return (ValCurs) unmarshaller.unmarshal(new StringReader(xml));
    }

    public static String formatRatesAsMessage(ValCurs valCurs) {
        StringBuilder sb = new StringBuilder();
        List<Valute> valutes = valCurs.getValutes();

        for (Valute valute : valutes) {
            try {
                double rate = Double.parseDouble(valute.getValue().replace(',', '.'));
                String formattedRate = String.format(Locale.ENGLISH, "%.2f", rate);
                sb.append(valute.getName()).append(" : ").append(formattedRate).append("\n");
            } catch (NumberFormatException e) {
                sb.append(valute.getName()).append(" : недоступен\n");
            }
        }

        return sb.toString().trim();
    }
}
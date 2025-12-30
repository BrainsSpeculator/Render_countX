package telega.cbr.bot;

import jakarta.xml.bind.annotation.*;

import java.util.List;

@XmlRootElement(name = "ValCurs")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValCurs {
    @XmlAttribute(name = "Date")
    private String date;

    @XmlAttribute(name = "name")
    private String name;

    @XmlElement(name = "Valute")
    private List<Valute> valutes;

    // Геттеры
    public String getDate() { return date; }
    public String getName() { return name; }
    public List<Valute> getValutes() { return valutes; }

    @Override
    public String toString() {
        return "telega.cbr.bot.ValCurs{date='" + date + "', name='" + name + "', valutes=" + valutes + "}";
    }
}
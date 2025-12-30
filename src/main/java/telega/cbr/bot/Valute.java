package telega.cbr.bot;

import jakarta.xml.bind.annotation.*;

@XmlAccessorType(XmlAccessType.FIELD)
public class Valute {
    @XmlAttribute(name = "ID")
    private String id;
    @XmlElement(name = "NumCode")
    private String numCode;
    @XmlElement(name = "CharCode")
    private String charCode;
    @XmlElement(name = "Nominal")
    private int nominal;
    @XmlElement(name = "Name")
    private String name;
    @XmlElement(name = "Value")
    private String value; // Используем String, т.к. разделитель — запятая
    @XmlElement(name = "VunitRate")
    private String vunitRate;

    public String getId() { return id; }
    public String getNumCode() { return numCode; }
    public String getCharCode() { return charCode; }
    public int getNominal() { return nominal; }
    public String getName() { return name; }
    public String getValue() { return value; }
    public String getVunitRate() { return vunitRate; }

    @Override
    public String toString() {
        return String.format(
                "telega.cbr.bot.Valute{id='%s', charCode='%s', name='%s', nominal=%d, value='%s'}",
                id, charCode, name, nominal, value
        );
    }
}
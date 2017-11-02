package ru.anton.hyscanconverter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class TestXmlClass {
    @XmlElement
    public int age;
    @XmlElement
    public String name;
}

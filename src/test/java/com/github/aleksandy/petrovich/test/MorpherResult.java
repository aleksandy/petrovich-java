package com.github.aleksandy.petrovich.test;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(
    name="GetXmlResult",
    namespace="http://morpher.ru/"
)
@XmlAccessorType(XmlAccessType.FIELD)
public class MorpherResult {

    @XmlElement(name="Р")
    String genitive;

    @XmlElement(name="Д")
    String dative;

    @XmlElement(name="В")
    String accusative;

    @XmlElement(name="Т")
    String instrumental;

    @XmlElement(name="П")
    String prepositional;

    @XmlElement(name="ФИО")
    FIO fio;


    public String getGenitive() {
        return this.genitive;
    }

//    @XmlElement(name="Р")
    public void setGenitive(String genitive) {
        this.genitive = genitive;
    }

    public String getDative() {
        return this.dative;
    }

//    @XmlElement(name="Д")
    public void setDative(String dative) {
        this.dative = dative;
    }

    public String getAccusative() {
        return this.accusative;
    }

//    @XmlElement(name="В")
    public void setAccusative(String accusative) {
        this.accusative = accusative;
    }

    public String getInstrumental() {
        return this.instrumental;
    }

//    @XmlElement(name="Т")
    public void setInstrumental(String instrumental) {
        this.instrumental = instrumental;
    }

    public String getPrepositional() {
        return this.prepositional;
    }

//    @XmlElement(name="П")
    public void setPrepositional(String prepositional) {
        this.prepositional = prepositional;
    }

    public FIO getFio() {
        return this.fio;
    }

//    @XmlElement(name="ФИО")
    public void setFio(FIO fio) {
        this.fio = fio;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append("Result[")
            .append("Р: ").append(getGenitive()).append("; ")
            .append("Д: ").append(getDative()).append("; ")
            .append("В: ").append(getAccusative()).append("; ")
            .append("Т: ").append(getInstrumental()).append("; ")
            .append("П: ").append(getPrepositional()).append("; ")
            .append(getFio()).append("]\n")
            .toString();
    }

}

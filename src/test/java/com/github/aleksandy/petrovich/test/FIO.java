package com.github.aleksandy.petrovich.test;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(
    name="ФИО",
    namespace="http://morpher.ru/"
)
@XmlAccessorType(XmlAccessType.FIELD)
public class FIO {

    @XmlElement(name="Ф")
    String lastname;

    @XmlElement(name="И")
    String firstname;

    @XmlElement(name="О")
    String middlename;

    public String getFirstname() {
        return this.firstname;
    }

//    @XmlElement(name="И")
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return this.lastname;
    }

//    @XmlElement(name="Ф")
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getMiddlename() {
        return this.middlename;
    }

//    @XmlElement(name="О")
    public void setMiddlename(String middlename) {
        this.middlename = middlename;
    }

    @Override
    public String toString() {
        return new StringBuilder()
            .append("ФИО: [")
            .append(getLastname()).append(' ')
            .append(getFirstname()).append(' ')
            .append(getMiddlename()).append(']')
            .toString();
    }

}

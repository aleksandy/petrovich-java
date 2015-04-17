package com.github.aleksandy.petrovich;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.aleksandy.petrovich.Petrovich.Names;

public class PetrovichTest {

    Petrovich petrovich = new Petrovich();

    @Test
    public void test() {
        assertEquals(
            new Names("Иванова", "Ивана", "Ивановича", Gender.MALE),
            petrovich.inflectTo(
                new Names("Иванов", "Иван", "Иванович", null),
                Case.GENITIVE
            )
        );

        assertEquals(
            new Names("Мамина-Сибиряка", "Дмитрия", "Наркисовича", Gender.MALE),
            petrovich.inflectTo(
                new Names("Мамин-Сибиряк", "Дмитрий", "Наркисович", Gender.MALE),
                Case.ACCUSATIVE
            )
        );

        assertEquals(
            new Names("Склодовскую-Кюри", "Марию", null, Gender.FEMALE),
            petrovich.inflectTo(
                new Names("Склодовская-Кюри", "Мария", null, Gender.FEMALE),
                Case.ACCUSATIVE
            )
        );

        assertEquals(
            new Names("Череззаборногузадерищенко", "Акакии", "Панасьевиче", Gender.MALE),
            petrovich.inflectTo(
                new Names("Череззаборногузадерищенко", "Акакий", "Панасьевич", Gender.MALE),
                Case.PREPOSITIONAL
            )
        );

        assertEquals(
            new Names("Ивановой", "Ольги", "Ивановны", Gender.FEMALE),
            petrovich.inflectTo(
                new Names("Иванова", "Ольга", "Ивановна", Gender.FEMALE),
                Case.GENITIVE
            )
        );
    }

}
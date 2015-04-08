package com.github.aleksandy.petrovich;

import static org.junit.Assert.*;

import org.junit.Test;

import com.github.aleksandy.petrovich.Petrovich.Names;

public class PetrovichTest {

    Petrovich petrovich = new Petrovich();

    @Test
    public void test() {
        assertEquals(
            new Names("Иванова", "Ивана", "Ивановича", Gender.male),
            petrovich.inflectTo(
                new Names("Иванов", "Иван", "Иванович", null),
                Case.GENITIVE
            )
        );

        assertEquals(
            new Names("Мамина-Сибиряка", "Дмитрия", "Наркисовича", Gender.male),
            petrovich.inflectTo(
                new Names("Мамин-Сибиряк", "Дмитрий", "Наркисович", Gender.male),
                Case.ACCUSATIVE
            )
        );

        assertEquals(
            new Names("Склодовскую-Кюри", "Марию", null, Gender.female),
            petrovich.inflectTo(
                new Names("Склодовская-Кюри", "Мария", null, Gender.female),
                Case.ACCUSATIVE
            )
        );

        assertEquals(
            new Names("Череззаборногузадерищенко", "Акакии", "Панасьевиче", Gender.male),
            petrovich.inflectTo(
                new Names("Череззаборногузадерищенко", "Акакий", "Панасьевич", Gender.male),
                Case.PREPOSITIONAL
            )
        );

        assertEquals(
            new Names("Ивановой", "Ольги", "Ивановны", Gender.female),
            petrovich.inflectTo(
                new Names("Иванова", "Ольга", "Ивановна", Gender.female),
                Case.GENITIVE
            )
        );
    }

}
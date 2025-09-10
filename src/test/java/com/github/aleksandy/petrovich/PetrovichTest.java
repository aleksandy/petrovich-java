package com.github.aleksandy.petrovich;


import com.github.aleksandy.petrovich.Petrovich.Names;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PetrovichTest {

    Petrovich petrovich = new Petrovich();

    @Test void test() {
        assertThat(this.petrovich.inflectTo(
            new Names("Иванов", "Иван", "Иванович", null),
            Case.GENITIVE
        )).isEqualTo(new Names("Иванова", "Ивана", "Ивановича", Gender.MALE));

        assertThat(this.petrovich.inflectTo(
            new Names("Мамин-Сибиряк", "Дмитрий", "Наркисович", Gender.MALE),
            Case.ACCUSATIVE
        )).isEqualTo(new Names("Мамина-Сибиряка", "Дмитрия", "Наркисовича", Gender.MALE));

        assertThat(this.petrovich.inflectTo(
            new Names("Склодовская-Кюри", "Мария", null, Gender.FEMALE),
            Case.ACCUSATIVE
        )).isEqualTo(new Names("Склодовскую-Кюри", "Марию", null, Gender.FEMALE));

        assertThat(this.petrovich.inflectTo(
            new Names("Череззаборногузадерищенко", "Акакий", "Панасьевич", Gender.MALE),
            Case.PREPOSITIONAL
        )).isEqualTo(new Names("Череззаборногузадерищенко", "Акакии", "Панасьевиче", Gender.MALE));

        assertThat(this.petrovich.inflectTo(
            new Names("Иванова", "Ольга", "Ивановна", Gender.FEMALE),
            Case.GENITIVE
        )).isEqualTo(new Names("Ивановой", "Ольги", "Ивановны", Gender.FEMALE));
    }

}
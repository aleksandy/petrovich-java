package com.github.aleksandy.petrovich;

import org.junit.jupiter.api.Test;

import static com.github.aleksandy.petrovich.Case.ACCUSATIVE;
import static com.github.aleksandy.petrovich.Case.DATIVE;
import static com.github.aleksandy.petrovich.Case.GENITIVE;
import static com.github.aleksandy.petrovich.Case.INSTRUMENTAL;
import static com.github.aleksandy.petrovich.Case.NOMINATIVE;
import static com.github.aleksandy.petrovich.Case.PREPOSITIONAL;
import static org.assertj.core.api.Assertions.assertThat;

class CaseTest {

    @Test void getSuffixesIndexShouldReturnCorrectIndex() {
        assertThat(NOMINATIVE).extracting(Case::getSuffixesIndex).isEqualTo(-1);
        assertThat(GENITIVE).extracting(Case::getSuffixesIndex).isEqualTo(0);
        assertThat(DATIVE).extracting(Case::getSuffixesIndex).isEqualTo(1);
        assertThat(ACCUSATIVE).extracting(Case::getSuffixesIndex).isEqualTo(2);
        assertThat(INSTRUMENTAL).extracting(Case::getSuffixesIndex).isEqualTo(3);
        assertThat(PREPOSITIONAL).extracting(Case::getSuffixesIndex).isEqualTo(4);
    }

}
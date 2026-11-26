/*
 * SPDX-License-Identifier: MIT
 * Copyright (c) 2014-2026 Andrey Alekseenko
 */

package com.github.aleksandy.petrovich;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class NamesTest {

    @Test void shouldAllowAnySingleNamePart() {
        assertThat(new Names(null, " Иван ", null, null))
            .extracting(Names::firstName, Names::lastName, Names::middleName)
            .containsExactly("Иван", null, null);
    }

    @Test void shouldRejectNamesWithoutParts() {
        assertThatThrownBy(() -> new Names(null, "  ", "", null))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("At least one");
    }

    @Test void toStringShouldSkipMissingParts() {
        assertThat(new Names("Иванов", null, null, null).toString()).isEqualTo("Иванов");
        assertThat(new Names(null, "Иван", "Иванович", null).toString()).isEqualTo("Иван Иванович");
    }

}

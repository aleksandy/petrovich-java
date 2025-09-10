package com.github.aleksandy.petrovich;

/**
 * Supported cases for inflect.
 */
public enum Case {

    NOMINATIVE,
    GENITIVE,
    DATIVE,
    ACCUSATIVE,
    INSTRUMENTAL,
    PREPOSITIONAL;

    /**
     * The method returns the index of the suffix array element in inflection rules.
     *
     * @return index
     */
    public int getSuffixesIndex() {
        return ordinal() - 1;
    }

}

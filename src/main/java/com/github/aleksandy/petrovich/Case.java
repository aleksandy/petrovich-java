package com.github.aleksandy.petrovich;

public enum Case {

    NOMINATIVE(-1),
    GENITIVE(0),
    DATIVE(1),
    ACCUSATIVE(2),
    INSTRUMENTAL(3),
    PREPOSITIONAL(4);

    private final int suffixesIndex;

    private Case(int suffixesIndex) {
        this.suffixesIndex = suffixesIndex;
    }

    public int getSuffixesIndex() {
        return this.suffixesIndex;
    }

}

package com.github.aleksandy.petrovich.rules;

import com.github.aleksandy.petrovich.rules.data.Rules;
import com.github.aleksandy.petrovich.rules.loader.RulesLoader;

public class RulesProvider {

    private final Rules rules;

    public RulesProvider(RulesLoader loader) {
        this.rules = loader.load();
    }

    public Rules getRules() {
        return rules;
    }

}

package com.github.aleksandy.petrovich.rules.loader;

import com.github.aleksandy.petrovich.rules.data.Rules;

public interface RulesLoader {

    String DEFAULT_RULES_RESOURCE = "/rules.yml";

    Rules load();

}

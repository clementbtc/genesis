package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

/**
 * Implementation of {@link CssBuilder}.
 *
 * @author Clément Roux
 */
final class CssBuilderImpl implements CssBuilder {
  private final CssModule module;
  // We used linked sets and maps to ensure consistency of the generated CSS
  // between different execution runs
  private final Map<SelectorOnMediaCondition, RuleOnMediaCondition> rules =
      Maps.newLinkedHashMap();
  private final Set<CssModule> dependencies = Sets.newLinkedHashSet();
  private final Set<CssImport> imports = Sets.newLinkedHashSet();
  private final Set<FontFace> fontFaces = Sets.newLinkedHashSet();
  private final Set<Keyframes> animations = Sets.newLinkedHashSet();

  CssBuilderImpl(CssModule module) {
    this.module = checkNotNull(module);
  }

  @Override public void install(CssModule module) {
    dependencies.add(module);
  }

  @Override public void addRule(Selector selector,
      PropertiesOrBuilder properties) {
    addRule(MediaQuery.ALL, selector, properties.build());
  }

  @Override public void addRule(String selector,
      PropertiesOrBuilder properties) {
    addRule(MediaQuery.ALL, Selector.from(selector), properties.build());
  }

  @Override public void addRule(Selector selector,
      PropertiesOrBuilder properties, MediaCondition condition) {
    addRule(condition, selector, properties.build());
  }

  @Override public void addRule(String selector, PropertiesOrBuilder properties,
      MediaCondition condition) {
    addRule(condition, Selector.from(selector), properties.build());
  }

  private void addRule(MediaCondition condition,
      Selector selector, Properties properties) {
    SelectorOnMediaCondition key =
        new SelectorOnMediaCondition(selector, condition);
    RuleOnMediaCondition old = rules.get(key);
    if (old != null) {
      // Aggregate the properties
      properties = Properties.builder()
          .copyFrom(old.properties)
          .copyFrom(properties)
          .build();
    }
    RuleOnMediaCondition newRule = new RuleOnMediaCondition(key, properties);
    rules.put(key, newRule);
  }

  @Override public void addRules(String rules) {
    addRules(rules, MediaQuery.ALL);
  }

  @Override public void addRules(String rules, MediaCondition condition) {
    checkNotNull(condition);
    List<Rule> ruleList = Rule.LIST_PARSER.from(rules);
    for (Rule rule : ruleList) {
      addRule(condition, rule.selector, rule.properties);
    }
  }

  @Override public void importCss(String uri) {
    importCss(uri, MediaQuery.ALL);
  }

  @Override public void importCss(String uri, MediaCondition condition) {
    CssImport cssImport = new CssImport(uri, condition);
    imports.add(cssImport);
  }

  @Override public void use(FontFace fontFace) {
    fontFaces.add(checkNotNull(fontFace));
  }

  @Override public void use(Keyframes keyframes) {
    animations.add(checkNotNull(keyframes));
  }

  Rules build() {
    return new Rules(module, rules.values(), dependencies, imports,
        fontFaces, animations);
  }
}

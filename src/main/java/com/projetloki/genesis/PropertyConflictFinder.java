package com.projetloki.genesis;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Map.Entry;

import com.google.common.collect.Maps;

/**
 * Utility class to verify that properties defined in different modules don't
 * intersect. Instances are mutable.
 *
 * @author Clément Roux
 */
final class PropertyConflictFinder {
  private final Map<SelectorOnMediaCondition, SelectorProperties>
      selectorToProperties = Maps.newHashMap();

  /**
   * Adds the given rules, and throws an illegal argument exception in case of
   * a property conflict.
   */
  void add(Rules rules) {
    for (RuleOnMediaCondition rule : rules.rules) {
      SelectorOnMediaCondition selector = rule.selector;
      SelectorProperties properties = selectorToProperties.get(selector);
      if (properties == null) {
        properties = new SelectorProperties(selector);
        selectorToProperties.put(selector, properties);
      }
      CssModule module = rules.module;
      properties.add(module, rule.properties);
    }
  }

  private static class SelectorProperties {
    final SelectorOnMediaCondition selector;
    final Map<String, CssModule> nameToModule = Maps.newHashMap();
    Entry<CssModule, Properties> pending;

    SelectorProperties(SelectorOnMediaCondition selector) {
      this.selector = checkNotNull(selector);
    }

    void add(CssModule module, Properties properties) {
      if (pending != null) {
        doAdd(pending.getKey(), pending.getValue());
        doAdd(module, properties);
        pending = null;
      } else if (nameToModule.isEmpty()) {
        pending = Maps.immutableEntry(module, properties);
      } else {
        doAdd(module, properties);
      }
    }

    private void doAdd(CssModule module, Properties properties) {
      for (String name : properties.properties.keySet()) {
        CssModule oldModule = nameToModule.get(name);
        checkArgument(oldModule == null,
            "property %s#%s set in two modules: %s and %s",
            selector, name, oldModule, module);
        nameToModule.put(name, module);
      }
    }
  }
}

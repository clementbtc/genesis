package com.projetloki.genesis;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Indicates that the CSS feature is not fully supported by all major browsers.
 *
 * <p>There exist many browsers, with many versions, and there also are many
 * degrees of support. This annotation is only a reductive aggregate of these
 * variables. We recommend that you check
 * <a href="http://caniuse.com">caniuse.com</a> every time you
 * want to know in more detail how well a feature is supported.</p>
 *
 * <p>The presence of this annotation depends a lot on the release date of the
 * Genesis version you are using. Browsers evolve very rapidly and browser
 * usage varies a lot every year. In a future version of Genesis, a method
 * marked as poorly-supported-by-browsers may stop being marked as so.</p>
 *
 * @author Clément Roux
 */
@Documented
@Retention(value=RetentionPolicy.CLASS)
public @interface PoorBrowserSupport {}

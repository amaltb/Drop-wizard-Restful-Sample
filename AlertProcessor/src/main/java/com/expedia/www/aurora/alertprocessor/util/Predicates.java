package com.expedia.www.aurora.alertprocessor.util;

import java.util.function.Predicate;

public class Predicates {

    public static final Predicate<Request> REQUEST_PREDICATE = req -> {
      final String tempAlias = req.getTemplateAliasName();
      return (tempAlias != null && !tempAlias.isEmpty());
    };
}

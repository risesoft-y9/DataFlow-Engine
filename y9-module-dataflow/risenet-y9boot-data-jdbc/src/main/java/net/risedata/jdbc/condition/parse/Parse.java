package net.risedata.jdbc.condition.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.risedata.jdbc.config.model.BeanConfig;

public interface Parse {
    Pattern getPattern();
    String parse(String group,BeanConfig bc,Matcher m);
}

package com.undsf.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Arathi on 2015/6/26.
 */
public class TemplateEngine {
    public static final String DEFAULT_LEFT_DELIMITER = "\\$\\{";
    public static final String DEFAULT_RIGHT_DELIMITER = "\\}";

    protected String template;
    protected String leftDelimiter;
    protected String rightDelimiter;
    protected Map<String, String> vars;

    public TemplateEngine(){
        init(DEFAULT_LEFT_DELIMITER, DEFAULT_RIGHT_DELIMITER);
    }

    public TemplateEngine(String ld, String rd){
        init(ld, rd);
    }

    public void init(String ld, String rd){
        leftDelimiter = ld;
        rightDelimiter = rd;
        template = "";
        vars = new HashMap<String, String>();
    }

    public void assign(String var, String value){
        setVar(var, value);
    }

    public void clear(){
        vars.clear();
    }

    public String getVar(String varName){
        return getVar(varName, null);
    }

    protected String getVar(String varName, String defaultValue){
        if (vars.containsKey(varName)){
            String varValue = vars.get(varName);
            return varValue;
        }
        return defaultValue;
    }

    protected void setVar(String var, String value){
        vars.put(var, value);
    }

    public String parse(String template){
        String result = template;
        String format = "%s([0-9A-Za-z_]+)%s";
        Pattern pattern = Pattern.compile(String.format(format, leftDelimiter, rightDelimiter));
        Matcher matcher = pattern.matcher(template);
        int index = 0;
        while (matcher.find(index)){
            String varName = matcher.group(1);
            String value = getVar(varName);
            String src = leftDelimiter + varName + rightDelimiter;
            String dest = value;
            result = result.replaceAll(src, dest);
            index = matcher.end();
        }
        return result;
    }
}

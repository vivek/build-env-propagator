package org.jenkinsci.plugins.envpropagator;

import hudson.EnvVars;
import hudson.model.InvisibleAction;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vivek Pandey
 */
public class EnvMapperAction extends InvisibleAction{
    private final Map<String,String> customEnvMapping = new HashMap<String, String>();

    public EnvMapperAction(Map<String, String> envVariables) {
        customEnvMapping.putAll(envVariables);
    }


    public Map<String,String> getEnvVariables(){
        return Collections.unmodifiableMap(customEnvMapping);
    }
    /** Merges existing env map in to given EnvVars **/
    public void merge(EnvVars that){
        that.putAll(customEnvMapping);
    }

    public void putAll(Map<String,String> env){
        customEnvMapping.putAll(env);
    }

    public void add(String k, String v){
        customEnvMapping.put(k,v);
    }
}

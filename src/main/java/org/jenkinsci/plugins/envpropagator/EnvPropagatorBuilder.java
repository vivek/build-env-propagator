package org.jenkinsci.plugins.envpropagator;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Vivek Pandey
 */
public class EnvPropagatorBuilder extends Builder{

    private final String envVariableString;

    /**
     * Captures given env variables states across build steps. If a given env variable does not exist it's added as a new
     * build variable. Existing build variables are overwritten with the new value
     *
     * @param envVariableString key1=val1:key2=val2:key3=val3 ....
     */
    @DataBoundConstructor
    public EnvPropagatorBuilder(String envVariableString) {
        //TODO: if invalid format how to throw error so that it appears on the form? Maybe it needs to happen inside DescriptionImpl?
        this.envVariableString = envVariableString;
    }

    @Override
    public boolean perform(AbstractBuild build, Launcher launcher, BuildListener listener) {
        EnvMapperAction action = build.getAction(EnvMapperAction.class);
        if(action == null){ //TODO: should it not be synchronized?
            action = new EnvMapperAction(mapEnvVariables());
            build.addAction(action);
        }else{
            action.putAll(mapEnvVariables());
        }

        //TODO: BuildEnvContributorAction is annotated with Extension, why do we need to add it explicitly?
        BuildEnvContributorAction contributorAction = build.getAction(BuildEnvContributorAction.class);

        if(contributorAction == null){ //TODO: should it not be synchronized?
            contributorAction = new BuildEnvContributorAction();
        }
        build.addAction(contributorAction);
        listener.getLogger().println("Build environment variables will be made available to the next build: "+envVariableString);

        return true;
    }

    public String getEnvVariableString() {
        return envVariableString;
    }

    private Map<String,String> mapEnvVariables(){
        Map<String,String> envVariables = new HashMap<String, String>();
        for(String param:envVariableString.split(":")){
            String[] kv=param.split("=");
            if(kv.length ==2){
                envVariables.put(kv[0],kv[1]);
            }
        }
        return envVariables;
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl)super.getDescriptor();
    }

    /**
     * Descriptor for {@link EnvPropagatorBuilder}. Used as a singleton.
     * The class is marked as public so that it can be accessed from views.
     *
     * <p>
     * See <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */
    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {

        /**
         * In order to load the persisted global configuration, you have to 
         * call load() in the constructor.
         */
        public DescriptorImpl() {
            load();
        }

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            // Indicates that this builder can be used with all kinds of project types 
            return true;
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        public String getDisplayName() {
            return "Propagate build environment variables";
        }

        /**
         * Performs on-the-fly validation of the form field 'envVariables'.
         *
         * @param value
         *      This parameter receives the value that the user has typed.
         * @return
         *      Indicates the outcome of the validation. This is sent to the browser.
         *      <p>
         *      Note that returning {@link hudson.util.FormValidation#error(String)} does not
         *      prevent the form from being saved. It just means that a message
         *      will be displayed to the user.
         */
        public FormValidation doCheckEnvVariableString(@QueryParameter String value)
                throws IOException, ServletException {
            if (value.length() == 0)
                return FormValidation.error("Please provide env variables in format a=b:c=d.");
            return FormValidation.ok();
        }
    }
}


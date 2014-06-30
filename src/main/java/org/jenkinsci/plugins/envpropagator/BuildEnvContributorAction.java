package org.jenkinsci.plugins.envpropagator;

import hudson.EnvVars;
import hudson.Extension;
import hudson.model.AbstractBuild;
import hudson.model.EnvironmentContributingAction;

/**
 * @author Vivek Pandey
 */
@Extension
public class BuildEnvContributorAction implements EnvironmentContributingAction {

    public void buildEnvVars(AbstractBuild<?, ?> build, EnvVars env) {
        EnvCopyAction action = build.getAction(EnvCopyAction.class);
        action.merge(env);
    }

    public String getIconFileName() {
        return null;
    }

    public String getDisplayName() {
        return null;
    }

    public String getUrlName() {
        return null;
    }
}

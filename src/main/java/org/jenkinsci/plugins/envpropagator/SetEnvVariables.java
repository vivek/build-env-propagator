package org.jenkinsci.plugins.envpropagator;

import hudson.Extension;
import hudson.cli.CommandDuringBuild;
import hudson.model.Run;
import org.kohsuke.args4j.Option;
import org.kohsuke.args4j.spi.MapOptionHandler;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * <p>
 * Set build time env variables.
 * </p>
 * <pre>
 *  <code>
 *     $java -jar cli.jar set-env-variables -m color=purple -m size=10
 * </code>
 * </pre>
 *
 * @author Vivek Pandey
 */
@Extension
public class SetEnvVariables extends CommandDuringBuild {

    @Option(name="-m",handler = MapOptionHandler.class, usage = "Set build environment variables using Jenkins CLI. For example: \n java -jar cli.jar set-env-variables -m color=purple -m size=10")
    public Map<String,String> envVariables = new HashMap<String, String>();

    @Override
    public String getShortDescription() {
        return "Set environment variables";
    }

    @Override
    protected int run() throws Exception {
        Run r = getCurrentlyBuilding();

        EnvMapperAction action = r.getAction(EnvMapperAction.class);
        // Perhaps this action has not been added by prior build steps, add it now
        if(action == null){
            r.addAction(new EnvMapperAction(envVariables));
        }else {
            action.putAll(envVariables);
        }

        //Make sure contributor action is available to add these action to other builds
        BuildEnvContributorAction contributorAction = r.getAction(BuildEnvContributorAction.class);
        if(contributorAction == null) {
            r.addAction(new BuildEnvContributorAction());
        }
        return 0;
    }

    @Override
    public int main(List<String> args, Locale locale, InputStream stdin, PrintStream stdout, PrintStream stderr) {
        return super.main(args, locale, stdin, stdout, stderr);
    }
}

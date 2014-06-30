package org.jenkinsci.plugins.envpropagator;

import hudson.Extension;
import hudson.cli.CommandDuringBuild;
import hudson.model.Run;
import org.kohsuke.args4j.Argument;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Locale;

/**
 * @author Vivek Pandey
 */
@Extension
public class SetEnvVariable extends CommandDuringBuild {

    @Argument(index=0, metaVar="NAME", required=true, usage="Name of the env variable")
    public String name;

    @Argument(index=1, metaVar="VALUE", required=true, usage="Value of the env variable")
    public String value;


    @Override
    public String getShortDescription() {
        return "Set environment variable";
    }

    @Override
    protected int run() throws Exception {
        Run r = getCurrentlyBuilding();

        EnvMapperAction copyAction = r.getAction(EnvMapperAction.class);
        copyAction.add(name,value);
        return 0;
    }

    @Override
    public int main(List<String> args, Locale locale, InputStream stdin, PrintStream stdout, PrintStream stderr) {
        return super.main(args, locale, stdin, stdout, stderr);
    }
}

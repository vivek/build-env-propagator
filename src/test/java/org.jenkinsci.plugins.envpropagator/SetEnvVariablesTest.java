package org.jenkinsci.plugins.envpropagator;

import hudson.Launcher;
import hudson.model.*;
import hudson.tasks.Shell;
import jenkins.model.JenkinsLocationConfiguration;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;
import org.jvnet.hudson.test.TestBuilder;

import java.io.IOException;
import java.net.URL;

/**
 * @author Vivek Pandey
 */
public class SetEnvVariablesTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void cliCommand() throws Exception {
        JenkinsLocationConfiguration.get().setUrl(j.getURL().toString());
        FreeStyleProject project = j.createFreeStyleProject();

        project.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
                URL jar = j.jenkins.servletContext.getResource("/WEB-INF/jenkins-cli.jar");
                build.getWorkspace().child("cli.jar").copyFrom(jar);
                return true;
            }
        });
        project.getBuildersList().add(new EnvPropagatorBuilder(null));
        project.getBuildersList().add(new Shell("java -jar cli.jar set-env-variables -m color=blue -m size=10"));
        project.getBuildersList().add(new Shell("if [ $color != \"blue\" ]\n" +
                "  then\n" +
                "    echo \"Was expecting color to be blue was $color\"\n" +
                "    exit 1\n" +
                "fi\n" +
                "\n" +
                "if [ $size != \"10\" ]\n" +
                "  then\n" +
                "    echo \"Was expecting size to be 10 was $size\"\n" +
                "    exit 1\n" +
                "fi\n"));

        AbstractBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        EnvMapperAction action = build.getAction(EnvMapperAction.class);
        Assert.assertNotNull(action);

        BuildEnvContributorAction contributorAction = build.getAction(BuildEnvContributorAction.class);
        Assert.assertNotNull(contributorAction);

        Assert.assertEquals(action.getEnvVariables().get("color"), "blue");
        Assert.assertEquals(action.getEnvVariables().get("size"), "10");
    }

    @Test
    public void cliCommandToChangeVariables() throws Exception {
        JenkinsLocationConfiguration.get().setUrl(j.getURL().toString());
        FreeStyleProject project = j.createFreeStyleProject("test");

        project.getBuildersList().add(new TestBuilder() {
            @Override
            public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) throws InterruptedException, IOException {
                URL jar = j.jenkins.servletContext.getResource("/WEB-INF/jenkins-cli.jar");
                build.getWorkspace().child("cli.jar").copyFrom(jar);
                return true;
            }
        });
        project.getBuildersList().add(new EnvPropagatorBuilder("color=blue:size=10"));

        project.getBuildersList().add(new Shell("if [ $color != \"blue\" ]\n" +
                "  then\n" +
                "    echo \"Was expecting color to be blue was $color\"\n" +
                "    exit 1\n" +
                "fi\n" +
                "\n" +
                "if [ $size != \"10\" ]\n" +
                "  then\n" +
                "    echo \"Was expecting size to be 10 was $size\"\n" +
                "    exit 1\n" +
                "fi\n"));

        project.getBuildersList().add(new Shell("java -jar cli.jar set-env-variables -m color=purple -m size=20"));
        project.getBuildersList().add(new Shell("if [ $color != \"purple\" ]\n" +
                "  then\n" +
                "    echo \"Was expecting color to be blue was $color\"\n" +
                "    exit 1\n" +
                "fi\n" +
                "\n" +
                "if [ $size != \"20\" ]\n" +
                "  then\n" +
                "    echo \"Was expecting size to be 10 was $size\"\n" +
                "    exit 1\n" +
                "fi\n"));


        project.getBuildersList().add(new Shell("java -jar cli.jar set-env-variables -m color=red -m JOB_NAME=testXXX"));
        project.getBuildersList().add(new Shell("if [ $color != \"red\" ]\n" +
                "  then\n" +
                "    echo \"Was expecting color to be red was $color\"\n" +
                "    exit 1\n" +
                "fi\n" +
                "\n" +
                "if [ $size != \"20\" ]\n" +
                "  then\n" +
                "    echo \"Was expecting size to be 10 was $size\"\n" +
                "    exit 1\n" +
                "fi\n" +
                "\n" +
                "if [ $JOB_NAME != \"testXXX\" ]\n" +
                "  then\n" +
                "    echo \"Was expecting JOB_NAME to be testXXX was $JOB_NAME\"\n" +
                "    exit 1\n" +
                "fi\n"));


        AbstractBuild build = j.assertBuildStatusSuccess(project.scheduleBuild2(0));

        EnvMapperAction action = build.getAction(EnvMapperAction.class);
        Assert.assertNotNull(action);

        BuildEnvContributorAction contributorAction = build.getAction(BuildEnvContributorAction.class);
        Assert.assertNotNull(contributorAction);

        Assert.assertEquals(action.getEnvVariables().get("color"), "red");
        Assert.assertEquals(action.getEnvVariables().get("size"), "20");
        Assert.assertEquals(action.getEnvVariables().get("JOB_NAME"), "testXXX");
    }

}

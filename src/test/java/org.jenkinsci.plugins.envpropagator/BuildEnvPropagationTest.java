package org.jenkinsci.plugins.envpropagator;

import hudson.model.FreeStyleBuild;
import hudson.model.FreeStyleProject;
import hudson.model.Result;
import hudson.tasks.Shell;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.jvnet.hudson.test.JenkinsRule;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/**
 * @author Vivek Pandey
 */
public class BuildEnvPropagationTest {
    @Rule
    public JenkinsRule j = new JenkinsRule();

    @Test
    public void addNewVariables() throws IOException, ExecutionException, InterruptedException {
        FreeStyleProject project = j.createFreeStyleProject("test1");

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

        FreeStyleBuild build = project.scheduleBuild2(0).get();
        Assert.assertEquals(Result.SUCCESS, build.getResult());

        EnvMapperAction action = build.getAction(EnvMapperAction.class);
        Assert.assertNotNull(action);

        BuildEnvContributorAction contributorAction = build.getAction(BuildEnvContributorAction.class);
        Assert.assertNotNull(contributorAction);

        Assert.assertEquals(action.getEnvVariables().get("color"), "blue");
        Assert.assertEquals(action.getEnvVariables().get("size"), "10");
    }

    @Test
    public void changeVariables() throws IOException, ExecutionException, InterruptedException {
        FreeStyleProject project = j.createFreeStyleProject("test");

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

        project.getBuildersList().add(new EnvPropagatorBuilder("color=red:JOB_NAME=testXXX"));

        project.getBuildersList().add(new Shell("if [ $color != \"red\" ]\n" +
                "  then\n" +
                "    echo \"Was expecting color to be red was $color\"\n" +
                "    exit 1\n" +
                "fi\n" +
                "\n" +
                "if [ $size != \"10\" ]\n" +
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

        FreeStyleBuild build = project.scheduleBuild2(0).get();

        EnvMapperAction action = build.getAction(EnvMapperAction.class);
        Assert.assertNotNull(action);

        BuildEnvContributorAction contributorAction = build.getAction(BuildEnvContributorAction.class);
        Assert.assertNotNull(contributorAction);



        Assert.assertEquals(action.getEnvVariables().get("color"), "red");
        Assert.assertEquals(action.getEnvVariables().get("size"), "10");
        Assert.assertEquals(action.getEnvVariables().get("JOB_NAME"), "testXXX");
    }

    @Test
    public void changePredefinedVariables() throws IOException, ExecutionException, InterruptedException {
        FreeStyleProject project = j.createFreeStyleProject("test");

        project.getBuildersList().add(new EnvPropagatorBuilder("JOB_NAME=testXXX"));
        project.getBuildersList().add(new Shell(
                "if [ $JOB_NAME != \"testXXX\" ]\n" +
                "  then\n" +
                "    echo \"Was expecting JOB_NAME to be testXXX was $JOB_NAME\"\n" +
                "    exit 1\n" +
                "fi\n"));

        FreeStyleBuild build = project.scheduleBuild2(0).get();

        EnvMapperAction action = build.getAction(EnvMapperAction.class);
        Assert.assertNotNull(action);

        BuildEnvContributorAction contributorAction = build.getAction(BuildEnvContributorAction.class);
        Assert.assertNotNull(contributorAction);

        Assert.assertEquals(action.getEnvVariables().get("JOB_NAME"), "testXXX");
    }


}

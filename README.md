# Build environment variable propagator

This plugin lets you add new build environment variables, override a pre-defined and existing variables at each build 
step. Set of variables defined at a build step is carried over to the sub-sequent build steps. Any subsequent build step
 can override any variable defined by the previous build step.   

You can set build env variables by adding Build Step *Propagate build environment variables* 

Enter the build env variables using format 
    
    key1=value1:key2=value2

Or you can add or override build env variables using Jenkins CLI
 
    $ java -jar cli.jar set-env-variables -m key1=val1 -m key2=val2

# Build

    $ mvn clean install

# Run locally
     
    $ mvn hpi:run   
    
# Build environment variable propagator

This plugin lets you add new build environment variables, override a pre-defined and existing variables at each build 
step. Set of variables defined at a build step is carried over to the sub-sequent build steps. Any subsequent build step
 can override any variable defined by the previous build step.   

# Build

    $ mvn clean install

# Run locally
     
    $ mvn hpi:run   
    
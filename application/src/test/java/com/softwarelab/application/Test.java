package com.softwarelab.application;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.model.Image;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.httpclient5.ApacheDockerHttpClient;
import com.github.dockerjava.transport.DockerHttpClient;
import org.apache.maven.shared.invoker.*;

import java.io.File;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class Test {

    @org.junit.Test
    public void test() {
        String path = new File(Test.class.getClassLoader().getResource(".").getFile()).getParentFile().getParentFile().getParent()+"/demo";
        System.out.println(path);
        buildDemoJar(path);
        buildImage(path);
    }

    public static void buildImage(String path){
        DefaultDockerClientConfig config = DefaultDockerClientConfig
                .createDefaultConfigBuilder()
                .withDockerHost("unix:///var/run/docker.sock")
                .build();
        DockerHttpClient httpClient = new ApacheDockerHttpClient.Builder()
                .dockerHost(config.getDockerHost())
                .sslConfig(config.getSSLConfig())
                .build();

        DockerClient dockerClient = DockerClientImpl.getInstance(config, httpClient);

        //check image exists
        // docker build demo -f demo/Dockerfile -t test
        //todo
        HashSet<String> sets = new HashSet<>();
        sets.add("softwarelab/demo:0.0.1");
        dockerClient.buildImageCmd()
                .withBaseDirectory(new File(path))
                .withDockerfile(new File(path+"/Dockerfile"))
                .withTags(sets)
                .start()
                .awaitImageId();

        List<Image> images = dockerClient.listImagesCmd().withImageNameFilter("softwarelab/test:0.0.1").exec();
        for (Image testImage : images) {
            String[] repoTags = testImage.getRepoTags();
            for (String repoTag : repoTags) {
                System.out.println(repoTag);
            }
//            System.out.println();
        }
    }

    public static void buildDemoJar(String path){
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile( new File( path+"/pom.xml" ) );
        request.setGoals( Collections.singletonList( "package" ) );

        Invoker invoker = new DefaultInvoker();
        System.out.println(System.getenv("M2_HOME"));
        invoker.setMavenHome(new File(System.getenv("M2_HOME")));

/*invoker.setLogger(new PrintStreamLogger(System.err,  InvokerLogger.ERROR){

} );
invoker.setOutputHandler(new InvocationOutputHandler() {
    @Override
    public void consumeLine(String s) throws IOException {
    }
});
*/

        try
        {
            invoker.execute( request );
        }
        catch (MavenInvocationException e)
        {
            e.printStackTrace();
        }



/*try{
    if(invoker.execute( request ).getExitCode()==0){
          System.out.println("success");
    }else{
          System.err.println("error");
    }
}catch (MavenInvocationException e) {
    e.printStackTrace();
}*/
    }
}

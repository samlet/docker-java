package pi.docker;

import avro.shaded.com.google.common.collect.Lists;
import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;
import org.apache.avro.tool.IdlTool;
import org.apache.avro.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Singleton;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by xiaofeiwu on 2016/12/1.
 */
@Singleton
public class DockerImageMeta {
    public static final Logger LOG = LoggerFactory.getLogger(DockerImageMeta.class);
    private DockerClient dockerClient;

    DockerImageMeta(){
        LOG.info("Connecting to Docker server");
        DockerCmdExecFactory dockerCmdExecFactory= DockerClientBuilder.getDefaultDockerCmdExecFactory();
        dockerClient = DockerClientBuilder.getInstance(config())
                .withDockerCmdExecFactory(dockerCmdExecFactory)
                .build();
    }
    public int getLabels(InputStream in, PrintStream out, PrintStream err) throws Exception {
        Tool tool = new IdlTool();
        List<String> args = Lists.newArrayList();
        // tool.run(System.in, System.out, System.err, args);
        return tool.run(in, out, err, args);
    }

    public void checkImage(String imageName){
        try {
            dockerClient.inspectImageCmd(imageName).exec(); //"busybox"
        } catch (NotFoundException e) {
            LOG.info("Pulling image '{}'", imageName);
            // need to block until image is pulled completely
            dockerClient.pullImageCmd(imageName).withTag("latest").exec(new PullImageResultCallback()).awaitSuccess();
        }
    }

    /**
     * Process service labels
     * @param imageName
     * @return return null if process fail or no packages definition
     * @throws Exception
     */
    public String processServiceLabels(String imageName) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream err = new PrintStream(baos);
        String ret= processServiceLabels(imageName, err);
        if(ret==null) {
            String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            LOG.error(content);
        }
        return ret;
    }

    public String processServiceLabels(String imageName, PrintStream err) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
        int ret=processServiceLabels(imageName, ps, err);
        if(ret==0) {
            String content = new String(baos.toByteArray(), StandardCharsets.UTF_8);
            return content;
        }
        return null;
    }

    public int processServiceLabels(String imageName, PrintStream out, PrintStream err) throws Exception {
        InspectImageResponse response=dockerClient.inspectImageCmd(imageName).exec(); // "scala_rest"
        Map<String, String> labels= response.getConfig().getLabels();

        String packages=labels.get("packages");
        if(packages!=null){
            String serviceDefs=labels.get(packages);
            if(serviceDefs!=null) {
                serviceDefs="@namespace(\""+packages+"\")\n"+serviceDefs;
                InputStream stream = new ByteArrayInputStream(serviceDefs.getBytes(StandardCharsets.UTF_8));
                return getLabels(stream, out, err);
            }
        }

        return -1;
    }

    public void printVersionLabels(String imageName){
        InspectImageResponse response=dockerClient.inspectImageCmd(imageName).exec(); // "scala_rest"
        Map<String, String> labels= response.getConfig().getLabels();
        System.out.println(labels.get("version"));
        System.out.println(labels.get("description"));

        System.out.println("other labels:");
        for (Map.Entry label:labels.entrySet()) {
            System.out.format("%s = %s\n", label.getKey(),label.getValue());
        }

    }
    private DefaultDockerClientConfig config() {
        return config(null);
    }

    protected DefaultDockerClientConfig config(String password) {
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                // .withRegistryUrl("https://index.docker.io/v1/");
                // .withDockerHost("unix:///var/run/docker.sock")
                // docker run --rm -it -v /var/run/docker.sock:/var/run/docker.sock -p 2377:2375 \
                //  bobrik/socat TCP-LISTEN:2375,reuseaddr,fork UNIX-CLIENT:/var/run/docker.sock
                // .withDockerHost("tcp://localhost:2377")
                .withDockerHost("tcp://socat:2375")
                .withDockerTlsVerify(false);
        if (password != null) {
            builder = builder.withRegistryPassword(password);
        }

        return builder.build();
    }
}

package pi.docker;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.api.command.DockerCmdExecFactory;
import com.github.dockerjava.api.command.InspectImageResponse;
import com.github.dockerjava.api.exception.NotFoundException;
import com.github.dockerjava.core.DefaultDockerClientConfig;
import com.github.dockerjava.core.DockerClientBuilder;
import com.github.dockerjava.core.command.PullImageResultCallback;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;

import static org.junit.Assert.assertNotNull;

/**
 * Created by xiaofeiwu on 2016/12/1.
 */
public class DockerLabels {
    public static final Logger LOG = LoggerFactory.getLogger(DockerLabels.class);
    protected DockerClient dockerClient;
    @Test
    public void testGetLabels(){
        LOG.info("Connecting to Docker server");
        DockerCmdExecFactory dockerCmdExecFactory= DockerClientBuilder.getDefaultDockerCmdExecFactory();
        dockerClient = DockerClientBuilder.getInstance(config())
                .withDockerCmdExecFactory(dockerCmdExecFactory)
                .build();

        try {
            dockerClient.inspectImageCmd("busybox").exec();
        } catch (NotFoundException e) {
            LOG.info("Pulling image 'busybox'");
            // need to block until image is pulled completely
            dockerClient.pullImageCmd("busybox").withTag("latest").exec(new PullImageResultCallback()).awaitSuccess();
        }

        assertNotNull(dockerClient);

        // Get container info and check its roundtrip to ensure the consistency
//        InspectContainerResponse containerInfo = dockerClient.inspectContainerCmd(container.getId()).exec();
//        assertEquals(containerInfo.getId(), container.getId());

        InspectImageResponse response=dockerClient.inspectImageCmd("scala_rest").exec();
        Map<String, String> labels= response.getConfig().getLabels();
        System.out.println(labels.get("version"));
        System.out.println(labels.get("description"));

    }
    private DefaultDockerClientConfig config() {
        return config(null);
    }

    protected DefaultDockerClientConfig config(String password) {
        DefaultDockerClientConfig.Builder builder = DefaultDockerClientConfig.createDefaultConfigBuilder()
                // .withRegistryUrl("https://index.docker.io/v1/");
                .withDockerHost("unix:///var/run/docker.sock")
                .withDockerTlsVerify(false);
        if (password != null) {
            builder = builder.withRegistryPassword(password);
        }

        return builder.build();
    }
}

package pi.docker;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by xiaofeiwu on 2016/12/1.
 */
public class DockerImageMetaTest {
    @Test
    public void getLabels() throws Exception {

    }

    @Test
    public void checkImage() throws Exception {

    }

    @Test
    public void processServiceLabels() throws Exception {
        Injector inject= Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

            }
        });
        DockerImageMeta meta=inject.getInstance(DockerImageMeta.class);
        meta.processServiceLabels("scala_rest", System.out, System.err);

        System.out.println("----------");
        System.out.println(meta.processServiceLabels("scala_rest"));
    }

    @Test
    public void config() throws Exception {

    }

    @Test
    public void printVersionLabels() throws Exception {
        Injector inject= Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

            }
        });
        DockerImageMeta meta=inject.getInstance(DockerImageMeta.class);
        meta.printVersionLabels("scala_rest");
    }

}
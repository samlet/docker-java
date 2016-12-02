package pi.docker;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

/**
 * Created by xiaofeiwu on 2016/12/1.
 */
public class AppModule {
    private static AppModule instance = null;
    private Injector injector;
    protected AppModule() {
        // Exists only to defeat instantiation.
        injector= Guice.createInjector(new AbstractModule() {
            @Override
            protected void configure() {

            }
        });
    }
    public static AppModule getInstance() {
        if(instance == null) {
            instance = new AppModule();
        }
        return instance;
    }

    public static DockerImageMeta meta(){
        return getInstance().injector.getInstance(DockerImageMeta.class);
    }
}

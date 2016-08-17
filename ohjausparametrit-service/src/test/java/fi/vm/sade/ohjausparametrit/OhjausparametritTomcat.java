package fi.vm.sade.ohjausparametrit;

import fi.vm.sade.integrationtest.tomcat.EmbeddedTomcat;
import fi.vm.sade.integrationtest.util.ProjectRootFinder;
import org.apache.catalina.LifecycleException;

import javax.servlet.ServletException;

public class OhjausparametritTomcat {
    static final String HAKU_MODULE_ROOT = ProjectRootFinder.findProjectRoot() + "/ohjausparametrit-service";
    static final String HAKU_CONTEXT_PATH = "/ohjausparametrit-service";
    static final int DEFAULT_PORT = 9093;
    static final int DEFAULT_AJP_PORT = 8506;
    private static EmbeddedTomcat tomcat = null;

    public final static void main(String... args) throws ServletException, LifecycleException {
        create(Integer.parseInt(System.getProperty("ohjausparametrit-service.port", String.valueOf(DEFAULT_PORT))),
                Integer.parseInt(System.getProperty("ohjausparametrit-service.port.ajp", String.valueOf(DEFAULT_AJP_PORT)))
        ).start().await();
    }

    public static EmbeddedTomcat create(int port, int ajpPort) {
        return new EmbeddedTomcat(port, ajpPort, HAKU_MODULE_ROOT, HAKU_CONTEXT_PATH);
    }

}
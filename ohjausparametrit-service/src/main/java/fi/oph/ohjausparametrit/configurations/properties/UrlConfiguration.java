package fi.oph.ohjausparametrit.configurations.properties;

import fi.vm.sade.properties.OphProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.util.StringUtils;

@Configuration
public class UrlConfiguration extends OphProperties {

    @Autowired
    public UrlConfiguration(Environment environment) {
        addFiles("/ohjausparametrit-service-oph.properties");
        addOverride("host-cas", environment.getRequiredProperty("host.host-cas"));
    }
}

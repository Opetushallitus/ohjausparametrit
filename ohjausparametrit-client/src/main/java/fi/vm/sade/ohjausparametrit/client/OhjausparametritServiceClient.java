/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package fi.vm.sade.ohjausparametrit.client;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author mlyly
 */
public class OhjausparametritServiceClient {

    private static final Logger LOG = LoggerFactory.getLogger(OhjausparametritServiceClient.class);

    String _serviceUrl = null;
    // "https://itest-virkailija.oph.ware.fi/ohjausparametrit-service/api/rest/parametri/P/1.2.246.562.29.36319310482";
    CloseableHttpClient _client = null;

    /**
     * Initialize client.
     *
     * @param ohjausparametritBaseUrl ex. "https://itest-virkailija.oph.ware.fi/ohjausparametrit-service/api/rest/parametri"
     */
    public OhjausparametritServiceClient(String ohjausparametritBaseUrl) {
        LOG.info("OhjausparametritServiceClient()");
        _serviceUrl = ohjausparametritBaseUrl;
        _client = HttpClients.createDefault();
    }

    /**
     * Returns map of parameters by path.
     * 
     * For example:
     * <pre>
     * {
     *      "PH_TJT" : parameter,
     *      "PH_HKLPT" : parameter2,
     *      ...
     * }
     * </pre>
     * 
     * @param target
     * @return
     * @throws Exception 
     */
    public Map<String, Parameter> getParametersAsMap(String target) throws Exception {
        Map<String, Parameter> result = new HashMap<String, Parameter>();

        List<Parameter> tmp = getParameters(target);
        for (Parameter parameter : tmp) {
            result.put(parameter.getPath(), parameter);
        }
        return result;                
    }
    
    public List<Parameter> getParameters(String target) throws Exception {
        String url = _serviceUrl + "/ALL/" + target;
        LOG.info("getParameters({})", url);

        HttpGet get = new HttpGet(url);
        CloseableHttpResponse response = _client.execute(get);
        LOG.info("  response: {}", response);

        BufferedReader br = new BufferedReader(new InputStreamReader((response.getEntity().getContent())));

        List<Parameter> result = new ArrayList<Parameter>();
        result = new Gson().fromJson(br, new TypeToken<List<Parameter>>() {
        }.getType());
        LOG.info("  response2: {}", result);

        for (Object object : result) {
            LOG.info("  response3: {}", object);
        }
        
        return result;
    }

    public class Parameter {

        private String name;
        private String path;
        private String value;

        public String getName() {
            return name;
        }

        public String getPath() {
            return path;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return getPath() + "/" + getName() + " == " + getValue() + " - D: " + getValueAsDate();
        }

        public Date getValueAsDate() {
            try {
                return new Date(getValueAsLong());
            } catch (NumberFormatException ex) {
                return null;
            }
        }

        public long getValueAsLong() {
            return Long.parseLong(value);
        }

        public boolean getValueAsBoolean() {
            return Boolean.parseBoolean(value);
        }

    }

}

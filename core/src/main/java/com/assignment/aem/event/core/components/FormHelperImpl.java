package com.assignment.aem.event.core.components;

import com.assignment.aem.event.core.services.form.FormHelper;
import com.day.cq.commons.jcr.JcrConstants;
import com.day.cq.commons.jcr.JcrUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.servlets.post.JSONResponse;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.jcr.Node;
import javax.jcr.RepositoryException;
import java.io.IOException;
import java.util.UUID;


@Component(
        service = {FormHelper.class}
)
@Designate(
        ocd = FormHelperImpl.Config.class
)
public class FormHelperImpl implements FormHelper {

    private static final int DEFAULT_CONNECTION_TIMEOUT = 6000;
    private static final int DEFAULT_SOCKET_TIMEOUT = 6000;
    private static final String DEFAULT_HTTP_ENDPOINT = "http://localhost:4502/bin/eventdata";
    private static final String EVENT_FORM_SUBMISSION_NODE_PATH = "/content/event-registration/event-submission";
    private static final Logger LOG = LoggerFactory.getLogger(FormHelperImpl.class);
    private static final String CHARSET = "UTF-8";

    private static String httpEndPointURL ;

    private CloseableHttpClient client;

    @Reference
    private HttpClientBuilderFactory clientBuilderFactory;

    @Override
    public boolean pushFormData(JSONObject formJsonData) {

        HttpPost post = new HttpPost(httpEndPointURL);

        // This is base 64 encoding of admin credentials in production scenarios this can be changed to service user
        post.addHeader("Authorization", "Basic YWRtaW46YWRtaW4=");
        post.setEntity(new StringEntity(formJsonData.toString(), ContentType.create(JSONResponse.RESPONSE_CONTENT_TYPE,
                CHARSET)));
        try  {
            String response = client.execute(post, new BasicResponseHandler());
            LOG.debug("POSTing form data to '{}' succeeded: response: {}", httpEndPointURL, response);
            return true;
        } catch (IOException e) {
            LOG.error("POSTing form data to '{}' failed: {}", httpEndPointURL, e.getMessage(), e);
            return false;
        }
    }

    public boolean submitDataInJCR(String jsonData, ResourceResolver resourceResolver) {

     if(resourceResolver.getResource(EVENT_FORM_SUBMISSION_NODE_PATH) != null){
         try {
         Resource res = resourceResolver.getResource(EVENT_FORM_SUBMISSION_NODE_PATH);
         UUID uuid = UUID.randomUUID();
         Node dataNode = res.adaptTo(Node.class);
         Node node = JcrUtil.createUniqueNode(dataNode,uuid.toString(), JcrConstants.NT_UNSTRUCTURED,dataNode.getSession());
         JSONObject json = new JSONObject(jsonData);
         if(node != null){
             JcrUtil.setProperty(node,"first-name",json.get("first-name"));
             JcrUtil.setProperty(node,"last-name",json.get("last-name"));
             JcrUtil.setProperty(node,"email",json.get("email"));
                 node.save();
                 dataNode.save();
                 return true ;
             }
         } catch (RepositoryException e) {
             LOG.error("Exception  while submitting data" +e.getMessage());
             return false ;
         } catch (JSONException e) {
             LOG.error("Exception  while parsing json" +e.getMessage());
             return false ;
         }
     }
        return false ;
    }


    @Activate
    public void activate(Config config) {

        String endPoint = config.httpEndpoint();
            if(endPoint != null && endPoint.equals(StringUtils.EMPTY)){
                throw new IllegalArgumentException("HTTP End point cannot be left blank");
            }
        httpEndPointURL = endPoint ;
        LOG.debug("End Point URL:::" +httpEndPointURL);

        int connectionTimeout = config.connectionTimeout();
        if (connectionTimeout < 0) {
            throw new IllegalArgumentException("Connection timeout value cannot be less than 0");
        }
        int socketTimeout = config.socketTimeout();
        if (socketTimeout < 0) {
            throw new IllegalArgumentException("Socket timeout value cannot be less than 0");
        }
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(connectionTimeout)
                .setConnectionRequestTimeout(connectionTimeout)
                .setSocketTimeout(socketTimeout)
                .build();

        client = clientBuilderFactory.newBuilder().setDefaultRequestConfig(requestConfig).build();
    }

    @Deactivate
    protected void deactivate() throws IOException {
        client.close();
    }

    @ObjectClassDefinition(
            name = "Event Registration Form API Client",
            description = "A HTTP Client wrapper for Form API requests"
    )
    public @interface Config {

        @AttributeDefinition(
                name = "HTTP Endpoint",
                description = "HTTP endpoint where event registration servlet pushed the data (defaults to mock AEM endpoint)",
                defaultValue = {"" + DEFAULT_HTTP_ENDPOINT}
        )
        String httpEndpoint() default DEFAULT_HTTP_ENDPOINT;

        @AttributeDefinition(
                name = "Connection timeout",
                description = "Timeout in milliseconds until a connection is established. A timeout value of zero is interpreted as an " +
                        "infinite timeout. Default is 6000ms",
                defaultValue = {"" + DEFAULT_CONNECTION_TIMEOUT}
        )
        int connectionTimeout() default DEFAULT_CONNECTION_TIMEOUT;

        @AttributeDefinition(
                name = "Socket timeout",
                description = "Timeout in milliseconds for waiting for data or a maximum period of inactivity between two consecutive " +
                        "data packets. Default is 6000ms",
                defaultValue = {"" + DEFAULT_SOCKET_TIMEOUT}
        )
        int socketTimeout() default DEFAULT_SOCKET_TIMEOUT;
    }
}

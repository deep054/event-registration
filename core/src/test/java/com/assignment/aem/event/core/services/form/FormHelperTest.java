
package com.assignment.aem.event.core.services.form;


import com.assignment.aem.event.core.components.FormHelperImpl;
import io.wcm.testing.mock.aem.junit5.AemContext;
import io.wcm.testing.mock.aem.junit5.AemContextExtension;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.osgi.services.HttpClientBuilderFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.lang.annotation.Annotation;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith({AemContextExtension.class})
public class FormHelperTest {

   //private WireMockServer wireMockServer;
    private int wireMockPort;
    private FormHelper underTest;

    @BeforeEach
    void setUp(AemContext context) {

        context.registerService(HttpClientBuilderFactory.class, HttpClientBuilder::create);
        underTest = context.registerInjectActivateService(new FormHelperImpl());
        ((FormHelperImpl) underTest).activate(new FormHelperImpl.Config() {
            @Override
            public String httpEndpoint() {
                return "http://localhost:4502/bin/eventdata";
            }

            @Override
            public int connectionTimeout() {
                return 6000;
            }

            @Override
            public int socketTimeout() {
                return 6000;
            }

            @Override
            public Class<? extends Annotation> annotationType() {
                return null;
            }
        });
    }

    @Test
    void testSendFormDataWithSuccess() throws JSONException {

        JSONObject formData = new JSONObject();
        formData.append("first-name", "Sample Name");
        formData.append("last-name", "Sample Last Name");
        formData.append("email", "sample@test.com");
        assertTrue(underTest.pushFormData(formData));
    }

}


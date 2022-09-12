package com.assignment.aem.event.core.services.form;

import org.apache.sling.api.resource.ResourceResolver;
import org.json.JSONObject;

/**
 * A service that posts form data as JSON to a remote service.
 *
 */
public interface FormHelper {

    /**
     * Push form data to a remote service.
     *
     * @param formJsonData    the form data JSON object
     * @return true if the remote request was successful, otherwise false
     */
    boolean pushFormData(JSONObject formJsonData);

    /**
     * Submits data into JCR .
     *
     * @param jsonData  the form data JSON object
     * @param resourceResolver the Resource Resolver Object
     * @return true if the remote request was successful, otherwise false
     */
    public boolean submitDataInJCR(String jsonData, ResourceResolver resourceResolver);
}

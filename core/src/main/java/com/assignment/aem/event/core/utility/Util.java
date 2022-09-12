package com.assignment.aem.event.core.utility;

import com.google.common.collect.ImmutableSet;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(Util.class);

    private static final Set<String> INTERNAL_PARAMETER = ImmutableSet.of(
            ":formstart",
            "_charset_",
            ":redirect",
            ":cq_csrf_token"
    );

    /**
     * Converts request parameters to a JSON object and filter AEM specific parameters out.
     *
     * @param request - the current {@link SlingHttpServletRequest}
     * @return JSON object of the request parameters
     */
    public static JSONObject getJsonOfRequestParameters(SlingHttpServletRequest request) throws JSONException {
        Set<String> formFieldNames = getFormFieldNames(request);
        org.json.JSONObject jsonObj = new org.json.JSONObject();
        Map<String, String[]> params = request.getParameterMap();

        for (Map.Entry<String, String[]> entry : params.entrySet()) {
            if (!INTERNAL_PARAMETER.contains(entry.getKey()) && formFieldNames.contains(entry.getKey())) {
                String[] v = entry.getValue();
                Object o = (v.length == 1) ? v[0] : v;
                jsonObj.put(entry.getKey(), o);
            }
        }
        return jsonObj;
    }

    /**
     * Returns a set of form field names for the form specified in the request.
     *
     * @param request - the current {@link SlingHttpServletRequest}
     * @return Set of form field names
     */
    public static Set<String> getFormFieldNames(SlingHttpServletRequest request) {
        Set<String> formFieldNames = new LinkedHashSet<>();
        getFieldNames(request.getResource(), formFieldNames);
        return formFieldNames;
    }

    public static void getFieldNames(Resource resource, Set<String> fieldNames) {
        if (resource != null) {
            for (Resource child : resource.getChildren()) {
                String name = child.getValueMap().get("name", String.class);
                if (StringUtils.isNotEmpty(name)) {
                    fieldNames.add(name);
                }
                getFieldNames(child, fieldNames);
            }
        }
    }

}

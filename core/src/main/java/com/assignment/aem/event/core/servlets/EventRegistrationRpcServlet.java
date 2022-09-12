package com.assignment.aem.event.core.servlets;


import com.assignment.aem.event.core.services.form.FormHelper;
import com.assignment.aem.event.core.utility.Util;
import com.day.cq.wcm.foundation.forms.FormsHandlingServletHelper;
import com.drew.lang.annotations.NotNull;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.apache.sling.jcr.contentloader.ContentTypeUtil;
import org.json.JSONException;
import org.json.JSONObject;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
/**
 * This servlet is used by the core form container as a form action to send the form data to a remote endpoint.
 */
@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_POST,
                "sling.servlet.resourceTypes=" + EventRegistrationRpcServlet.RESOURCE_TYPE,
                "sling.servlet.selectors=" + EventRegistrationRpcServlet.SELECTOR
        }
)

public class EventRegistrationRpcServlet extends SlingAllMethodsServlet {

    static final String RESOURCE_TYPE = "event-registration/components/form/actions/rpc";
    static final String SELECTOR = "post";
    private static final String ATTR_RESOURCE = FormsHandlingServletHelper.class.getName() + "/resource";
    private static final Logger LOG = LoggerFactory.getLogger(EventRegistrationRpcServlet.class);
    private static  String SUCCESS_PAGE = "/content/event-registration/us/en/event-registration/error";
    private static  String ERROR_PAGE = "/content/event-registration/us/en/event-registration/success";
    public static final String HTML_SUFFIX = ".html";

    @Reference
    private FormHelper formHelper;

    protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType(ContentTypeUtil.TYPE_JSON + ";"+ StandardCharsets.UTF_8);
        JSONObject formJsonData  = new JSONObject();

        if(request.getParameter("success-page") != null){

            SUCCESS_PAGE = request.getParameter("success-page");
        }
        if(request.getParameter("error-page") != null){

            ERROR_PAGE = request.getParameter("error-page");
        }
        try{
          formJsonData = Util.getJsonOfRequestParameters(request);
        }catch(JSONException ex) {
            LOG.error("Error in getting data from request parameter", ex.getMessage());

        }
        boolean result = formHelper.pushFormData(formJsonData);

        if(result == true){
            response.sendRedirect(SUCCESS_PAGE+HTML_SUFFIX);
        }else {
            response.sendRedirect(ERROR_PAGE+HTML_SUFFIX);
        }
    }
}

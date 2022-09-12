package com.assignment.aem.event.core.servlets;


import com.assignment.aem.event.core.services.form.FormHelper;
import com.drew.lang.annotations.NotNull;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * This servlet provides mock end point to submit event data and store user details in JCR.
 */
@Component(
        service = Servlet.class,
        property = {
                "sling.servlet.methods=" + HttpConstants.METHOD_GET,
                "sling.servlet.paths=" + "/bin/eventdata",
                "sling.servlet.selectors=" + EventRegistrationRpcServlet.SELECTOR
        }
)

public class HandleDataSubmission extends SlingAllMethodsServlet {

    private static final Logger LOG = LoggerFactory.getLogger(HandleDataSubmission.class);

    @Reference
    FormHelper formHelper ;

    protected void doPost(@NotNull SlingHttpServletRequest request, @NotNull SlingHttpServletResponse response)
            throws ServletException, IOException {

        try {
            String body = IOUtils.toString(request.getReader());
            LOG.info("String data in JCR" +body);
            boolean result = formHelper.submitDataInJCR(body,request.getResourceResolver());
        } catch (Exception e) {
            LOG.error("Error while parsing request body" + e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}

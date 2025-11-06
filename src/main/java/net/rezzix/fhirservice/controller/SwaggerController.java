package net.rezzix.fhirservice.controller;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Controller to serve Swagger UI directly via embedded HTML to avoid servlet conflicts
 */
@Controller
public class SwaggerController {

    @GetMapping(value = "/custom-swagger-ui.html", produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public String swaggerUi() {
        // Return the HTML content directly as a string
        return "<!DOCTYPE html>\n" +
               "<html>\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <title>Swagger UI for DPP FHIR Service</title>\n" +
               "    <link rel=\"stylesheet\" type=\"text/css\" href=\"https://unpkg.com/swagger-ui-dist@latest/swagger-ui.css\"/>\n" +
               "</head>\n" +
               "<body>\n" +
               "    <div id=\"swagger-ui\"></div>\n" +
               "    <script src=\"https://unpkg.com/swagger-ui-dist@latest/swagger-ui-bundle.js\"></script>\n" +
               "    <script>\n" +
               "        SwaggerUIBundle({\n" +
               "            url: '/v3/api-docs',\n" +
               "            dom_id: '#swagger-ui',\n" +
               "            presets: [\n" +
               "                SwaggerUIBundle.presets.apis,\n" +
               "                SwaggerUIBundle.presets.standalone\n" +
               "            ]\n" +
               "        });\n" +
               "    </script>\n" +
               "</body>\n" +
               "</html>";
    }
    
    @GetMapping("/swagger")
    public String swaggerRedirect() {
        return "redirect:/custom-swagger-ui.html";
    }
}
package com.videotel.voa.lti;

import com.fasterxml.jackson.databind.util.TypeKey;
import com.videotel.voa.model.DbAssessment;
import com.videotel.voa.response.ChoiceResponse;
import com.videotel.voa.shared.AssessmentItemWrapper;
import com.videotel.voa.shared.AssessmentTestWrapper;
import com.videotel.voa.shared.interactions.SimpleChoiceRenderer;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;

import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@CrossOrigin
@RequestMapping("/lti/")
@RestController
public class LtiController {

    @RequestMapping(value="/launch", method = POST)
    public RedirectView launch(HttpSession session, @RequestParam Map<String,String> allRequestParams) {
        System.out.println(allRequestParams);
        // successfully launched
        RedirectView redirectView = new RedirectView();
        redirectView.setUrl("https://localhost:8080");
        return redirectView;
    }


}

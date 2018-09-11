package com.videotel.voa.delivery.controllers;

import com.videotel.voa.shared.AssessmentTestWrapper;
import com.videotel.voa.shared.interactions.SimpleChoiceRenderer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectUtilities;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.FieldValue;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.reading.QtiObjectReader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.AssessmentObjectResolver;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.serialization.QtiSerializer;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.validation.AssessmentObjectValidator;
import uk.ac.ed.ph.jqtiplus.validation.ItemValidationResult;
import uk.ac.ed.ph.jqtiplus.value.BaseType;
import uk.ac.ed.ph.jqtiplus.value.Cardinality;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NullResourceLocator;

import java.util.Date;

import static org.springframework.web.bind.annotation.RequestMethod.GET;

@RequestMapping("/api/runner/")
@RestController
public class RunnerController {

    private AssessmentTestWrapper test;

    public RunnerController() {
        test = new AssessmentTestWrapper("samples/simple-linear-individual.xml");
    }

    @RequestMapping(method = GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String index() {
        return "VOA QTI Assessment POC";
    }

    @RequestMapping(value="/start", method = GET)
    public SimpleChoiceRenderer start() {

        Date testEntryTimestamp = new Date();
        Date testPartEntryTimestamp = ObjectUtilities.addToTime(testEntryTimestamp, 1000L);

        System.out.println("Entering the test");
        test.enterTest();

        System.out.println("\r\nRendering the next item:");
        //test.getCurrentItem().renderItem();
        return test.getCurrentItem().getInteraction(0);
    }

    @RequestMapping(value="/next", method = GET)
    public SimpleChoiceRenderer next() {
        System.out.println("\nMoving to the next item...");
        test.testSessionController.advanceItemLinear(new Date());
        //test.getCurrentItem().renderItem();
        return test.getCurrentItem().getInteraction(0);
    }

}
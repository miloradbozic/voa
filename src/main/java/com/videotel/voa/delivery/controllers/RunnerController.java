package com.videotel.voa.delivery.controllers;

import com.videotel.voa.response.ChoiceResponse;
import com.videotel.voa.shared.AssessmentTestWrapper;
import com.videotel.voa.shared.interactions.SimpleChoiceRenderer;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
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
import java.util.List;

import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.POST;

@CrossOrigin
@RequestMapping("/api/runner/")
@RestController
public class RunnerController {

    private AssessmentTestWrapper test;
    private int currentQuestion = 1;

    public RunnerController() {

    }

    @RequestMapping(value="/start", method = GET)
    public ChoiceResponse start() {
        test = new AssessmentTestWrapper("samples/simple-linear-individual.xml");
        currentQuestion = 1;
        Date testEntryTimestamp = new Date();
        Date testPartEntryTimestamp = ObjectUtilities.addToTime(testEntryTimestamp, 1000L);

        System.out.println("Entering the test");
        test.enterTest();

        System.out.println("\r\nRendering the next item:");
        //test.getCurrentItem().renderItem();
        SimpleChoiceRenderer renderer = test.getCurrentItem().getInteraction(0);
        return new ChoiceResponse(renderer, 3, currentQuestion++);
    }

    //@todo fix when there are no more questions left
    @RequestMapping(value="/next", method = GET)
    public ChoiceResponse next() {
        System.out.println("\nMoving to the next item...");
        test.testSessionController.advanceItemLinear(new Date());
        //test.getCurrentItem().renderItem();
        SimpleChoiceRenderer renderer = test.getCurrentItem().getInteraction(0);
        return new ChoiceResponse(renderer, 3, currentQuestion++);
    }

    @RequestMapping(value = "/submit-answer", method = POST)
    public ResponseEntity add(@RequestParam("answerId") String answerId) {
        System.out.println(answerId);

        test.handleChoiceResponse(new Date(), "c" + answerId);

        //get score item1
        String scoreItem1 = test.getItemScore(1); //i1
        System.out.println("First item score: " + scoreItem1);

        //score item2
        String scoreItem2 = test.getItemScore(2); //i2
        System.out.println("Second item score: " + scoreItem2);

        //score item3
        String scoreItem3 = test.getItemScore(3); //i3
        System.out.println("Third item score: " + scoreItem3);

        //check if test scoring is performed
        boolean processed = test.isOutcomeProcessed();
        System.out.println("Test processed: " + processed);
        return ResponseEntity.ok("OK");
    }

}
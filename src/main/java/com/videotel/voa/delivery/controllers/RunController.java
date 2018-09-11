package com.videotel.voa.delivery.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
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

@RequestMapping("/api/run")
@RestController
public class RunController {

    @RequestMapping(method = RequestMethod.GET, produces = {MediaType.APPLICATION_JSON_VALUE})
    public String index() {

        /* Create empty AssessmentItem and add necessary properties to make it valid */
        final AssessmentItem assessmentItem = new AssessmentItem();
        assessmentItem.setIdentifier("MyItem");
        assessmentItem.setTitle("Videotel Question 1");
        assessmentItem.setAdaptive(Boolean.FALSE);
        assessmentItem.setTimeDependent(Boolean.FALSE);

        /* Declare a SCORE outcome variable */
        final OutcomeDeclaration score = new OutcomeDeclaration(assessmentItem);
        score.setIdentifier(Identifier.assumedLegal("SCORE"));
        score.setCardinality(Cardinality.SINGLE);
        score.setBaseType(BaseType.FLOAT);
        final DefaultValue defaultValue = new DefaultValue(score);
        defaultValue.getFieldValues().add(new FieldValue(defaultValue, new FloatValue(0.0)));
        score.setDefaultValue(defaultValue);
        assessmentItem.getOutcomeDeclarations().add(score);

        /* Validate */
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(NullResourceLocator.getInstance(), false);
        final AssessmentObjectResolver resolver = new AssessmentObjectResolver(qtiObjectReader);
        final ResolvedAssessmentItem resolvedAssessmentItem = resolver.resolveAssessmentItem(assessmentItem);
        final AssessmentObjectValidator validator = new AssessmentObjectValidator(jqtiExtensionManager);
        final ItemValidationResult validationResult = validator.validateItem(resolvedAssessmentItem);

        /* Print out validation result */
        System.out.println("Validation result:");
        ObjectDumper.dumpObjectToStdout(validationResult);

        /* Finally serialize the assessmentItem to XML and print it out */
        final QtiSerializer qtiSerializer = new QtiSerializer(jqtiExtensionManager);
        System.out.println("Serialized XML:");
        //System.out.println(qtiSerializer.serializeJqtiObject(assessmentItem));



        return qtiSerializer.serializeJqtiObject(assessmentItem);
    }
}
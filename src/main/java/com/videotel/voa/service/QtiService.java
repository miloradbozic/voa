package com.videotel.voa.service;

import com.videotel.voa.model.Assessment;
import com.videotel.voa.model.Question;
import com.videotel.voa.repository.AssessmentItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.ObjectDumper;
import uk.ac.ed.ph.jqtiplus.node.content.ItemBody;
import uk.ac.ed.ph.jqtiplus.node.content.basic.TextRun;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.CorrectResponse;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.ChoiceInteraction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Prompt;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleChoice;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.item.response.processing.ResponseProcessing;
import uk.ac.ed.ph.jqtiplus.node.outcome.declaration.OutcomeDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.FieldValue;
import uk.ac.ed.ph.jqtiplus.node.shared.declaration.DefaultValue;
import uk.ac.ed.ph.jqtiplus.node.test.*;
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
import uk.ac.ed.ph.jqtiplus.value.IdentifierValue;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.NullResourceLocator;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class QtiService {

    @Autowired
    AssessmentItemRepository assessmentItemRepository;

    public String generateAssessmentItemQtiXml(Question question) {
        String identifier = "item-" + this.getRandomString();
        final AssessmentItem assessmentItem = new AssessmentItem();
        assessmentItem.setIdentifier("item-" + identifier); //todo dynamic
        assessmentItem.setTitle(question.getQuestion());
        assessmentItem.setAdaptive(Boolean.FALSE);
        assessmentItem.setTimeDependent(Boolean.FALSE);

        /* Declare a RESPONSE declaration */
        final ResponseDeclaration responseDeclaration = new ResponseDeclaration(assessmentItem);
        assessmentItem.getResponseDeclarations().add(responseDeclaration);
        responseDeclaration.setIdentifier(Identifier.assumedLegal("RESPONSE"));
        responseDeclaration.setCardinality(Cardinality.SINGLE);
        responseDeclaration.setBaseType(BaseType.IDENTIFIER);
        final String correctAnswer = "c" + question.getAnswer();
        final CorrectResponse correctResponse = new CorrectResponse(responseDeclaration);
        responseDeclaration.setCorrectResponse(correctResponse);
        correctResponse.getFieldValues().add(new FieldValue(correctResponse, new IdentifierValue(correctAnswer)));

        /* Declare a SCORE outcome variable */
        final OutcomeDeclaration score = new OutcomeDeclaration(assessmentItem);
        score.setIdentifier(Identifier.assumedLegal("SCORE"));
        score.setCardinality(Cardinality.SINGLE);
        score.setBaseType(BaseType.FLOAT);
        final DefaultValue defaultValue = new DefaultValue(score);
        defaultValue.getFieldValues().add(new FieldValue(defaultValue, new FloatValue(0.0)));
        score.setDefaultValue(defaultValue);
        assessmentItem.getOutcomeDeclarations().add(score);

        /* Add a choiceInteraction with a prompt and 2 simpleChoices,
         * linked to the RESPONSE variable we declared above */
        final ItemBody itemBody = new ItemBody(assessmentItem);
        final ChoiceInteraction choiceInteraction = new ChoiceInteraction(itemBody);
        itemBody.getBlocks().add(choiceInteraction);
        choiceInteraction.setResponseIdentifier(responseDeclaration.getIdentifier());
        choiceInteraction.setShuffle(true); // choices in random order
        choiceInteraction.setMaxChoices(1);
        final Prompt prompt = new Prompt(choiceInteraction);
        choiceInteraction.setPrompt(prompt);
        prompt.getInlineStatics().add(new TextRun(prompt, question.getQuestion()));

        for (int i=1; i<=5; ++i) {
            if (!question.getChoice(i).equals("")) {
                final SimpleChoice simpleChoice = new SimpleChoice(choiceInteraction);
                simpleChoice.setIdentifier(Identifier.assumedLegal("c" + i));
                simpleChoice.getFlowStatics().add(new TextRun(simpleChoice, question.getChoice(i)));
                choiceInteraction.getSimpleChoices().add(simpleChoice);
            }
        }

        assessmentItem.setItemBody(itemBody);

        /* Add response processing, using one of the default templates */
        final ResponseProcessing responseProcessing = new ResponseProcessing(assessmentItem);
        responseProcessing.setTemplate(URI.create("http://www.imsglobal.org/question/qti_v2p1/rptemplates/match_correct"));
        assessmentItem.setResponseProcessing(responseProcessing);

        assessmentItem.setSystemId(URI.create(UUID.randomUUID().toString()));

        /* Validate */
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final QtiXmlReader qtiXmlReader = new QtiXmlReader(jqtiExtensionManager);
        final QtiObjectReader qtiObjectReader = qtiXmlReader.createQtiObjectReader(NullResourceLocator.getInstance(), false);
        final AssessmentObjectResolver resolver = new AssessmentObjectResolver(qtiObjectReader);
        final ResolvedAssessmentItem resolvedAssessmentItem = resolver.resolveAssessmentItem(assessmentItem);
        final AssessmentObjectValidator validator = new AssessmentObjectValidator(jqtiExtensionManager);
        final ItemValidationResult validationResult = validator.validateItem(resolvedAssessmentItem);

        /* Print out validation result */
        //System.out.println("Validation result:");
        //ObjectDumper.dumpObjectToStdout(validationResult);

        /* Finally serialize the assessmentItem to XML and print it out */
        final QtiSerializer qtiSerializer = new QtiSerializer(jqtiExtensionManager);
        String qtiAssessmentXml = qtiSerializer.serializeJqtiObject(assessmentItem);

        String rootPath = QtiService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        File dir = new File(rootPath + File.separator + "webapp"+File.separator+"res"+File.separator+"qti");
        if (!dir.exists())
            dir.mkdirs();

        java.io.FileWriter fw = null;
        String xmlPath = identifier + ".xml";
        String xmlFileName = "qtifiles/" + xmlPath;
        try {
            fw = new java.io.FileWriter(xmlFileName);
            fw.write(qtiAssessmentXml);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //return qtiAssessmentXml;
        return xmlPath;
    }

    private String getRandomString() {
        SecureRandom prng = null;
        try {
            prng = SecureRandom.getInstance("SHA1PRNG");
            //generate a random number
            String randomNum = Integer.valueOf(prng.nextInt()).toString();
            //get its digest
            MessageDigest sha = MessageDigest.getInstance("SHA-1");
            byte[] result =  sha.digest(randomNum.getBytes());
            return hexEncode(result);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String hexEncode(byte[] input){
        StringBuilder result = new StringBuilder();
        char[] digits = {'0', '1', '2', '3', '4','5','6','7','8','9','a','b','c','d','e','f'};
        for (int idx = 0; idx < input.length; ++idx) {
            byte b = input[idx];
            result.append(digits[ (b&0xf0) >> 4 ]);
            result.append(digits[ b&0x0f]);
        }
        return result.toString();
    }

    public String generateAssessmentXml(Assessment assessment) {

        List<com.videotel.voa.model.AssessmentItem> questions = assessmentItemRepository.findAll();

        //start creating assessment item
        /* Create empty Assessment and add necessary properties to make it valid */

        final AssessmentTest assessmentTest = new AssessmentTest();
        assessmentTest.setIdentifier("MYTest");
        assessmentTest.setTitle("Videotel Test Example");

        // Declare a SCORE outcome variable
        final OutcomeDeclaration score = new OutcomeDeclaration(assessmentTest);
        score.setIdentifier(Identifier.assumedLegal("OP_DONE"));
        score.setCardinality(Cardinality.SINGLE);
        score.setBaseType(BaseType.FLOAT);
        final DefaultValue defaultValue = new DefaultValue(score);
        defaultValue.getFieldValues().add(new FieldValue(defaultValue, new FloatValue(0.0)));
        score.setDefaultValue(defaultValue);
        assessmentTest.getOutcomeDeclarations().add(score);

        // Declare test part variable
        final TestPart testPart = new TestPart(assessmentTest);
        testPart.setIdentifier(Identifier.assumedLegal("p"));
        testPart.setNavigationMode(NavigationMode.LINEAR);
        testPart.setSubmissionMode(SubmissionMode.INDIVIDUAL);
        final AssessmentSection assessmentSection = new AssessmentSection(testPart);
        assessmentSection.setVisible(true);
        assessmentSection.setIdentifier(Identifier.assumedLegal("s1"));
        assessmentSection.setTitle("Section");

        String number1= assessment.getNumber(1);
        String number2 = assessment.getNumber(2);
        String number4 = assessment.getNumber(4);

        int k = 1;
        for (int i = 1; i <= 6; ++i)
            if (assessment.getNumber(i) != null && !assessment.getNumber(i).equals("")) {
                //get questions with this tag
                String tag = assessment.getTag(i);
                List<com.videotel.voa.model.AssessmentItem> taggedQuestions = questions.stream()
                        .filter(q -> q.getTag().equals(tag))
                        .collect(Collectors.toList());

                String e = assessment.getNumber(i);

                for (int j = 0; j < Integer.valueOf(assessment.getNumber(i)); ++j) {
                    final AssessmentItemRef itemRef = new AssessmentItemRef(assessmentSection);
                    com.videotel.voa.model.AssessmentItem aItem = taggedQuestions.get(j);
                    itemRef.setHref(URI.create(aItem.getPath()));
                    itemRef.setIdentifier(Identifier.assumedLegal("i" + k++));
                    assessmentSection.getSectionParts().add(itemRef);
                }
            }

        testPart.getAssessmentSections().add(assessmentSection);
        assessmentTest.getTestParts().add(testPart);

        //validate
        // @todo
        //System.out.println("Validation result:");
        //ObjectDumper.dumpObjectToStdout(validationResult);

        // Finally serialize the assessmentItem to XML and print it out
        final JqtiExtensionManager jqtiExtensionManager = new JqtiExtensionManager();
        final QtiSerializer qtiSerializer = new QtiSerializer(jqtiExtensionManager);
        System.out.println("Serialized XML:");
        System.out.println(qtiSerializer.serializeJqtiObject(assessmentTest));

        String qtiAssessmentTestXml = qtiSerializer.serializeJqtiObject(assessmentTest);

        //save in filesystem
        String rootPath = QtiService.class.getProtectionDomain().getCodeSource().getLocation().getPath();
//        File dir = new File(rootPath + File.separator + "webapp"+File.separator+"res"+File.separator+"qti");
//        if (!dir.exists())
//            dir.mkdirs();

        java.io.FileWriter fw = null;
        String xmlFileName = "qtifiles/" + "assessment" + ".xml";
        try {
            fw = new java.io.FileWriter(xmlFileName);
            fw.write(qtiAssessmentTestXml);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}

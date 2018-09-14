package com.videotel.voa.authoring;

import com.videotel.voa.shared.AssessmentItemWrapper;
import com.videotel.voa.shared.AssessmentTestWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.internal.util.DumpMode;
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

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static org.springframework.web.bind.annotation.RequestMethod.POST;

@RequestMapping("/api/author/")
@RestController
public class AuthorController {

    @RequestMapping(value = "/create", method = POST)
    public String create(@RequestParam("question") String question,
                                 @RequestParam("choice1") String choice1,
                                 @RequestParam("choice2") String choice2,
                                 @RequestParam("choice3") String choice3,
                                 @RequestParam("answer") String answer) {


    String randomString = this.getRandomString();
    final AssessmentItem assessmentItem = new AssessmentItem();
    assessmentItem.setIdentifier("Item-"+randomString); //todo dynamic
    assessmentItem.setTitle(question);
    assessmentItem.setAdaptive(Boolean.FALSE);
    assessmentItem.setTimeDependent(Boolean.FALSE);

    /* Declare a RESPONSE declaration */
    final ResponseDeclaration responseDeclaration = new ResponseDeclaration(assessmentItem);
    assessmentItem.getResponseDeclarations().add(responseDeclaration);
    responseDeclaration.setIdentifier(Identifier.assumedLegal("RESPONSE"));
    responseDeclaration.setCardinality(Cardinality.SINGLE);
    responseDeclaration.setBaseType(BaseType.IDENTIFIER);
    final String correctAnswer = "c" + answer;
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
    prompt.getInlineStatics().add(new TextRun(prompt, question));
    final SimpleChoice simpleChoice1 = new SimpleChoice(choiceInteraction);
    simpleChoice1.setIdentifier(Identifier.assumedLegal("c1"));
    simpleChoice1.getFlowStatics().add(new TextRun(simpleChoice1, choice1));
    choiceInteraction.getSimpleChoices().add(simpleChoice1);
    final SimpleChoice simpleChoice2 = new SimpleChoice(choiceInteraction);
    simpleChoice2.setIdentifier(Identifier.assumedLegal("c2"));
    simpleChoice2.getFlowStatics().add(new TextRun(simpleChoice2, choice2));
    choiceInteraction.getSimpleChoices().add(simpleChoice2);
    final SimpleChoice simpleChoice3 = new SimpleChoice(choiceInteraction);
    simpleChoice3.setIdentifier(Identifier.assumedLegal("c3"));
    simpleChoice3.getFlowStatics().add(new TextRun(simpleChoice3, choice3));
    choiceInteraction.getSimpleChoices().add(simpleChoice3);
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
    System.out.println("Validation result:");
    ObjectDumper.dumpObjectToStdout(validationResult);
    System.out.println(question + choice1 + choice2 + choice3 + answer);

    /* Finally serialize the assessmentItem to XML and print it out */
    final QtiSerializer qtiSerializer = new QtiSerializer(jqtiExtensionManager);
    System.out.println("Serialized XML:");
    String qtiAssessmentXml = qtiSerializer.serializeJqtiObject(assessmentItem);

    /*
    String rootPath = AuthorController.class.getProtectionDomain().getCodeSource().getLocation().getPath();
    File dir = new File(rootPath + File.separator + "webapp"+File.separator+"res"+File.separator+"qti");
    if (!dir.exists())
        dir.mkdirs();

    java.io.FileWriter fw = null;
    try {
        fw = new java.io.FileWriter("qtifiles/choice5.xml");
        fw.write(qtiAssessmentXml);
        fw.close();
    } catch (IOException e) {
        e.printStackTrace();
    }

    File testFile = new File("C:\\Users\\legion\\Code\\voa\\qtifiles\\choice5.xml"); //C:/Users/legion/Code/voa/qtifiles/choice5.xml
    AssessmentItemWrapper itemWrapper = new AssessmentItemWrapper(testFile.toURI().toString());
//    itemWrapper.bindAndCommitResponses("c2");
//    itemWrapper.processResponseAndCloseItem();
//    System.out.println(ObjectDumper.dumpObject(itemWrapper.getState(), DumpMode.DEEP));
//
//        Path newFilePath = Paths.get("src/main/resources/newFile_jdk7.txt");
//        try {
//            Files.createFile(newFilePath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        */

        return qtiAssessmentXml;

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

}

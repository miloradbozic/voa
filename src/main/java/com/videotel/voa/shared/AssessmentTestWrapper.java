package com.videotel.voa.shared;

import com.videotel.voa.shared.interactions.SimpleChoiceRenderer;
import uk.ac.ed.ph.jqtiplus.node.item.AssessmentItem;
import uk.ac.ed.ph.jqtiplus.node.item.CorrectResponse;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.response.declaration.ResponseDeclaration;
import uk.ac.ed.ph.jqtiplus.node.shared.FieldValue;
import uk.ac.ed.ph.jqtiplus.node.test.AssessmentItemRef;
import uk.ac.ed.ph.jqtiplus.node.test.TestPart;
import uk.ac.ed.ph.jqtiplus.running.TestSessionController;
import uk.ac.ed.ph.jqtiplus.state.*;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.value.BooleanValue;
import uk.ac.ed.ph.jqtiplus.value.FloatValue;
import uk.ac.ed.ph.jqtiplus.value.Value;

import java.net.URI;
import java.util.*;

/*
@missing functions:
    assertChoiceItemResponseProcessingRun(item1SessionState);
    assertChoiceItemResponseProcessingNotRun(item2SessionState);
    assertOutcomeProcessingRun();
 */
public class AssessmentTestWrapper {
    public final Identifier CHOICE_ITEM_RESPONSE = Identifier.assumedLegal("RESPONSE");
    public final Identifier CHOICE_ITEM_SCORE = Identifier.assumedLegal("SCORE");
    public final Identifier TEST_SCORE = Identifier.assumedLegal("TEST_SCORE");
    public static final Identifier TEST_OP_DONE = Identifier.assumedLegal("OP_DONE");

    public TestSessionController testSessionController;
    public TestSessionState testSessionState;       //@todo refactor
    protected TestPlan testPlan;
    protected TestProcessingMap testProcessingMap;
    protected List<TestPlanNode> itemRefs = new ArrayList<>();

    String filePath;
    protected String getTestFilePath() {
        return this.filePath;
    }

    public AssessmentTestWrapper(String filePath) {
        this.filePath = filePath;
        this.initTestSessionController();
    }

    public void initTestSessionController() {
        Date testEntryTimestamp = new Date();
        this.testSessionController = TestHelper.loadUnitTestAssessmentTestForControl(this.getTestFilePath(), true);
        this.testSessionController.initialize(testEntryTimestamp);
        this.testSessionState = this.testSessionController.getTestSessionState();
        this.testPlan = this.testSessionState.getTestPlan();
        this.testProcessingMap = testSessionController.getTestProcessingMap();

        // @todo: we want to be able to access nodes by identifiers (i1, i2 etc)
        itemRefs = testPlan.getTestPartNodes().get(0).searchDescendants(TestPlanNode.TestNodeType.ASSESSMENT_ITEM_REF);
        //Todo debud : this.getItemQuestion(itemRefs.get(0));
//        testPlanNodesByIdentifierStringMap = new HashMap<String, TestPlanNode>();
//        for (final String testNodeIdentifierString : testNodes()) {
//            final TestPlanNode testPlanNode = TestHelper.getSingleTestPlanNode(testPlan, testNodeIdentifierString);
//            testPlanNodesByIdentifierStringMap.put(testNodeIdentifierString, testPlanNode);
//        }
    }

    public void enterTest() {
        testSessionController.enterTest(new Date());
        testSessionController.enterNextAvailableTestPart(new Date());
    }

    public void handleChoiceResponse(final Date timestamp, final String choiceIdentifier) {
        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        responseMap.put(CHOICE_ITEM_RESPONSE, new StringResponseData(choiceIdentifier));
        this.testSessionController.handleResponsesToCurrentItem(timestamp, responseMap);
    }

    //@todo refactor - we want to be able to get item by identifier, not like this (testSessionController.selectItemNonlinear(operationTimestamp, getTestNodeKey("i1")))
    public void selectItem(int identifier) {
        int id = identifier - 1;
        final TestPlanNode itemRefNode = testPlan.getTestPartNodes().get(0).searchDescendants(TestPlanNode.TestNodeType.ASSESSMENT_ITEM_REF).get(id);
//        AssessmentSection s = new AssessmentSection(null);
//        AssessmentItemRef itre = new AssessmentItemRef(s);

        this.testSessionController.selectItemNonlinear(new Date(), itemRefNode.getKey());
        TestPart tp = this.testSessionController.getCurrentTestPart();
    }

    // @todo: we want Double type instead of String probably
    public String getItemScore(int identifier) {
        //@todo: we want map of identifiers instead of accessing like this
        int id = identifier - 1;
        TestPlanNode itemRefNode = this.itemRefs.get(id);
        return this.getItemScore(itemRefNode);
    }

    public List<TestPlanNode> getItemRefs() {
        return itemRefs;
    }

    public String getItemScore(TestPlanNode itemRefNode) {
        //this.testSessionController.selectItemNonlinear(new Date(), itemRefNode.getKey());
        ItemSessionState itemSessionState = this.getItemSessionState(itemRefNode);
        return String.valueOf(((FloatValue)itemSessionState.getOutcomeValue(CHOICE_ITEM_SCORE)).doubleValue());
    }

    public boolean isOutcomeProcessed() {
        return testSessionState.getOutcomeValue(TEST_OP_DONE).equals(BooleanValue.TRUE);
    }

    // @todo return Double type
    public String getScore() {
        return testSessionState.getOutcomeValue(TEST_SCORE).toQtiString();
    }

    /*** functions important for rendering ***/
    public String getItemPathByIdentifier(int identifier) {
        int id = identifier - 1;
        TestPlanNode itemRefNode = this.getItemRefNode(id);
        return getItemPathByRefNode(itemRefNode);
    }

    public String getItemPathByRefNode(TestPlanNode itemRefNode) {
        AssessmentItemRef assessmentItemRef = (AssessmentItemRef) this.testProcessingMap.resolveAbstractPart(itemRefNode);
        return assessmentItemRef.getHref().toString();
    }

    /* Returns the current item of the test */
    public AssessmentItemWrapper getCurrentItem() {
        TestPlanNodeKey currentItemKey = this.testSessionState.getCurrentItemKey();
        System.out.println("Current key");
        System.out.println(currentItemKey);
        TestPlanNode currentTestPlanNode = this.testSessionState.getTestPlan().getNode(currentItemKey);
        String path = currentTestPlanNode.getItemSystemId().toString();
        return new AssessmentItemWrapper(path, currentItemKey.getIdentifier().toString());
    }

    /* Returns the item with the given identifier */
    public AssessmentItemWrapper getItem(int identifier) {
        String path = this.getItemPathByIdentifier(identifier);
        //return new AssessmentItemWrapper("classpath:/samples/" + path, "i"+identifier);
        return new AssessmentItemWrapper("file:/C:/Users/legion/Code/voa/qtifiles/" + path, "i"+identifier);
        //return URI.create("file:/C:/Users/legion/Code/voa/qtifiles/" + testFilePath);
    }

    public AssessmentItemWrapper getItem(TestPlanNode itemRef) {
        String path = this.getItemPathByRefNode(itemRef);
        return new AssessmentItemWrapper("file:/C:/Users/legion/Code/voa/qtifiles/" + path, itemRef.getIdentifier().toString());
    }

    /** Private methods ***/
    private TestPlanNode getItemRefNode(int id) {
        return testPlan.getTestPartNodes().get(0).searchDescendants(TestPlanNode.TestNodeType.ASSESSMENT_ITEM_REF).get(id);
    }

    private ItemSessionState getItemSessionState(TestPlanNode itemRefNode) {
        return this.testSessionState.getItemSessionStates().get(itemRefNode.getKey());
    }

    public boolean isItemRespondedCorrectly(TestPlanNode itemRef) {
        ItemSessionState itemSessionState = this.getItemSessionState(itemRef);
        return itemSessionState.isRespondedValidly();
    }

    public String getItemQuestion(TestPlanNode itemRef) {
        return this.getItem(itemRef).getInteraction(0).getQuestionText();
    }

    public String getItemProvidedAnswer(TestPlanNode itemRef) {
        ItemSessionState itemSessionState = this.getItemSessionState(itemRef);
        AssessmentItemWrapper item = this.getItem(itemRef);
        Interaction interaction = item.itemProcessingMap.getInteractions().get(0);
        Value responseValue = itemSessionState.getResponseValue(interaction);
        String responseIdentifier = responseValue.toQtiString();
        if (responseIdentifier.equals("NULL")) {
            return "No answer provided.";
        }
        String identifier = responseIdentifier.substring(1);
        SimpleChoiceRenderer renderer = item.getInteraction(0);
        return renderer.getChoices().get(identifier);
    }

    public String getItemCorrectAnswer(TestPlanNode itemRef) {
        AssessmentItemWrapper item = this.getItem(itemRef);
        final AssessmentItem assessmentItem = item.itemSessionController.getSubjectItem();
        ResponseDeclaration declaration = assessmentItem.getResponseDeclarations().get(0);
        CorrectResponse correctrespone = declaration.getCorrectResponse();
        String correctAnswerIdentifier = correctrespone.getFieldValues().get(0).getSingleValue().toQtiString();
        SimpleChoiceRenderer renderer = item.getInteraction(0);
        correctAnswerIdentifier = correctAnswerIdentifier.substring(1);
//        for( String key: renderer.getChoices().keySet()) {
//            System.out.println(key);
//        }

        return renderer.getChoices().get(correctAnswerIdentifier);
    }
}

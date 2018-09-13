package com.videotel.voa.shared;

import com.videotel.voa.shared.interactions.SimpleChoiceRenderer;
import uk.ac.ed.ph.jqtiplus.SimpleJqtiFacade;
import uk.ac.ed.ph.jqtiplus.node.content.basic.TextRun;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.Interaction;
import uk.ac.ed.ph.jqtiplus.node.item.interaction.choice.SimpleChoice;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.running.ItemProcessingInitializer;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionController;
import uk.ac.ed.ph.jqtiplus.running.ItemSessionControllerSettings;
import uk.ac.ed.ph.jqtiplus.state.ItemProcessingMap;
import uk.ac.ed.ph.jqtiplus.state.ItemSessionState;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.types.ResponseData;
import uk.ac.ed.ph.jqtiplus.types.StringResponseData;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;
import java.util.*;

/* @todo: maybe create factory with base url in order to avoid adding classpath://base+filename everywhere */
public class AssessmentItemWrapper {
    public ItemSessionController itemSessionController;
    protected ItemSessionState itemSessionState;
    protected ItemProcessingMap itemProcessingMap;
    String filePath = null;
    String identifier = null; //only when created from assessment

    public AssessmentItemWrapper(String filePath) {
        this.filePath = filePath;
        this.initItemSessionController();
    }
    public AssessmentItemWrapper(String filePath, String identifier) {
        this.filePath = filePath;
        this.identifier = identifier;
        this.initItemSessionController();
    }

    public void initItemSessionController() {
        final ResourceLocator inputResourceLocator = new ClassPathResourceLocator();
        final URI inputUri = URI.create(this.filePath);

        final SimpleJqtiFacade simpleJqtiFacade = new SimpleJqtiFacade();
        final ResolvedAssessmentItem resolvedAssessmentItem = simpleJqtiFacade.loadAndResolveAssessmentItem(inputResourceLocator, inputUri);

        //System.out.println(resolvedAssessmentItem);
        this.itemProcessingMap = new ItemProcessingInitializer(resolvedAssessmentItem, false).initialize();
        //System.out.println("Run map is: " + ObjectDumper.dumpObject(this.itemProcessingMap, DumpMode.DEEP));
        this.itemSessionState = new ItemSessionState();

        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        this.itemSessionController = simpleJqtiFacade.createItemSessionController(itemSessionControllerSettings, this.itemProcessingMap, this.itemSessionState);

        final Date timestamp1 = new Date();
        this.itemSessionController.initialize(timestamp1);
        this.itemSessionController.performTemplateProcessing(timestamp1);
        //System.out.println("State after init: " + ObjectDumper.dumpObject(this.itemSessionState, DumpMode.DEEP));

        this.itemSessionController.enterItem(new Date());



        //System.out.println("State after entry: " + ObjectDumper.dumpObject(this.itemSessionState, DumpMode.DEEP));
    }

    public void bindAndCommitResponses(String responseChoice) {
        final Map<Identifier, ResponseData> responseMap = new HashMap<Identifier, ResponseData>();
        responseMap.put(Identifier.parseString("RESPONSE"), new StringResponseData(responseChoice));
        this.itemSessionController.bindResponses(new Date(), responseMap);
        System.out.println("Unbound responses: " + this.itemSessionState.getUnboundResponseIdentifiers());
        System.out.println("Invalid responses:" + this.itemSessionState.getInvalidResponseIdentifiers());
        this.itemSessionController.commitResponses(new Date());
    }

    public void processResponseAndCloseItem() {
        this.itemSessionController.performResponseProcessing(new Date());
        this.itemSessionController.endItem(new Date());
        this.itemSessionController.exitItem(new Date());
    }

    public Object getState() {
        return this.itemSessionState;
    }

    /** @todo:
     * we need to create it for each interaction type
     * **/
    public void renderItem() {
        SimpleChoiceRenderer renderer = this.getInteraction(0);
        renderer.render();
    }

    public SimpleChoiceRenderer getInteraction(int index) {
        SimpleChoiceRenderer renderer = new SimpleChoiceRenderer();
        List<Interaction> interactions = this.itemProcessingMap.getInteractions();
        TextRun tr2= (TextRun) interactions.get(index).getNodeGroups().get(0).getChildren().get(0).getNodeGroups().get(0).getChildren().get(0);
        String question = tr2.getTextContent();
        if (this.identifier != null) {
            //only in the context of a test
            renderer.setId(this.identifier.substring((1)));
        }

        renderer.setQuestion(question);

        List<String> choices = new ArrayList<>();
        List<SimpleChoice> children = (List<SimpleChoice>)interactions.get(0).getNodeGroups().get(1).getChildren();

        for (SimpleChoice child : children) {
            TextRun tr = (TextRun)child.getNodeGroups().get(0).getChildren().get(0);
            String choice = tr.getTextContent();
            renderer.addChoice(choice);
        }

        return renderer;
    }

}

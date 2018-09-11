/* Copyright (c) 2012-2013, University of Edinburgh.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice, this
 *   list of conditions and the following disclaimer in the documentation and/or
 *   other materials provided with the distribution.
 *
 * * Neither the name of the University of Edinburgh nor the names of its
 *   contributors may be used to endorse or promote products derived from this
 *   software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 *
 * This software is derived from (and contains code from) QTItools and MathAssessEngine.
 * QTItools is (c) 2008, University of Southampton.
 * MathAssessEngine is (c) 2010, University of Edinburgh.
 */
package com.videotel.voa.shared;

import uk.ac.ed.ph.jqtiplus.JqtiExtensionManager;
import uk.ac.ed.ph.jqtiplus.reading.AssessmentObjectXmlLoader;
import uk.ac.ed.ph.jqtiplus.reading.QtiXmlReader;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentItem;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentObject;
import uk.ac.ed.ph.jqtiplus.resolution.ResolvedAssessmentTest;
import uk.ac.ed.ph.jqtiplus.running.*;
import uk.ac.ed.ph.jqtiplus.state.*;
import uk.ac.ed.ph.jqtiplus.types.Identifier;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlReadResult;
import uk.ac.ed.ph.jqtiplus.xmlutils.XmlResourceNotFoundException;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ClassPathResourceLocator;
import uk.ac.ed.ph.jqtiplus.xmlutils.locators.ResourceLocator;

import java.net.URI;
import java.util.List;

/**
 * FIXME: Refactor this more appropriately!
 *
 * @author David McKain
 */
public final class TestHelper {


    public static URI createTestResourceUri(final String testFilePath) {
        return URI.create("classpath:/" + testFilePath);
    }

    public static JqtiExtensionManager createJqtiExtensionManager() {
        return new JqtiExtensionManager();
    }

    public static QtiXmlReader createUnitTestQtiXmlReader() {
        return new QtiXmlReader(createJqtiExtensionManager());
    }

    public static ResourceLocator createTestFileResourceLocator() {
        return new ClassPathResourceLocator();
    }


    public static AssessmentObjectXmlLoader createUnitTestAssessmentObjectXmlLoader() {
        final QtiXmlReader qtiXmlReader = createUnitTestQtiXmlReader();
        return new AssessmentObjectXmlLoader(qtiXmlReader, createTestFileResourceLocator());
    }

    public static XmlReadResult readUnitTestFile(final String testFilePath, final boolean schemaValiadating) {
        final QtiXmlReader reader = createUnitTestQtiXmlReader();
        final URI testFileUri = createTestResourceUri(testFilePath);
        try {
            return reader.read(createTestFileResourceLocator(), testFileUri, schemaValiadating);
        }
        catch (final XmlResourceNotFoundException e) {
            /* Should not happen! */
            return null;
        }
    }

    public static ResolvedAssessmentItem resolveUnitTestAssessmentItem(final String testFilePath) {
        final AssessmentObjectXmlLoader assessmentObjectXmlLoader = createUnitTestAssessmentObjectXmlLoader();
        final URI testFileUri = createTestResourceUri(testFilePath);
        return assessmentObjectXmlLoader.loadAndResolveAssessmentItem(testFileUri);
    }

    public static ResolvedAssessmentTest resolveUnitTestAssessmentTest(final String testFilePath) {
        final AssessmentObjectXmlLoader assessmentObjectXmlLoader = createUnitTestAssessmentObjectXmlLoader();
        final URI testFileUri = createTestResourceUri(testFilePath);
        return assessmentObjectXmlLoader.loadAndResolveAssessmentTest(testFileUri);
    }

    public static ItemSessionController loadUnitTestAssessmentItemForControl(final String testFilePath, final boolean isValid) {
        final ResolvedAssessmentItem resolvedAssessmentItem = resolveUnitTestAssessmentItem(testFilePath);

        final ItemSessionControllerSettings itemSessionControllerSettings = new ItemSessionControllerSettings();
        final ItemProcessingMap itemProcessingMap = new ItemProcessingInitializer(resolvedAssessmentItem, isValid).initialize();
        final ItemSessionState itemSessionState = new ItemSessionState();
        return new ItemSessionController(createJqtiExtensionManager(), itemSessionControllerSettings,
                itemProcessingMap, itemSessionState);
    }

    public static TestSessionController loadUnitTestAssessmentTestForControl(final String testFilePath, final boolean isValid) {
        final ResolvedAssessmentTest resolvedAssessmentTest = resolveUnitTestAssessmentTest(testFilePath);

        final TestSessionControllerSettings testSessionControllerSettings = new TestSessionControllerSettings();
        final TestProcessingMap testProcessingMap = new TestProcessingInitializer(resolvedAssessmentTest, isValid).initialize();
        final TestPlanner testPlanner = new TestPlanner(testProcessingMap);
        final TestPlan testPlan = testPlanner.generateTestPlan();
        final TestSessionState testSessionState = new TestSessionState(testPlan);
        return new TestSessionController(createJqtiExtensionManager(), testSessionControllerSettings,
                testProcessingMap, testSessionState);
    }

    private static boolean isSuccessfulResolution(final ResolvedAssessmentObject<?> resolvedAssessmentObject) {
        return resolvedAssessmentObject.getRootNodeLookup().wasSuccessful();
    }

    public static TestPlanNode getSingleTestPlanNode(final TestPlan testPlan, final String identifier) {
        final List<TestPlanNode> nodes = testPlan.getNodes(Identifier.assumedLegal(identifier));
        return nodes.get(0);
    }

}

package com.videotel.voa.authoring;

import com.videotel.voa.model.Assessment;
import com.videotel.voa.model.AssessmentItem;
import com.videotel.voa.model.Question;
import com.videotel.voa.repository.AssessmentItemRepository;
import com.videotel.voa.service.QtiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class QuestionController {

    @Autowired
    private QtiService qtiService;

    @Autowired
    AssessmentItemRepository assessmentItemRepository;

    @GetMapping("/question")
    public String questionForm(Model model) {
        model.addAttribute("question", new Question());
        return "question";
    }

    @PostMapping("/question")
    public String questionSubmit(@ModelAttribute Question question) {
        String xmlPath = qtiService.generateAssessmentItemQtiXml(question);
        //System.out.println(xml);

        assessmentItemRepository.save(new AssessmentItem(xmlPath, question.getTag()));
        return "result";
    }

    @GetMapping("/assessment")
    public String assessmentForm(Model model) {
        model.addAttribute("assessment", new Assessment());
        List<AssessmentItem> assessmentItems = assessmentItemRepository.findAll();
        Map<String, String> tags = new HashMap<>();
        for(AssessmentItem i : assessmentItems) {
            tags.put(i.getTag(), i.getTag());
        }

        model.addAttribute("tags", tags.keySet().toArray());
        return "assessment";
    }

    @PostMapping("/assessment")
    public String assessmentSubmit(@ModelAttribute Assessment assessment) {
        String xmlPath = qtiService.generateAssessmentXml(assessment);
        //assessmentItemRepository.save(new AssessmentItem(xmlPath, "English Language"));
        return "result";
    }

}

package com.videotel.voa.shared.interactions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract public class InteractionAbstractRenderer implements InteractionRenderer{
    protected String id;
    protected String question_text;
    protected Map<String, String> choices = new HashMap<>();
}

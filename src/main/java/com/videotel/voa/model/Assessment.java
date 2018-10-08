package com.videotel.voa.model;

import com.fasterxml.jackson.databind.JsonNode;

import java.lang.reflect.Field;

public class Assessment {

    private String tag1;
    private String number1;
    private String tag2;
    private String number2;
    private String tag3;
    private String number3;
    private String tag4;
    private String number4;
    private String tag5;
    private String number5;

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        this.tag1 = tag1;
    }

    public String getNumber1() {
        return number1;
    }

    public void setNumber1(String number1) {
        this.number1 = number1;
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        this.tag2 = tag2;
    }

    public String getNumber2() {
        return number2;
    }

    public void setNumber2(String number2) {
        this.number2 = number2;
    }

    public String getTag3() {
        return tag3;
    }

    public void setTag3(String tag3) {
        this.tag3 = tag3;
    }

    public String getNumber3() {
        return number3;
    }

    public void setNumber3(String number3) {
        this.number3 = number3;
    }

    public String getTag4() {
        return tag4;
    }

    public void setTag4(String tag4) {
        this.tag4 = tag4;
    }

    public String getNumber4() {
        return number4;
    }

    public void setNumber4(String number4) {
        this.number4 = number4;
    }

    public String getTag5() {
        return tag5;
    }

    public void setTag5(String tag5) {
        this.tag5 = tag5;
    }

    public String getNumber5() {
        return number5;
    }

    public void setNumber5(String number5) {
        this.number5 = number5;
    }

    public String getTag(int i) {
        try {
            Field tag = this.getClass().getDeclaredField("tag" + i);
            tag.setAccessible(true);
            return (String) tag.get(this);
        } catch (Exception e) {
            return null;
        }
    }

    public String getNumber(int i) {
        try {
            Field number = this.getClass().getDeclaredField("number" + i);
            number.setAccessible(true);
            return (String) number.get(this);
        } catch (Exception e) {
            return null;
        }
    }
}
package com.daon.onorder.Model;

public class SampleCategoryModel {
    String code;
    String storecode;
    String name;

    public SampleCategoryModel(String code, String storecode, String name) {
        this.code = code;
        this.storecode = storecode;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getStorecode() {
        return storecode;
    }

    public void setStorecode(String storecode) {
        this.storecode = storecode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

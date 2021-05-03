package com.daon.onorder.Model;

public class SampleMenuModel {
    String name;
    String picurl;
    String code;
    String price;
    String info;
    String ctgcode;

    public SampleMenuModel(String name, String picurl, String code, String price, String info, String ctgcode) {
        this.name = name;
        this.picurl = picurl;
        this.code = code;
        this.price = price;
        this.info = info;
        this.ctgcode = ctgcode;
    }

    public String getCtgcode() {
        return ctgcode;
    }

    public void setCtgcode(String ctgcode) {
        this.ctgcode = ctgcode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicurl() {
        return picurl;
    }

    public void setPicurl(String picurl) {
        this.picurl = picurl;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
}

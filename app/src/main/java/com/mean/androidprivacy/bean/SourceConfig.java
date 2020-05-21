package com.mean.androidprivacy.bean;

import com.alibaba.fastjson.JSON;

import lombok.Data;

@Data
public class SourceConfig {
    private String className;
    private String functionName;
    private String returnType;
    private boolean enable = false;
    private int mode; //0->null, 1->default, 2->modify
    private String modifyData;

    public Object generateFakeReturnObject(){
        try {
            return JSON.parseObject(modifyData, Class.forName(className));
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    public String toFineString(){
        return String.format("%s\n%s %s",className,returnType,functionName);
    }
}

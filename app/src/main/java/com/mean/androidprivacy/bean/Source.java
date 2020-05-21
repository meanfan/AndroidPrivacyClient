package com.mean.androidprivacy.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import lombok.Data;

@Data
@XStreamAlias("Source")
public class Source {

    @XStreamAsAttribute
    @XStreamAlias("Statement")
    private String statement;

    @XStreamAsAttribute
    @XStreamAlias("Method")
    private String method;

    @XStreamAlias("AccessPath")
    private AccessPath accessPath;
}

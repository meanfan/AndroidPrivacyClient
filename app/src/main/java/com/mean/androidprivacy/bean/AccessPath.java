package com.mean.androidprivacy.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import lombok.Data;

@Data
@XStreamAlias("AccessPath")
public class AccessPath {

    @XStreamAsAttribute
    @XStreamAlias("Value")
    String value;

    @XStreamAsAttribute
    @XStreamAlias("Type")
    String type;

    @XStreamAsAttribute
    @XStreamAlias("TaintSubFields")
    Boolean taintSubFields;
}

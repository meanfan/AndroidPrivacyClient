package com.mean.androidprivacy.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import lombok.Data;

/**
 * @ClassName: AccessPath
 * @Description: DataFlowResults->Results->Result->(Sources->Source/Sink)下的AccessPath节点
 * @Author: MeanFan
 * @Version: 1.0
 */
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

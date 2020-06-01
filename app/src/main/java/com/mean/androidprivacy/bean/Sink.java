package com.mean.androidprivacy.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import lombok.Data;


/**
 * @ClassName: Sink
 * @Description: 对应分析结果XML中DataFlowResults->Results->Result节点下的Sink节点
 * @Author: MeanFan
 * @Version: 1.0
 */
@Data
@XStreamAlias("Sink")
public class Sink {

    @XStreamAsAttribute
    @XStreamAlias("Statement")
    private String statement;

    @XStreamAsAttribute
    @XStreamAlias("Method")
    private String method;

    @XStreamAlias("AccessPath")
    private AccessPath accessPath;
}

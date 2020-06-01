package com.mean.androidprivacy.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

import lombok.Data;

/**
 * @ClassName: Source
 * @Description: 对应分析结果XML中DataFlowResults->Results->Result->Sources节点下的Source节点
 * @Author: MeanFan
 * @Version: 1.0
 */
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

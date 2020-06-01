package com.mean.androidprivacy.bean;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

import lombok.Data;

/**
 * @ClassName: Result
 * @Description: 对应分析结果XML中DataFlowResults->Results节点下的Result节点
 * @Author: MeanFan
 * @Version: 1.0
 */
@Data
@XStreamAlias("Result")
public class Result {

    @XStreamAlias("Sink")
    private Sink sink;

    @XStreamAlias("Sources")
    private Sources sources;

    public List<Source> getImplicitSources(){
        return sources.getSources();
    }

    public void setImplicitSources(List<Source> sources){
        this.sources.setSources(sources);
    }


    /**
     * @ClassName: Result.Sources
     * @Description: 对应分析结果XML中DataFlowResults->Results->Result节点下的Sources节点
     * @Author: MeanFan
     * @Version: 1.0
     */
    @Data
    @XStreamAlias("Sources")
    public static class Sources{
        @XStreamImplicit(itemFieldName = "Source")
        private List<Source> sources;
    }
}

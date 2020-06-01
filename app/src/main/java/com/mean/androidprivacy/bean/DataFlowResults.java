package com.mean.androidprivacy.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

import lombok.Data;


/**
 * @ClassName: DataFlowResults
 * @Description: 对应分析结果XML的根节点DataFlowResults
 * @Author: MeanFan
 * @Version: 1.0
 */
@Data
@XStreamAlias("DataFlowResults")
public class DataFlowResults {

    @XStreamAlias("Results")
    private Results results;

    public List<Result> getImplicitResults(){
        return results.getResults();
    }

    public void setImplicitResults(List<Result> results){
        this.results.setResults(results);
    }

    /**
     * @ClassName: DataFlowResults.Results
     * @Description: 对应分析结果XML中DataFlowResults节点下的Results节点
     * @Author: MeanFan
     * @Version: 1.0
     */
    @Data
    @XStreamAlias("Results")
    public static class Results{
        @XStreamImplicit(itemFieldName = "Result")
        private List<Result> results;
    }

}

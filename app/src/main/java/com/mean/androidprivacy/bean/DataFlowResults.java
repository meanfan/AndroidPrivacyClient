package com.mean.androidprivacy.bean;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

import lombok.Data;

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

    @Data
    @XStreamAlias("Results")
    public static class Results{
        @XStreamImplicit(itemFieldName = "Result")
        private List<Result> results;
    }

}

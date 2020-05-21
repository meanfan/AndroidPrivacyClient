package com.mean.androidprivacy.bean;


import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

import java.util.List;

import lombok.Data;

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

    @Data
    @XStreamAlias("Sources")
    public static class Sources{
        @XStreamImplicit(itemFieldName = "Source")
        private List<Source> sources;
    }
}

package com.mean.androidprivacy.converter;

import com.mean.androidprivacy.bean.AccessPath;
import com.mean.androidprivacy.bean.Result;
import com.mean.androidprivacy.bean.DataFlowResults;
import com.mean.androidprivacy.bean.Sink;
import com.mean.androidprivacy.bean.Source;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

import org.greenrobot.greendao.converter.PropertyConverter;

/**
 * @ClassName: DataFlowResultsConverter
 * @Description: 将DataFlowResults类与XML互转的类
 * @Author: MeanFan
 * @Version: 1.0
 */
public class DataFlowResultsConverter implements PropertyConverter<DataFlowResults,String> {
    private XStream xStream;
    public DataFlowResultsConverter() {
        xStream = new XStream();
        xStream.alias("Results", DataFlowResults.class);
        xStream.alias("Result", Result.class);
        xStream.alias("Sink", Sink.class);
        xStream.alias("Source", Source.class);
        xStream.alias("AccessPath", AccessPath.class);
    }

    /**
    * @Author: MeanFan
    * @Description: 将XML转为DataFlowResults类
    * @Param: [databaseValue]
    * @return: com.mean.androidprivacy.bean.DataFlowResults
    **/
    @Override
    public DataFlowResults convertToEntityProperty(String databaseValue) {
        if (databaseValue == null) {
            return null;
        }
        xStream.processAnnotations(DataFlowResults.class);
        xStream.ignoreUnknownElements();
        return  (DataFlowResults) xStream.fromXML(databaseValue);
    }

    /**
    * @Author: MeanFan
    * @Description: 将DataFlowResults类转为XML
    * @Param: [entityProperty]
    * @return: java.lang.String
    **/
    @Override
    public String convertToDatabaseValue(DataFlowResults entityProperty) {
        if (entityProperty == null) {
            return null;
        }
        xStream.processAnnotations(DataFlowResults.class);
        xStream.ignoreUnknownElements();
        return xStream.toXML(entityProperty);
    }
}
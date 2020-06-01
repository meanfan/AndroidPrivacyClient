package com.mean.androidprivacy.converter;

import com.alibaba.fastjson.JSON;
import com.mean.androidprivacy.bean.SourceConfig;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.List;

/**
 * @ClassName: SourceConfigConverter
 * @Description: 将List<SourceConfig>与JSON字符串互转的类
 * @Author: MeanFan
 * @Version: 1.0
 */
public class SourceConfigConverter implements PropertyConverter<List<SourceConfig>,String> {

    /**
    * @Author: MeanFan
    * @Description: 将List<SourceConfig>转为JSON字符串
    * @Date: 11:07 2020/6/1 0001
    * @Param: [entityProperty]
    * @return: java.lang.String
    **/
    @Override
    public String convertToDatabaseValue(List<SourceConfig> entityProperty) {
        return JSON.toJSONString(entityProperty);
    }

    /**
    * @Author: MeanFan
    * @Description: 将JSON字符串转为List<SourceConfig>
    * @Param: [databaseValue]
    * @return: java.util.List<com.mean.androidprivacy.bean.SourceConfig>
    **/
    @Override
    public List<SourceConfig> convertToEntityProperty(String databaseValue) {
        return JSON.parseArray(databaseValue,SourceConfig.class);
    }
}

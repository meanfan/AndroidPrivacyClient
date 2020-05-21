package com.mean.androidprivacy.converter;

import com.alibaba.fastjson.JSON;
import com.mean.androidprivacy.bean.Source;
import com.mean.androidprivacy.bean.SourceConfig;

import org.greenrobot.greendao.converter.PropertyConverter;

import java.util.ArrayList;
import java.util.List;

public class SourceConfigConverter implements PropertyConverter<List<SourceConfig>,String> {

    @Override
    public String convertToDatabaseValue(List<SourceConfig> entityProperty) {
        return JSON.toJSONString(entityProperty);
    }

    @Override
    public List<SourceConfig> convertToEntityProperty(String databaseValue) {
        return JSON.parseArray(databaseValue,SourceConfig.class);
    }
}

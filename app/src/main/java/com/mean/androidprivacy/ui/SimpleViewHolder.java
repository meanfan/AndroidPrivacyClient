package com.mean.androidprivacy.ui;

import android.view.View;
import android.widget.TextView;

import com.mean.androidprivacy.R;

import de.blox.graphview.ViewHolder;

/**
 * @ClassName: MethodDetailActivity
 * @Description: 配置项详情页面中的GraphView控件用到的ViewHolder
 * @Author: MeanFan
 * @Version: 0.1
 */
class SimpleViewHolder extends ViewHolder {
    TextView tv_statement;

    SimpleViewHolder(View itemView) {
        super(itemView);
        tv_statement = itemView.findViewById(R.id.tv_desc);
    }
}
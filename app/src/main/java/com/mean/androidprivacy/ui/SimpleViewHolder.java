package com.mean.androidprivacy.ui;

import android.view.View;
import android.widget.TextView;

import com.mean.androidprivacy.R;

import de.blox.graphview.ViewHolder;

class SimpleViewHolder extends ViewHolder {
    TextView tv_statement;

    SimpleViewHolder(View itemView) {
        super(itemView);
        tv_statement = itemView.findViewById(R.id.tv_desc);
    }
}
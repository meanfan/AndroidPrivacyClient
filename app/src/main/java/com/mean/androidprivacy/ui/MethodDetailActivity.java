package com.mean.androidprivacy.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mean.androidprivacy.App;
import com.mean.androidprivacy.R;
import com.mean.androidprivacy.bean.AppConfig;
import com.mean.androidprivacy.bean.Result;
import com.mean.androidprivacy.bean.Source;

import de.blox.graphview.BaseGraphAdapter;
import de.blox.graphview.Graph;
import de.blox.graphview.GraphView;
import de.blox.graphview.Node;
import de.blox.graphview.ViewHolder;
import de.blox.graphview.tree.BuchheimWalkerAlgorithm;
import de.blox.graphview.tree.BuchheimWalkerConfiguration;

public class MethodDetailActivity extends AppCompatActivity {
    private AppConfig appConfig;
    private String methodDesc;
    private LinearLayout ll_results;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_detail);
        ll_results = findViewById(R.id.ll_results);

        String appPackageName = getIntent().getStringExtra("appPackageName");
        appConfig = App.appConfigs.get(appPackageName);
        methodDesc = getIntent().getStringExtra("methodDesc");
        if(appConfig == null || appConfig.getDataFlowResults()==null || appConfig.getDataFlowResults().getResults()==null){
            Toast.makeText(this,"参数错误",Toast.LENGTH_SHORT).show();
            finish();
        }
        for(Result result:appConfig.getDataFlowResults().getImplicitResults()){
            String sinkStatement = result.getSink().getStatement();
            String sinkMethod = result.getSink().getMethod();
            String sinkDesc = String.format("%s\n%s",sinkStatement,sinkMethod);
            final GraphView graphView = new GraphView(this);
            final Graph graph = new Graph();
            final Node rootNode = new Node(sinkDesc);
            for(Source source:result.getImplicitSources()){
                String sourceStatement = source.getStatement();
                String sourceMethod = source.getMethod();
                String sourceDesc = String.format("%s\n%s",sourceStatement,sourceMethod);
                final Node sourceNode = new Node(sourceDesc);
                graph.addEdge(rootNode,sourceNode);
            }
            final BaseGraphAdapter<ViewHolder> adapter = new BaseGraphAdapter<ViewHolder>(graph) {

                @NonNull
                @Override
                public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                    final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.graphview_node, parent, false);
                    return new SimpleViewHolder(view);
                }

                @Override
                public void onBindViewHolder(ViewHolder viewHolder, Object data, int position) {
                    ((SimpleViewHolder)viewHolder).tv_statement.setText(data.toString());
                }
            };
            graphView.setAdapter(adapter);
            graphView.setLineThickness(10);
            graphView.setLineColor(Color.BLACK);
            graphView.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
            // set the algorithm here
            final BuchheimWalkerConfiguration configuration = new BuchheimWalkerConfiguration.Builder()
                    .setSiblingSeparation(100)
                    .setLevelSeparation(100)
                    .setSubtreeSeparation(100)
                    .setOrientation(BuchheimWalkerConfiguration.ORIENTATION_BOTTOM_TOP)
                    .build();
            adapter.setAlgorithm(new BuchheimWalkerAlgorithm(configuration));
            ll_results.addView(graphView,new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,200));
            break;
        }

    }
}

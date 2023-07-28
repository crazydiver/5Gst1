package ru.scoltech.openran.speedtest.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ru.scoltech.openran.speedtest.R;
import ru.scoltech.openran.speedtest.util.Pipeline;
import ru.scoltech.openran.speedtest.util.SharedStorageController;

public class PipelineAdapter extends ArrayAdapter<Pipeline> {
    private Context context;
    private ArrayList<Pipeline> pipelineArrayList;

    @Override
    public int getCount() {
        return pipelineArrayList.size();
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);


        convertView = inflater.inflate(R.layout.stage_sample, null);
        TextView name = convertView.findViewById(R.id.stage_name);
        TextView devicePrefs = convertView.findViewById(R.id.device_args);
        TextView serverPrefs = convertView.findViewById(R.id.server_args);
        ImageButton deleteButton = convertView.findViewById(R.id.deleteButton);
        Log.i("sj", position+"");
        name.setText(pipelineArrayList.get(position).getName());
        devicePrefs.setText(pipelineArrayList.get(position).getDevicePrefs());
        serverPrefs.setText(pipelineArrayList.get(position).getServerPrefs());

        Pipeline p = new Pipeline( name.getText().toString(), devicePrefs.getText().toString(),
                serverPrefs.getText().toString());
        SharedStorageController.addPipeline(p);

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v){
                SharedStorageController.removePipeline(p);
            }
        });
        position++;
        return convertView;
    }


    public PipelineAdapter(@NonNull Context context,
                           @NonNull ArrayList<Pipeline> pipelineArrayList) {
        super(context, R.layout.stage_sample, pipelineArrayList);
        this.context = context;
        this.pipelineArrayList = pipelineArrayList;

    }
}

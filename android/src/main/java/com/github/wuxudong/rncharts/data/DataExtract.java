package com.github.wuxudong.rncharts.data;

import android.util.Log;

import com.facebook.react.bridge.ReadableArray;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableType;
import com.github.mikephil.charting.data.ChartData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.interfaces.datasets.IDataSet;
import com.github.wuxudong.rncharts.utils.BridgeUtils;

import java.util.ArrayList;

/**
 * Created by xudong on 02/03/2017.
 */

public abstract class DataExtract<D extends ChartData, U extends Entry> {

    public D extract(ReadableMap propMap) {
        if (!BridgeUtils.validate(propMap, ReadableType.Array, "dataSets")) {
            return null;
        }

        D chartData = createData();


        ReadableArray dataSets = propMap.getArray("dataSets");
        for (int i = 0; i < dataSets.size(); i++) {
            ReadableMap dataSetReadableMap = dataSets.getMap(i);

            // TODO validation
            ReadableArray values = dataSetReadableMap.getArray("values");
            String label = dataSetReadableMap.getString("label");

            ArrayList<U> entries = createEntries(values);

            IDataSet<U> dataSet = createDataSet(entries, label);

            if (BridgeUtils.validate(dataSetReadableMap, ReadableType.Map, "config")) {
                dataSetConfig(dataSet, dataSetReadableMap.getMap("config"));
            }

            chartData.addDataSet(dataSet);
        }

        if (BridgeUtils.validate(propMap, ReadableType.Map, "config")) {
            dataConfig(chartData, propMap.getMap("config"));
        }


        return chartData;
    }

    abstract D createData();

    void dataConfig(D data, ReadableMap config) {}

    abstract IDataSet<U> createDataSet(ArrayList<U> entries, String label);

    abstract void dataSetConfig(IDataSet<U> dataSet, ReadableMap config);

    ArrayList<U> createEntries(ReadableArray yValues) {
        ArrayList<U> entries = new ArrayList<>(yValues.size());
        for (int j = 0; j < yValues.size(); j++) {
            if (!yValues.isNull(j)) {
                try {
                    entries.add(createEntry(yValues, j));
                } catch (Exception e) {
                    Log.d("Zest", "[react-native-chart-wrapper - createEntries] " + e);
                }
            }
        }
        return entries;
    }

    abstract U createEntry(ReadableArray values, int index);



}

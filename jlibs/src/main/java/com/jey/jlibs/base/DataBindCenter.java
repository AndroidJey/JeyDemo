package com.jey.jlibs.base;

import com.jey.jlibs.dataModel.BaseDataModel;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public final class DataBindCenter {
    public interface Requester {
        public void handleData(BaseDataModel data, int position) throws Exception;
    }

    private static Map<Object, List<Requester>> _readers = new Hashtable<Object, List<Requester>>();

    public static void bind(BaseDataModel data, Requester reader,int position) {
        List<Requester> list = _readers.get(data);
        if (list == null) {
            list = new ArrayList<Requester>();
            _readers.put(data, list);
        }
        if (list.contains(reader)) return;
        try {
            reader.handleData(data,position);
            list.add(reader);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void unBind(BaseDataModel data, Requester reader) {
        if (data == null) return;
        List<Requester> list = _readers.get(data);
        if (list == null) return;
        list.remove(reader);
    }

    public static void notify(BaseDataModel data,int position) {
        List<Requester> readerList = _readers.get(data);
        if (readerList == null) return;
        List<Requester> invalidList = new ArrayList<Requester>();
        try {
            for (int i = 0; i < readerList.size(); i++) {
                Requester rq = readerList.get(i);
                try {
                    rq.handleData(data,position);
                } catch (Exception e1) {
                    invalidList.add(rq);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (Requester rq : invalidList) readerList.remove(rq);
    }
}

package com.jey.jlibs.base;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class BroadcastCenter {
    public enum TITLE {
        INVISIBLE,
        ViSIBLE,
        PASSLOGIN,
        NEWSRECEIVED,
        LOGSTATUSCHANGED,
        LOCATIONCHANGED,
        DATADELETED,
        DATAADDED,
        ASYHTTPERR,
        NETWORKCHANGED,
        NAVIGATETOPAGE,
        BACK,
        RETURNHOME,
        HEARTTICK,
        LOGINSUCCESS,
        LOADING,
        USERINFOUPDATE,
        INVITE,
        SELECTPHOTO,
        SHOWPHOTOVIEW,
        NEEDLOGIN,
        CLICKFLOAT,
        SHUAXINADDRESS,
        CHECKBOXSEL,
        DATACHANGE,
    }

    public interface Reader {
        public void readNews(TITLE title, Object content);
    }

    private static Map<TITLE, List<Reader>> _readers = new Hashtable<TITLE, List<Reader>>();

    public static void subscribe(TITLE title, Reader reader) {
        List<Reader> list = _readers.get(title);
        if (list == null) {
            list = new ArrayList<Reader>();
            _readers.put(title, list);
        }
        list.add(reader);
    }

    public static void unSubscribe(TITLE title, Reader reader) {
        List<Reader> list = _readers.get(title);
        if (list == null) return;
        list.remove(reader);
    }

    public static void unSubscribe(TITLE title) {
        List<Reader> list = _readers.get(title);
        if (list == null) return;
        list.clear();
    }

    public static void publish(TITLE title, Object content) {
        try {
            Iterator<Reader> it = _readers.get(title).iterator();
            while (it.hasNext()) {
                try {
                    it.next().readNews(title, content);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
        }
    }
}

package com.jey.jlibs.dataModel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.jey.jlibs.base.BroadcastCenter;
import com.jey.jlibs.utils.CommonFunction;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;


public final class Database implements Serializable {
    private static final long serialVersionUID = -8009280532285964319L;
    public static String packName = "";
    private String workPath = "";
    private static String _host = "http://xinge1982.f3322.org:8180/api";
    private ArrayList<String> pages = new ArrayList<String>();
    Map<String, Map<String, ArrayList<String>>> favority = new Hashtable<String, Map<String, ArrayList<String>>>();
    private static Database _instance;
    private static File saveFile;
    private UserModel _user;
    private static Context _context;

    public static void init(Context context) {
        _context = context;

        packName = context.getApplicationInfo().dataDir;
        try {
            saveFile = CommonFunction.getFile(getDataPath(), "Database.dat");
            if (saveFile != null) {
                _instance = Load();
            } else
                _instance = new Database();
        } catch (Exception e) {
            _instance = new Database();
            save();
            e.printStackTrace();
        }
    }

    private static Database Load() {
        ObjectInputStream ois;
        Database s = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(saveFile));
            s = (Database) ois.readObject();
        } catch (Exception e) {
            s = new Database();
            e.printStackTrace();
        }
        return s;
    }

    public static void save() {
        CommonFunction.saveObject(saveFile, _instance);
    }

    public void setWorkPath(String value) {
        if (value.equalsIgnoreCase(getWorkPath()))
            return;
        File newPath = new File(value);
        if (!newPath.exists())
            return;
        File oldPath = new File(getWorkPath());
        if (oldPath.renameTo(newPath)) {
            workPath = value;
            Database.save();
        }
    }


    public static String getWorkPath() {
        String p = _instance.workPath;
        if (p == null || "".equals(_instance.workPath)) {
            if (!Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                p = getDataPath();
            } else {
                p = getDataPath();
//				p = Environment.getExternalStorageDirectory().getAbsolutePath()
//						+ "/" + packName;
            }
        }
        File path = new File(p);
        if (!path.exists()) {
            path.mkdirs();
        }
        _instance.workPath = path.getAbsolutePath() + "/";
        return _instance.workPath;
    }


    public static String getCachePath() {
        File path = new File(getWorkPath() + "/cache/");
        if (!path.exists())
            path.mkdirs();
        return path.getAbsolutePath() + "/";
    }

    public static String getAlbumPath() {
        File path = new File(getWorkPath() + "/album/");
        if (!path.exists())
            path.mkdirs();
        return path.getAbsolutePath() + "/";
    }

    public static String getDataPath() {
        return _context.getCacheDir().getAbsolutePath() + "/";
    }

    public static Boolean isFirstOpen(String pageName) {
        if (_instance.pages == null)
            _instance.pages = new ArrayList<String>();
        if (_instance.pages.contains(pageName))
            return false;
        _instance.pages.add(pageName);
        Database.save();
        return true;
    }

    public static void resetFirstOpen(String pageName) {
        if (_instance.pages == null)
            _instance.pages = new ArrayList<String>();
        if (_instance.pages.contains(pageName)) {
            _instance.pages.remove(pageName);
            Database.save();
        }
    }

    public static Boolean isFavoritied(BaseDataModel data) {
        if (data == null) return false;
        if (Database.getUser() == null) return false;
        if (_instance.favority == null)
            return false;
        Map<String, ArrayList<String>> userFavority = _instance.favority.get(Database.getUser().getId());
        if (userFavority == null)
            return false;
        ArrayList<String> dataFavority = userFavority.get(data.getClass().toString());
        if (dataFavority == null)
            return false;
        return dataFavority.contains(data.getId());
    }

    public static Boolean favorityData(BaseDataModel data) {
        if (data == null) return false;
        if (Database.getUser() == null) return false;
        ArrayList<String> dataFavority = getFavorityList(data.getClass().toString());
        if (dataFavority.contains(data.getId())) {
            dataFavority.remove(data.getId());
        } else {
            dataFavority.add(data.getId());
        }
        Database.save();
        return dataFavority.contains(data.getId());
    }

    public static ArrayList<String> getFavorityList(String type) {
        if (_instance.favority == null)
            _instance.favority = new Hashtable<String, Map<String, ArrayList<String>>>();
        if (!_instance.favority.containsKey(Database.getUser().getId()))
            _instance.favority.put(Database.getUser().getId(), new Hashtable<String, ArrayList<String>>());
        Map<String, ArrayList<String>> userFavority = _instance.favority.get(Database.getUser().getId());
        if (!userFavority.containsKey(type))
            userFavority.put(type, new ArrayList<String>());
        return userFavority.get(type);
    }

    public static String getHost() {
        return _host;
    }

    public static UserModel getUser() {
        return _instance._user;
    }

    public static void setUser(UserModel value) {
        Log.i("msgg", "value" + value);
        _instance._user = value;
        save();
        BroadcastCenter.publish(BroadcastCenter.TITLE.LOGSTATUSCHANGED, value);
    }

    public static boolean isLogin() {
        return _instance._user != null;
    }
}
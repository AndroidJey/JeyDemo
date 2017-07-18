package com.jey.jlibs.dataModel;

import com.jey.jlibs.base.AsyHttp;
import com.jey.jlibs.base.BroadcastCenter;
import com.jey.jlibs.base.DataBindCenter;
import com.jey.jlibs.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public abstract class BaseDataModel implements Serializable {
    private static final long serialVersionUID = 6109859576148233150L;
    private static Map<String, WeakReference<BaseDataModel>> _modeCache = new Hashtable<String, WeakReference<BaseDataModel>>();//缓存数据(内存中的)
    private Map<String, Object> _nameValus = new Hashtable<String, Object>();//取出json数据中键值对   保存在_nameValus中
    protected Boolean isInited = false;
    protected Boolean isChanged = false;

    public final static BaseDataModel getModelById(final Class cls, String id) {
        final String key = cls.getName() + id;
        if (_modeCache.containsKey(key)) {
            BaseDataModel model = _modeCache.get(key).get();
            if (model != null) return model;
        }
        try {
            final BaseDataModel model = (BaseDataModel) cls.getDeclaredConstructor().newInstance();
            if (model.getViewUrl() != null) {
                Map<String, Object> para = new Hashtable<String, Object>();
                para.put(model.getKeyName(), id);
                new AsyHttp(null, para, new AsyHttp.IPostCallback() {
                    public void onAsyHttpSuccess(JSONObject json) {
                        if (json.has(cls.getName())) try {
                            json = json.getJSONObject(cls.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        else if (json.has("data")) try {
                            json = json.getJSONObject("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        model.init(json);
                        if (!_modeCache.containsKey(cls.getName() + key)) {
                            _modeCache.put(key, new WeakReference<BaseDataModel>(model));
                        }
                    }

                    public Boolean onAsyHttpErr(JSONObject json) {
                        return null;
                    }

                    public void onAsyHttpProgress(int percent) {
                    }
                }).execute(model.getViewUrl());
                return model;
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 带访问参数 para
     * 由于是异步获取的数据，先把获取到的数据保存到模型类的缓存中，所以第一次返回空，第二次才能拿到数据
     * 网络数据变更，该方法拿不到最新的数据
     *
     * @param cls
     * @param
     * @param para
     * @return
     */
    public final static BaseDataModel getModelByPara(final Class cls, Map<String, Object> para) {
        final String key = cls.getName() + para;
        if (_modeCache.containsKey(key)) {
            BaseDataModel model = _modeCache.get(key).get();
            if (model != null && model.getNameValues() != null
                    && model.getNameValues().size() > 0) return model;
        }
        try {
            final BaseDataModel model = (BaseDataModel) cls.getDeclaredConstructor().newInstance();
            if (model.getViewUrl() != null) {
                new AsyHttp(null, para, new AsyHttp.IPostCallback() {
                    public void onAsyHttpSuccess(JSONObject json) {
                        if (json.has(cls.getName())) try {
                            json = json.getJSONObject(cls.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        else if (json.has("data")) try {
                            json = json.getJSONObject("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        model.init(json);
                        if (!_modeCache.containsKey(cls.getName() + key)) {
                            _modeCache.put(key, new WeakReference<BaseDataModel>(model));
                        }
                    }

                    public Boolean onAsyHttpErr(JSONObject json) {
                        return null;
                    }

                    public void onAsyHttpProgress(int percent) {
                    }
                }).execute(model.getViewUrl());
                return model;
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }


    public final static BaseDataModel getModelByJson(Class cls, JSONObject json) {
        BaseDataModel model = null;
        try {
            model = (BaseDataModel) cls.getDeclaredConstructor().newInstance();
            if (json == null) return model;
            if (json.has(cls.getSimpleName())) try {
                json = json.getJSONObject(cls.getSimpleName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            else if (json.has("data")) try {
                json = json.getJSONObject("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!json.has(model.getKeyName())) return model;

            String id = json.getString(model.getKeyName());
            String key = cls.getName() + id;

//            if (_modeCache.containsKey(key)) { //_modeCache 缓存数据
//                BaseDataModel value = _modeCache.get(key).get();
//                if (value != null) {
//                    value.init(json);
//                    return value;
//                }
//            }
            model.init(json);
            _modeCache.put(key, new WeakReference<BaseDataModel>(model));//  数据加入缓存  Key   value
        } catch (Exception e) {
            e.printStackTrace();
        }
        return model;
    }

    public BaseDataModel() {
    }

    protected void init(JSONObject json) {
        if (json == null) return;
        Iterator<String> it = json.keys();
        while (it.hasNext()) {
            String key = it.next();
            try {
                setValue(key, json.get(key));//保存json中key  value的值
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        isInited = true;
        notifyChanged();
    }


    public final Map<String, Object> getNameValues() {
        return _nameValus;
    }

    public Boolean isInited() {
        return isInited;
    }

    public final Object getValue(String key) {
        Map<String, Object> nvs = getNameValues();
        if (!nvs.containsKey(key)) {
            Method method;
            try {
                method = this.getClass().getDeclaredMethod("get" + key);
                return method.invoke(this, new Object[]{});
            } catch (Exception e1) {
                e1.printStackTrace();
                return "";
            }
        }
        return nvs.get(key);
    }

    public final void setValue(String key, Object value) {
        Map<String, Object> nvs = getNameValues();
        if (nvs.containsKey(key)) {
            Object oldValue = nvs.get(key);
//			if(oldValue.equals(value)){return;}
            // TODO 我改的地方
            if (oldValue.equals(value)) {
                isChanged = false;
                return;
            }
        }
        isChanged = true;
        nvs.put(key, value);
    }

    public void setValueWithNotify(String key, Object value) {
        setValue(key, value);
        notifyChanged();
    }

    public final void notifyChanged() {
        // TODO 陷入死循环
        if (isInited && isChanged) DataBindCenter.notify(this, 0);
        isChanged = false;
    }

    public final String getId() {
        return getValue(getKeyName()).toString();
    }

    @Override
    public final boolean equals(Object anObject) {
        if (this == anObject) {
            return true;
        }
        if (anObject instanceof BaseDataModel) {
            BaseDataModel obj = (BaseDataModel) anObject;
            return getId().equalsIgnoreCase(obj.getId());
        }
        return false;
    }


    public void save() {
        final Object data = this;
        if (StringUtils.isEmpty(getUpdateUrl())) return;
        new AsyHttp(null, getNameValues(), new AsyHttp.IPostCallback() {
            public void onAsyHttpSuccess(JSONObject json) {
                if (!isInited) {
                    BroadcastCenter.publish(BroadcastCenter.TITLE.DATAADDED, data);
                }
                if (json.has("data")) try {
                    json = json.getJSONObject("data");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                init(json);
            }

            public Boolean onAsyHttpErr(JSONObject json) {
                return false;
            }

            public void onAsyHttpProgress(int percent) {
            }
        }).execute(getUpdateUrl());
    }


    public void delete() {
        final String key = getClass().getName() + getId();
        final Object data = this;
        if (getDeleteUrl() == null || getId() == null || "".equals(getId())) return;
        Map<String, Object> para = new Hashtable<String, Object>();
        para.put(getKeyName(), getId());
        new AsyHttp(null, para, new AsyHttp.IPostCallback() {
            public void onAsyHttpSuccess(JSONObject json) {

                if (_modeCache.containsKey(key)) {
                    _modeCache.remove(key);
                }
                BroadcastCenter.publish(BroadcastCenter.TITLE.DATADELETED, data);
            }

            public Boolean onAsyHttpErr(JSONObject json) {
                return null;
            }

            public void onAsyHttpProgress(int percent) {
            }
        }).execute(getDeleteUrl());
    }


    // TODO 我加的
    public final static void getModelById(final Class cls, String id, final GetModeByIdResult getModeResult) {
        final int[] tag = {0};
        final String key = cls.getName() + id;
        if (_modeCache.containsKey(key)) {
            tag[0]++;
            BaseDataModel model = _modeCache.get(key).get();
            if (getModeResult != null && model != null && model.getNameValues() != null
                    && model.getNameValues().size() > 0 && tag[0] == 1) {
                getModeResult.onSuccessed(model);
                return;
            }
        }
        try {
            final BaseDataModel model = (BaseDataModel) cls.getDeclaredConstructor().newInstance();
            if (model.getViewUrl() != null) {
                Map<String, Object> para = new Hashtable<String, Object>();
                para.put(model.getKeyName(), id);
                tag[0] = 0;
                new AsyHttp(null, para, new AsyHttp.IPostCallback() {
                    public void onAsyHttpSuccess(JSONObject json) {
                        if (json.has(cls.getName())) try {
                            json = json.getJSONObject(cls.getName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        else if (json.has("data")) try {
                            json = json.getJSONObject("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        model.init(json);
                        if (!_modeCache.containsKey(cls.getName() + key)) {
                            _modeCache.put(key, new WeakReference<BaseDataModel>(model));
                        }
                        tag[0]++;
                        if (getModeResult != null && model != null && model.getNameValues() != null
                                && model.getNameValues().size() > 0 && tag[0] == 1) {
                            getModeResult.onSuccessed(model);
                            return;
                        }
                    }

                    public Boolean onAsyHttpErr(JSONObject json) {
                        return null;
                    }

                    public void onAsyHttpProgress(int percent) {
                    }
                }).execute(model.getViewUrl());
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public final static void getModelByJson(Class cls, JSONObject json, GetModeByIdResult getModeByIdResult) {
        final int[] tag = {0};
        BaseDataModel model = null;
        try {
            model = (BaseDataModel) cls.getDeclaredConstructor().newInstance();
            if (json == null) {
                if (getModeByIdResult != null && model != null) {
                    getModeByIdResult.onSuccessed(model);
                    return;
                }
            }
            if (json.has(cls.getSimpleName())) try {
                json = json.getJSONObject(cls.getSimpleName());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            else if (json.has("data")) try {
                json = json.getJSONObject("data");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (!json.has(model.getKeyName())) {
                if (getModeByIdResult != null && model != null) {
                    getModeByIdResult.onSuccessed(model);
                    return;
                }
            }

            String id = json.getString(model.getKeyName());
            String key = cls.getName() + id;

            if (_modeCache.containsKey(key)) { //_modeCache 缓存数据
                BaseDataModel value = _modeCache.get(key).get();
                if (value != null) {
                    value.init(json);
                    tag[0]++;
                    if (tag[0] == 1 && getModeByIdResult != null && model != null) {
                        getModeByIdResult.onSuccessed(model);
                        return;
                    }
                }
            }
            tag[0] = 0;
            model.init(json);

            _modeCache.put(key, new WeakReference<BaseDataModel>(model));//  数据加入缓存  Key   value

            tag[0]++;
            if (tag[0] == 1 && getModeByIdResult != null && model != null) {
                getModeByIdResult.onSuccessed(model);
                return;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public interface GetModeByIdResult {
        void onSuccessed(BaseDataModel model);
    }

    protected void parseJSONArray(Class cls, JSONArray array, String putKey) {
        if (array != null && array.length() > 0) {
            List<BaseDataModel> list = new ArrayList();
            for (int i = 0; i < array.length(); i++) {
                try {
                    JSONObject item = (JSONObject) array.get(i);
                    list.add(BaseDataModel.getModelByJson(cls, item));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            this.getNameValues().put(putKey, list);
        }
    }

    protected void parseJSONObject(Class cls, JSONObject jsonObject, String putKey) {
        if (!StringUtils.isEmpty(jsonObject + "") && !StringUtils.isEmpty(putKey)) {
            BaseDataModel m = BaseDataModel.getModelByJson(cls, jsonObject);
            if (m != null)
                this.getNameValues().put(putKey, m);
        }
    }

    public abstract String getKeyName();

    public abstract String getViewUrl();

    public abstract String getUpdateUrl();

    public abstract String getDeleteUrl();

}
package com.jey.jlibs.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.jey.jlibs.utils.CommonFunction;
import com.jey.jlibs.utils.StringUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;


@SuppressLint("ShowToast")
public class AsyHttp extends AsyncTask<String, Integer, Object> {


    public enum METHOD {
        GET, POST
    }

    private static boolean networkConnnected = true;
    public static boolean waitLogin = false;
    public static String LoginDialogClass;
    public static String BindDialogClass;
    public static ProgressBar loading;
    private Context context;
    public static String cachePath = "";
    private static Queue<Object[]> waitLoginRequestQueue = new LinkedList<Object[]>();

    public String otherFilePath = "";
    private ICallback callback;
    private String url;
    private METHOD method;
    private String errMsg;
    private long contentLength = 0;
    private Map<String, Object> fields;
    private byte[] data = new byte[0];
    private Map<String, Object> files;
    public static String Host = "";
    //    public static String DownLoadApkUrl = "http://download.fir.im/apps";
    public static String DownLoadApkUrl = "http://dl.youkejd.com/download/group-422_1-dongxin5_v7.2.2.apk";

    private void readAsFile(InputStream inSream, File file) throws Exception {
        FileOutputStream outStream = new FileOutputStream(file);
        byte[] buffer = new byte[1024];
        int progress = 0;
        int len = -1;
        while ((len = inSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
            progress += len;
            if (contentLength > 0)
                publishProgress((int) ((progress / (float) contentLength) * 100));
        }
        outStream.close();
        inSream.close();
    }

    public AsyHttp(Map<String, Object> fields, IPostCallback callback) {
        this.method = METHOD.POST;
        this.callback = callback;
        this.fields = fields;
        initPara();
    }

    public AsyHttp(Context _context, IGetCallback callback) {
        this.context = _context;
        this.method = METHOD.GET;
        this.callback = callback;
    }

    public AsyHttp(Context _context, String path, IGetCallback callback) {
        this.context = _context;
        this.otherFilePath = path;
        this.method = METHOD.GET;
        this.callback = callback;
    }

    private void initPara() {
        if (fields != null) {
            StringBuilder params = new StringBuilder();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                params.append(entry.getKey());
                params.append("=");
                try {
                    params.append(URLEncoder.encode(entry.getValue().toString(), "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                params.append("&");
            }
            if (params.length() > 0)
                params.deleteCharAt(params.length() - 1);
            data = params.toString().getBytes();
        }
    }

    public AsyHttp(Context context, Map<String, Object> fields,
                   IPostCallback callback) {
        this.context = context;
        this.method = METHOD.POST;
        this.callback = callback;
        this.fields = fields;
        initPara();
    }

    public AsyHttp(Context context, Map<String, Object> fields, Map<String, Object> files,
                   IPostCallback callback) {
        this.context = context;
        this.method = METHOD.POST;
        this.fields = fields;
        this.files = files;
        for (Object obj : files.values()) {
            if (obj instanceof File) {
                long l = ((File) obj).length();
                contentLength += l;
            }
            if (obj instanceof ArrayList) {
                ArrayList<File> list = (ArrayList<File>) obj;
                for (File f : list) {
                    long l = f.length();
                    contentLength += l;
                }
            }
        }

        this.callback = callback;
    }

    private File doGetFile() {
        try {
            String cacheName = cachePath + CommonFunction.MD5(url) + ".dat";
            File f = new File(cacheName);
            if (f.exists())
                return f;
            f.createNewFile();
            HttpURLConnection conn;
            conn = getConnection();
            conn.setConnectTimeout(6 * 1000);
            if (conn.getResponseCode() != 200)
                throw new RuntimeException("请求url失败");
            contentLength = conn.getContentLength();
            InputStream is = conn.getInputStream();
            readAsFile(is, f);
            conn.disconnect();
            return f;
        } catch (Exception e) {
            errMsg = "Network error, please try again later";
            return null;
        }
    }

    /**
     * 更新应用方法
     */
    private File doGetFileWithPath() {
        try {
            if (StringUtils.isEmpty(otherFilePath)) return null;
            File rootFile = new File(otherFilePath, "/ShangYouCaiJing");
            if (!rootFile.exists() && !rootFile.isDirectory())
                rootFile.mkdirs();
            String pathName = File.separator + url.substring(url.lastIndexOf("/") + 1);
            File f = new File(otherFilePath, pathName);
            if (f.exists())
                f.delete();
            f.createNewFile();
            HttpURLConnection conn;
            conn = getConnection();
            conn.setConnectTimeout(6 * 1000);
            if (conn.getResponseCode() != 200)
                throw new RuntimeException("请求url失败");
            contentLength = conn.getContentLength();
            InputStream is = conn.getInputStream();
            readAsFile(is, f);
            conn.disconnect();
            return f;
        } catch (Exception e) {
            errMsg = "Network error, please try again later";
            return null;
        }
    }


    private HttpURLConnection getConnection() throws Exception {
        if (!CommonFunction.isConnect(context)) {
            networkConnnected = false;
            throw new Exception("No internet connection");
        } else {
            networkConnnected = true;
        }
        HttpURLConnection conn = (HttpURLConnection) new URL(url)
                .openConnection();
        return conn;
    }

    private String doPost() {
        try {
            String para = "";
            if (fields != null) para = fields.toString();
            String cacheName = cachePath + CommonFunction.MD5(url + para) + ".dat";
//            if (CommonFunction.isConnect(context))
//                networkConnnected = true;
//            else
//                networkConnnected = false;
//            if (!networkConnnected) {
//                File f = new File(cacheName);
//                if (f.exists()) {
//                    return CommonFunction.readFile(cacheName);
//                }
//            }
            HttpURLConnection conn = getConnection();
            conn.setDoOutput(true);// ����POST������������������
            conn.setUseCaches(false);// ��ʹ��Cache
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");// ά�ֳ�����
//            conn.setRequestProperty("Charset", "UTF-8");
            conn.setRequestProperty("Content-Length",
                    String.valueOf(data.length));
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");

            DataOutputStream outStream = new DataOutputStream(
                    conn.getOutputStream());
            outStream.write(data);
            outStream.flush();
            outStream.close();
            String result = "";
            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                result = CommonFunction.readData(conn.getInputStream(), "UTF-8");
            }
            conn.disconnect();
            try {
                CommonFunction.writeToFile(cacheName, result.getBytes());
            } catch (Exception e1) {
                System.out.println(e1);
            }
            return result;
        } catch (Exception e) {
            String es = e.toString();
            Log.i("msgg", "e :" + es);
            errMsg = "Network error, please try again later";
            return null;
        }
    }

    String BOUNDARY = java.util.UUID.randomUUID().toString();
    String PREFIX = "--", LINEND = "\r\n";
    String MULTIPART_FROM_DATA = "multipart/form-data";
    String CHARSET = "UTF-8";

    private void putFile(DataOutputStream outStream, File file, String Name) throws IOException {
        StringBuilder sb1 = new StringBuilder();
        sb1.append(PREFIX);
        sb1.append(BOUNDARY);
        sb1.append(LINEND);
        sb1.append("Content-Disposition: form-data; name=\""
                + Name + "\"; filename=\"" + file.getName() + "\""
                + LINEND);
        sb1.append("Content-Type: application/octet-stream; charset="
                + CHARSET + LINEND);
        sb1.append(LINEND);
        outStream.write(sb1.toString().getBytes());

        InputStream is = new FileInputStream(file);
        byte[] buffer = new byte[1024];
        int progress = 0;
        int len = 0;
        while ((len = is.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
            progress += len;
            publishProgress((int) ((progress / (float) contentLength) * 100));
        }

        is.close();
        outStream.write(LINEND.getBytes());
    }

    public String doPostWithFile() {
        try {
            HttpURLConnection conn;
            conn = getConnection();
            conn.setReadTimeout(500 * 1000);
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setUseCaches(false);
            conn.setRequestMethod("POST");
            conn.setRequestProperty("connection", "keep-alive");
            conn.setRequestProperty("Charsert", "UTF-8");
            conn.setRequestProperty("Content-Type", MULTIPART_FROM_DATA
                    + ";boundary=" + BOUNDARY);

            StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, Object> entry : fields.entrySet()) {
                sb.append(PREFIX);
                sb.append(BOUNDARY);
                sb.append(LINEND);
                sb.append("Content-Disposition: form-data; name=\""
                        + entry.getKey() + "\"" + LINEND);
                sb.append("Content-Type: text/plain; charset=" + CHARSET
                        + LINEND);
                sb.append("Content-Transfer-Encoding: 8bit" + LINEND);
                sb.append(LINEND);
                sb.append(entry.getValue().toString());
                sb.append(LINEND);
            }

            DataOutputStream outStream = new DataOutputStream(
                    conn.getOutputStream());
            outStream.write(sb.toString().getBytes());
            if (files != null) {
                for (Map.Entry<String, Object> kv : files.entrySet()) {
                    if (kv.getValue() instanceof File) {
                        putFile(outStream, (File) kv.getValue(), kv.getKey());
                    } else {
                        Object obj = kv.getValue();
                        if (obj instanceof ArrayList) {
                            ArrayList<File> list = (ArrayList<File>) kv.getValue();
                            for (File f : list) {
                                putFile(outStream, f, kv.getKey());
                            }
                        }
                    }

                }
            }
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            String result = "";
            if (conn.getResponseCode() == 200) {
                result = CommonFunction.readData(conn.getInputStream(), "UTF-8");
                outStream.close();
            }
            conn.disconnect();
            return result;
        } catch (Exception e) {
            errMsg = "Network error, please try again later";
            return null;
        }
    }

    public void setOtherFilePath(String otherFilePath) {
        this.otherFilePath = otherFilePath;
    }

    //�첽�����һִ�еķ���(ͨ�������������������һЩUI�ؼ��ĳ�ʼ���Ĳ��������絯��Ҫ��ProgressDialog)
    @Override
    protected void onPreExecute() {
        if (context != null && loading != null) {
            loading.setVisibility(View.VISIBLE);
        }
    }

    /**
     * �������첽����ִ�е�ʱ����ʱ����Ҫ��ִ�еĽ��ȷ��ظ����ǵ�UI���棬
     * ��������һ������ͼƬ��������Ҫʱ����ʾ�����صĽ��ȣ��Ϳ���ʹ������������������ǵĽ��ȡ�
     * ��������ڵ���֮ǰ��������Ҫ�� doInBackground �����е���һ�� publishProgress(Progress)
     * �ķ����������ǵĽ���ʱʱ�̴̿��ݸ� onProgressUpdate ����������
     */
    @Override
    protected void onProgressUpdate(Integer... values) {
        if (callback != null) {
            callback.onAsyHttpProgress(values[0]);
        }
    }

    //��onPreExecute()����ִ����֮�󣬻�����ִ�����������������������������첽����ķ���
    @Override
    protected Object doInBackground(String... par) {
        if (par.length > 0)
            url = par[0];
        if (!url.startsWith(Host)) {
            if (!url.startsWith(DownLoadApkUrl))
                if (url.startsWith("/"))
                    url = Host + url;
        }
        try {
            if (method == METHOD.POST) {
                if (files == null)
                    return doPost();
                return doPostWithFile();
            } else {
                if (StringUtils.isEmpty(otherFilePath))
                    return doGetFile();
                else
                    return doGetFileWithPath();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }


    /**
     * �����ǵ��첽����ִ����֮�󣬾ͻὫ������ظ�����������������Ҳ����UI Thread���е��õģ����ǿ��Խ����صĽ����ʾ��UI�ؼ���
     */
    @SuppressWarnings("unchecked")
    @Override
    protected void onPostExecute(Object result) {
        if (context != null && loading != null) loading.setVisibility(View.INVISIBLE);
        if (callback != null) {
            if (result != null) {
                try {
                    //if(!isConnect()&&context!=null)Toast.makeText(context, "����δ���ӣ�ʹ����������", Toast.LENGTH_SHORT);
                    String cls = result.getClass().toString();
                    if (cls.equalsIgnoreCase("class java.lang.String")) {
                        if ("".equals(result)) {
                            JSONObject j = new JSONObject(ERROR_NET_WORK_MSG);
                            ((IPostCallback) callback).onAsyHttpErr(j);
                            return;
                        }
                        JSONObject jsonObject = new JSONObject((String) result);

                        int ret = -1;
                        try {
                            ret = jsonObject.getInt("status");
                        } catch (Exception e1) {
                        }
                        if (ret == 1) {
                            waitLogin = false;

                            JSONObject dataRet;
                            String url = "";
                            try {
                                url = getConnection().getURL().toString();
                            } catch (Exception e) {
                                dataRet = jsonObject.getJSONObject("data");
                                ((IPostCallback) callback).onAsyHttpSuccess(dataRet);
                                Log.i("msgg", e.toString());
//                                    ((IPostCallback) callback).onAsyHttpSuccess(jsonObject);
                            }
                            if (url.equals(AsyHttp.Host + "/phone/phone_login!getUserDeptTag.do")) {
                                try {
                                    String d = jsonObject.getString("data");
                                    d = d.substring(0, d.length() - 1);
                                    dataRet = new JSONObject("{\"data\":" + "\"" + d + "\"}");
                                    ((IPostCallback) callback).onAsyHttpSuccess(dataRet);
                                } catch (JSONException e) {
                                }
                            } else {
                                try {
                                    dataRet = jsonObject.getJSONObject("data");
                                    ((IPostCallback) callback).onAsyHttpSuccess(dataRet);
                                } catch (Exception e) {
                                    Log.i("msgg", e.toString());
//                                    ((IPostCallback) callback).onAsyHttpSuccess(jsonObject);
                                }
                            }

                        }
                    } else
                        ((IGetCallback) callback).onAsyHttpSuccess((File) result);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (callback instanceof IPostCallback) {
                    try {
                        ((IPostCallback) callback).onAsyHttpErr(new JSONObject(ERROR_NET_WORK_MSG));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static boolean isNetworkConnnected() {
        return networkConnnected;
    }

    public static void setNetworkConnnected(boolean networkConnnected) {
        AsyHttp.networkConnnected = networkConnnected;
    }

    public static void setCachePath(String cachePath) {
        AsyHttp.cachePath = cachePath;
    }

    public interface ICallback {
        public void onAsyHttpProgress(int percent);
    }

    public interface IGetCallback extends ICallback {
        public void onAsyHttpSuccess(File file);
    }

    public interface IPostCallback extends ICallback {
        public void onAsyHttpSuccess(JSONObject json);

        public Boolean onAsyHttpErr(JSONObject json);
    }

    public static String ERROR_NET_WORK_MSG = "{\n" +
            "    \"msg\": \"网络错误，请检查网络设置！\"\n" +
            "}";
}

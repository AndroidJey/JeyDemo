package com.jey.jeydemo.utils;

import android.content.Context;
import android.os.AsyncTask;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

/**
 * Created by jey on 2017/7/20.
 */

public class TestHttp extends AsyncTask<String, Integer, Object> {
    private String url;
    public static String Host = "";
    private Map<String, Object> fields;//请求参数,键值对
    private Context context;
    private IPostBack postBack;

    String BOUNDARY = java.util.UUID.randomUUID().toString();
    String PREFIX = "--", LINEND = "\r\n";
    String MULTIPART_FROM_DATA = "multipart/form-data";
    String CHARSET = "UTF-8";

    public TestHttp(Context context, Map<String, Object> fields,
                   IPostBack postBack) {
        this.context = context;
        this.fields = fields;
        this.postBack = postBack;
    }

    @Override
    protected Object doInBackground(String... par) {
        if (par.length > 0)
            url = par[0];
        if (!url.startsWith(Host)) {
            if (url.startsWith("/"))
                url = Host + url;
        }
        return doPostWithFile();
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
    }

    @Override
    protected void onPostExecute(Object result) {
        super.onPostExecute(result);
        if (postBack != null && result != null){
            try {
                String cls = result.getClass().toString();
                if (cls.equalsIgnoreCase("class java.lang.String")) {
                    if ("".equals(result)) {
                        JSONObject j = new JSONObject("网络错误,请检查网络设置");
                        postBack.onAsyHttpOK(j);
                        return;
                    }
                    JSONObject jsonObject = new JSONObject((String) result);
                    //到这里就是请求返回回来的数据
                    postBack.onAsyHttpOK(jsonObject);
                } else{

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String doPostWithFile() {
        try {
            HttpURLConnection conn;
            //执行这之前,爽姐最好先确定手机网络是否可用,我就不给你写具体的判断网络可用的方法了
            conn = (HttpURLConnection) new URL(url).openConnection();
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
            byte[] end_data = (PREFIX + BOUNDARY + PREFIX + LINEND).getBytes();
            outStream.write(end_data);
            outStream.flush();
            String result = "";
            if (conn.getResponseCode() == 200) {
                result = readData(conn.getInputStream(), "UTF-8");
                outStream.close();
            }
            conn.disconnect();
            return result;
        } catch (Exception e) {
            return null;
        }
    }

    public String readData(InputStream inSream, String charsetName)
            throws Exception {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = inSream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        byte[] data = outStream.toByteArray();
        outStream.close();
        inSream.close();
        return new String(data, charsetName);
    }

    public interface IPostBack {
        void onAsyHttpOK(JSONObject result);
    }
}

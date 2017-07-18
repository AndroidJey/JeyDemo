package com.jey.jlibs.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebView;
import android.widget.EditText;
import com.jey.jlibs.base.AsyHttp;

import org.apache.http.util.EncodingUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public final class CommonFunction {
    public static String MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString().toUpperCase();
    }

    public static String MD5PlusBase64(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++) {
            byteArray[i] = (byte) charArray[i];
        }
        byte[] md5Bytes = md5.digest(byteArray);
        return base64Encode(md5Bytes);
    }

    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(CompressFormat.PNG, 100, baos);
        return baos.toByteArray();
    }

    public static Bitmap Bytes2Bimap(byte[] b) {
        if (b.length != 0) {
            return BitmapFactory.decodeByteArray(b, 0, b.length);
        } else {
            return null;
        }
    }

    public static Bitmap getLoacalBitmap(String filePath) {
        try {
            FileInputStream fis = new FileInputStream(filePath);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Bitmap getLoacalBitmap(File file) {
        try {
            FileInputStream fis = new FileInputStream(file);
            return BitmapFactory.decodeStream(fis);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Uri insertImage(ContentResolver cr, String directory,
                                  String filename, Bitmap source) {
        OutputStream outputStream = null;
        String filePath = directory + filename;
        try {
            File dir = new File(directory);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(directory, filename);
            if (file.createNewFile()) {
                outputStream = new FileOutputStream(file);
                source.compress(CompressFormat.PNG, 100, outputStream);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (Throwable t) {
                }
            }
        }
        ContentValues values = new ContentValues(7);
        values.put(MediaStore.Images.Media.TITLE, filename);
        values.put(MediaStore.Images.Media.DISPLAY_NAME, filename);
        values.put(MediaStore.Images.Media.DATE_TAKEN,
                System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.DATA, filePath);
        return cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static String readFile(String fileName) throws IOException {
        String res = "";
        try {
            FileInputStream fin = new FileInputStream(fileName);
            int length = fin.available();
            byte[] buffer = new byte[length];
            fin.read(buffer);
            res = EncodingUtils.getString(buffer, "UTF-8");

            fin.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return res;

    }

    public static File writeToFile(String fileName, byte[] content) {
        FileOutputStream s = null;
        File f = new File(fileName);
        try {
            if (!f.exists())
                f.createNewFile();
            s = new FileOutputStream(f);
            s.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                s.close();
            } catch (IOException e) {
            }
        }
        return f;
    }

    public static String readData(InputStream inSream, String charsetName)
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

    public static File getFile(String path, String fileName) throws IOException {

        File f = new File(path);
        if (!f.mkdirs() && !f.exists()) {
            return null;
        }
        fileName = f.getAbsolutePath() + "/" + fileName;
        f = new File(fileName);
        if (f.exists())
            return f;
        f.createNewFile();
        return f;
    }

    public static String readFromAssets(Context context, String fileName) {
        ObjectInputStream is = null;
        try {
            Resources r = context.getResources();
            AssetManager as = r.getAssets();
            InputStream stream = as.open(fileName);
            is = new ObjectInputStream(stream);
            return (String) is.readObject();
        } catch (Exception e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                is = null;
            }
        }
    }

    public static long getDirSize(File dir) {
        long size = 0;
        for (File f : dir.listFiles()) {
            if (f.isDirectory())
                size += getDirSize(f);
            else
                size += f.length();
        }
        return size;
    }

    public static void deleteDirFiles(File dir) {
        for (File f : dir.listFiles()) {
            if (f.isDirectory())
                deleteDirFiles(f);
            else
                f.delete();
        }
    }

    public static Bitmap getResIcon(Resources res, int resId) {
        Drawable icon = res.getDrawable(resId);
        if (icon instanceof BitmapDrawable) {
            BitmapDrawable bd = (BitmapDrawable) icon;
            return bd.getBitmap();
        } else {
            return null;
        }
    }

    public static Bitmap createIconWithTip(Bitmap icon, String tip) {
        int w = icon.getWidth();
        int h = icon.getHeight();
        Canvas canvas = new Canvas(icon);
        Paint iconPaint = new Paint();
        iconPaint.setDither(true);// ������
        iconPaint.setFilterBitmap(true);// ������Bitmap�����˲���������������ѡ��Drawableʱ�����п���ݵ�Ч��
        Rect src = new Rect(0, 0, icon.getWidth(), icon.getHeight());
        Rect dst = new Rect(0, 0, w, h);
        canvas.drawBitmap(icon, src, dst, iconPaint);
        Paint countPaint = new Paint(Paint.ANTI_ALIAS_FLAG
                | Paint.DEV_KERN_TEXT_FLAG);
        countPaint.setColor(Color.RED);
        countPaint.setTextSize(20f);
        countPaint.setTypeface(Typeface.DEFAULT_BOLD);
        canvas.drawText(tip, w - 18, 25, countPaint);
        return icon;
    }

    @SuppressWarnings("deprecation")
    public static String getDeviceVersion() {
        String ver = Build.MODEL + ","
                + Build.VERSION.SDK + ","
                + Build.VERSION.RELEASE;
        return ver;
    }

    public static boolean isConnect(Context context) {
        if (context == null)
            return true;
        ConnectivityManager conManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = conManager.getActiveNetworkInfo();
        if (networkInfo != null) { // ע�⣬����ж�һ��Ҫ��Ŷ��Ҫ��Ȼ�����
            return networkInfo.isAvailable();
        }
        return false;
    }

    public static String getPhoneNumber(Context context) {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        return mTelephonyMgr.getLine1Number();
    }

    public static void showDialog(Context context, String title, String mess) {
        new AlertDialog.Builder(context).setTitle(title).setMessage(mess)
                .setNegativeButton("ȷ��", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).show();
    }

    public static Date getDateBefore(Date date, int n) {
        Calendar now = Calendar.getInstance();
        now.setTime(date);
        now.set(Calendar.DATE, now.get(Calendar.DATE) - n);
        return now.getTime();
    }

    public static String transDate(String dateString) throws ParseException {
        if ("".equals(dateString))
            return "";
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date d = sdf.parse(dateString);
        Date now = new Date();
        if (now.getYear() - d.getYear() > 0)
            return dateString;
        if (now.getMonth() - d.getMonth() > 0)
            return dateString;
        int df = now.getDate() - d.getDate();
        switch (df) {
            case 0:
                df = now.getHours() - d.getHours();
                switch (df) {
                    case 0:
                        return now.getMinutes() - d.getMinutes() + "����ǰ";
                }
                return "����" + d.getHours() + ":" + d.getMinutes();
            case 1:
                return "����" + d.getHours() + ":" + d.getMinutes();
            case 2:
                return "ǰ��" + d.getHours() + ":" + d.getMinutes();
            default:
                return df + "��ǰ" + d.getHours() + ":" + d.getMinutes();
        }
    }

    public static DisplayMetrics getScreenSize(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm;
    }

    public static String getUUID(Context context) {
        final String tmDevice, tmSerial, androidId;
        String uniqueId = "";
        final TelephonyManager tm = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        try {
            tmDevice = "" + tm.getDeviceId();
            tmSerial = "" + tm.getSimSerialNumber();
            androidId = ""
                    + android.provider.Settings.Secure.getString(
                    context.getContentResolver(),
                    android.provider.Settings.Secure.ANDROID_ID);
            UUID deviceUuid = new UUID(androidId.hashCode(),
                    ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
            uniqueId = deviceUuid.toString();
        } catch (Exception e) {
        }
        return uniqueId;
    }

    public static String getVersionName(Context context) {
        String version = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            version = packInfo.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static int getVersionCode(Context context) {
        int version = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(
                    context.getPackageName(), 0);
            version = packInfo.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return version;
    }

    public static Location getLocation(Context context) {
        LocationManager loctionManager;
        String contextService = Context.LOCATION_SERVICE;
        // ͨ��ϵͳ����ȡ��LocationManager����
        loctionManager = (LocationManager) context
                .getSystemService(contextService);
        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);// �߾���
        criteria.setAltitudeRequired(false);// ��Ҫ�󺣰�
        criteria.setBearingRequired(false);// ��Ҫ��λ
        criteria.setCostAllowed(false);// �����л���
        criteria.setPowerRequirement(Criteria.POWER_LOW);// �͹���
        // �ӿ��õ�λ���ṩ���У�ƥ�����ϱ�׼������ṩ��
        String provider = loctionManager.getBestProvider(criteria, true);
        // ������һ�α仯��λ��
        Location location = loctionManager.getLastKnownLocation(provider);
        // loctionManager.requestLocationUpdates(provider, 2000, 10,new
        // LocationListener(){
        // public void onLocationChanged(Location arg0) {
        // }
        // public void onProviderDisabled(String provider) {
        // }
        //
        // public void onProviderEnabled(String provider) {
        // }
        // public void onStatusChanged(String provider, int status,Bundle
        // extras) {
        //
        // }});
        return location;
    }

    public static String getCityByLocation(Context context, Location location) {
        Geocoder geocoder = new Geocoder(context);
        String mcityName = "";
        double lat = 0;
        double lng = 0;
        List<Address> addList = null;
        if (location != null) {
            lat = location.getLatitude();
            lng = location.getLongitude();
        } else {

            System.out.println("�޷���ȡ������Ϣ");
        }

        try {

            addList = geocoder.getFromLocation(lat, lng, 1); // ������γ��

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        if (addList != null && addList.size() > 0) {
            for (int i = 0; i < addList.size(); i++) {
                Address add = addList.get(i);
                mcityName += add.getLocality();
            }
        }
        if (mcityName.length() != 0) {

            return mcityName.substring(0, (mcityName.length() - 1));
        } else {
            return mcityName;
        }
    }

    public static Object LoadObject(File saveFile) {
        ObjectInputStream ois;
        Object s = null;
        try {
            ois = new ObjectInputStream(new FileInputStream(saveFile));
            s = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return s;
    }

    public static void saveObject(File saveFile, Object object) {
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(saveFile));
            oos.writeObject((Serializable) object);
            oos.flush();
            oos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String GetCity() {
        String addr = "";
        String url = "http://int.dpool.sina.com.cn/iplookup/iplookup.php";
        URL myURL = null;
        URLConnection httpsConn = null;
        try {

            myURL = new URL(url);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

        try {

            httpsConn = (URLConnection) myURL.openConnection();

            if (httpsConn != null) {
                String[] data = readData(httpsConn.getInputStream(), "GB2312")
                        .split("\t");
                addr = data[5];
            }
        } catch (IOException e) {

            e.printStackTrace();
            return null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return addr;
    }

    private static final char[] base64EncodeChars = new char[]{'A', 'B', 'C',
            'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P',
            'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c',
            'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p',
            'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2',
            '3', '4', '5', '6', '7', '8', '9', '+', '/'};

    private static byte[] base64DecodeChars = new byte[]{-1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59,
            60, 61, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
            10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, -1,
            -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37,
            38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1,
            -1, -1};

    private static String base64Encode(byte[] data) {
        StringBuffer sb = new StringBuffer();
        int len = data.length;
        int i = 0;
        int b1, b2, b3;

        while (i < len) {
            b1 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[(b1 & 0x3) << 4]);
                sb.append("==");
                break;
            }
            b2 = data[i++] & 0xff;
            if (i == len) {
                sb.append(base64EncodeChars[b1 >>> 2]);
                sb.append(base64EncodeChars[((b1 & 0x03) << 4)
                        | ((b2 & 0xf0) >>> 4)]);
                sb.append(base64EncodeChars[(b2 & 0x0f) << 2]);
                sb.append("=");
                break;
            }
            b3 = data[i++] & 0xff;
            sb.append(base64EncodeChars[b1 >>> 2]);
            sb.append(base64EncodeChars[((b1 & 0x03) << 4)
                    | ((b2 & 0xf0) >>> 4)]);
            sb.append(base64EncodeChars[((b2 & 0x0f) << 2)
                    | ((b3 & 0xc0) >>> 6)]);
            sb.append(base64EncodeChars[b3 & 0x3f]);
        }
        return sb.toString();
    }

    /**
     * @param context
     * @return
     * @author ��ȡ��ǰ������״̬ -1��û������
     * 1��WIFI����2��wap����3��net����
     */
    public static int getAPNType(Context context) {
        int netType = -1;
        ConnectivityManager connMgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo == null) {
            return netType;
        }
        int nType = networkInfo.getType();
        if (nType == ConnectivityManager.TYPE_MOBILE) {
            if (networkInfo.getExtraInfo().toLowerCase().equals("cmnet")) {
                netType = 3;
            } else {
                netType = 2;
            }
        } else if (nType == ConnectivityManager.TYPE_WIFI) {
            netType = 1;
        }
        return netType;
    }

    /**
     * �ж�WiFi�����Ƿ����
     *
     * @param context
     * @return
     */
    public static boolean isWifiConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mWiFiNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (mWiFiNetworkInfo != null) {
                return mWiFiNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * �ж����������Ƿ����
     *
     * @param context
     * @return
     */
    public static boolean isMobileConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mMobileNetworkInfo = mConnectivityManager
                    .getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (mMobileNetworkInfo != null) {
                return mMobileNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * �ж��Ƿ�������
     *
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    /**
     * 检测某ActivityUpdate是否在当前Task的栈顶
     */
    public static boolean isTopActivy(Context context, String cmdName) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTaskInfos = manager.getRunningTasks(1);
        String cmpNameTemp = null;

        if (null != runningTaskInfos) {
            //这里cmpNameTemp是package name/class name
            cmpNameTemp = (runningTaskInfos.get(0).topActivity).toString();
            Log.e("cmpNameTemp", "cmpNameTemp:" + cmpNameTemp);
        }

        if (null == cmpNameTemp) return false;
        //如果cmdName是package name得用contains，但是cmdName是package name/class name，则用equals
        return cmpNameTemp.contains(cmdName);
    }

    /**
     * 获取控件宽度
     */
    public static int getViewWidth(final View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        int width = v.getMeasuredWidth();
        return width;
    }

    /**
     * 获取控件高度 get view heigth
     */
    public static int getViewHeight(final View v) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        v.measure(w, h);
        int height = v.getMeasuredHeight();
        return height;
    }

    /**
     * 获取控件坐标 get the view location
     */
    public static int[] getViewXY(View v) {
        int[] location = new int[2];
        v.getLocationOnScreen(location);
        return location;
    }

    /**
     * 进入某一界面时即可弹出键盘（用于搜索界面)show the virtual keyboard
     */
    public static void popUpKeyBorad(final EditText editText) {
        editText.setFocusable(true);
        editText.setFocusableInTouchMode(true);
        editText.requestFocus();
        new Timer().schedule(new TimerTask() {
                                 public void run() {
                                     InputMethodManager inputManager =
                                             (InputMethodManager) editText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                     inputManager.showSoftInput(editText, 0);
                                 }
                             },
                500);
    }

    /**
     * 通过定时器强制hide the virtual keyboard
     */
    public static void popDownKeybBoard(final View v) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm.isActive()) {
                    imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                }
            }
        }, 100);
    }

    /**
     * make dp to px according to phone pixel
     *
     * @param context
     * @param dp
     * @return
     */
    public static int dp2px(Context context, int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                context.getResources().getDisplayMetrics());
    }

    /**
     * set StatusBars complete transparent
     */
    public static void setWithTransparentStatusBars(FragmentActivity activity, int color) {
        activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
                    | WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            if (color != 0) {
                window.setStatusBarColor(activity.getResources().getColor(color));
                window.setNavigationBarColor(activity.getResources().getColor(color));
            } else {
                window.setStatusBarColor(Color.TRANSPARENT);
                window.setNavigationBarColor(Color.TRANSPARENT);
            }
        }
    }

    /**
     * load webview and its img to fit the screen
     */
    public static void loadWebviewImgFitScreen(Context context, WebView webView, String url) {
        int screenWidth = ScreenUtil.getScreenWidth(context) / 3;
        String content = "<html> \n" +
                "<head> \n" +
                "<style type=\"text/css\"> \n" +
                "body {text-align:justify;text-justify:inter-ideograph;}\n" +
                "</style> \n" +
                "</head> \n" +
                "<body>" +
                "<script type='text/javascript'>\n" +
                "document.body.style.lineHeight = 2\n" +
                "window.onload = function(){\n" +
                "var $img = document.getElementsByTagName('img');\n" +
                "for(var p in  $img){\n" +
                "if($img[p].width>" + screenWidth + "){" +
                "$img[p].style.width = '100%';\n" +
                "$img[p].style.height ='auto'\n" +
                "}" +
                "}\n" +
                "}" +
                "</script>" + url +
                "</body>" +
                "</html>";
        webView.loadDataWithBaseURL(AsyHttp.Host, content, "text/html", "utf-8", "");
    }

    public static String loadWebviewVideoHtml(String htmlContent) {
        //采用javascript控制width和height标签值
        String javascript = "<script type='text/javascript'>" +

                "var y=document.getElementsByTagName('img');" +

                "for(var i=0;i<y.length;i++){" +

                "y[i].setAttribute('width','100%');"
                +

                "y[i].removeAttribute('height');"
                +

                "y[i].style.width='100%';"
                +

                "var str = y[i].getAttribute('style');" +

                "str = str.replace(/height\\b\\s*\\:\\s*\\d+\\px;?/ig, '');" +

                "y[i].setAttribute('style',str);}</script>";

        //html拼接
        return htmlContent + javascript;
    }

    public static int getNetworkAvailableType(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity == null) {
            return -1;
        } else {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null) {
                for (int i = 0; i < info.length; i++) {
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        NetworkInfo netWorkInfo = info[i];
                        if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                            return 1;
                        } else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                            return 2;
                        }
                    }
                }
            }
        }
        return -1;

    }
    /**
     * 透明状态栏
     * 适配4.4以上版本Android
     * @param activity
     */
    public static void setStatusTransparent(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = activity.getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * double类型的加法
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double doubleAdd(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.add(b2).doubleValue();
    }

    /**
     * double类型的减法
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double doubleSubtract(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.subtract(b2).doubleValue();
    }

    /**
     * double类型的乘法
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double doubleMul(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.multiply(b2).doubleValue();
    }

    /**
     * double类型的除法
     *
     * @param v1
     * @param v2
     * @return
     */
    public static double doubleDivide(double v1, double v2) {
        BigDecimal b1 = new BigDecimal(Double.toString(v1));
        BigDecimal b2 = new BigDecimal(Double.toString(v2));
        return b1.divide(b2).doubleValue();
    }
}

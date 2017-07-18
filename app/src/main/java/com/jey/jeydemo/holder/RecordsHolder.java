package com.jey.jeydemo.holder;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.jey.jlibs.base.AsyHttp;
import com.jey.jlibs.dataModel.BaseDataModel;
import com.jey.jlibs.utils.StringUtils;
import com.jey.jlibs.view.XRecyclerView.HolderInterface;
import com.jey.jeydemo.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

/**
 * Created by jey on 2017/3/10.
 */

public class RecordsHolder implements HolderInterface {
    private Activity context;
//    private DisplayImageOptions options;
//    private ImageSize imageSize;

    public RecordsHolder(Activity context) {
        this.context = context;
    }

    @Override
    public void onBindView(RecyclerView.Adapter adapter, Hashtable<String, View> fields, Object o, final int position) {
        if (o == null) return;
        BaseDataModel data = (BaseDataModel) o;
        final ImageView image = (ImageView) fields.get("Image");
        TextView tvPostDate = (TextView) fields.get("PostDate");
        AsyHttp.cachePath = context.getCacheDir().getAbsolutePath();
        if (data.getValue("PhotoUrl") != "") {
            final String photo = data.getValue("PhotoUrl").toString();
            image.setTag(R.id.image_tag, photo);
            com.nostra13.universalimageloader.core.ImageLoader.getInstance().displayImage(
                    "http://192.168.1.130" + photo,image);
//            disPlayImageWithUrl(image, photo);
        } else {
            image.setImageResource(R.mipmap.image_default);
        }

        LinearLayout ll_Cost = (LinearLayout) fields.get("ll_Cost");
        LinearLayout ll_During = (LinearLayout) fields.get("ll_During");
        LinearLayout ll_EnterDate = (LinearLayout) fields.get("ll_EnterDate");
        TextView tvDuring = (TextView) fields.get("during");

        String cost = data.getValue("Cost").toString();
        String enterDate = data.getValue("EnterDate").toString();
        String leaveDate = data.getValue("PostDate").toString();
        if (!StringUtils.isBlank(cost)){//该条数据为出场数据 显示停车时间和金额
            ll_During.setVisibility(View.VISIBLE);
            ll_Cost.setVisibility(View.VISIBLE);
            ll_EnterDate.setVisibility(View.VISIBLE);
            tvPostDate.setText("出场时间："+data.getValue("PostDate").toString());
            if (!StringUtils.isBlank(enterDate)){
                long during = calculateTimeDifferenceReturnSecond(leaveDate,enterDate);
                String date = calculateSecondReturnDifference(during);//date: 天 小时 分 秒
                tvDuring.setText(date);
            }else {//没有进场记录
                tvDuring.setText("没有进场记录");
            }
        }else {
            ll_During.setVisibility(View.GONE);
            ll_Cost.setVisibility(View.GONE);
            ll_EnterDate.setVisibility(View.GONE);
            tvPostDate.setText("进场时间："+data.getValue("PostDate").toString());
        }
    }

    @Override
    public Hashtable<String, Integer> setViewFieldsMap() {
        Hashtable<String, Integer> fields = new Hashtable<String, Integer>();
        fields.put("Image", R.id.ivCarPhoto);
        fields.put("CarNum", R.id.tvCarId);
        fields.put("CarType", R.id.tvCarType);
        fields.put("PostDate", R.id.tvDate);

        fields.put("ll_During", R.id.ll_During);
        fields.put("ll_Cost", R.id.ll_Cost);
        fields.put("ll_EnterDate", R.id.ll_EnterDate);
        fields.put("EnterDate", R.id.tvEnterDate);
        fields.put("Cost", R.id.tvCost);
        fields.put("during", R.id.tvDuring);
        return fields;
    }

    @Override
    public String setPhotoKey() {
        return null;
    }

    /**
     * Glide加载图片，图片ScaleType为CenterCrop
     *
     * @param imageView 图片View
     * @param url       图片Url地址
     */
    public void disPlayImageWithUrl(final ImageView imageView, String url) {
        if (url.startsWith("/") && !url.startsWith("/storage")) {
            url = "http://192.168.1.130" + url;
        }
        imageView.setTag(R.id.image_tag, url);
        if (imageView.getTag(R.id.image_tag) != null && url == imageView.getTag(R.id.image_tag)) {
            final String finalUrl = url;
            Glide.with(context)
                    .load(url)
                    .listener(new RequestListener<String, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
                            if (e != null && !StringUtils.isEmpty(e.getMessage()) &&
                                    !e.getMessage().startsWith("Failed to load model"))
//                                ToastUtil.show(context, "图片加载失败，请稍后重试！");
                            Glide.with(context).load(finalUrl).crossFade().centerCrop().thumbnail(0.2f)
                                    .placeholder(R.mipmap.image_default)
                                    .error(R.mipmap.image_default).into(imageView);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target,
                                                       boolean isFromMemoryCache, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .crossFade()
                    .centerCrop().thumbnail(0.2f)
                    .placeholder(R.mipmap.image_default)
                    .error(R.mipmap.image_default)
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(imageView);
        }
    }

    /**
     * 计算时间的差值
     *
     * @param bigDate
     * @return
     */
    private long calculateTimeDifferenceReturnSecond(String bigDate, String smallDate) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date d1 = df.parse(bigDate);
            Date d2 = df.parse(smallDate);
            long diff = d1.getTime() - d2.getTime();//这样得到的差值是微秒级别
            return diff;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * 得到时间差计算天数 小时 分
     *
     * @param diff
     * @return
     */
    private String calculateSecondReturnDifference(long diff) {
        long days = diff / (1000 * 60 * 60 * 24);
        long hours = (diff - days * (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60)) / (1000 * 60);
        long second = (diff - days * (1000 * 60 * 60 * 24) - hours * (1000 * 60 * 60) - minutes * (1000 * 60)) / 1000;
        return "" + days + "天" + hours + "小时" + minutes + "分" + second + "秒";
    }

}

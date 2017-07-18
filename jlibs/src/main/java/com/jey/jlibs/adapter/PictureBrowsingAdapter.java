package com.jey.jlibs.adapter;

import android.app.Activity;
import android.content.DialogInterface;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.example.jlibs.R;
import com.jey.jlibs.activity.PictureBrowsingActivity;
import com.jey.jlibs.dataModel.PictureThumbEntity;
import com.jey.jlibs.interface_.ActionCompeleteListener;
import com.jey.jlibs.interface_.OnProgressBarListener;
import com.jey.jlibs.utils.PhotoSelectUtil.PictureBrowsingHelper;
import com.jey.jlibs.zoom.PhotoViewAttacher;
import com.jey.jlibs.zoom.SmoothImageView;

import java.util.Hashtable;
import java.util.List;

import static com.jey.jlibs.zoom.SmoothImageView.STATE_TRANSFORM_IN;
import static com.jey.jlibs.zoom.SmoothImageView.STATE_TRANSFORM_OUT;

/**
 * 图集Activity的Adapter
 */
public class PictureBrowsingAdapter extends PagerAdapter implements ViewPager.OnPageChangeListener,
        ActionCompeleteListener, SmoothImageView.TransformListener, PictureBrowsingHelper.OnPermissionAllowedOrRefusedListener {

    private boolean allowSavePicture = false;

    private Activity mActivity;

    private List<PictureThumbEntity> mDataList;

    private Hashtable<Integer, SmoothImageView> imageViewMaps;

    /**
     * 当前position
     */
    private int mCurrentPosition = -1;


    public PictureBrowsingAdapter(Activity activity, List<PictureThumbEntity> list, int mCurrentPosition, boolean allowSavePicture) {
        this.mActivity = activity;
        mDataList = list;
        this.mCurrentPosition = mCurrentPosition;
        this.allowSavePicture = allowSavePicture;
        if (mActivity instanceof PictureBrowsingActivity) {
            ((PictureBrowsingActivity) mActivity).setActionCompeleteListener(this);
        }
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View parentView = LayoutInflater.from(mActivity).inflate(R.layout.item_picture_browsing_layout, null);
        final SmoothImageView mCurrentImageView = (SmoothImageView) parentView.findViewById(R.id.item_picture_browsing_iv);
        final ProgressBar bar = (ProgressBar) parentView.findViewById(R.id.item_picture_browsing_pro_bar);
        if (imageViewMaps == null) imageViewMaps = new Hashtable<>();
        setMatchLayoutParams(mCurrentImageView);
        mCurrentImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (mCurrentPosition != -1 && mCurrentPosition == position) {

            mCurrentImageView.setOriginalInfo(
                    mDataList.get(position).getViewWidth(),
                    mDataList.get(position).getViewHeight(),
                    mDataList.get(position).getLeft(),
                    mDataList.get(position).getTop());
            mCurrentImageView.transformIn();
        }

        PictureBrowsingHelper.disPlayImageWithUrlFitCenterAndNoAnimation(mActivity, mCurrentImageView,
                mDataList.get(position).getUrl(), new OnProgressBarListener() {
                    @Override
                    public void onProgressChange(int current, int max) {
                    }

                    @Override
                    public void onStart() {
                        bar.setVisibility(View.VISIBLE);
                    }

                    @Override
                    public void onFailed(Exception e) {
                        bar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onCompleted() {
                        bar.setVisibility(View.GONE);
                    }
                });
        /** 单机退出图集Activity **/
        mCurrentImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float x, float y) {
                onResult(true, null);
            }
        });
        /** 长按提示是否保存图片 **/
        if (allowSavePicture) {
            mCurrentImageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    PictureBrowsingHelper.showNormalAlertDialog(mActivity, "温馨提示", "是否保存该图片到相册？", "确定", "取消", true,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    if (which != DialogInterface.BUTTON_POSITIVE) return;
                                    if (((PictureBrowsingActivity) mActivity).requestPermission()) {
                                        ((PictureBrowsingActivity) mActivity).showLoadingDialog();
                                        PictureBrowsingHelper.savePictureToAlbum(mActivity, mDataList.get(position).getUrl(), "");
                                    }
                                }
                            });
                    return false;
                }
            });
        }
        container.addView(parentView);
        if (!imageViewMaps.contains(mCurrentImageView))
            imageViewMaps.put(position, mCurrentImageView);
        return parentView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return super.getPageTitle(position);
    }

    @Override
    public int getCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View) object);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        mCurrentPosition = position;
        changeTransform(position);
        if (mActivity instanceof PictureBrowsingActivity) {
            ((PictureBrowsingActivity) mActivity).setPageText(mCurrentPosition, getCount());
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPermissionAllowedOrRefused(boolean[] isAllowed, String[] messageInfo) {
        if (isAllowed[0]) {
            ((PictureBrowsingActivity) mActivity).showLoadingDialog();
            PictureBrowsingHelper.savePictureToAlbum(mActivity, mDataList.get(mCurrentPosition).getUrl(), "");
        }
    }

    @Override
    public void onResult(boolean isSuccessed, Object obj) {
        if (imageViewMaps == null || imageViewMaps.size() == 0) return;
        if (!imageViewMaps.containsKey(mCurrentPosition)) return;
        imageViewMaps.get(mCurrentPosition).setOnTransformListener(this);
        imageViewMaps.get(mCurrentPosition).transformOut();
    }

    @Override
    public void onTransformComplete(int mode) {
        if (mode == STATE_TRANSFORM_OUT)
            mActivity.finish();
    }

    private void changeTransform(int position) {
        if (imageViewMaps == null || imageViewMaps.size() == 0) return;
        if (!imageViewMaps.containsKey(mCurrentPosition)) return;
        imageViewMaps.get(position).setOriginalInfo(
                mDataList.get(position).getViewWidth(),
                mDataList.get(position).getViewHeight(),
                mDataList.get(position).getLeft(),
                mDataList.get(position).getTop());
        imageViewMaps.get(position).changeTransformState(STATE_TRANSFORM_IN);
    }

    private void setMatchLayoutParams(View view) {
        if (view == null) return;
        FrameLayout.LayoutParams paramsM = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(paramsM);
    }
}

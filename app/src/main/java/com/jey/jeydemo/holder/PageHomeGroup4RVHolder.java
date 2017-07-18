package com.jey.jeydemo.holder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jey.jlibs.dataModel.BaseDataModel;
import com.jey.jlibs.utils.StringUtils;
import com.jey.jlibs.view.XRecyclerView.HolderInterface;
import com.jey.jeydemo.R;

import java.util.Hashtable;

/**
 * 首页第4个GroupView中的RecyclerView的Holder
 */
public class PageHomeGroup4RVHolder implements HolderInterface {

    private Context context;
//    private CommonFunctionUtils utils;
    private int showConsignmentStatus;

    public PageHomeGroup4RVHolder(Context context, int showConsignmentStatus) {
        this.context = context;
//        utils = new CommonFunctionUtils(context);
        this.showConsignmentStatus = showConsignmentStatus;
    }

    @Override
    public String setPhotoKey() {
        return null;
    }

    @Override
    public Hashtable<String, Integer> setViewFieldsMap() {
        Hashtable<String, Integer> fileds = new Hashtable<>();
        fileds.put("Title", R.id.item_home_page_group4_title_tv);
        fileds.put("YearAndMileage", R.id.item_home_page_group4_year_and_mileage_tv);
        fileds.put("Price", R.id.item_home_page_group4_price_tv);
        fileds.put("IV", R.id.item_home_page_group4_iv);
        fileds.put("Consignment", R.id.item_home_page_group4_status_consign_tv);
        fileds.put("Wholesale", R.id.item_home_page_group4_status_wholesale_tv);
        fileds.put("Report", R.id.item_home_page_group4_status_report_tv);
        fileds.put("Saled", R.id.item_home_page_group4_status_saled_tv);
        fileds.put("Auction", R.id.item_home_page_group4_status_auction_tv);
        fileds.put("Auction1", R.id.item_home_page_group4_status_auction_tv1);
        fileds.put("AuctionParent", R.id.item_home_page_group4_status_auction_parent_ll);
        fileds.put("merchId", R.id.item_home_page_group4_merch);
        return fileds;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindView(RecyclerView.Adapter adapter, Hashtable<String, View> fields, Object o, int position) {
        if (o == null) return;
        BaseDataModel data = (BaseDataModel) o;
        if (data.getNameValues().size() == 0) return;
        //MerchId
        ImageView ivMerch = (ImageView) fields.get("merchId");
        if (ivMerch != null) {
            if (StringUtils.isEmpty(data.getValue("MerchId") + "")) {
                ivMerch.setVisibility(View.GONE);
            } else {
                ivMerch.setVisibility(View.VISIBLE);
            }
        }

//        WaterMarkImageView iv = (WaterMarkImageView) fields.get("IV");
//        CommonFunctionUtils.holderShowPicture(utils, iv, data);
        ImageView tvSaled = (ImageView) fields.get("Saled");
        if (data.getValue("IsHaveSaled").toString().equals("true")) {
//            iv.setShowFuzzyLayoutEnable(true);
            tvSaled.setVisibility(View.VISIBLE);
        } else {
//            iv.setShowFuzzyLayoutEnable(false);
            tvSaled.setVisibility(View.GONE);
        }
        showViewAndVisible(fields, data);

        showCarViewData(data, fields);
    }

    /**
     * 显示车辆基本信息
     *
     * @param data
     * @param fields
     */
    @SuppressLint("SetTextI18n")
    private void showCarViewData(BaseDataModel data, Hashtable<String, View> fields) {
        TextView tvTitle = (TextView) fields.get("Title");
        if (tvTitle != null) {
            if (!StringUtils.isEmpty(data.getValue("ManuFacturer").toString()) ||
                    !StringUtils.isEmpty(data.getValue("CarType").toString()) ||
                    !StringUtils.isEmpty(data.getValue("Name").toString())) {
                tvTitle.setText(data.getValue("ManuFacturer") + " " + data.getValue("CarType") + " " + data.getValue("Name"));
            } else {
                tvTitle.setText("未查到该车辆信息");
            }
        }

        TextView tvYearAndMileage = (TextView) fields.get("YearAndMileage");
//        CommonFunctionUtils.holderShowMileageAndYear(tvYearAndMileage, data, "CarMileage", "FirstInstallCarNoTime");

        showReportStatus(data, fields);
    }

    /**
     * 显示是否已认证标签
     *
     * @param data
     * @param fields
     */
    private void showReportStatus(BaseDataModel data, Hashtable<String, View> fields) {
        ImageView tvReport = (ImageView) fields.get("Report");
        if (tvReport != null) {
            String reportId = data.getValue("ReportId") + "";
            String reportStatus = data.getValue("ReportStatus") + "";
            if (!StringUtils.isEmpty(reportId)) {
                if (reportStatus.equals("-2")) { //等待评估和评估完成
                    tvReport.setVisibility(View.VISIBLE);
                } else {
                    tvReport.setVisibility(View.GONE);
                }
            } else {
                tvReport.setVisibility(View.GONE);
            }
        }
    }

    /**
     * 显示拍卖、寄售、批发三种状态得显示
     *
     * @param fields
     * @param data
     */
    @SuppressLint("SetTextI18n")
    private void showViewAndVisible(Hashtable<String, View> fields, BaseDataModel data) {
        ImageView tvConsignment = (ImageView) fields.get("Consignment");
        ImageView tvWholesale = (ImageView) fields.get("Wholesale");
        TextView tvAuction = (TextView) fields.get("Auction");
        ImageView tvAuction1 = (ImageView) fields.get("Auction1");
        LinearLayout auctionParentLL = (LinearLayout) fields.get("AuctionParent");
        TextView tvPrice = (TextView) fields.get("Price");
        if (tvConsignment != null && !StringUtils.isEmpty(data.getValue("ConsignmentId") + "")) {
            tvConsignment.setVisibility(View.VISIBLE);
        } else {
            if (tvConsignment != null) {
                tvConsignment.setVisibility(View.GONE);
            }
        }

        if (tvWholesale != null && !StringUtils.isEmpty(data.getValue("WholeSaleId") + "")) {
            tvWholesale.setVisibility(View.VISIBLE);
        } else {
            if (tvWholesale != null) {
                tvWholesale.setVisibility(View.GONE);
            }
        }

//        if (tvAuction != null && auctionParentLL != null && dividerView != null) {
//            auctionParentLL.setVisibility(View.VISIBLE);
//            tvAuction.setVisibility(View.VISIBLE);
//            dividerView.setVisibility(View.VISIBLE);
//        }

        if (tvAuction != null && auctionParentLL != null) {
            if (!StringUtils.isEmpty(data.getValue("AuctionWithSessionId") + "")) {
                auctionParentLL.setVisibility(View.VISIBLE);
                tvAuction.setVisibility(View.VISIBLE);
                tvAuction1.setVisibility(View.VISIBLE);

                int isAuctioning = 0;
                if (!StringUtils.isEmpty(data.getValue("Auctioning") + "")) {
                    isAuctioning = (int) data.getValue("Auctioning");
                }

                String startTime = data.getValue("AuctionSessionStartTime") + "";
                String endTime = data.getValue("AuctionSessionEndTime") + "";

//                if (!StringUtils.isEmpty(startTime) && !StringUtils.isEmpty(endTime)) {
//                    long compareWithSatrtTime = TimeUtil.calculateTimeDifferenceReturnSecond(startTime);
//                    long compareWithEndTime = TimeUtil.calculateTimeDifferenceReturnSecond(endTime);
//
//                    if ((compareWithSatrtTime < 0 && compareWithEndTime > 0) && isAuctioning == 1) {
//                        tvAuction.setText("正在拍卖  结束时间：" + TimeUtil.getMonthAndDayAndHourAndMinute(endTime));
//                    } else if (compareWithSatrtTime > 0) {
//                        tvAuction.setText("即将开始  开始时间：" + TimeUtil.getMonthAndDayAndHourAndMinute(startTime));
//                    } else if (compareWithEndTime < 0) {
//                        tvAuction.setText("拍卖已结束");
//                    }
//                } else {
//                    tvAuction.setText("等待管理员安排拍卖场次");
//                }
            }
            // 无拍卖
            else {
                auctionParentLL.setVisibility(View.GONE);
                tvAuction.setVisibility(View.GONE);
                tvAuction1.setVisibility(View.GONE);
            }

        }

        String price = data.getValue("DisplayPrice").toString();
        if (tvPrice != null && !StringUtils.isEmpty(price)) {
//            tvPrice.setText("￥" + CommonFunctionUtils.changeToYuanAndKeepTwoCountAfterPoint(price));
            tvPrice.setText("￥" + price);
        }

        if (showConsignmentStatus == 1) {
            String isConsignment = data.getValue("IsConsignment") + "";
            if (!StringUtils.isEmpty(isConsignment)) { // 有寄售记录
                if (isConsignment.equals("0")) { // 可以直接参加寄售
                } else if (isConsignment.equals("2")) { // 已通过审核，正在寄售中，只能取消寄售，在未上架列表中不可能出现此情况，只能出现在已上架列表中
                } else { // 正在审核中，可以进行资料的提交
                    tvPrice.setText("车辆寄售审核中");
                }
            }
        }
    }

}

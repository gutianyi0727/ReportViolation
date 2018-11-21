package com.example.elvis.reportviolation.Activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.elvis.reportviolation.R;
import com.example.elvis.reportviolation.bean.MyUser;
import com.example.elvis.reportviolation.bean.ReporterViolationCase;
import com.rmondjone.locktableview.LockTableView;
import com.rmondjone.xrecyclerview.ProgressStyle;
import com.rmondjone.xrecyclerview.XRecyclerView;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ReporterHistoryTable extends BaseActivity {
        private LinearLayout mContentView;
        private List<ReporterViolationCase> reports;
        private MyUser myUser;
        ArrayList<ArrayList<String>> mTableDatas;
        ArrayList<String> mfristData;



        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_reporter_history_table);

            mContentView = (LinearLayout) findViewById(R.id.contentView);
            mTableDatas = new ArrayList<ArrayList<String>>();
            mfristData = new ArrayList<String>();
            mfristData.add("Report ObjectId");
            mfristData.add("Plate Number");
            mfristData.add("Report Types");
            mfristData.add("Location");
            mfristData.add("Date");
            mfristData.add("Status");
            mTableDatas.add(mfristData);

            BmobQuery<ReporterViolationCase> query = new BmobQuery<ReporterViolationCase>();
            myUser = BmobUser.getCurrentUser(MyUser.class);
            String userID = myUser.getObjectId();
            query.addWhereEqualTo("reporter", userID);
            query.findObjects(new FindListener<ReporterViolationCase>() {
                @Override
                public void done(List<ReporterViolationCase> list, BmobException e) {
                    if (e == null) {
//                    Log.e("getReport",list.get(0).getObjectId());
                        for (int i = 0; i < list.size(); i++) {
                            ArrayList<String> rowDatas = new ArrayList<String>();
                            rowDatas.add(list.get(i).getObjectId());
                            rowDatas.add(list.get(i).getPlateNumber());
                            rowDatas.add(list.get(i).getReportTitleandType());
                            rowDatas.add(list.get(i).getDelatedLoc());
                            rowDatas.add(list.get(i).getCreatedAt());
                            if (list.get(i).getDealt()){
                                rowDatas.add("Completed");
                            }else{
                                rowDatas.add("Processing");
                            }
                            mTableDatas.add(rowDatas);
                            Log.e("getReportInFor", mTableDatas.get(i + 1).get(1));
                        }
                        Log.e("getReport", mTableDatas.get(2).get(1));
                        Log.e("getReport", mTableDatas.get(2).get(2));
                        Log.e("getReport", mTableDatas.get(2).get(3));
                        Log.e("getReport", mTableDatas.get(2).get(4));
                    }
                }
            });

            //The following code is from https://github.com/RmondJone/LockTableView
            final LockTableView mLockTableView = new LockTableView(this, mContentView, mTableDatas);
            Log.e("表格加载开始", "当前线程：" + Thread.currentThread());
            mLockTableView.setLockFristColumn(true) //是否锁定第一列
                    .setLockFristRow(true) //是否锁定第一行
                    .setMaxColumnWidth(100) //列最大宽度
                    .setMinColumnWidth(60) //列最小宽度
                    .setColumnWidth(1,60) //设置指定列文本宽度(从0开始计算,宽度单位dp)
                    .setMinRowHeight(20)//行最小高度
                    .setMaxRowHeight(60)//行最大高度
                    .setTextViewSize(16) //单元格字体大小
                    .setFristRowBackGroudColor(R.color.table_head)//表头背景色
                    .setTableHeadTextColor(R.color.beijin)//表头字体颜色
                    .setTableContentTextColor(R.color.border_color)//单元格字体颜色
                    .setNullableString("N/A") //空值替换值
                    .setTableViewListener(new LockTableView.OnTableViewListener() {
                        //设置横向滚动监听
                        @Override
                        public void onTableViewScrollChange(int x, int y) {
                            Log.e("滚动值","["+x+"]"+"["+y+"]");
                        }
                    })
                    .setTableViewRangeListener(new LockTableView.OnTableViewRangeListener() {
                        //设置横向滚动边界监听
                        @Override
                        public void onLeft(HorizontalScrollView view) {
                            Log.e("滚动边界","滚动到最左边");
                        }

                        @Override
                        public void onRight(HorizontalScrollView view) {
                            Log.e("滚动边界","滚动到最右边");
                        }
                    })
                    .setOnLoadingListener(new LockTableView.OnLoadingListener() {
                        //下拉刷新、上拉加载监听
                        @Override
                        public void onRefresh(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                            Log.e("表格主视图", String.valueOf(mXRecyclerView));
                            Log.e("表格所有数据", String.valueOf(mTableDatas));
                            //如需更新表格数据调用,部分刷新不会全部重绘
                            mLockTableView.setTableDatas(mTableDatas);
                            //停止刷新
                            mXRecyclerView.refreshComplete();
                        }

                        @Override
                        public void onLoadMore(final XRecyclerView mXRecyclerView, final ArrayList<ArrayList<String>> mTableDatas) {
                            Log.e("表格主视图", String.valueOf(mXRecyclerView));
                            Log.e("表格所有数据", String.valueOf(mTableDatas));
                            //如需更新表格数据调用,部分刷新不会全部重绘
                            mLockTableView.setTableDatas(mTableDatas);
                            //停止刷新
                            mXRecyclerView.loadMoreComplete();
                            //如果没有更多数据调用
                            mXRecyclerView.setNoMore(true);
                        }
                    })
                    .setOnItemClickListenter(new LockTableView.OnItemClickListenter() {
                        @Override
                        public void onItemClick(View item, int position) {
                            Log.e("点击事件",position+"");
                        }
                    })
                    .setOnItemLongClickListenter(new LockTableView.OnItemLongClickListenter() {
                        @Override
                        public void onItemLongClick(View item, int position) {
                            Log.e("长按事件",position+"");
                        }
                    })
                    .setOnItemSeletor(R.color.dashline_color)//设置Item被选中颜色
                    .show(); //显示表格,此方法必须调用
            mLockTableView.getTableScrollView().setPullRefreshEnabled(true);
            mLockTableView.getTableScrollView().setLoadingMoreEnabled(true);
            mLockTableView.getTableScrollView().setRefreshProgressStyle(ProgressStyle.SquareSpin);


        }

    public void historyTableBackUserInfor(View view) {
        finish();
    }
    }
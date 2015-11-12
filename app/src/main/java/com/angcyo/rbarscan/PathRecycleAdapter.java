package com.angcyo.rbarscan;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by angcyo on 15-11-06-006.
 */
public class PathRecycleAdapter extends RecyclerView.Adapter<PathRecycleAdapter.BaseViewHolder> {

    List<String> datas;
    OnItemClick itemClick;
    Context context;

    public PathRecycleAdapter(Context context) {
        datas = new ArrayList<>();
        this.context = context;
    }

    public PathRecycleAdapter(Context context, List<String> datas, OnItemClick click) {
        this.datas = datas;
        this.itemClick = click;
        this.context = context;
    }

    public void setItemClick(OnItemClick click) {
        this.itemClick = click;
    }

    public void setDatas(List<String> datas) {
        this.datas = datas;
        notifyDataSetChanged();
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TextView textView = new TextView(context);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        textView.setBackgroundResource(R.drawable.path_item_selector);
        textView.setTextSize(20f);
        textView.setPadding(40, 6, 2, 6);
        return new BaseViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, final int position) {
        ((TextView) holder.itemView).setText(datas.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (itemClick != null) {
                    itemClick.onItemClick(datas.get(position), position);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public interface OnItemClick {
        void onItemClick(String str, int position);
    }

    static class BaseViewHolder extends RecyclerView.ViewHolder {
        View itemView;

        public BaseViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
        }
    }
}
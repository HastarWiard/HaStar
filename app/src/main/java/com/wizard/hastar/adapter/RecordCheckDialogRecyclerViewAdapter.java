package com.wizard.hastar.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.wizard.hastar.R;
import com.wizard.hastar.ui.money_manager.model.Record;
import com.wizard.hastar.ui.money_manager.util.RecordManager;
import com.wizard.hastar.util.HaStarUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by 伟平 on 2015/11/1.
 */
public class RecordCheckDialogRecyclerViewAdapter extends RecyclerView.Adapter<RecordCheckDialogRecyclerViewAdapter.viewHolder> {

    private OnItemClickListener onItemClickListener;

    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<Record> coCoinRecords;

    public RecordCheckDialogRecyclerViewAdapter(Context context, List<Record> list) {
        coCoinRecords = new ArrayList<>();
        coCoinRecords = list;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
    }

    public RecordCheckDialogRecyclerViewAdapter(Context context, List<Record> list, OnItemClickListener onItemClickListener) {
        coCoinRecords = new ArrayList<>();
        coCoinRecords = list;
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        this.onItemClickListener = onItemClickListener;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new viewHolder(mLayoutInflater.inflate(R.layout.record_check_item, parent, false));
    }

    @Override
    public void onBindViewHolder(viewHolder holder, final int position) {
        holder.imageView.setImageResource(
                HaStarUtil.GetTagIcon(coCoinRecords.get(position).getTag()));
        holder.date.setText(coCoinRecords.get(position).getCalendarString());
        holder.date.setTypeface(HaStarUtil.typefaceLatoLight);
        holder.money.setTypeface(HaStarUtil.typefaceLatoLight);
        holder.money.setText(String.valueOf((int) coCoinRecords.get(position).getMoney()));
        holder.money.setTextColor(
                HaStarUtil.GetTagColorResource(RecordManager.TAGS.get(coCoinRecords.get(position).getTag()).getId()));
        holder.index.setText((position + 1) + "");
        holder.index.setTypeface(HaStarUtil.typefaceLatoLight);
        holder.remark.setText(coCoinRecords.get(position).getRemark());
        holder.remark.setTypeface(HaStarUtil.typefaceLatoLight);

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemClickListener.onItemClick(v, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        if (coCoinRecords == null) {
            return 0;
        }
        return coCoinRecords.size();
    }

    public class viewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.image_view)
        ImageView imageView;
        @BindView(R.id.date)
        TextView date;
        @BindView(R.id.remark)
        TextView remark;
        @BindView(R.id.money)
        TextView money;
        @BindView(R.id.index)
        TextView index;
        @BindView(R.id.material_ripple_layout)
        MaterialRippleLayout layout;

        viewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
//            onItemClickListener.onItemClick(v, getPosition());
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
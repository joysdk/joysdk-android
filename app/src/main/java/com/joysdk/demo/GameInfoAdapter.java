package com.joysdk.demo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.joysdk.android.model.JoyGameInfoModel;

import java.util.ArrayList;
import java.util.List;

public class GameInfoAdapter extends RecyclerView.Adapter<GameInfoAdapter.ViewHolder> {

    private List<JoyGameInfoModel> list;
    private Context context;
    private OnItemClickListener onItemClickListener;

    public GameInfoAdapter(ArrayList<JoyGameInfoModel> data, Context context) {
        this.list = data;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_game_info, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(GameInfoAdapter.ViewHolder holder, int position) {
        JoyGameInfoModel model = list.get(position);
        holder.mName.setText(model.getGameName());
        Glide.with(context).load(model.getIconUrl()).into(holder.mIcon);
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onClick(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView mIcon;
        TextView mName;

        public ViewHolder(View itemView) {
            super(itemView);
            mIcon = (ImageView) itemView.findViewById(R.id.iv_icon);
            mName = (TextView) itemView.findViewById(R.id.tv_name);
        }
    }

    public interface OnItemClickListener {
        void onClick(JoyGameInfoModel model);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }
}

package com.afwsamples.testdpc.policy.locktask;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.afwsamples.testdpc.R;

import java.util.List;

public class KioskAppsRecyclerAdapter extends RecyclerView.Adapter<KioskAppsRecyclerAdapter.ViewHolder> {
    private static final String TAG = "KioskAppsRecyclerAdapter";
    private final List<String> mPackages;
    private final Context mContext;
    private final PackageManager mPackageManager;
    private final OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(String packageName);
    }

    public KioskAppsRecyclerAdapter(Context context, List<String> packages, OnItemClickListener listener) {
        this.mContext = context;
        this.mPackages = packages;
        this.mPackageManager = context.getPackageManager();
        this.mListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.kiosk_mode_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String packageName = mPackages.get(position);
        ApplicationInfo applicationInfo;
        try {
            applicationInfo = mPackageManager.getApplicationInfo(packageName, 0);
            holder.iconImageView.setImageDrawable(applicationInfo.loadIcon(mPackageManager));
            
            if (mContext.getPackageName().equals(packageName)) {
                holder.pkgNameTextView.setText(mContext.getString(R.string.stop_kiosk_mode));
            } else {
                holder.pkgNameTextView.setText(applicationInfo.loadLabel(mPackageManager));
            }
            
            holder.itemView.setOnClickListener(v -> {
                if (mListener != null) {
                    mListener.onItemClick(packageName);
                }
            });
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to retrieve application info for the entry: " + position, e);
        }
    }

    @Override
    public int getItemCount() {
        return mPackages.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView iconImageView;
        TextView pkgNameTextView;

        ViewHolder(View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.pkg_icon);
            pkgNameTextView = itemView.findViewById(R.id.pkg_name);
        }
    }
} 
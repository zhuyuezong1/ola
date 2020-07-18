package com.kasa.ola.ui.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.kasa.ola.R;

import java.util.List;

public class ProvinceAdapter extends RecyclerView.Adapter {
    private Activity act;
    private List<AddressInterface> data;
    private ItemClickListener listener;

    public enum AddressType {
        PROVINCE, CITY, AREA
    }

    private int type = 0;

    public interface AddressInterface {
        String getName();

        String getCode();
    }

    public interface ItemClickListener{
        void jumpOrReturn(@Nullable AddressInterface vo);
    }

    public ProvinceAdapter(@Nullable Activity act, @Nullable List<AddressInterface> data, int type) {
        this.act = act;
        this.data = data;
        this.type = type;
    }

    public void setListener(ItemClickListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeViewHolder(LayoutInflater.from(act).inflate(R.layout.cell_province, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final AddressInterface vo = data.get(position);
        HomeViewHolder viewHolder = (HomeViewHolder) holder;

        viewHolder.mContentTv.setText(vo.getName());
        viewHolder.mContentTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doClick(vo);
            }
        });
    }

    private void doClick(AddressInterface vo) {
        listener.jumpOrReturn(vo);
//        if (type == AddressType.PROVINCE.ordinal()) {
//            jump(vo.getCode());
//        } else if (type == AddressType.CITY.ordinal()) {
//            jump(vo.getCode());
//        } else if (type == AddressType.AREA.ordinal()) {
//            Intent intent = new Intent();
//            intent.putExtra(Const.EX_AREA, );
//            act.setResult(Activity.RESULT_OK, intent);
//            act.finish();
//        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class HomeViewHolder extends RecyclerView.ViewHolder {

        public TextView mContentTv;


        public HomeViewHolder(View itemView) {
            super(itemView);
            mContentTv = itemView.findViewById(R.id.text_content);
        }
    }
}

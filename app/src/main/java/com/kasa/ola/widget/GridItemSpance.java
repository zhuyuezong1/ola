package com.kasa.ola.widget;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class GridItemSpance extends RecyclerView.ItemDecoration {

    private int left;
    private int right;
    private int column;
    public GridItemSpance(int left,int right,int column) {
        this.left = left;
        this.right = right;
        this.column = column;
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        if (column==2){
            if (pos% column == 0){
                outRect.left=right;
                outRect.right=left;
            }else {
                outRect.left=left;
                outRect.right=right;
            }
        }
    }
}

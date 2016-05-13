package org.jaram.ds.views;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import org.jaram.ds.util.SLog;

/**
 * Created by jdekim43 on 2016. 5. 11..
 */
public class SwipeTouchHelper extends ItemTouchHelper.SimpleCallback {

    public interface SwipeListener {
        void onSwiped(int position);
    }

    private SwipeListener listener;

    public SwipeTouchHelper(SwipeListener listener) {
        super(0, ItemTouchHelper.START | ItemTouchHelper.END);
        this.listener = listener;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        listener.onSwiped(viewHolder.getAdapterPosition());
    }
}

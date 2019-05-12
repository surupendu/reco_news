package com.irlab.news.newsrecommender;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class RecyclerViewListener implements RecyclerView.OnItemTouchListener {

    private OnItemClickListener onItemClickListener;

    public interface OnItemClickListener{
        public void OnItemClick(View view,int position);
    }

    private GestureDetector gestureDetector;

    public RecyclerViewListener(Context context,OnItemClickListener onItemClickListener){
        this.onItemClickListener = onItemClickListener;
        gestureDetector = new GestureDetector(context,new GestureDetector.SimpleOnGestureListener(){
            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return true;
            }
        });
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {
        View childView = recyclerView.findChildViewUnder(motionEvent.getX(),motionEvent.getY());
        if(childView!=null && onItemClickListener!=null && gestureDetector.onTouchEvent(motionEvent)){
            onItemClickListener.OnItemClick(childView, recyclerView.getChildAdapterPosition(childView));
        }
        return false;
    }

    @Override
    public void onTouchEvent(@NonNull RecyclerView recyclerView, @NonNull MotionEvent motionEvent) {

    }

    @Override
    public void onRequestDisallowInterceptTouchEvent(boolean b) {

    }
}

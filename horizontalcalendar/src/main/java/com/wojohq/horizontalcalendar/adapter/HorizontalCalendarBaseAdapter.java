package com.wojohq.horizontalcalendar.adapter;

import android.graphics.drawable.Drawable;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wojohq.horizontalcalendar.HorizontalCalendar;
import com.wojohq.horizontalcalendar.HorizontalCalendarView;
import com.wojohq.horizontalcalendar.HorizontalLayoutManager;
import com.wojohq.horizontalcalendar.R;
import com.wojohq.horizontalcalendar.model.CalendarEvent;
import com.wojohq.horizontalcalendar.model.CalendarItemStyle;
import com.wojohq.horizontalcalendar.utils.CalendarEventsPredicate;
import com.wojohq.horizontalcalendar.utils.HorizontalCalendarListener;
import com.wojohq.horizontalcalendar.utils.HorizontalCalendarPredicate;
import com.wojohq.horizontalcalendar.utils.Utils;

import java.util.Calendar;
import java.util.Collections;
import java.util.List;

/**
 * Base class for all adapters for {@link HorizontalCalendarView HorizontalCalendarView}
 *
 * @author Mulham-Raee
 * @since v1.3.0
 */
public abstract class HorizontalCalendarBaseAdapter<VH extends DateViewHolder, T extends Calendar> extends RecyclerView.Adapter<VH> {

    private final int itemResId;
    final HorizontalCalendar horizontalCalendar;
    private final HorizontalCalendarPredicate disablePredicate;
    private CalendarEventsPredicate eventsPredicate;
    private final int cellWidth;
    private CalendarItemStyle disabledItemStyle;
    private boolean highlightSelected = true;

    public void setHighlightSelected(boolean highlightSelected){
        this.highlightSelected=highlightSelected;
        notifyDataSetChanged();
    }

    public void updateEvents(CalendarEventsPredicate calendarEventsPredicate) {
        this.eventsPredicate = calendarEventsPredicate;
        notifyDataSetChanged();
    }

    protected Calendar startDate;
    protected int itemsCount;

    protected HorizontalCalendarBaseAdapter(int itemResId, final HorizontalCalendar horizontalCalendar, Calendar startDate, Calendar endDate, HorizontalCalendarPredicate disablePredicate, CalendarEventsPredicate eventsPredicate) {
        this.itemResId = itemResId;
        this.horizontalCalendar = horizontalCalendar;
        this.disablePredicate = disablePredicate;
        this.startDate = startDate;
        if (disablePredicate != null) {
            this.disabledItemStyle = disablePredicate.style();
        }
        this.eventsPredicate = eventsPredicate;

        cellWidth = Utils.calculateCellWidth(horizontalCalendar.getContext(), horizontalCalendar.getNumberOfDatesOnScreen());
        itemsCount = calculateItemsCount(startDate, endDate);
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(itemResId, parent, false);

        final VH viewHolder = createViewHolder(itemView, cellWidth);
        viewHolder.itemView.setOnClickListener(new MyOnClickListener(viewHolder));
        viewHolder.itemView.setOnLongClickListener(new MyOnLongClickListener(viewHolder));
        viewHolder.itemView.setOnDragListener(new MyOnDragListener(horizontalCalendar.getCalendarView(),viewHolder));
        if (eventsPredicate != null) {
            initEventsRecyclerView(viewHolder.eventsRecyclerView);
        } else {
            viewHolder.eventsRecyclerView.setVisibility(View.INVISIBLE);
        }

        return viewHolder;
    }

    private void initEventsRecyclerView(RecyclerView recyclerView) {
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(new EventsAdapter(Collections.<CalendarEvent>emptyList()));
        GridLayoutManager layoutManager = new GridLayoutManager(recyclerView.getContext(), 1);
        recyclerView.setLayoutManager(layoutManager);
    }

    protected abstract VH createViewHolder(View itemView, int cellWidth);

    public abstract T getItem(int position);

    @Override
    public int getItemCount() {
        return itemsCount;
    }

    public boolean isDisabled(int position) {
        if (disablePredicate == null) {
            return false;
        }
        Calendar date = getItem(position);
        return disablePredicate.test(date);
    }

    protected void showEvents(VH viewHolder, Calendar date) {
        if (eventsPredicate == null) {
            return;
        }

        List<CalendarEvent> events = eventsPredicate.events(date);
        if ((events == null) || events.isEmpty()) {
            viewHolder.eventsRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.eventsRecyclerView.setVisibility(View.VISIBLE);
            EventsAdapter eventsAdapter = (EventsAdapter) viewHolder.eventsRecyclerView.getAdapter();
            if (eventsAdapter!=null)
            eventsAdapter.update(events);
        }
    }

    protected void applyStyle(VH viewHolder, Calendar date, int position) {
        int selectedItemPosition = horizontalCalendar.getSelectedDatePosition();

        if (disablePredicate != null) {
            boolean isDisabled = disablePredicate.test(date);
            viewHolder.itemView.setEnabled(!isDisabled);
            if (isDisabled && (disabledItemStyle != null)) {
                applyStyle(viewHolder, disabledItemStyle);
                viewHolder.selectionView.setVisibility(View.INVISIBLE);
                return;
            }
        }

        // Selected Day
        if (position == selectedItemPosition && highlightSelected) {
            applyStyle(viewHolder, horizontalCalendar.getSelectedItemStyle());
            viewHolder.selectionView.setVisibility(View.VISIBLE);
        }
        // Unselected Days
        else {
            applyStyle(viewHolder, horizontalCalendar.getDefaultStyle());
            viewHolder.selectionView.setVisibility(View.INVISIBLE);
        }
    }

    protected void applyStyle(VH viewHolder, CalendarItemStyle itemStyle) {
        viewHolder.textTop.setTextColor(itemStyle.getColorTopText());
        viewHolder.textMiddle.setTextColor(itemStyle.getColorMiddleText());
        viewHolder.textBottom.setTextColor(itemStyle.getColorBottomText());

        viewHolder.itemView.setBackground(itemStyle.getBackground());
    }

    public void update(Calendar startDate, Calendar endDate, boolean notify) {
        this.startDate = startDate;
        itemsCount = calculateItemsCount(startDate, endDate);
        if (notify) {
            notifyDataSetChanged();
        }
    }

    protected abstract int calculateItemsCount(Calendar startDate, Calendar endDate);

    private class MyOnClickListener implements View.OnClickListener {
        private final RecyclerView.ViewHolder viewHolder;

        MyOnClickListener(RecyclerView.ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public void onClick(View v) {
            int position = viewHolder.getAdapterPosition();
            if (position == -1)
                return;

            horizontalCalendar.getCalendarView().setSmoothScrollSpeed(HorizontalLayoutManager.SPEED_SLOW);
            horizontalCalendar.centerCalendarToPosition(position);
            horizontalCalendar.getCalendarListener().onDateSelected(getItem(position),position);
        }
    }

    private class MyOnLongClickListener implements View.OnLongClickListener {
        private final RecyclerView.ViewHolder viewHolder;

        MyOnLongClickListener(RecyclerView.ViewHolder viewHolder) {
            this.viewHolder = viewHolder;
        }

        @Override
        public boolean onLongClick(View v) {
            HorizontalCalendarListener calendarListener = horizontalCalendar.getCalendarListener();
            if (calendarListener == null) {
                return false;
            }

            int position = viewHolder.getAdapterPosition();
            Calendar date = getItem(position);

            return calendarListener.onDateLongClicked(date, position);
        }
    }

    private class MyOnDragListener implements View.OnDragListener{

        private final RecyclerView recyclerView;
        private final RecyclerView.ViewHolder viewHolder;
        private int mScrollDistance;

        Drawable enterShape ;
        Drawable normalShape ;

        MyOnDragListener(RecyclerView recyclerView,RecyclerView.ViewHolder viewHolder){
            this.recyclerView=recyclerView;
            mScrollDistance=horizontalCalendar.getCalendarView().getScrollX();
            this.viewHolder=viewHolder;
            enterShape = ContextCompat.getDrawable(viewHolder.itemView.getContext(), R.drawable.shape_droptarget);
            normalShape = ContextCompat.getDrawable(viewHolder.itemView.getContext(),R.drawable.shape);
        }

        @Override
        public boolean onDrag(View v, DragEvent event) {
            HorizontalCalendarListener calendarListener = horizontalCalendar.getCalendarListener();
            if (calendarListener == null) {
                return false;
            }
            int position = viewHolder.getAdapterPosition();
            Calendar date = getItem(position);
            int action = event.getAction();
            switch (action){
                case DragEvent.ACTION_DRAG_STARTED:
                    // do nothing
                    break;
                case DragEvent.ACTION_DRAG_LOCATION:
                    /*int x = Math.round(event.getX());
                    int translatedX = x - mScrollDistance;
                    int threshold = 50;
                    // make a scrolling up due the y has passed the threshold
                    if (translatedX < threshold) {
                        // make a scroll up by 30 px
                        recyclerView.scrollBy(-30, 0);
                    }
                    // make a autoscrolling down due y has passed the 500 px border
                    if (translatedX + threshold > 500) {
                        // make a scroll down by 30 px
                        recyclerView.scrollBy(30, 0);
                    }*/
                    break;
                case DragEvent.ACTION_DRAG_ENTERED:
                    v.setBackground(enterShape);
                    break;
                case DragEvent.ACTION_DRAG_EXITED:
                    v.setBackgroundColor(0);
                    break;
                case DragEvent.ACTION_DROP:
                    calendarListener.onViewDropped(event,date,position);
                case DragEvent.ACTION_DRAG_ENDED:
                    v.setBackgroundColor(0);
                   // notifyDataSetChanged();
                default:
                    break;

            }
            return true;
        }
    }

}

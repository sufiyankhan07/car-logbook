package com.wojohq.horizontalcalendar.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.wojohq.horizontalcalendar.R;
import com.wojohq.horizontalcalendar.model.CalendarEvent;

import java.util.List;

/**
 * @author Mulham-Raee
 * @since v1.3.2
 */
public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.EventViewHolder> {

    private List<CalendarEvent> eventList;

    public EventsAdapter(List<CalendarEvent> eventList) {
        this.eventList = eventList;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        ImageView imageView = new ImageView(context);

        Drawable circle = ContextCompat.getDrawable(context, R.drawable.ic_circle_white_8dp);
        Drawable drawableWrapper = DrawableCompat.wrap(circle);

        imageView.setImageDrawable(drawableWrapper);

        return new EventViewHolder(imageView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        CalendarEvent event = getItem(position);

        ImageView imageView = (ImageView) holder.itemView;

        imageView.setContentDescription(event.getDescription());
        if (event.getDescription().equals("hidden")) {
            imageView.setVisibility(View.INVISIBLE);
            //DrawableCompat.setTint(imageView.getDrawable(), Color.TRANSPARENT);
        } else {
            imageView.setVisibility(View.VISIBLE);
            DrawableCompat.setTint(imageView.getDrawable(), event.getColor());
        }
    }

    public CalendarEvent getItem(int position) throws IndexOutOfBoundsException {
        return eventList.get(position);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public void update(List<CalendarEvent> eventList) {
        this.eventList = eventList;
        notifyDataSetChanged();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {

        EventViewHolder(View itemView) {
            super(itemView);
        }
    }
}


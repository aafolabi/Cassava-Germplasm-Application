package ng.com.cs.nextgengi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class CustomAdapter extends BaseAdapter implements Filterable {

    Context mContext;
    List<String> mItems;
    private List<String> filteredItems;
    private MainActivity activity;
    private FriendFilter friendFilter;
    public CustomAdapter(MainActivity activity, List<String> items) {
       this.mContext = activity;
        this.mItems = items;
        this.filteredItems = items;
        // typeface = Typeface.createFromAsset(activity.getAssets(), "fonts/vegur_2.otf");

        getFilter();
    }


    @Override
    public int getCount() {
        return filteredItems.size();
    }

    @Override
    public Object getItem(int position) {
        return mItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.search_list_item, null);
        } else {
            view = convertView;
        }

        TextView titleView = (TextView) view.findViewById(R.id.heading);
        titleView.setText(mItems.get(position));

        return view;
    }

    @Override
    public Filter getFilter() {
        if (friendFilter == null) {
            friendFilter = new FriendFilter();
        }

        return friendFilter;
    }

    /**
     * Keep reference to children view to avoid unnecessary calls
     */
    static class ViewHolder {
        TextView iconText;
        TextView name;
    }

    /**
     * Custom filter for friend list
     * Filter content in friend list according to the search text
     */
    private class FriendFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if (constraint!=null && constraint.length()>0) {
                ArrayList<String> tempList = new ArrayList<>();

                // search content in friend list
                for (String item : mItems) {
                    if (item.toLowerCase().contains(constraint.toString().toLowerCase())) {
                        tempList.add(item);
                    }
                }

                filterResults.count = tempList.size();
                filterResults.values = tempList;
            } else {
                filterResults.count = mItems.size();
                filterResults.values = mItems;
            }

            return filterResults;
        }

        /**
         * Notify about filtered list to ui
         * @param constraint text
         * @param results filtered result
         */
        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredItems = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }
    }

}


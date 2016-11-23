package com.explorify.companyname.explorify;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;

import java.util.List;

/**
 * Created by sagar and sayali on 11-01-2016.
 */
public class NewsListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<JobDItem> feedItems;
    ImageLoader imageLoader = MyApplication.getInstance().getImageLoader();

    public NewsListAdapter(Activity activity, List<JobDItem> feedItems) {
        this.activity = activity;
        this.feedItems = feedItems;
    }

    @Override
    public int getCount() {
        return feedItems.size();
    }

    @Override
    public Object getItem(int location) {
        return feedItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.news_items, null);

        if (imageLoader == null)
            imageLoader = MyApplication.getInstance().getImageLoader();

        TextView title = (TextView) convertView.findViewById(R.id.txtTitleMsg);
        TextView posteddate = (TextView) convertView.findViewById(R.id.txtDate);
        TextView expireDate = (TextView) convertView.findViewById(R.id.txtExpireDate);
        //title.setTypeface(null, Typeface.BOLD);
        TextView description = (TextView) convertView
                .findViewById(R.id.txtDescriptionMsg);
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.feedImage1);

        TextView companyName = (TextView) convertView.findViewById(R.id.txtCompanyName);

        JobDItem item = feedItems.get(position);

        title.setText(item.getJobTitle());

        posteddate.setText(item.getPosteddate());
        expireDate.setText(item.getExpireDate());

        description.setText(item.getJobDetails());
        companyName.setText(item.getCompanyName());


        // Feed image
        if (item.getImage() != null) {
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView.setImageUrl(item.getImage(), imageLoader);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess() {
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        return convertView;
    }
}

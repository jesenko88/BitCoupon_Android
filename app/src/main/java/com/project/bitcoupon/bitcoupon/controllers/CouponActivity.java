package com.project.bitcoupon.bitcoupon.controllers;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.bitcoupon.bitcoupon.R;
import com.project.bitcoupon.bitcoupon.models.Coupon;
import com.project.bitcoupon.bitcoupon.service.ServiceRequest;
import com.project.bitcoupon.bitcoupon.singletons.CouponFeed;
import com.project.bitcoupon.bitcoupon.singletons.UserData;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class CouponActivity extends BaseActivity {

    public static final String MyPREFERENCES = "MyPrefs" ;
    private static final String TAG = "CouponActivity_Tag";
    private SharedPreferences mSharedPreferences;
    private ListView mCouponList;
    private EditText mFilter;
    private CouponAdapter mAdapter;
    private int couponId;
    static ArrayList<Coupon> coupons = new ArrayList<Coupon>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon);

        mCouponList= (ListView)findViewById(R.id.list_view_coupons);
        CouponFeed couponFeed = CouponFeed.getInstance();
        //if mobile oriantation is changed - don't take a new list
        if(couponFeed.getFeed().size() == 0){
            couponFeed.getFeed(getString(R.string.service_posts));
            coupons = couponFeed.getFeed();
        }

        // This is custom Adapter for List
        mAdapter = new CouponAdapter(this, coupons);
        mCouponList.setAdapter(mAdapter);
        mCouponList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * For each item in list, if is clicked send a post Request
                 */
                Coupon clicked = coupons.get(position);
                couponId = clicked.getId();
                String url = getString(R.string.service_single_coupon);
                JSONObject clickedCoupon = new JSONObject();
                try {
                    clickedCoupon.put("id", Integer.toString(couponId));
                    Log.d(TAG, "JSON ID " + id);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String json = clickedCoupon.toString();
                Log.d(TAG, json);
                ServiceRequest.post(url, json, getCoupon());
            }
        });
        mCouponList.setAdapter(mAdapter);

        /**
         * This is search filter for our liste
         */
        mFilter = (EditText)findViewById(R.id.edit_text_filter);
        mFilter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ((CouponAdapter)mCouponList.getAdapter()).getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    @Override
    public void onBackPressed(){
       moveTaskToBack(false);
    }

    /**
     * This is response for our JSON request
     * onFailure - if JSON don't successe
     * onResponse - if JSON success we expect this attributes
     * @return
     */
    private Callback getCoupon() {
        return new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                makeToast(R.string.toast_try_again);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseJson = response.body().string();

                try {
                    JSONObject coupon = new JSONObject(responseJson);
                    Intent goToCoupon = new Intent(CouponActivity.this, SingleCouponActivity.class);
                    goToCoupon.putExtra("couponId", couponId);
                    goToCoupon.putExtra("name", coupon.getString("name"));
                    goToCoupon.putExtra("price", coupon.getString("price"));
                    goToCoupon.putExtra("expiration", coupon.getString("expiration"));
                    goToCoupon.putExtra("picture", coupon.getString("picture"));
                    goToCoupon.putExtra("categoryName", coupon.getString("categoryName"));
                    goToCoupon.putExtra("description", coupon.getString("description"));
                    goToCoupon.putExtra("remark", coupon.getString("remark"));
                    goToCoupon.putExtra("seller", coupon.getString("seller"));
                    goToCoupon.putExtra("minOrder", coupon.getString("minOrder"));
                    goToCoupon.putExtra("maxOrder", coupon.getString("maxOrder"));
                    startActivity(goToCoupon);
                } catch (JSONException e) {
                    makeToast(R.string.toast_try_again);
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * This is method for making message on user mobile display - Toast
     * @param messageId
     */
    private void makeToast(final int messageId){
        new Handler(Looper.getMainLooper())
                .post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CouponActivity.this,
                                messageId,
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    /**
     * This is response for our JSON request
     * onFailure - if JSON don't successe
     * onResponse - if JSON success we expect this attributes
     * @return
     */
    private Callback getProfile() {
        return new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                makeToast(R.string.toast_try_again);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseJson = response.body().string();

                try {

                    JSONObject profile = new JSONObject(responseJson);
                    Log.d(TAG, "profil");
                    Intent goToProfile = new Intent(CouponActivity.this, UserProfileActivity.class);
                    goToProfile.putExtra("id", profile.getString("id"));
                    goToProfile.putExtra("name", profile.getString("name"));
                    goToProfile.putExtra("surname", profile.getString("surname"));
                    goToProfile.putExtra("email",profile.getString("email"));
                    goToProfile.putExtra("address", profile.getString("address"));
                    goToProfile.putExtra("city",profile.getString("city"));
                    String pic = profile.getString("picture");
                    Log.d(TAG, pic);
                    goToProfile.putExtra("picture", profile.getString("picture"));

                    startActivity(goToProfile);

                } catch (JSONException e) {
                    makeToast(R.string.toast_try_again);
                    e.printStackTrace();
                }
            }
        };
    }

    /**
     * This is private class for creating custom adapter
     */
    private  class CouponAdapter extends ArrayAdapter<Coupon> {

        private final Context context;
        private ArrayList<Coupon> origin;
        private ArrayList<Coupon> mListToShow;
        private Filter mFilter;

        public CouponAdapter(Context context, ArrayList<Coupon> origin) {
            super(context, R.layout.row, origin);
            this.context = context;
            this.mListToShow = origin;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new CouponsFilter();
            }
            return mFilter;
        }

        @Override
        public int getCount() {
            return mListToShow.size();
        }

        @Override
        public Coupon getItem(int position) {
            return mListToShow.get(position);
        }

        /**
         * Search filter for list of coupons when User input some value
         */
        private class CouponsFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    ArrayList<Coupon> origin = CouponFeed.getInstance().getFeed();
                    results.values = origin;
                    results.count = origin.size();
                } else {

                    String searchString = constraint.toString().toLowerCase();

                    ArrayList<Coupon> filteredList = new ArrayList<Coupon>();
                    for (int i = 0; i < mListToShow.size(); i++) {
                        Coupon c = mListToShow.get(i);
                        String postTitle = c.getName().toLowerCase();

                        if (postTitle.contains(searchString)) {
                            filteredList.add(c);
                        }
                    }
                    results.values = filteredList;
                    results.count = filteredList.size();
                }

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListToShow = (ArrayList<Coupon>) results.values;
                notifyDataSetChanged();
            }
        }

        /**
         * For all coupons in list we define how will be shown to user in one list
         * @param position - id of coupons in list
         * @param convertView - layout view for one coupons in list
         * @param viewGroup
         * @return - convertView
         */
        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            Coupon current = getItem(position);
            if (convertView == null) {
                convertView = CouponActivity.this.getLayoutInflater()
                        .inflate(R.layout.row, null);
            }


            TextView couponName = (TextView) convertView.findViewById(R.id.textview_name);
            couponName.setText(current.getName());

            TextView couponPrice = (TextView) convertView.findViewById(R.id.textview_price);

            couponPrice.setText("" + current.getPrice() + getString(R.string.currency));
            ImageView couponImage = (ImageView) convertView.findViewById(R.id.imageview_image);
            String img = current.getPicture();
            img = img.replaceAll("\\\\","/");
            Log.d("TAG", "IMG" + img);
            Picasso.with(getContext()).load(img).into(couponImage);
            return convertView;

        }
    }
}

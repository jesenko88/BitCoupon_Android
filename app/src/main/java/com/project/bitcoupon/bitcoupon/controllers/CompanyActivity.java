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
import com.project.bitcoupon.bitcoupon.models.Company;
import com.project.bitcoupon.bitcoupon.models.Coupon;
import com.project.bitcoupon.bitcoupon.service.ServiceRequest;
import com.project.bitcoupon.bitcoupon.singletons.CompanyFeed;
import com.project.bitcoupon.bitcoupon.singletons.CouponFeed;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

public class CompanyActivity extends BaseActivity {

    private ListView mCompanyList;
    private static final String TAG = "CompanyActivity_Tag";
    private EditText mFilter;
    private CompanyAdapter mAdapter;
    public static final String MyPREFERENCES = "MyPrefs" ;
    static ArrayList<Company> companies = new ArrayList<Company>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_company);

        mCompanyList= (ListView)findViewById(R.id.list_view_companies);
        CompanyFeed companyFeed = CompanyFeed.getInstance();

        //if mobile orientation is changed don't take a new list
        if(companyFeed.getFeed().size() == 0){
            companyFeed.getFeed(getString(R.string.service_companies));
            companies = companyFeed.getFeed();
        }

        // This is custom Adapter for List
        mAdapter = new CompanyAdapter(CompanyActivity.this, companies);

        mCompanyList.setAdapter(mAdapter);
        mCompanyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                /**
                 * For each item in list, if is clicked send a post Request
                 */
                Company clicked = companies.get(position);
                String url = getString(R.string.service_single_company);
                JSONObject clickedCompany = new JSONObject();
                try {
                    clickedCompany.put("email", clicked.getmEmail());
                    Log.d(TAG, "json email for post");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String json = clickedCompany.toString();
                Log.d(TAG, "Json: " + json);
                ServiceRequest.post(url, json, getCompany());
            }
        });

        mCompanyList.setAdapter(mAdapter);

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
                ( (ArrayAdapter<Company>)mCompanyList.getAdapter()).getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    /**
     * This is response for our JSON request
     * onFailure - if JSON don't successe
     * onResponse - if JSON success we expect this attributes
     * @return
     */
    private Callback getCompany() {
        return new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                makeToast(R.string.toast_try_again);
            }

            @Override
            public void onResponse(Response response) throws IOException {
                String responseJson = response.body().string();

                try {
                    JSONObject company = new JSONObject(responseJson);
                    Intent goToCompany = new Intent(CompanyActivity.this, SingleCompanyActivity.class);
                    goToCompany.putExtra("id", company.getInt("id") );
                    goToCompany.putExtra("name", company.getString("name"));
                    goToCompany.putExtra("email", company.getString("email"));
                    goToCompany.putExtra("address", company.getString("address"));
                    goToCompany.putExtra("city", company.getString("city"));
                    goToCompany.putExtra("contact", company.getString("contact"));
                    goToCompany.putExtra("logo", company.getString("logo"));
                    startActivity(goToCompany);
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
                        Toast.makeText(CompanyActivity.this,
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
                    Log.d(TAG, "profile");
                    Intent goToProfile = new Intent(CompanyActivity.this, UserProfileActivity.class);
                    goToProfile.putExtra("id", profile.getString("id"));
                    goToProfile.putExtra("name", profile.getString("name"));
                    goToProfile.putExtra("surname", profile.getString("surname"));
                    goToProfile.putExtra("email",profile.getString("email"));
                    goToProfile.putExtra("address", profile.getString("address"));
                    goToProfile.putExtra("city",profile.getString("city"));
                    String pic = profile.getString("picture");
                    Log.d("TAG", pic);
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
    private  class CompanyAdapter extends ArrayAdapter<Company> {

        private final Context context;
        private ArrayList<Company> origin;
        private ArrayList<Company> mListToShow;
        private Filter mFilter;

        public CompanyAdapter(Context context, ArrayList<Company> origin) {
            super(context, R.layout.row, origin);
            this.context = context;
            this.mListToShow = origin;
        }

        @Override
        public Filter getFilter() {
            if (mFilter == null) {
                mFilter = new CompaniesFilter();
            }
            return mFilter;
        }

        @Override
        public int getCount() {
            return mListToShow.size();
        }

        @Override
        public Company getItem(int position) {
            return mListToShow.get(position);
        }

        /**
         * Search filter for list of companies when User input some value
         */
        private class CompaniesFilter extends Filter {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    ArrayList<Company> origin = CompanyFeed.getInstance().getFeed();
                    results.values = origin;
                    results.count = origin.size();
                } else {

                    String searchString = constraint.toString().toLowerCase();

                    ArrayList<Company> filteredList = new ArrayList<Company>();
                    for (int i = 0; i < mListToShow.size(); i++) {
                        Company c = mListToShow.get(i);
                        String postTitle = c.getmName().toLowerCase();

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
                mListToShow = (ArrayList<Company>) results.values;
                notifyDataSetChanged();
            }
        }

        /**
         * For all companies in list we define how will be shown to user in one list
         * @param position - id of companies in list
         * @param convertView - layout view for one companies in list
         * @param viewGroup
         * @return - convertView
         */

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {

            Company current = getItem(position);
            if (convertView == null) {
                convertView = CompanyActivity.this.getLayoutInflater()
                        .inflate(R.layout.row, null);
            }

            TextView companyName = (TextView) convertView.findViewById(R.id.textview_name);
            companyName.setText(current.getmName());

            TextView companyEmail = (TextView) convertView.findViewById(R.id.textview_price);
            companyEmail.setText(current.getmEmail());


            ImageView companyImage = (ImageView) convertView.findViewById(R.id.imageview_image);
            String img = current.getmLogo();
            img = img.replaceAll("\\\\","/");
            Log.d(TAG, "Image:" + img);
            Picasso.with(getContext()).load(img).into(companyImage);
            return convertView;
        }
    }
}

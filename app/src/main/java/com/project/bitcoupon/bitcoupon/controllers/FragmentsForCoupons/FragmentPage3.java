package com.project.bitcoupon.bitcoupon.controllers.FragmentsForCoupons;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.project.bitcoupon.bitcoupon.R;
import com.project.bitcoupon.bitcoupon.controllers.PayPalActivity;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentPage3 extends Fragment {

    public static final String FRAG_THREE = "com.project.bitcoupon.bitcoupon.frag.three";
    private  int couponId;
    public FragmentPage3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_fragment_page3, container, false);
        Bundle arguments = getArguments();
        int position = arguments.getInt(FRAG_THREE);


        //For picture
        String imgPath = arguments.getString("picture");
        String name = arguments.getString("name");
        String price = arguments.getString("price");
        couponId= arguments.getInt("couponId");
        String remark = arguments.getString("remark");



        TextView mName = (TextView) v.findViewById(R.id.textview_singleCouponName);
        mName.setText(name);

        TextView mRemark = (TextView) v.findViewById(R.id.textview_singleCouponRemark);
        mRemark.setText(remark);

        ImageView mPicture = (ImageView) v.findViewById(R.id.imageview_singleCouponImage);
        String img = getString(R.string.image_path) + imgPath;
        img = img.replaceAll("\\\\","/");
        Picasso.with(getActivity()).load(img).into(mPicture);


        TextView mPrice = (TextView) v.findViewById(R.id.textview_singleCouponPrice);
        mPrice.setText("Price: " + price + " " +getString(R.string.currency));



        Button buyButton = (Button)v.findViewById(R.id.buy_button);
        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PayPalActivity.class);

                i.putExtra("couponId", couponId);
                startActivity(i);
            }
        });

        return v;
    }


}

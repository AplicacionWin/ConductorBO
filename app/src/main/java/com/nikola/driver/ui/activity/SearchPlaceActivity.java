package com.nikola.driver.ui.activity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.maps.model.LatLng;
import com.nikola.driver.R;
import com.nikola.driver.ui.adapter.PlacesAutoCompleteAdapter;
import com.nikola.driver.ui.fragment.BaseMapFragment;
import com.nikola.driver.utils.AndyUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by user on 1/7/2017.
 */

public class SearchPlaceActivity extends AppCompatActivity {
    @BindView(R.id.search_back)
    ImageButton searchBack;
    @BindView(R.id.toolbar_search_place)
    Toolbar toolbarSearchPlace;
    @BindView(R.id.et_source_address)
    AutoCompleteTextView etSourceAddress;
    @BindView(R.id.et_destination_address)
    AutoCompleteTextView etDestinationAddress;
    private PlacesAutoCompleteAdapter placesadapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        setContentView(R.layout.source_destination_layout);
        ButterKnife.bind(this);
        setUpPlacesAdapter(savedInstanceState);
        onItemClickListeners();
    }

    private void setUpPlacesAdapter(Bundle savedInstanceState) {
        placesadapter = new PlacesAutoCompleteAdapter(this, R.layout.autocomplete_list_text);
        if (placesadapter != null) {
            etSourceAddress.setAdapter(placesadapter);
            etDestinationAddress.setAdapter(placesadapter);
        }

        String source_address = "";
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                source_address = "";
            } else {
                source_address = extras.getString("pickup_address");
                etSourceAddress.setText(source_address);
                etSourceAddress.setSelection(0);
            }
        } else {
            source_address = (String) savedInstanceState.getSerializable("pickup_address");
            etSourceAddress.setText(source_address);
            etSourceAddress.setSelection(0);
        }
        etDestinationAddress.requestFocus();
        LatLng latLng = getLocationFromAddress(this, etSourceAddress.getText().toString());
        if (latLng != null) {
            BaseMapFragment.pic_latlan = latLng;
        }
    }

    private void onItemClickListeners()
    {
        etSourceAddress.setOnItemClickListener((adapterView, view, i, l) -> {
            etSourceAddress.setSelection(0);
            LatLng latLng = getLocationFromAddress(getApplicationContext(), etSourceAddress.getText().toString());
            if (latLng != null) {
                BaseMapFragment.pic_latlan = latLng;

            }
        });

        etDestinationAddress.setOnItemClickListener((adapterView, view, i, l) -> {
            etDestinationAddress.setSelection(0);

            LatLng des_latLng = getLocationFromAddress(getApplicationContext(), etDestinationAddress.getText().toString());

            if (des_latLng != null) {
                BaseMapFragment.drop_latlan = des_latLng;
                BaseMapFragment.searching = true;
                AndyUtils.hideKeyBoard(getApplicationContext());
            }
            onBackPressed();
        });

        etDestinationAddress.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                onBackPressed();
                BaseMapFragment.searching = true;
                AndyUtils.hideKeyBoard(getApplicationContext());
                return true;
            }
            return false;
        });

    }

    public LatLng getLocationFromAddress(Context context, String strAddress) {
        Geocoder coder = new Geocoder(context);
        List<Address> address;
        LatLng p1 = null;
        try {
            address = coder.getFromLocationName(strAddress, 5);
            if (address == null) {
                return null;
            }
            Address location = address.get(0);
            location.getLatitude();
            location.getLongitude();
            p1 = new LatLng(location.getLatitude(), location.getLongitude());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return p1;
    }

    @OnClick(R.id.search_back)
    public void onViewClicked() {
        onBackPressed();
    }
}

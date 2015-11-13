package com.dekaisheng.courier.lbs.poi;

import java.util.concurrent.TimeUnit;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import android.app.Activity;

public class GooglePoiService {

	private GoogleApiClient googleApiClient;

	public GooglePoiService(Activity context, 
			ConnectionCallbacks connBack, OnConnectionFailedListener connFailed){
		googleApiClient = new GoogleApiClient
				.Builder(context)
				.addApi(Places.GEO_DATA_API)
				.addApi(Places.PLACE_DETECTION_API)
				.addConnectionCallbacks(connBack)
				.addOnConnectionFailedListener(connFailed)
				.build();
	}

	public void start(){
		googleApiClient.connect();
	}

	public void stop(){
		googleApiClient.disconnect();
	}

	public void search(String key, LatLngBounds bounds, 
			AutocompleteFilter typeFilter, final IPoiListener poiListener){
		PendingResult<AutocompletePredictionBuffer> result = Places.GeoDataApi
				.getAutocompletePredictions(googleApiClient, key, bounds, typeFilter);

		result.setResultCallback(new ResultCallback<AutocompletePredictionBuffer>(){

			@Override
			public void onResult(AutocompletePredictionBuffer arg0) {
				poiListener.onPoiSearchCallback(arg0);
			}

		}, 60, TimeUnit.SECONDS);
	}
}

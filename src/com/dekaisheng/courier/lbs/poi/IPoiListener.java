package com.dekaisheng.courier.lbs.poi;

import com.google.android.gms.location.places.AutocompletePredictionBuffer;

public interface IPoiListener {

	public void onPoiSearchCallback(AutocompletePredictionBuffer buffer);
}

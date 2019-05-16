package com.example.kirill.bskpayment;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class RemittanceRequest extends StringRequest {

    private static final String URL = "https://kirill-kotovich1.000webhostapp.com/Remittance.php";
    private Map<String, String> params;

    public RemittanceRequest(String bankCardNum, String bankCardCardVerificationValue, String metropolitenCardNum, double sum, Response.Listener<String> listener) {
        super(Request.Method.POST, URL, listener, null);
        params = new HashMap<>();
        params.put("B_CARDNUM", bankCardNum);
        params.put("CVV_CVC", bankCardCardVerificationValue);
        params.put("MT_CARDNUM", metropolitenCardNum);
        params.put("SUM", sum + "");
    }

    @Override
    public Map<String, String> getParams() { return params; }
}

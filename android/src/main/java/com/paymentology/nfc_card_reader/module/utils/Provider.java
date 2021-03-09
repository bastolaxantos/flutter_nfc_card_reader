package com.paymentology.nfc_card_reader.module.utils;

import android.nfc.tech.IsoDep;
import android.util.Log;

import com.paymentology.nfc_card_reader.module.enums.SwEnum;
import com.paymentology.nfc_card_reader.module.exception.CommunicationException;
import com.paymentology.nfc_card_reader.module.parser.IProvider;

import java.io.IOException;

public class Provider implements IProvider {


    private static final String TAG = Provider.class.getName();

    private final LogListener logListener;

    private StringBuffer log = new StringBuffer();

    private IsoDep mTagCom;

    public Provider(LogListener logListener) {
        this.logListener = logListener;
    }

    public void setmTagCom(final IsoDep mTagCom) {
        this.mTagCom = mTagCom;
    }


    public StringBuffer getLog() {
        return log;
    }

    @Override
    public byte[] transceive(byte[] pCommand) throws CommunicationException {
        log.append("=================\n");
        log.append("Request: " + BytesUtils.bytesToString(pCommand));

        Log.d(TAG, "Request: " + BytesUtils.bytesToString(pCommand));


        byte[] response = null;
        try {
            // send command to emv card
            response = mTagCom.transceive(pCommand);
        } catch (IOException e) {
            throw new CommunicationException(e.getMessage());
        }

        log.append("Response: " + BytesUtils.bytesToString(response));
        Log.d(TAG, "Response: " + BytesUtils.bytesToString(response));
        try {
            Log.d(TAG, "Response: " + TlvUtil.prettyPrintAPDUResponse(response));
            SwEnum val = SwEnum.getSW(response);
            if (val != null) {
                Log.d(TAG, "Response: " + val.getDetail());
            }
            log.append(TlvUtil.prettyPrintAPDUResponse(response));
        } catch (Exception e) {
            e.printStackTrace();
        }

        logListener.onLog(log.toString());

        return response;
    }
}

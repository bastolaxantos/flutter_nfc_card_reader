package com.paymentology.nfc_card_reader.module;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.os.AsyncTask;
import android.util.Log;

import com.paymentology.nfc_card_reader.module.enums.EmvCardScheme;
import com.paymentology.nfc_card_reader.module.model.EmvCard;
import com.paymentology.nfc_card_reader.module.parser.EmvParser;
import com.paymentology.nfc_card_reader.module.utils.LogListener;
import com.paymentology.nfc_card_reader.module.utils.Provider;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;

/**
 * Created by pro100svitlo on 21.03.16.
 */
public class CardNfcAsyncTask extends AsyncTask<Void, Void, Object> {

    public static class Builder {
        private Tag mTag;
        private CardNfcInterface mInterface;
        private boolean mFromStart;
        private LogListener mLogListener;


        public Builder(CardNfcInterface i, Tag tag, boolean fromCreate, LogListener logListener) {
            mInterface = i;
            mTag = tag;
            mFromStart = fromCreate;
            mLogListener = logListener;
        }

        public CardNfcAsyncTask build() {
            return new CardNfcAsyncTask(this);
        }
    }

    public interface CardNfcInterface {
        void startNfcReadCard();

        void cardIsReadyToRead();

        void doNotMoveCardSoFast();

        void unknownEmvCard();

        void cardWithLockedNfc();

        void finishNfcReadCard();
    }

    public final static String CARD_UNKNOWN = EmvCardScheme.UNKNOWN.toString();
    public final static String CARD_VISA = EmvCardScheme.VISA.toString();
    public final static String CARD_NAB_VISA = EmvCardScheme.NAB_VISA.toString();
    public final static String CARD_MASTER_CARD = EmvCardScheme.MASTER_CARD.toString();
    public final static String CARD_AMERICAN_EXPRESS = EmvCardScheme.AMERICAN_EXPRESS.toString();
    public final static String CARD_CB = EmvCardScheme.CB.toString();
    public final static String CARD_LINK = EmvCardScheme.LINK.toString();
    public final static String CARD_JCB = EmvCardScheme.JCB.toString();
    public final static String CARD_DANKORT = EmvCardScheme.DANKORT.toString();
    public final static String CARD_COGEBAN = EmvCardScheme.COGEBAN.toString();
    public final static String CARD_DISCOVER = EmvCardScheme.DISCOVER.toString();
    public final static String CARD_BANRISUL = EmvCardScheme.BANRISUL.toString();
    public final static String CARD_SPAN = EmvCardScheme.SPAN.toString();
    public final static String CARD_INTERAC = EmvCardScheme.INTERAC.toString();
    public final static String CARD_ZIP = EmvCardScheme.ZIP.toString();
    public final static String CARD_UNIONPAY = EmvCardScheme.UNIONPAY.toString();
    public final static String CARD_EAPS = EmvCardScheme.EAPS.toString();
    public final static String CARD_VERVE = EmvCardScheme.VERVE.toString();
    public final static String CARD_TENN = EmvCardScheme.TENN.toString();
    public final static String CARD_RUPAY = EmvCardScheme.RUPAY.toString();
    public final static String CARD_ПРО100 = EmvCardScheme.ПРО100.toString();
    public final static String CARD_ZKA = EmvCardScheme.ZKA.toString();
    public final static String CARD_BANKAXEPT = EmvCardScheme.BANKAXEPT.toString();
    public final static String CARD_BRADESCO = EmvCardScheme.BRADESCO.toString();
    public final static String CARD_MIDLAND = EmvCardScheme.MIDLAND.toString();
    public final static String CARD_PBS = EmvCardScheme.PBS.toString();
    public final static String CARD_ETRANZACT = EmvCardScheme.ETRANZACT.toString();
    public final static String CARD_GOOGLE = EmvCardScheme.GOOGLE.toString();
    public final static String CARD_INTER_SWITCH = EmvCardScheme.INTER_SWITCH.toString();

    private final static String NFC_A_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcA]";
    private final static String NFC_B_TAG = "TAG: Tech [android.nfc.tech.IsoDep, android.nfc.tech.NfcB]";
    private final String UNKNOWN_CARD_MESS =
            "===========================================================================\n\n" +
                    "We couldn't recognize this card" +
//                    "Hi! This library is not familiar with your credit card. \n " +
//                    "Please, write me an email with information of your bank: \n" +
//                    "country, bank name, card type, etc) and i will try to do my best, \n" +
//                    "to add your bank as a known one into this lib. \n" +
//                    "Great thanks for using and reporting!!! \n" +
//                    "Here is my email: pro100svitlo@gmail.com. \n\n" +
                    "===========================================================================";

    private Provider mProvider;
    private boolean mException;
    private EmvCard mCard;
    private CardNfcInterface mInterface;
    private Tag mTag;
    private String mCardNumber;
    private String mExpireDate;
    private String mCardType;

    private CardNfcAsyncTask(Builder b) {
        mTag = b.mTag;
        mProvider = new Provider(b.mLogListener);
        if (mTag != null) {
            mInterface = b.mInterface;
            try {
                if (mTag.toString().equals(NFC_A_TAG) || mTag.toString().equals(NFC_B_TAG)) {
                    execute();
                } else {
                    if (!b.mFromStart) {
                        mInterface.unknownEmvCard();
                    }
                    clearAll();
                }
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
        }
    }

    public String getCardNumber() {
        return mCardNumber;
    }

    public String getCardExpireDate() {
        return mExpireDate;
    }

    public String getCardType() {
        return mCardType;
    }

    public EmvCard getCard() {
        return mCard;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        mInterface.startNfcReadCard();
        mProvider.getLog().setLength(0);
    }

    @Override
    protected Object doInBackground(final Void... params) {

        Object result = null;

        try {
            doInBackground();
        } catch (Exception e) {
            result = e;
            Log.e(CardNfcAsyncTask.class.getName(), e.getMessage(), e);
        }

        return result;
    }

    @Override
    protected void onPostExecute(final Object result) {

        if (!mException) {
            if (mCard != null) {
                if (StringUtils.isNotBlank(mCard.getCardNumber())) {
                    mCardNumber = mCard.getCardNumber();
                    mExpireDate = mCard.getExpireDate();
                    mCardType = mCard.getType().toString();
                    if (mCardType.equals(EmvCardScheme.UNKNOWN.toString())) {
                        Log.d("creditCardNfcReader", UNKNOWN_CARD_MESS);
                    }
                    mInterface.cardIsReadyToRead();
                } else if (mCard.isNfcLocked()) {
                    mInterface.cardWithLockedNfc();
                }
            } else {
                mInterface.unknownEmvCard();
            }
        } else {
            mInterface.doNotMoveCardSoFast();
        }
        mInterface.finishNfcReadCard();
        clearAll();
    }

    private void doInBackground() {
        IsoDep mIsoDep = IsoDep.get(mTag);
        if (mIsoDep == null) {
            mInterface.doNotMoveCardSoFast();
            return;
        }
        mException = false;

        try {
            // Open connection
            mIsoDep.connect();

            mProvider.setmTagCom(mIsoDep);

            EmvParser parser = new EmvParser(mProvider, true);
            mCard = parser.readEmvCard();
        } catch (IOException e) {
            mException = true;
        } finally {
            IOUtils.closeQuietly(mIsoDep);
        }
    }

    private void clearAll() {
        mInterface = null;
        mProvider = null;
        mCard = null;
        mTag = null;
        mCardNumber = null;
        mExpireDate = null;
        mCardType = null;
    }
}
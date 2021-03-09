package com.paymentology.nfc_card_reader;


import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Build;

import com.paymentology.nfc_card_reader.module.CardNfcAsyncTask;
import com.paymentology.nfc_card_reader.module.model.EmvCard;
import com.paymentology.nfc_card_reader.module.utils.CardNfcUtils;
import com.paymentology.nfc_card_reader.module.utils.LogListener;

import io.flutter.plugin.common.EventChannel;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class CardReader implements LogListener, NfcAdapter.ReaderCallback {
    private CardNfcAsyncTask mCardNfcAsyncTask;
    private NfcAdapter mNfcAdapter;

    private CardNfcUtils mCardNfcUtils;

    public CardReader(Activity activity, Context context, EventChannel eventChannel) {
        mNfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (mNfcAdapter == null) {
            return;
        }
        eventChannel.setStreamHandler(streamHandler);
        mCardNfcUtils = new CardNfcUtils(activity);
    }

    private CardNfcAsyncTask.CardNfcInterface cardNfcInterface;
    EventChannel.StreamHandler streamHandler = new EventChannel.StreamHandler() {
        @Override
        public void onListen(Object arguments, final EventChannel.EventSink events) {
            cardNfcInterface = new CardNfcAsyncTask.CardNfcInterface() {
                @Override
                public void startNfcReadCard() {
                    events.success("Start NFC read card");
                    System.out.println("Start NFC read card");
                }

                @Override
                public void cardIsReadyToRead() {
                    events.success("Card is ready to read");
                    System.out.println("Card is ready to read");
                }

                @Override
                public void doNotMoveCardSoFast() {
                    events.success("");
                    System.out.println("Do not move card so fast");
                }

                @Override
                public void unknownEmvCard() {
                    events.success("Unknown emv card");
                    System.out.println("Unknown emv card");
                }

                @Override
                public void cardWithLockedNfc() {
                    events.success("NFC is locked on this card");
                    System.out.println("NFC is locked on this card");
                }

                @Override
                public void finishNfcReadCard() {
                    events.success("Card read finished");
                    System.out.println("Card read finished");
                }
            };
        }

        @Override
        public void onCancel(Object arguments) {

        }
    };

    @Override
    public void onLog(String log) {
        System.out.println("Log: " + log);
    }

    @Override
    public void onTagDiscovered(Tag tag) {
        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
            EmvCard readCard = new EmvCard();
            try {
                mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(cardNfcInterface, tag, false, this)
                        .build();
                System.out.println("Card: " + readCard.getAid());
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
}

package com.paymentology.nfc_card_reader.module.iso7816emv;

import com.paymentology.nfc_card_reader.module.enums.TagTypeEnum;
import com.paymentology.nfc_card_reader.module.enums.TagValueTypeEnum;


public interface ITag {

	enum Class {
		UNIVERSAL, APPLICATION, CONTEXT_SPECIFIC, PRIVATE
	}

	boolean isConstructed();

	byte[] getTagBytes();

	String getName();

	String getDescription();

	TagTypeEnum getType();

	TagValueTypeEnum getTagValueType();

	Class getTagClass();

	int getNumTagBytes();

}

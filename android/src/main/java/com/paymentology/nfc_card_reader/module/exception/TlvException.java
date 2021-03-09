package com.paymentology.nfc_card_reader.module.exception;

/**
 * Exception during TLV reading
 * 
 */
public class TlvException extends RuntimeException {

	/**
	 * Constructor using field
	 * 
	 * @param pCause
	 *            cause
	 */
	public TlvException(final String pCause) {
		super(pCause);
	}

}

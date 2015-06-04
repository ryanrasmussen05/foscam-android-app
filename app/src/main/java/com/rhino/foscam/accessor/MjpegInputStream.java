package com.rhino.foscam.accessor;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class MjpegInputStream extends DataInputStream {
	
	private static final String CONTENT_LENGTH = "Content-Length";
	private static final byte[] SOI_MARKER = { (byte)0xFF, (byte)0xD8 };
	private static final byte[] EOF_MARKER = { (byte)0xFF, (byte)0xD9 };
	private static final int HEADER_MAX_LENGTH = 100;
	private static final int FRAME_MAX_LENGTH = 400000 + HEADER_MAX_LENGTH;
	
	public MjpegInputStream(InputStream inputStream) {
		super(new BufferedInputStream(inputStream, FRAME_MAX_LENGTH));
	}
	
	public Bitmap readMjpegFrame() throws IOException {
		int contentLength = -1;
		
		mark(FRAME_MAX_LENGTH);
		int headerLength = getStartOfSequence(this, SOI_MARKER);
		reset();
		byte[] header = new byte[headerLength];
		readFully(header);
		
		try {
			contentLength = parseContentLength(header);
		} catch (NumberFormatException nfe) {
			contentLength = getEndOfSequence(this, EOF_MARKER);
		}
		reset();
		
		byte[] imageData = new byte[contentLength];
		skipBytes(headerLength);
		readFully(imageData);
		return BitmapFactory.decodeByteArray(imageData, 0, imageData.length);
	}
	
	//returns the index where given sequence begins
	private int getStartOfSequence(DataInputStream in, byte[] sequence) throws IOException {
		int end = getEndOfSequence(in, sequence);
		if (end < 0) {
			return end;
		} else {
			return end - sequence.length;
		}
	}
	
	//returns the index in the frame after the appearance of given sequence
	private int getEndOfSequence(DataInputStream in, byte[] sequence) throws IOException {
		int sequenceIndex = 0;
		byte current;
		
		for(int i = 0; i < FRAME_MAX_LENGTH; i++) {
			current = (byte)in.readUnsignedByte();
			if(current == sequence[sequenceIndex]) {
				sequenceIndex++;
				if(sequenceIndex == sequence.length) {
					return i + 1;
				}
			} else {
				sequenceIndex = 0;
			}
		}
		return -1;
	}
	
	private int parseContentLength(byte[] headerBytes) throws IOException {
		ByteArrayInputStream headerIn = new ByteArrayInputStream(headerBytes);
		Properties props = new Properties();
		props.load(headerIn);
		return Integer.parseInt(props.getProperty(CONTENT_LENGTH));
	}

}

package com.itcalf.renhe.utils;

/*
 * Copyright (C) 2007 The Android Open Source Project
 * Copyright (c) 2009, Code Aurora Forum. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.HashMap;
import java.util.Iterator;

/**
 * MediaScanner helper class. Media允许的格式文件 {@hide}
 */
// 是否允许Wma
public class MediaFile {
	// comma separated list of all file extensions supported by the media
	// scanner
	public final static String sFileExtensions;

	// Audio file types
	public static final int FILE_TYPE_MP3 = 1;
	public static final int FILE_TYPE_M4A = 2;
	public static final int FILE_TYPE_WAV = 3;
	public static final int FILE_TYPE_AMR = 4;
	public static final int FILE_TYPE_AWB = 5;
	public static final int FILE_TYPE_WMA = 6;
	public static final int FILE_TYPE_OGG = 7;
	public static final int FILE_TYPE_QCP = 8;
	public static final int FILE_TYPE_AAC = 9;
	public static final int FILE_TYPE_3GPA = 10;
	private static final int FIRST_AUDIO_FILE_TYPE = FILE_TYPE_MP3;
	private static final int LAST_AUDIO_FILE_TYPE = FILE_TYPE_3GPA;

	// MIDI file types
	public static final int FILE_TYPE_MID = 11;
	public static final int FILE_TYPE_SMF = 12;
	public static final int FILE_TYPE_IMY = 13;
	private static final int FIRST_MIDI_FILE_TYPE = FILE_TYPE_MID;
	private static final int LAST_MIDI_FILE_TYPE = FILE_TYPE_IMY;

	// Video file types
	public static final int FILE_TYPE_MP4 = 21;
	public static final int FILE_TYPE_M4V = 22;
	public static final int FILE_TYPE_3GPP = 23;
	public static final int FILE_TYPE_3GPP2 = 24;
	public static final int FILE_TYPE_WMV = 25;
	public static final int FILE_TYPE_ASF = 26;
	private static final int FIRST_VIDEO_FILE_TYPE = FILE_TYPE_MP4;
	private static final int LAST_VIDEO_FILE_TYPE = FILE_TYPE_ASF;

	// Image file types
	public static final int FILE_TYPE_JPEG = 31;
	public static final int FILE_TYPE_GIF = 32;
	public static final int FILE_TYPE_PNG = 33;
	public static final int FILE_TYPE_BMP = 34;
	public static final int FILE_TYPE_WBMP = 35;
	public static final int FILE_TYPE_RAW = 36;
	private static final int FIRST_IMAGE_FILE_TYPE = FILE_TYPE_JPEG;
	private static final int LAST_IMAGE_FILE_TYPE = FILE_TYPE_RAW;

	// Playlist file types
	public static final int FILE_TYPE_M3U = 41;
	public static final int FILE_TYPE_PLS = 42;
	public static final int FILE_TYPE_WPL = 43;
	private static final int FIRST_PLAYLIST_FILE_TYPE = FILE_TYPE_M3U;
	private static final int LAST_PLAYLIST_FILE_TYPE = FILE_TYPE_WPL;

	static class MediaFileType {

		int fileType;
		String mimeType;

		MediaFileType(int fileType, String mimeType) {
			this.fileType = fileType;
			this.mimeType = mimeType;
		}
	}

	private static HashMap<String, MediaFileType> sFileTypeMap = new HashMap<String, MediaFileType>();
	private static HashMap<String, Integer> sMimeTypeMap = new HashMap<String, Integer>();

	// 添加默认的允许的
	static void addFileType(String extension, int fileType, String mimeType) {
		sFileTypeMap.put(extension, new MediaFileType(fileType, mimeType));
		sMimeTypeMap.put(mimeType, Integer.valueOf(fileType));
	}

	// 添加默认允许的格式
	static {
		addFileType("MP3", FILE_TYPE_MP3, "audio/mpeg");
		addFileType("M4A", FILE_TYPE_M4A, "audio/mp4");
		addFileType("WAV", FILE_TYPE_WAV, "audio/x-wav");
		addFileType("AMR", FILE_TYPE_AMR, "audio/amr");
		addFileType("AWB", FILE_TYPE_AWB, "audio/amr-wb");
		addFileType("QCP", FILE_TYPE_QCP, "audio/qcp");
		addFileType("OGG", FILE_TYPE_OGG, "application/ogg");
		addFileType("OGA", FILE_TYPE_OGG, "application/ogg");
		addFileType("AAC", FILE_TYPE_AAC, "audio/aac");
		addFileType("3GPP", FILE_TYPE_3GPA, "audio/3gpp");

		addFileType("MID", FILE_TYPE_MID, "audio/midi");
		addFileType("MIDI", FILE_TYPE_MID, "audio/midi");
		addFileType("XMF", FILE_TYPE_MID, "audio/midi");
		addFileType("MXMF", FILE_TYPE_MID, "audio/mobile-xmf");
		addFileType("RTTTL", FILE_TYPE_MID, "audio/midi");
		addFileType("SMF", FILE_TYPE_SMF, "audio/sp-midi");
		addFileType("IMY", FILE_TYPE_IMY, "audio/imelody");
		addFileType("RTX", FILE_TYPE_MID, "audio/midi");
		addFileType("OTA", FILE_TYPE_MID, "audio/midi");

		addFileType("MPEG", FILE_TYPE_MP4, "video/mpeg");
		addFileType("MP4", FILE_TYPE_MP4, "video/mp4");
		addFileType("M4V", FILE_TYPE_M4V, "video/mp4");
		addFileType("3GP", FILE_TYPE_3GPP, "video/3gpp");
		addFileType("3GPP", FILE_TYPE_3GPP, "video/3gpp");
		addFileType("3G2", FILE_TYPE_3GPP2, "video/3gpp2");
		addFileType("3GPP2", FILE_TYPE_3GPP2, "video/3gpp2");

		addFileType("JPG", FILE_TYPE_JPEG, "image/jpeg");
		addFileType("JPEG", FILE_TYPE_JPEG, "image/jpeg");
		addFileType("GIF", FILE_TYPE_GIF, "image/gif");
		addFileType("PNG", FILE_TYPE_PNG, "image/png");
		addFileType("BMP", FILE_TYPE_BMP, "image/x-ms-bmp");
		addFileType("WBMP", FILE_TYPE_WBMP, "image/vnd.wap.wbmp");
		addFileType("RAW", FILE_TYPE_RAW, "image/raw");

		addFileType("M3U", FILE_TYPE_M3U, "audio/x-mpegurl");
		addFileType("PLS", FILE_TYPE_PLS, "audio/x-scpls");
		addFileType("WPL", FILE_TYPE_WPL, "application/vnd.ms-wpl");

		// compute file extensions list for native Media Scanner
		StringBuilder builder = new StringBuilder();
		Iterator<String> iterator = sFileTypeMap.keySet().iterator();

		while (iterator.hasNext()) {
			if (builder.length() > 0) {
				builder.append(',');
			}
			builder.append(iterator.next());
		}
		sFileExtensions = builder.toString();// 输出JPG,MP3,3GP.....
	}

	// 是否是允许的类型声音
	public static boolean isAudioFileType(int fileType) {
		return ((fileType >= FIRST_AUDIO_FILE_TYPE && fileType <= LAST_AUDIO_FILE_TYPE) || (fileType >= FIRST_MIDI_FILE_TYPE && fileType <= LAST_MIDI_FILE_TYPE));
	}

	// 是否是允许的类型视频
	public static boolean isVideoFileType(int fileType) {
		return (fileType >= FIRST_VIDEO_FILE_TYPE && fileType <= LAST_VIDEO_FILE_TYPE);
	}

	// 是否是允许的类型图像
	public static boolean isImageFileType(int fileType) {
		return (fileType >= FIRST_IMAGE_FILE_TYPE && fileType <= LAST_IMAGE_FILE_TYPE);
	}

	public static boolean isPlayListFileType(int fileType) {
		return (fileType >= FIRST_PLAYLIST_FILE_TYPE && fileType <= LAST_PLAYLIST_FILE_TYPE);
	}

	// 获得文件类型
	public static MediaFileType getFileType(String path) {
		int lastDot = path.lastIndexOf(".");
		if (lastDot < 0)
			return null;
		return sFileTypeMap.get(path.substring(lastDot + 1).toUpperCase());
	}

	// 是否是允许的类型是否是支持的mime类型
	public static int getFileTypeForMimeType(String mimeType) {
		Integer value = sMimeTypeMap.get(mimeType);
		return (value == null ? 0 : value.intValue());
	}

}
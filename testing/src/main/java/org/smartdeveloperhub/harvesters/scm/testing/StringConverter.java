/**
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   This file is part of the Smart Developer Hub Project:
 *     http://www.smartdeveloperhub.org/
 *
 *   Center for Open Middleware
 *     http://www.centeropenmiddleware.com/
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Copyright (C) 2015-2016 Center for Open Middleware.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 *   Artifact    : org.smartdeveloperhub.harvesters.scm:scm-harvester-testing:0.3.0-SNAPSHOT
 *   Bundle      : scm-harvester-testing-0.3.0-SNAPSHOT.jar
 * #-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=#
 */
package org.smartdeveloperhub.harvesters.scm.testing;

import java.nio.charset.Charset;

import com.google.common.base.Charsets;

final class StringConverter {

	public static void printBytes(final byte[] array, final String name) {
		for (int k = 0; k < array.length; k++) {
			System.out.println(name + "[" + k + "] = " + "0x" + UnicodeFormatter.byteToHex(array[k]));
		}
	}

	public static void main(final String[] args) {
		final String original = new String("A" + "\u00ea" + "\u00f1" + "\u00fc"+ "C");
		analyze(original, "original", Charset.defaultCharset());
		analyze(transcode(original, Charsets.ISO_8859_1), "other",Charsets.ISO_8859_1);
	}

	private static void analyze(final String original, final String name, final Charset charset) {
		System.out.printf("%s [%s]: %s%n",name,charset.displayName(),original);
		byteDump(original, charset);
		charDump(original);
		codePointDump(original);
	}

	private static String transcode(final String original, final Charset charset) {
		return new String(original.getBytes(charset),charset);
	}

	private static void byteDump(final String original, final Charset charset) {
		final byte[] bytes=original.getBytes(charset);
		final int length = bytes.length;
		for(int i=0;i<length;i++) {
			System.out.printf("byte[%d]=0x%1x%n",i,bytes[i]);
		}
	}

	private static void charDump(final String original) {
		final int length = original.length();
		for(int i=0;i<length;i++) {
			System.out.printf("charAt[%d]=0x%04x%n",i,(int)original.charAt(i));
		}
	}

	private static void codePointDump(final String original) {
		final int length = original.length();
		for(int i=0;i<length;i++) {
			System.out.printf("codePointAt[%d]=0x%08x%n",i,Character.codePointAt(original,i));
		}
	}

}
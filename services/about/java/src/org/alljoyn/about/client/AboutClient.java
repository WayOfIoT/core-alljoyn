/******************************************************************************
 *  *    Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
 *    Source Project (AJOSP) Contributors and others.
 *
 *    SPDX-License-Identifier: Apache-2.0
 *
 *    All rights reserved. This program and the accompanying materials are
 *    made available under the terms of the Apache License, Version 2.0
 *    which accompanies this distribution, and is available at
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
 *    Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for
 *    any purpose with or without fee is hereby granted, provided that the
 *    above copyright notice and this permission notice appear in all
 *    copies.
 *
 *     THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *     WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *     WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *     AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *     DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *     PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *     TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *     PERFORMANCE OF THIS SOFTWARE.
 ******************************************************************************/

package org.alljoyn.about.client;

import java.util.Map;

import org.alljoyn.about.AboutKeys;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.BusObject;
import org.alljoyn.services.common.BusObjectDescription;
import org.alljoyn.services.common.ClientBase;

/**
 * An interface for retrieval of remote IoE device's About data.
 * Encapsulates the AboutTransport BusInterface
 */
public interface AboutClient extends ClientBase
{
	/**
	 * Get the language that is used for Announcements.
	 * @return a String representing the language. IETF language tags specified by  RFC 5646.
	 * @throws BusException
	 */
	public String getDefaultLanguage() throws BusException;
	
	/**
	 * Get the languages that are supported by the device.
	 * @return a String array of languages. IETF language tags specified by  RFC 5646.
	 * @throws BusException
	 */
	public String[] getLanguages() throws BusException;
	
	/**
	 * Return all the configuration fields based on the language tag. 
	 * @param languageTag IETF language tags specified by  RFC 5646
	 * @return All the configuration fields based on the language tag.  If language tag is not specified (i.e. ""), fields based on device's default language are returned
	 * @throws BusException
	 * @see AboutKeys
	 */
	public Map <String, Object> getAbout(String languageTag) throws BusException;
	
	/**
	 * Returns the Bus Interfaces and Bus Objects supported by the device. 
	 * @return the array of object paths and the list of all interfaces available at the given object path.
	 * @throws BusException
	 * @see BusInterface
	 * @see BusObject
	 */
	public BusObjectDescription[] getBusObjectDescriptions() throws BusException;
}
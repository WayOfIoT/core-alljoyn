/******************************************************************************
 *    Copyright (c) Open Connectivity Foundation (OCF), AllJoyn Open Source
 *    Project (AJOSP) Contributors and others.
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
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
 *    WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
 *    WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
 *    AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
 *    DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
 *    PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
 *    TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
 *    PERFORMANCE OF THIS SOFTWARE.
******************************************************************************/
#ifndef _ABOUTSERVICE_H
#define _ABOUTSERVICE_H

#include <vector>
#include <map>
#include <alljoyn/BusAttachment.h>
#include <alljoyn/about/PropertyStore.h>

namespace ajn {
namespace services {

/**
 * AboutService is an AllJoyn BusObject that implements the org.alljoyn.About standard interface.
 * Applications that provide AllJoyn IoE services use an instance of this class to announce
 * their capabilities and other identifying details of the services being provided.
 */
class AboutService : public ajn::BusObject {
  public:
    /**
     * Construct an AboutService.
     * @param[in]  bus    BusAttachment instance associated with this AboutService
     * @param[in]  store  Set of key/value pairs used to populate required/optional/app-defined
     *               members of about and announce data
     */
    AboutService(ajn::BusAttachment& bus, PropertyStore& store);
    /**
     * destructor
     */
    virtual ~AboutService() {
    }

    /**
     * Register  the AboutService on the AllJoyn bus passing the port to be announced.
     * @param port used to bind the session.
     * @return status.
     */
    QStatus Register(int port);
    /**
     * Unregister the About service  from the bus
     */
    void Unregister();

    /**
     * AddObjectDescription adds objects Description to the AboutService announcement.
     * @param[in]  path of the interface.
     * @param[in]  interfaceNames
     * @return ER_OK if successful.
     */
    QStatus AddObjectDescription(qcc::String const& path, std::vector<qcc::String> const& interfaceNames);
    /**
     * RemoveObjectDescription adds objects Description to the AboutService announcement.
     * @param[in]  path of the interface.
     * @param[in]  interfaceNames
     * @return ER_OK if successful.
     */
    QStatus RemoveObjectDescription(qcc::String const& path, std::vector<qcc::String> const& interfaceNames);

    /**
     * Send or replace the org.alljoyn.About.Announce sessionless signal.
     *
     * Validate store and object announcements and emit the announce signal.
     *
     * @return
     * - ER_MANDATORY_FIELD_MISSING: Logs an error with specific field that has a problem.
     */
    QStatus Announce();

  private:
    /**
     * Handles  GetAboutData method
     * @param[in]  member
     * @param[in]  msg reference of AllJoyn Message
     */
    void GetAboutData(const ajn::InterfaceDescription::Member* member, ajn::Message& msg);

    /**
     *	Handles  GetObjectDescription method
     * @param[in]  member
     * @param[in]  msg reference of AllJoyn Message
     */
    void GetObjectDescription(const ajn::InterfaceDescription::Member* member, ajn::Message& msg);

    /**
     * Handles the GetPropery request
     * @param[in]  ifcName  interface name
     * @param[in]  propName the name of the properly
     * @param[in]  val reference of MsgArg out parameter.
     * @return ER_OK if successful.
     */
    QStatus Get(const char* ifcName, const char* propName, MsgArg& val);

    /**
     *  pointer to BusAttachment
     */
    ajn::BusAttachment* m_BusAttachment;
    /**
     *  pointer to PropertyStore
     */
    PropertyStore* m_PropertyStore;
    /**
     *  stores the signal member initialized  in the Register(..)
     */
    const ajn::InterfaceDescription::Member* m_AnnounceSignalMember;
    /**
     * port published  by the announcement
     */
    int m_AnnouncePort;
    /**
     *	map that holds interfaces that will be announced
     */
    std::map<qcc::String, std::vector<qcc::String> > m_AnnounceObjectsMap;

};

}
}

#endif /*_ABOUTSERVICE_H*/
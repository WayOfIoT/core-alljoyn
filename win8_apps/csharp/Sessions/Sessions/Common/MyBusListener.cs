﻿//-----------------------------------------------------------------------
// <copyright file="MyBusListener.cs" company="AllSeen Alliance.">
//     //        Copyright (c) Open Connectivity Foundation (OCF) and AllJoyn Open
//        Source Project (AJOSP) Contributors and others.
//
//        SPDX-License-Identifier: Apache-2.0
//
//        All rights reserved. This program and the accompanying materials are
//        made available under the terms of the Apache License, Version 2.0
//        which accompanies this distribution, and is available at
//        http://www.apache.org/licenses/LICENSE-2.0
//
//        Copyright (c) Open Connectivity Foundation and Contributors to AllSeen
//        Alliance. All rights reserved.
//
//        Permission to use, copy, modify, and/or distribute this software for
//        any purpose with or without fee is hereby granted, provided that the
//        above copyright notice and this permission notice appear in all
//        copies.
//
//         THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL
//         WARRANTIES WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED
//         WARRANTIES OF MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE
//         AUTHOR BE LIABLE FOR ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL
//         DAMAGES OR ANY DAMAGES WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR
//         PROFITS, WHETHER IN AN ACTION OF CONTRACT, NEGLIGENCE OR OTHER
//         TORTIOUS ACTION, ARISING OUT OF OR IN CONNECTION WITH THE USE OR
//         PERFORMANCE OF THIS SOFTWARE.
// </copyright>
//-----------------------------------------------------------------------

namespace Sessions.Common
{
    using System;
    using System.Collections.Generic;
    using System.Linq;
    using System.Text;
    using System.Threading.Tasks;
    using AllJoyn;

    /// <summary>
    /// Handles all events occurring over the alljoyn bus
    /// </summary>
    public class MyBusListener
    {
        /// <summary>
        /// Bus listener for session app
        /// </summary>
        private BusListener busListener;

        /// <summary>
        /// Seesion listener for session app
        /// </summary>
        private SessionListener sessionListener;

        /// <summary>
        /// Session port listener for session app
        /// </summary>
        private SessionPortListener sessionPortListener;

        /// <summary>
        /// Reference to the Session operations for this application
        /// </summary>
        private SessionOperations sessionOps;

        /// <summary>
        /// Initializes a new instance of the <see cref="MyBusListener" /> class.
        /// </summary>
        /// <param name="busAtt">object responsible for connecting to and optionally managing a message
        /// bus.</param>
        /// <param name="ops">Session operations object for this application</param>
        public MyBusListener(BusAttachment busAtt, SessionOperations ops)
        {
            this.sessionOps = ops;

            // Create Bus Listener and register signal handlers 
            this.busListener = new BusListener(busAtt);
            this.busListener.BusDisconnected += new BusListenerBusDisconnectedHandler(this.BusListenerBusDisconnected);
            this.busListener.BusStopping += new BusListenerBusStoppingHandler(this.BusListenerBusStopping);
            this.busListener.FoundAdvertisedName += new BusListenerFoundAdvertisedNameHandler(this.BusListenerFoundAdvertisedName);
            this.busListener.ListenerRegistered += new BusListenerListenerRegisteredHandler(this.BusListenerListenerRegistered);
            this.busListener.ListenerUnregistered += new BusListenerListenerUnregisteredHandler(this.BusListenerListenerUnregistered);
            this.busListener.LostAdvertisedName += new BusListenerLostAdvertisedNameHandler(this.BusListenerLostAdvertisedName);
            this.busListener.NameOwnerChanged += new BusListenerNameOwnerChangedHandler(this.BusListenerNameOwnerChanged);

            // Create Session Listener and register signal handlers 
            this.sessionListener = new SessionListener(busAtt);
            this.sessionListener.SessionLost += new SessionListenerSessionLostHandler(this.SessionListenerSessionLost);
            this.sessionListener.SessionMemberAdded += new SessionListenerSessionMemberAddedHandler(this.SessionListenerSessionMemberAdded);
            this.sessionListener.SessionMemberRemoved += new SessionListenerSessionMemberRemovedHandler(this.SessionListenerSessionMemberRemoved);

            // Create Session Port Listener and register signal handlers 
            this.sessionPortListener = new SessionPortListener(busAtt);
            this.sessionPortListener.AcceptSessionJoiner += new SessionPortListenerAcceptSessionJoinerHandler(this.SessionPortListenerAcceptSessionJoiner);
            this.sessionPortListener.SessionJoined += new SessionPortListenerSessionJoinedHandler(this.SessionPortListenerSessionJoined);

            busAtt.RegisterBusListener(this.busListener);
        }

        /// <summary>
        /// Return the bus listener for 'mbl'
        /// </summary>
        /// <param name="mbl">the bus listener for the program.</param>
        /// <returns>session listener stored in mbl</returns>
        public static implicit operator BusListener(MyBusListener mbl)
        {
            return mbl.busListener;
        }

        /// <summary>
        /// Return the sesssion listener for 'mbl'.
        /// </summary>
        /// <param name="mbl">the bus listener for the program.</param>
        /// <returns>session listener stored in mbl</returns>
        public static implicit operator SessionListener(MyBusListener mbl)
        {
            return mbl.sessionListener;
        }

        /// <summary>
        /// Return the sesssion port listener for 'mbl'.
        /// </summary>
        /// <param name="mbl">the bus listener for the program.</param>
        /// <returns>session listener stored in mbl</returns>
        public static implicit operator SessionPortListener(MyBusListener mbl)
        {
            return mbl.sessionPortListener;
        }

        /// <summary>
        /// Called when a BusAttachment this listener is registered with is has become disconnected 
        /// from the bus.
        /// </summary>
        public void BusListenerBusDisconnected()
        {
        }

        /// <summary>
        /// Called when a BusAttachment this listener is registered with is stopping.
        /// </summary>
        public void BusListenerBusStopping()
        {
        }

        /// <summary>
        /// Called by the bus when an external bus is discovered that is advertising a well-known 
        /// name that this attachment has registered interest in via a DBus call to 
        /// org.alljoyn.Bus.FindAdvertisedName
        /// </summary>
        /// <param name="name">A well known name that the remote bus is advertising.</param>
        /// <param name="transport">Transport that received the advertisement.</param>
        /// <param name="namePrefix">The well-known name prefix used in call to FindAdvertisedName 
        /// that triggered this callback.</param>
        public void BusListenerFoundAdvertisedName(string name, TransportMaskType transport, string namePrefix)
        {
            this.sessionOps.Output("Discovered name : " + name);
            this.sessionOps.FoundAdvertisedName(name, transport);
        }

        /// <summary>
        /// Called by the bus when the listener is registered.
        /// </summary>
        /// <param name="bus">The bus the listener is registered with.</param>
        public void BusListenerListenerRegistered(BusAttachment bus)
        {
        }

        /// <summary>
        /// Called by the bus when the listener is unregistered.
        /// </summary>
        public void BusListenerListenerUnregistered()
        {
        }

        /// <summary>
        /// Called by the bus when an advertisement previously reported through FoundName has 
        /// become unavailable.
        /// </summary>
        /// <param name="name">A well known name that the remote bus is advertising that is 
        /// of interest to this attachment.</param>
        /// <param name="transport">Transport that stopped receiving the given advertised 
        /// name.</param>
        /// <param name="namePrefix">The well-known name prefix that was used in a call to 
        /// FindAdvertisedName that triggered this callback.</param>
        public void BusListenerLostAdvertisedName(string name, TransportMaskType transport, string namePrefix)
        {
            this.sessionOps.Output("LostAdvertisedName name=" + name + ", namePrefix=" + namePrefix);
            this.sessionOps.LostAdvertisedName(name);
        }

        /// <summary>
        /// Called by the bus when ownership of any well-known name changes.
        /// </summary>
        /// <param name="busName">the well-known name that has changed.</param>
        /// <param name="prevOwner">The unique name that previously owned the name or NULL if 
        /// there was no previous owner.</param>
        /// <param name="newOwner">The unique name that now owns the name or NULL if the there 
        /// is no new owner.</param>
        public void BusListenerNameOwnerChanged(string busName, string prevOwner, string newOwner)
        {
            this.sessionOps.Output("NameOwnerChanged: name=<" + busName + ">, oldOwner=<" + prevOwner + ">, newOwner=<" + newOwner + ">");
        }

        /// <summary>
        /// Called by the bus when an existing session becomes disconnected.
        /// </summary>
        /// <param name="sessionID">Id of session that was lost.</param>
        public void SessionListenerSessionLost(uint sessionID)
        {
            bool validSession = this.sessionOps.SessionLost(sessionID);
            if (validSession)
            {
                this.sessionOps.Output("Session " + sessionID + "is lost");
            }
            else
            {
                this.sessionOps.Output("Session Lost for unknown session id " + sessionID);
            }
        }

        /// <summary>
        /// Called by the bus when a member of a multipoint session is added.
        /// </summary>
        /// <param name="sessionID">Id of session whose member(s) changed.</param>
        /// <param name="uniqueName">Unique name of member who was added.</param>
        public void SessionListenerSessionMemberAdded(uint sessionID, string uniqueName)
        {
            this.sessionOps.Output(uniqueName + " was added to session (sessionid=" + sessionID + ")");
            this.sessionOps.SessionMemberAdded(sessionID, uniqueName);
        }

        /// <summary>
        /// Called by the bus when a member of a multipoint session is removed.
        /// </summary>
        /// <param name="sessionID">Id of session whose member(s) changed.</param>
        /// <param name="uniqueName">Unique name of member who was removed.</param>
        public void SessionListenerSessionMemberRemoved(uint sessionID, string uniqueName)
        {
            this.sessionOps.Output(uniqueName + " was removed from session (sessionid=" + sessionID + ")");
            this.sessionOps.SessionMemberRemoved(sessionID, uniqueName);
        }

        /// <summary>
        /// Accept or reject an incoming JoinSession request. The session does not exist until 
        /// after this function returns.
        /// </summary>
        /// <param name="sessionPort">Session port that was joined.</param>
        /// <param name="joiner">Unique name of potential joiner.</param>
        /// <param name="opts">Session options requested by the joiner.</param>
        /// <returns>Return true if JoinSession request is accepted. false if rejected</returns>
        private bool SessionPortListenerAcceptSessionJoiner(ushort sessionPort, string joiner, SessionOpts opts)
        {
            bool ret = this.sessionOps.AcceptSessionJoiner(sessionPort);
            if (ret)
            {
                this.sessionOps.Output("Accepting join request on " + sessionPort + " from " + joiner);
            }
            else
            {
                this.sessionOps.Output("Rejecting join attempt to unregistered port " + sessionPort + " from " + joiner);
            }

            return ret;
        }

        /// <summary>
        /// Called by the bus when a session has been successfully joined. The session is 
        /// now fully up.
        /// </summary>
        /// <param name="sessionPort">Session port that was joined.</param>
        /// <param name="id">Id of session.</param>
        /// <param name="joiner">Unique name of the joiner.</param>
        private void SessionPortListenerSessionJoined(ushort sessionPort, uint id, string joiner)
        {
            this.sessionOps.SessionJoined(sessionPort, id, joiner);
        }
    }
}
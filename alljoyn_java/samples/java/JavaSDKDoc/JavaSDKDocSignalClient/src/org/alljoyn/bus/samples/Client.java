/*
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
*/
package org.alljoyn.bus.samples;

import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusListener;
import org.alljoyn.bus.Mutable;
import org.alljoyn.bus.SessionListener;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.annotation.BusSignalHandler;

public class Client {
    static {
        System.loadLibrary("alljoyn_java");
    }
    private static final short CONTACT_PORT=42;
    static BusAttachment mBus;

    public static class SampleSignalHandler  {
        @BusSignalHandler(iface="org.alljoyn.bus.samples.SampleInterface", signal="buttonClicked")
        public void buttonClicked(int id) {
            switch(id) {
            case 0:
                startNewGame();
                break;
            case 1:
                continueGame();
                break;
            case 2:
                quitGame();
                break;
            default:
                break;
            }
        }

        @BusSignalHandler(iface="org.alljoyn.bus.samples.SampleInterface", signal="playerPosition")
        public void playerPosition(int x, int y, int z) {
            updatePlayerPosition(x,y,z);
        }

        public void startNewGame() {
            System.out.println("Starting a new Game");
        }

        public void continueGame() {
            System.out.println("Continuing Game");
        }

        public void quitGame() {
            System.out.println("Quiting Game");
        }

        public void updatePlayerPosition(int x, int y, int z) {
            System.out.println(String.format("Players position is %d, %d, %d.", x, y, z));
        }
    }

    private static SampleOnJoinSessionListener mOnJoined;

    public static void main(String[] args) {

        class MyBusListener extends BusListener {
            public void foundAdvertisedName(String name, short transport, String namePrefix) {
                System.out.println(String.format("BusListener.foundAdvertisedName(%s, %d, %s)", name, transport, namePrefix));

                if (!mOnJoined.acquire()) return;

                if (mOnJoined.isConnected()) {
                    mOnJoined.release();
                    return;
                }

                Status status = mBus.joinSession(name,
                        CONTACT_PORT,
                        new SessionOpts(),
                        new PrintSessionListener(),
                        mOnJoined,
                        null);

                if (status != Status.OK) {
                    mOnJoined.release();
                    System.out.println("BusAttachment.joinSession call failed " + status);
                }
            }
            public void nameOwnerChanged(String busName, String previousOwner, String newOwner){
                if ("com.my.well.known.name".equals(busName)) {
                    System.out.println("BusAttachment.nameOwnerChagned(" + busName + ", " + previousOwner + ", " + newOwner);
                }
            }

        }

        mBus = new BusAttachment("AppName", BusAttachment.RemoteMessage.Receive);
        mOnJoined = new SampleOnJoinSessionListener();

        BusListener listener = new MyBusListener();
        mBus.registerBusListener(listener);

        Status status = mBus.connect();
        if (status != Status.OK) {
            return;
        }
        System.out.println("BusAttachment.connect successful");

        SampleSignalHandler mySignalHandlers = new SampleSignalHandler();

        status = mBus.registerSignalHandlers(mySignalHandlers);
        if (status != Status.OK) {
            return;
        }
        System.out.println("BusAttachment.registerSignalHandlers successful");

        status = mBus.findAdvertisedName("com.my.well.known.name");
        if (status != Status.OK) {
            return;
        }
        System.out.println("BusAttachment.findAdvertisedName successful " + "com.my.well.known.name");
        while(true) {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                System.out.println("Program interupted");
            }
        }
    }
}

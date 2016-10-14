/*
 * Copyright AllSeen Alliance. All rights reserved.
 *
 *    Permission to use, copy, modify, and/or distribute this software for any
 *    purpose with or without fee is hereby granted, provided that the above
 *    copyright notice and this permission notice appear in all copies.
 *
 *    THE SOFTWARE IS PROVIDED "AS IS" AND THE AUTHOR DISCLAIMS ALL WARRANTIES
 *    WITH REGARD TO THIS SOFTWARE INCLUDING ALL IMPLIED WARRANTIES OF
 *    MERCHANTABILITY AND FITNESS. IN NO EVENT SHALL THE AUTHOR BE LIABLE FOR
 *    ANY SPECIAL, DIRECT, INDIRECT, OR CONSEQUENTIAL DAMAGES OR ANY DAMAGES
 *    WHATSOEVER RESULTING FROM LOSS OF USE, DATA OR PROFITS, WHETHER IN AN
 *    ACTION OF CONTRACT, NEGLIGENCE OR OTHER TORTIOUS ACTION, ARISING OUT OF
 *    OR IN CONNECTION WITH THE USE OR PERFORMANCE OF THIS SOFTWARE.
 */
package org.alljoyn.bus.samples;

import java.util.Map;
import java.io.File;
import java.util.Scanner;
import java.util.UUID;
import java.util.Set;
import java.util.HashSet;

import org.alljoyn.bus.AboutObj;
import org.alljoyn.bus.AboutData;
import org.alljoyn.bus.PermissionConfigurationListener;
import org.alljoyn.bus.PermissionConfigurator;
import org.alljoyn.bus.BusAttachment;
import org.alljoyn.bus.BusException;
import org.alljoyn.bus.AuthListener;
import org.alljoyn.bus.Status;
import org.alljoyn.bus.SessionOpts;
import org.alljoyn.bus.SessionPortListener;
import org.alljoyn.bus.Mutable;

public class SecureDoorClient {
    static {
        System.loadLibrary("alljoyn_java");
    }
    private static BusAttachment mBus;

    private static final String filterRule = "type='signal',interface='" + Door.DOOR_INTF_NAME + "',member='" + Door.DOOR_STATE_CHANGED + "'";
    private static final String defaultManifestXml = "<manifest>" +
    "<node>" +
    "<interface>" +
    "<any>" +
    "<annotation name = \"org.alljoyn.Bus.Action\" value = \"Modify\"/>" +
    "<annotation name = \"org.alljoyn.Bus.Action\" value = \"Observe\"/>" +
    "<annotation name = \"org.alljoyn.Bus.Action\" value = \"Provide\"/>" +
    "</any>" +
    "</interface>" +
    "</node>" +
    "</manifest>";

    public static void main(String[] args) throws Exception {
        String appName = "SecureDoorConsumerJava";
        if (args.length > 0) {
            appName = args[0];
        }
        System.out.println("Starting door consumer " + appName);

        mBus = new BusAttachment(appName, BusAttachment.RemoteMessage.Receive);
        mBus.connect();

        mBus.registerAuthListener("ALLJOYN_ECDHE_NULL ALLJOYN_ECDHE_PSK ALLJOYN_ECDHE_ECDSA", authListener, null, false, pclistener);
        mBus.getPermissionConfigurator().setClaimCapabilities((short) (PermissionConfigurator.CLAIM_CAPABILITY_CAPABLE_ECDHE_PSK | PermissionConfigurator.CLAIM_CAPABILITY_CAPABLE_ECDHE_NULL));
        mBus.getPermissionConfigurator().setClaimCapabilityAdditionalInfo((short) (PermissionConfigurator.CLAIM_CAPABILITY_ADDITIONAL_PSK_GENERATED_BY_SECURITY_MANAGER | PermissionConfigurator.CLAIM_CAPABILITY_ADDITIONAL_PSK_GENERATED_BY_APPLICATION));
        mBus.getPermissionConfigurator().setManifestTemplateFromXml(defaultManifestXml);

        SessionOpts sessionOpts = new SessionOpts();
        sessionOpts.traffic = SessionOpts.TRAFFIC_MESSAGES;
        sessionOpts.isMultipoint = false;
        sessionOpts.proximity = SessionOpts.PROXIMITY_ANY;
        sessionOpts.transports = SessionOpts.TRANSPORT_ANY;

        final Mutable.ShortValue contactPort = new Mutable.ShortValue(DoorSessionManager.DOOR_APPLICATION_PORT);
        Status status = mBus.bindSessionPort(contactPort, sessionOpts,
                new SessionPortListener() {
            public boolean acceptSessionJoiner(short sessionPort, String joiner, SessionOpts sessionOpts) {
                System.out.println("SessionPortListener.acceptSessionJoiner called");
                return sessionPort == contactPort.value;
            }
            public void sessionJoined(short sessionPort, int id, String joiner) {
            }
        });

        if (status != Status.OK) {
            System.out.println("bindSessionPort " + status);
            return;
        }

        System.out.println("Waiting to be claimed...");

        while (mBus.getPermissionConfigurator().getApplicationState() != PermissionConfigurator.ApplicationState.CLAIMED) {
            Thread.sleep(1000);
        }

        DoorAboutListener dal = new DoorAboutListener();
        mBus.registerAboutListener(dal);

        String ifaces[] = {Door.DOOR_INTF_NAME};
        status = mBus.whoImplements(ifaces);
        if (status != Status.OK) {
            System.out.println("Status whoImplements: " + status);
            return;
        }

        printHelp();
        Scanner scanner = new Scanner(System.in);

        Set<String> doors;
        DoorSessionManager doorSessionManager = new DoorSessionManager(mBus);

        while (true) {
            System.out.print("> ");
            String command = scanner.next();

            if (command.equals("q")) {
                break;
            }
            if (command.equals("h")) {
                printHelp();
            }
            if (command.equals("o")) {
                doors = dal.getDoorNames();
                if (doors.size() == 0) {
                    System.out.println("no doors found");
                    continue;
                }
                for (String doorName : doors.toArray(new String[doors.size()])) {
                    System.out.println(doorName);
                    try {
                        doorSessionManager.getProxyDoorObject(doorName).open();
                    } catch (BusException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (command.equals("c")) {
                doors = dal.getDoorNames();
                if (doors.size() == 0) {
                    System.out.println("no doors found");
                    continue;
                }
                for (String doorName : doors.toArray(new String[doors.size()])) {
                    System.out.println(doorName);
                    try {
                        doorSessionManager.getProxyDoorObject(doorName).close();
                    } catch (BusException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (command.equals("g")) {
                doors = dal.getDoorNames();
                if (doors.size() == 0) {
                    System.out.println("no doors found");
                    continue;
                }
                for (String doorName : doors.toArray(new String[doors.size()])) {
                    System.out.print(doorName);
                    try {
                        System.out.println(doorSessionManager.getProxyDoorObject(doorName).getState() ? " is open" : " is closed");
                    } catch (BusException e) {
                        e.printStackTrace();
                    }
                }
            }
            if (command.equals("s")) {
                doors = dal.getDoorNames();
                if (doors.size() == 0) {
                    System.out.println("no doors found");
                    continue;
                }
                for (String doorName : doors.toArray(new String[doors.size()])) {
                    System.out.print(doorName);
                    try {
                        System.out.println(doorSessionManager.getProxyDoorObject(doorName).getStateM() ? " is open" : " is closed");
                    } catch (BusException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        doorSessionManager.stop();
    }

    private static AuthListener authListener = new AuthListener() {
        public boolean requested(String mechanism, String authPeer, int count, String userName,
                AuthRequest[] requests) {
            return true;
        }

        public void completed(String mechanism, String authPeer, boolean authenticated) {}
    };

    private static PermissionConfigurationListener pclistener = new PermissionConfigurationListener() {

        public Status factoryReset() {
            return Status.OK;
        }

        public void policyChanged() {
        }

        public void startManagement() {
        }

        public void endManagement() {
        }
    };

    public static void printHelp() {
        System.out.println("Welcome to the door consumer - enter 'h' for this menu\n" +
           "Menu\n" +
           ">o : Open doors\n" +
           ">c : Close doors\n" +
           ">s : Doors state - using ProxyBusObject->MethodCall\n" +
           ">g : Get doors state - using ProxyBusObject->GetProperty\n" +
           ">q : Quit\n");
    }
}

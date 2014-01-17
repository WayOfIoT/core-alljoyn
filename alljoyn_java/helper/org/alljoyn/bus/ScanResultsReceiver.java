/**
 * @file
 */

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
package org.alljoyn.bus;

import android.content.BroadcastReceiver;
import org.alljoyn.bus.ScanResultMessage;
import org.alljoyn.bus.AllJoynAndroidExt;

import android.content.Intent;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;


import java.util.ArrayList;
import java.util.List;

public class ScanResultsReceiver extends BroadcastReceiver{

	AllJoynAndroidExt jniProximity;
	//
	// Need a constructor that will take the instance on Proximity Service
	//
	public ScanResultsReceiver(AllJoynAndroidExt jniProximity){
		super();
		this.jniProximity = jniProximity; 
	}
	
	public ScanResultsReceiver getScanReceiver(){
		return this;
	}
	
	@Override
	public void onReceive(Context c, Intent intent){
		
		if(intent.getAction().equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)){
			List<ScanResult> scanResults = jniProximity.wifiMgr.getScanResults();
			
			if(scanResults.size() == 0){
				jniProximity.scanResultsObtained = true;
				return;
			}
			
			jniProximity.lastScanTime = System.currentTimeMillis();
			
			jniProximity.scanResultMessage = new ScanResultMessage[scanResults.size()];
						
			String currentBSSID = jniProximity.wifiMgr.getConnectionInfo().getBSSID();
			int currentBSSIDIndex = 0;
			for (ScanResult result : scanResults){

				jniProximity.scanResultMessage[currentBSSIDIndex] = new ScanResultMessage();
				jniProximity.scanResultMessage[currentBSSIDIndex].bssid = result.BSSID;
				jniProximity.scanResultMessage[currentBSSIDIndex].ssid = result.SSID;
				if(currentBSSID !=null && currentBSSID.equals(result.BSSID)){
					jniProximity.scanResultMessage[currentBSSIDIndex].attached = true;
				}
				currentBSSIDIndex++;
				
			}
			currentBSSIDIndex = 0;
			jniProximity.scanResultsObtained = true;
	}

}
}
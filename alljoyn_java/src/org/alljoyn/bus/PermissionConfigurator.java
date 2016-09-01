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

package org.alljoyn.bus;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.alljoyn.bus.common.CertificateX509;
import org.alljoyn.bus.common.KeyInfoNISTP256;
import org.alljoyn.bus.common.ECCPublicKey;
import org.alljoyn.bus.common.KeyInfoECC;

public class PermissionConfigurator {

    private BusAttachment m_bus;

    public PermissionConfigurator(BusAttachment bus) throws Exception{
        create(bus);
        m_bus = bus;
    }

    /**
     * Java automatically implements a toString
     * function that returns the name of the enum.
     * The implementation in the C++ bindings
     * does not need to be explicitly replicated.
     */
    public enum ApplicationState {
        /**
         * The application is not claimed and not accepting claim requests.
         */
        NOT_CLAIMABLE,
        /**
         * The application is not claimed and is accepting claim requests.
         */
        CLAIMABLE,
        /**
         * The application is claimed and can be configured.
         */
        CLAIMED,
        /**
         * The application is claimed, but requires a configuration update (after a software upgrade).
         */
        NEED_UPDATE
    }

     public static final int CLAIM_CAPABILITY_CAPABLE_ECDHE_NULL = 0x01;
     /**
      * @deprecated in Alljoyn 15.09, needs to be supported 16.04 and 16.10
      */
     public static final short CLAIM_CAPABILITY_CAPABLE_ECDHE_PSK = 0x02;
     public static final short CLAIM_CAPABILITY_CAPABLE_ECDHE_ECDSA = 0x04;
     public static final short CLAIM_CAPABILITY_CAPABLE_ECDHE_SPEKE = 0x08;

    /** 
     * Default ClaimCapabilities: NULL, PSK and SPEKE.
     */
    public static final short CLAIM_CAPABILITIES_DEFAULT = CLAIM_CAPABILITY_CAPABLE_ECDHE_NULL | CLAIM_CAPABILITY_CAPABLE_ECDHE_PSK | CLAIM_CAPABILITY_CAPABLE_ECDHE_SPEKE;

    public static final short CLAIM_CAPABILITY_ADDITIONAL_PSK_GENERATED_BY_SECURITY_MANAGER = 0x01;
    public static final short CLAIM_CAPABILITY_ADDITIONAL_PSK_GENERATED_BY_APPLICATION = 0x02;

    /**
     * Allocate native resources.
     */
    private native void create(BusAttachment bus) throws Exception;

    /**
     * Let the Java garbage collector release resources.
     */
    @Override
    protected void finalize() throws Throwable {
        try {
            destroy(m_bus);
        } finally {
            super.finalize();
        }
    }

    /**
     * virtual destructor
     */
    public synchronized native void destroy(BusAttachment bus);

    /**
     * Get the manifest template for the application as XML.
     *
     * @return manifestTemplateXml String to receive the manifest template as XML.
     *
     * @throws BusException ER_OK if successful; otherwise, an error code.
     */
    public native String getManifestTemplateAsXml() throws BusException;

    /**
     * Set the manifest template for the application from an XML.
     *
     * @param manifestTemplateXml XML containing the manifest template.
     *
     * @throws BusException ER_OK if successful; otherwise, an error code.
     */
    public native void setManifestTemplateFromXml(String manifestTemplateXml) throws BusException;

    /**
     * Retrieve the state of the application.
     * @return applicationState the application state
     * @throws BusException
     *      - #ER_OK if successful
     *      - #ER_NOT_IMPLEMENTED if the method is not implemented
     *      - #ER_FEATURE_NOT_AVAILABLE if the value is not known
     */
    public native ApplicationState getApplicationState() throws BusException;

    /**
     * Set the application state.  The state can't be changed from CLAIMED to
     * CLAIMABLE.
     * @param newState The new application state
     * @throws BusException
     *      - #ER_OK if action is allowed.
     *      - #ER_INVALID_APPLICATION_STATE if the state can't be changed
     *      - #ER_NOT_IMPLEMENTED if the method is not implemented
     */
    public native void setApplicationState(ApplicationState newState) throws BusException;

    /**
     * Retrieve the public key info for the signing key.
     * @return keyInfo the public key info
     * @throws BusException ER_OK if successful; otherwise, an error code.
     */
    public native KeyInfoECC getSigningPublicKey() throws BusException;

    /**
     * Sign the X509 certificate using the signing key
     * @param cert the certificate to be signed
     * @throws BusException ER_OK if successful; otherwise, an error code.
     */
    public native void signCertificate(CertificateX509 cert) throws BusException;

    /**
     * Reset the permission settings by removing the manifest all the
     * trust anchors, installed policy and certificates. This call
     * must be invoked after the bus attachment has enable peer security.
     *
     * @throws BusException
     * ER_OK if successful; otherwise, an error code.
     */
    public native void reset() throws BusException;

    /**
     * Get the connected peer ECC public key if the connection uses the
     * ECDHE_ECDSA key exchange.
     * @param guid the peer guid
     * @return publicKey the buffer to hold the ECC public key.
     * @throws BusException ER_OK if successful; otherwise, error code.
     */
    public native ECCPublicKey getConnectedPeerPublicKey(UUID guid) throws BusException;

    /**
     * Set the authentication mechanisms the application supports for the
     * claim process.  It is a bit mask.
     *
     * | Mask                | Description              |
     * |---------------------|--------------------------|
     * | CAPABLE_ECDHE_NULL  | claiming via ECDHE_NULL  |
     * | CAPABLE_ECDHE_PSK   | claiming via ECDHE_PSK   |
     * | CAPABLE_ECDHE_ECDSA | claiming via ECDHE_ECDSA |
     *
     * @param claimCapabilities The authentication mechanisms the application supports
     *
     * @throws BusException
     *  - #ER_OK if successful
     *  - an error status indicating failure
     */
    public native void setClaimCapabilities(int claimCapabilities) throws BusException;

    /**
     * Get the authentication mechanisms the application supports for the
     * claim process.
     *
     * @return claimCapabilities The authentication mechanisms the application supports
     *
     * @throws BusException
     *  - #ER_OK if successful
     *  - an error status indicating failure
     */
    public native int getClaimCapabilities() throws BusException;

    /**
     * Set the additional information on the claim capabilities.
     * It is a bit mask.
     *
     * | Mask                              | Description                       |
     * |-----------------------------------|-----------------------------------|
     * | PSK_GENERATED_BY_SECURITY_MANAGER | PSK generated by Security Manager |
     * | PSK_GENERATED_BY_APPLICATION      | PSK generated by application      |
     *
     * @param additionalInfo The additional info
     *
     * @throws BusException
     *  - #ER_OK if successful
     *  - an error status indicating failure
     */
    public native void setClaimCapabilityAdditionalInfo(int additionalInfo) throws BusException;

    /**
     * Get the additional information on the claim capabilities.
     * @return additionalInfo The additional info
     *
     * @throws BusException
     *  - #ER_OK if successful
     *  - an error status indicating failure
     */
    public native int getClaimCapabilityAdditionalInfo() throws BusException;

    /**
     * Perform claiming of this app locally/offline.
     *
     * @param certificateAuthority Certificate authority public key
     * @param adminGroupGuid Admin group GUID
     * @param adminGroupAuthority Admin group authority public key
     * @param identityCertChain Identity cert chain
     * @param identityCertChainCount Count of certs array
     * @param manifestsXmls Signed manifests in XML format
     * @param manifestsCount Number of manifests in the manifestsXmls array
     *
     * @throws BusException
     *    - #ER_OK if the app was successfully claimed
     *    - #ER_FAIL if the app could not be claimed, but could not then be reset back to original state.
     *               App will be in unknown state in this case.
     *    - other error code indicating failure, but app is returned to reset state
     */
    public native void claim(KeyInfoNISTP256 certificateAuthority, UUID adminGroupGuid,
        KeyInfoNISTP256 adminGroupAuthority, CertificateX509[] identityCertChain,
        long identityCertChainCount,
        String[] manifestsXmls,
        long manifestsCount) throws BusException;

    /**
     * Perform a local UpdateIdentity to replace the identity certificate chain and
     * the signed manifests. If successful, already-installed certificates and signed
     * manifests are cleared; on failure, the state of both are unchanged.
     *
     * @param certs Identity cert chain to be installed
     * @param certCount Number of certificates in certs array
     * @param manifestsXmls Signed manifests to be installed
     * @param manifestsCount Number of signed manifests in manifestsXmls array
     *
     * @throws BusException
     *    - #ER_OK if the identity was successfully updated
     *    - other error code indicating failure
     */
    public native void updateIdentity(CertificateX509[] certs,
        long certCount,
        String[] manifestsXmls,
        long manifestsCount) throws BusException;

    /**
     * Retrieve the local app's identity certificate chain.
     *
     * @return certChain A vector containing the identity certificate chain
     *
     * @throws BusException
     *    - #ER_OK if the chain is successfully stored in certChain
     *    - other error indicating failure
     */
    public native CertificateX509 getIdentity() throws BusException;

    /**
     * Retrieve the local app's identity certificate information.
     *
     * @return IdentityCertificateId 
     *
     * @throws BusException
     *    - #ER_OK if the identity certificate information is placed in the parameters
     *    - #ER_CERTIFICATE_NOT_FOUND if no identity certificate is installed
     *    - other error code indicating failure
     */
    public native IdentityCertificateId getIdentityCertificateId() throws BusException;

    /**
     * Reset the local app's policy to the default policy.
     *
     * @throws BusException
     *    - #ER_OK if the policy was successfully reset
     *    - other error code indicating failure
     */
    public native void resetPolicy() throws BusException;

    /**
     * Get the membership certificate summaries.
     *
     * @throws BusException
     *    - #ER_OK if the membership summaries are successfully retrieved
     *    - other error indicating failure
     */
    public native IdentityCertificateId[] getMembershipSummaries() throws BusException;

    /**
     * Install a membership certificate chain.
     *
     * @param certChain Certificate chain
     * @param certCount Number of certificates in certChain
     *
     * @throws BusException
     *    - #ER_OK if the membership certificate chain is successfully installed
     *    - other error code indicating failure
     */
    public native void installMembership(CertificateX509[] certChain, long certCount) throws BusException;

    /**
     * Signal the app locally that management is starting.
     *
     * @throws BusException
     *    - #ER_OK if the start management signal was sent
     *    - #ER_MANAGEMENT_ALREADY_STARTED if the app is already in this state
     */
    public native void startManagement() throws BusException;

    /**
     * Signal the app locally that management is ending.
     *
     * @throws BusException
     *    - #ER_OK if the start management signal was sent
     *    - #ER_MANAGEMENT_NOT_STARTED if the app was not in the management state
     */
    public native void endManagement() throws BusException;

    /**
     * Install signed manifests to the local app.
     *
     * @param manifestsXmls An array of signed manifests to install in XML format
     * @param manifestsCount The number of manifests in the manifests array
     * @param append True to append the manifests to any already installed, or false to clear manifests out first
     *
     * On failure, the installed set of manifests is unchanged.
     *
     * @throws BusException
     *    - #ER_OK if manifests are successfully installed
     *    - #ER_DIGEST_MISMATCH if none of the manifests have been signed
     *    - other error code indicating failure
     */
    public native void installManifests(String[] manifestsXmls, long manifestsCount, boolean append) throws BusException;

    /**
     * The native connection handle.
     */
    private long handle;
}

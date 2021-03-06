/*
 * Copyright (c) 2020, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */
package org.wso2.carbon.identity.application.authz.opa.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.wso2.carbon.identity.application.authentication.framework.JsFunctionRegistry;
import org.wso2.carbon.identity.application.authz.opa.InvokeOpaFunction;
import org.wso2.carbon.identity.application.authz.opa.InvokeOpaFunctionImpl;
import org.wso2.carbon.identity.claim.metadata.mgt.ClaimMetadataManagementService;

/**
 * Services component which handles OPA related auth functions.
 */
@Component(
        name = "identity.application.authz.opa.component",
        immediate = true
)
public class OPAFunctionsServiceComponent {

    private static final Log LOG = LogFactory.getLog(OPAFunctionsServiceComponent.class);
    public static final String FUNC_INVOKE_OPA = "invokeOPA";

    @Activate
    protected void activate(ComponentContext ctxt) {

        try {
            JsFunctionRegistry jsFunctionRegistry = OPAFunctionsServiceHolder.getInstance().getJsFunctionRegistry();
            InvokeOpaFunction invokeOPA = new InvokeOpaFunctionImpl();
            jsFunctionRegistry.register(JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER, FUNC_INVOKE_OPA, invokeOPA);
        } catch (Throwable e) {
            LOG.error("Error occurred while registering invokeOPA function", e);
        }
    }

    @Deactivate
    protected void deactivate(ComponentContext ctxt) {

        JsFunctionRegistry jsFunctionRegistry = OPAFunctionsServiceHolder.getInstance().getJsFunctionRegistry();
        if (jsFunctionRegistry != null) {
            jsFunctionRegistry.deRegister(JsFunctionRegistry.Subsystem.SEQUENCE_HANDLER, FUNC_INVOKE_OPA);
        }
    }

    @Reference(
            service = JsFunctionRegistry.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetJsFunctionRegistry"
    )
    public void setJsFunctionRegistry(JsFunctionRegistry jsFunctionRegistry) {

        OPAFunctionsServiceHolder.getInstance().setJsFunctionRegistry(jsFunctionRegistry);
    }

    public void unsetJsFunctionRegistry(JsFunctionRegistry jsFunctionRegistry) {

        OPAFunctionsServiceHolder.getInstance().setJsFunctionRegistry(null);
    }

    @Reference(name = "claim.meta.mgt.service",
            service = ClaimMetadataManagementService.class,
            cardinality = ReferenceCardinality.MANDATORY,
            policy = ReferencePolicy.DYNAMIC,
            unbind = "unsetClaimMetaMgtService")
    protected void setClaimMetaMgtService(ClaimMetadataManagementService claimMetaMgtService) {

        OPAFunctionsServiceHolder.getInstance().setClaimMetadataManagementService(claimMetaMgtService);
    }

    protected void unsetClaimMetaMgtService(ClaimMetadataManagementService claimMetaMgtService) {

        OPAFunctionsServiceHolder.getInstance().setClaimMetadataManagementService(null);
    }
}

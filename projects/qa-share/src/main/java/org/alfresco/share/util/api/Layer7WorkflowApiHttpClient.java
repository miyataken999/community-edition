/*
 * #%L
 * qa-share
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
package org.alfresco.share.util.api;

import org.alfresco.rest.api.tests.client.AuthenticatedHttp;
import org.alfresco.rest.api.tests.client.HttpClientProvider;
import org.alfresco.rest.api.tests.client.HttpResponse;
import org.alfresco.rest.api.tests.client.RequestContext;
import org.alfresco.rest.workflow.api.tests.WorkflowApiHttpClient;
import org.alfresco.share.util.AbstractUtils;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpMethod;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by dmitry.yukhnovets on 13.10.2014.
 */
public class Layer7WorkflowApiHttpClient extends WorkflowApiHttpClient {
    private String tokenKey;
    private HttpClientProvider httpProvider;
    private WebDrone drone;
    public Layer7WorkflowApiHttpClient(WebDrone drone, String scheme, String host, int port, String contextPath, String servletName, AuthenticatedHttp authenticatedHttp) {
        super(scheme, host, port, contextPath, servletName, authenticatedHttp);
        apiName = "workflow";
        this.drone = drone;
        httpProvider = authenticatedHttp.getHttpProvider();
    }

    @Override
    public HttpResponse submitRequest(HttpMethod req, final RequestContext rq) throws IOException
    {
        try
        {
            final long start = System.currentTimeMillis();
            String tokenKey = AbstractUtils.getTokenKey(drone, rq.getRunAsUser(), rq.getPassword());
            req.addRequestHeader("Authorization","Bearer " + tokenKey);

            final AuthenticatedHttp.HttpRequestCallback<HttpResponse> callback = new AuthenticatedHttp.HttpRequestCallback<HttpResponse>()
            {
                @Override
                public HttpResponse onCallSuccess(HttpMethod method) throws Exception
                {
                    long end = System.currentTimeMillis();
                    Map<String, String> headersMap = null;
                    Header[] headers = method.getResponseHeaders();
                    if (headers != null)
                    {
                        headersMap = new HashMap<String, String>(headers.length);
                        for (Header header : headers)
                        {
                            headersMap.put(header.getName(), header.getValue());
                        }
                    }
                    return new HttpResponse(method,
                                            rq.getRunAsUser(),
                                            method.getResponseBodyAsString(),
                                            headersMap, (end - start));
                }

                @Override
                public boolean onError(HttpMethod method, Throwable t)
                {
                    return false;
                }
            };

            HttpResponse response = null;
            response = executeWithOauthAuthentication(req, tokenKey, callback);

            return response;
        }
        finally
        {
            if(req != null)
            {
                req.releaseConnection();
            }
        }
    }

    private <T extends Object> T executeWithOauthAuthentication(HttpMethod method, String tokenKey, AuthenticatedHttp.HttpRequestCallback<T> callback)
    {
        try
        {
            httpProvider.getHttpClient().executeMethod(null, method);

            if(callback != null)
            {
                return callback.onCallSuccess(method);
            }

            // No callback used, return null
            return null;
        }
        catch(Throwable t)
        {
            boolean handled = false;

            // Delegate to callback to handle error. If not available, throw exception
            if(callback != null)
            {
                handled = callback.onError(method, t);
            }

            if(!handled)
            {
                throw new RuntimeException("Error while executing HTTP-call (" + method.getPath() +")", t);
            }

            return null;
        }
        finally
        {
            method.releaseConnection();
        }
    }
}


/*******************************************************************************
 * Copyright (c) 2018 EclipseSource Services GmbH and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *   
 * Contributors:
 * 	Tobias Ortmayr - initial API and implementation
 ******************************************************************************/
package com.eclipsesource.glsp.server.provider;

import java.util.Set;

import com.eclipsesource.glsp.api.handler.ActionHandler;
import com.eclipsesource.glsp.api.provider.ActionHandlerProvider;
import com.google.inject.Inject;

public class DefaultActionHandlerProvider implements ActionHandlerProvider {
	private Set<ActionHandler> handlers;
	@Inject
	public DefaultActionHandlerProvider(Set<ActionHandler> handlers) {
		this.handlers=handlers;
	}

	@Override
	public Set<ActionHandler> getHandlers() {
		return handlers;
	}

}

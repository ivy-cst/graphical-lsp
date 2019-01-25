/********************************************************************************
 * Copyright (c) 2019 EclipseSource and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 ********************************************************************************/
import { injectable } from "inversify";
import { Action, ActionHandlerRegistry, IActionHandler, IActionHandlerInitializer, ICommand, SModelRoot } from "sprotty/lib";
import { IModelUpdateObserver } from "../../base/command-stack";
import { EdgeEditConfig, edgeEditConfig, EditConfig, NodeEditConfig, nodeEditConfig } from "../../base/edit-config/edit-config";
import { contains } from "../../utils/array-utils";
import { EdgeTypeHint, isSetTypeHintsAction, NodeTypeHint, SetTypeHintsAction } from "./action-definition";


@injectable()
export class TypeHintsActionIntializer implements IActionHandlerInitializer, IActionHandler, IModelUpdateObserver {

    protected editConfigs: Map<string, EditConfig> = new Map
    handle(action: Action): ICommand | Action | void {
        if (isSetTypeHintsAction(action)) {
            action.nodeHints.forEach(hint => this.editConfigs.set(hint.elementTypeId, createNodeEditConfig(hint)))
            action.edgeHints.forEach(hint => this.editConfigs.set(hint.elementTypeId, createEdgeEditConfig(hint)))
        }
    }

    initialize(registry: ActionHandlerRegistry): void {
        registry.register(SetTypeHintsAction.KIND, this)
    }

    beforeServerUpdate(model: SModelRoot) {
        this.applyConfig(model)
    }

    applyConfig(model: SModelRoot) {
        model.index.all().forEach(element => {
            const config = this.editConfigs.get(element.type)
            if (config) {
                Object.keys(config).forEach(key => (<any>element)[key] = (<any>config)[key])
            }
        })
    }
}

export function createNodeEditConfig(hint: NodeTypeHint): NodeEditConfig {
    return <NodeEditConfig>{
        deletable: hint.deletable,
        repositionable: hint.repositionable,
        resizable: hint.resizable,
        configType: nodeEditConfig,
        isContainableElement: (element) => { return hint.containableElementTypeIds ? contains(hint.containableElementTypeIds, element.type) : false },
        isContainer: () => { return hint.containableElementTypeIds ? hint.containableElementTypeIds.length > 0 : false }
    }
}


export function createEdgeEditConfig(hint: EdgeTypeHint): EdgeEditConfig {
    return <EdgeEditConfig>{
        deletable: hint.deletable,
        repositionable: hint.repositionable,
        routable: hint.routable,
        configType: edgeEditConfig,
        isAllowedSource: (source) => { return contains(hint.sourceElementTypeIds, source.type) },
        isAllowedTarget: (target) => { return contains(hint.targetElementTypeIds, target.type) }
    }
}
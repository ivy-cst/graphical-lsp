import { injectable } from "inversify";
import { IConnection } from "@theia/languages/lib/node"
import { createSocketConnection } from 'vscode-ws-jsonrpc/lib/server'
import * as net from 'net'
import { WorkflowLanguage } from "../common/workflow-language";
import { BaseGraphicalLanguageServerContribution } from 'glsp-theia-extension/lib/node'

function getPort(): number | undefined {
    let arg = process.argv.filter(arg => arg.startsWith('--WORKFLOW_LSP='))[0]
    if (!arg) {
        return undefined
    } else {
        return Number.parseInt(arg.substring('--WORKFLOW_LSP='.length), 10)
    }
}
@injectable()
export class WorkflowGLServerContribution extends BaseGraphicalLanguageServerContribution {
    readonly id = WorkflowLanguage.Id
    readonly name = WorkflowLanguage.Name

    readonly description = {
        id: 'workflow',
        name: 'Workflow',
        documentSelector: ['workflow'],
        fileEvents: [
            '**/*.workflow'
        ]
    }

    start(clientConnection: IConnection): void {
        let socketPort = getPort();
        if (socketPort) {
            const socket = new net.Socket()
            const serverConnection = createSocketConnection(socket, socket, () => {
                socket.destroy()
            });
            this.forward(clientConnection, serverConnection)
            socket.connect(socketPort)
        } else {
            console.error("Error when trying to connect to Workflow graphical language server")
        }
    }


}
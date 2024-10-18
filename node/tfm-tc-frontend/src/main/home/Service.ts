import {Chat, Container, DockerContainer, Prompt} from './Model'
import * as Fetch from '../common/FetchUtils'

/**
 * Contract to be supported by the service used by the Home Page.
 */
 export interface Service {
  addPrompt: (prompt:AddPromptModel, credentials?: string) => Fetch.Request<AddPromptOutputModel>
  createContainer: (container: string, chatId: string, credentials?: string) => Fetch.Request<CreateContainerOutputModel>
  startContainer: (containerId: string, credentials?: string) => Fetch.Request<ManageContainerOutputModel>
  stopContainer: (containerId: string, credentials?: string) => Fetch.Request<ManageContainerOutputModel>
  getChat: (chatId: string, credentials?: string) => Fetch.Request<Array<Prompt>>
  getChats: (credentials?: string) => Fetch.Request<Array<Chat>>
  getContainers: (credentials?: string) => Fetch.Request<Array<DockerContainer>>
  getContainer: (containerId: string, credentials?: string) => Fetch.Request<Container>
  pingAPI: (credentials?: string) => Fetch.Request<string>
}

export type PromptsModel = {
  prompt: Array<Prompt>
}

export type ChatsModel = {
    chats: Array<string>
}

export type AddPromptModel = {
    text: string,
    type: string,
    model: string,
    chatId:string|undefined
}

export type AddPromptOutputModel = {
    id: string,
    date: string,
    contents: Array<ModelContent>
}

export type CreateContainerOutputModel = {
  id: string,
  warnings: Array<string>
}

export type ManageContainerOutputModel = {
  message: string
}

export type ModelContent = {
    type: string,
    text: string
}

/**
 * An implementation of the home service.
 * @param chatsUrl      - the chats resource URL.
 * @param servicesUrl   - the services resource URL.
 * @param pingUrl       - the ping resource URL.
 * @returns the newly instantiated service.
 */
export function getService(chatsUrl: URL,servicesUrl:URL,pingUrl: URL): Service {
  return {
    addPrompt: (prompt: AddPromptModel, credentials?: string): Fetch.Request<AddPromptOutputModel> => {
        const headers = new Headers({ 'Content-type' : 'application/json' })
        if (credentials) headers.append('Authorization', `Bearer ${credentials}`)
        return Fetch.cancelableRequest<AddPromptOutputModel>(chatsUrl, {
          method: 'POST',
          headers,
          body: JSON.stringify(prompt)
        })
      },
      createContainer: (container: string, chatId: string, credentials?: string): Fetch.Request<CreateContainerOutputModel> => {
        const blob = new Blob([container], { type: "text/plain" });
        const headers = new Headers({ 'Content-type' : 'application/json' })
        if (credentials) headers.append('Authorization', `Bearer ${credentials}`)
        return Fetch.cancelableRequest<CreateContainerOutputModel>(new URL(`${servicesUrl}/${chatId}`), {
          method: 'POST',
          headers,
          body: new File([blob],"dockerfile",{ type: "text/plain" })
        })
      },
    startContainer: (containerId: string, credentials?: string): Fetch.Request<ManageContainerOutputModel> => {
      const headers = new Headers({ 'Content-type' : 'application/json' })
      if (credentials) headers.append('Authorization', `Bearer ${credentials}`)
      return Fetch.cancelableRequest<ManageContainerOutputModel>(new URL(`${servicesUrl}/${containerId}/start`), {
        method: 'POST',
        headers
      })
    },
    stopContainer: (containerId: string, credentials?: string): Fetch.Request<ManageContainerOutputModel> => {
      const headers = new Headers({ 'Content-type' : 'application/json' })
      if (credentials) headers.append('Authorization', `Bearer ${credentials}`)
      return Fetch.cancelableRequest<ManageContainerOutputModel>(new URL(`${servicesUrl}/${containerId}/stop`), {
        method: 'POST',
        headers
      })
    },
    getChat: (chatId: string, credentials?: string): Fetch.Request<Array<Prompt>> => {
      return Fetch.cancelableRequest<Array<Prompt>>(new URL(`${chatsUrl}/${chatId}`), {
        headers: credentials ? { 
          'Authorization': `Bearer ${credentials}`
         } : { },
      })
    },
    getChats: (credentials?: string): Fetch.Request<Array<Chat>> => {
        console.log(credentials)
        return Fetch.cancelableRequest<Array<Chat>>(new URL(chatsUrl), {
          headers: credentials ? { 
            'Authorization': `Bearer ${credentials}`
          } : { },
        })
      },
      getContainers: (credentials?: string): Fetch.Request<Array<DockerContainer>> => {
        console.log(credentials)
        return Fetch.cancelableRequest<Array<DockerContainer>>(new URL(servicesUrl), {
          headers: credentials ? { 
            'Authorization': `Bearer ${credentials}`
          } : { },
        })
      },
      getContainer: (containerId: string, credentials?: string): Fetch.Request<Container> => {
        console.log(credentials)
        return Fetch.cancelableRequest<Container>(new URL(`${servicesUrl}/${containerId}`), {
          headers: credentials ? { 
            'Authorization': `Bearer ${credentials}`
          } : { },
        })
      },
      pingAPI: (credentials?: string): Fetch.Request<string> => {
        return Fetch.cancelableRequest<string>(new URL(pingUrl), {
          headers: credentials ? { 
            'Authorization': `Bearer ${credentials}`
        } : { },
        })
      }
  }
}
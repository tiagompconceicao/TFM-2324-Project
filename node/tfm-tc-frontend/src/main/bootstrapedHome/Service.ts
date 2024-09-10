import {Chat, Prompt} from './Model'
import * as Fetch from '../common/FetchUtils'

/**
 * Contract to be supported by the service used by the TestsPage.
 */
 export interface BootstrapService {
  addPrompt: (prompt:AddPromptModel, credentials?: String) => Fetch.Request<AddPromptOutputModel>
  getChat: (chatId: String, credentials?: String) => Fetch.Request<Array<Prompt>>
  getChats: (credentials?: String) => Fetch.Request<Array<String>>
  pingAPI: (credentials?: String) => Fetch.Request<String>
}

export type PromptsModel = {
  prompt: Array<Prompt>
}

export type ChatsModel = {
    chats: Array<String>
}

export type AddPromptModel = {
    text: String,
    type: String,
    model: String,
    conversationId:String
}

export type AddPromptOutputModel = {
    text: String,
    date: String,
    contents: Array<ModelContent>
}

export type ModelContent = {
    type: String,
    text: String
}

/**
 * An implementation of the tests service.
 * @param url       - the tests resource URL.
 * @returns the newly instantiated service.
 */
export function getService(chatsUrl: URL,pingUrl: URL): BootstrapService {
  return {
    addPrompt: (prompt: AddPromptModel, credentials?: String): Fetch.Request<AddPromptOutputModel> => {
        const headers = new Headers({ 'Content-type' : 'application/json' })
        if (credentials) headers.append('Authorization', `Bearer ${credentials}`)
        return Fetch.cancelableRequest<AddPromptOutputModel>(chatsUrl, {
          method: 'POST',
          headers,
          body: JSON.stringify(prompt)
        })
      },
    getChat: (chatId: String, credentials?: String): Fetch.Request<Array<Prompt>> => {
      return Fetch.cancelableRequest<Array<Prompt>>(new URL(`${chatsUrl}/${chatId}`), {
        headers: credentials ? { 
          'Authorization': `Bearer ${credentials}`
         } : { },
      })
    },
    getChats: (credentials?: String): Fetch.Request<Array<String>> => {
        return Fetch.cancelableRequest<Array<String>>(new URL(chatsUrl), {
          headers: credentials ? { 
            'Authorization': `Bearer ${credentials}`
          } : { },
        })
      },
      pingAPI: (credentials?: String): Fetch.Request<String> => {
        return Fetch.cancelableRequest<String>(new URL(pingUrl), {
          headers: credentials ? { 
            'Authorization': `Bearer ${credentials}`
        } : { },
        })
      }
  }
}
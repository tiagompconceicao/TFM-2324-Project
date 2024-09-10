import { ReactNode, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './Page.css'
import { ChatsModel, PromptsModel, BootstrapService, getService } from './Service'
import * as API from '../common/FetchUtils'
import { getAuth, getIdToken } from "firebase/auth";
import { Prompt } from './Model'
//import 'bootstrap/dist/css/bootstrap.css'


type PageProps = {
  service: BootstrapService
}

type PromptsUpdate = API.Request<Array<String>>
type PromptsInfo = API.FetchInfo<Array<String>>

const Home2 = (props: PageProps) => {
  const navigate = useNavigate()

  const [promptsInfo, setPromptsInfo] = useState<PromptsInfo| undefined>()
  const [currentPromptInfo, setcurrentPromptInfo] = useState<Array<Prompt> | undefined>()
  const [promptsUpdate, setpromptsUpdate] = useState<PromptsUpdate>()
  //const [servicesInfo, setservicesInfo] = useState<ProjectsInfo | undefined>()
  const [uiMode, setuiMode] = useState<number>(1)

  useEffect(() => {

    async function getUserIdToken(){
      const user = getAuth().currentUser
      if (user == null||undefined) throw ("User not found")
      return await getIdToken(user,true)
    }
    async function sendRequest() {
      try {
      setPromptsInfo({ status: API.FetchState.NOT_READY })
      const result: API.Result<Array<String>> = await props.service.getChats(await getUserIdToken()).send()
      console.log("Chats")
      console.log(result)
      
      setPromptsInfo({ 
        status: result.header.ok && result.body ? API.FetchState.SUCCESS : API.FetchState.ERROR,
        result
      })
      }
      catch(reason) {
        setPromptsInfo({ status: API.FetchState.ERROR })
      }
    }

    sendRequest()
  }, [props.service, promptsUpdate])

  async function getUserIdToken(){
    const user = getAuth().currentUser
    if (user == null||undefined) throw ("User not found")
    return await getIdToken(user,true)
  }

  async function getChatMessages(id:String) {
    try {
      const result = await props.service.getChat(id,await getUserIdToken()).send()
      setcurrentPromptInfo(result.body)
      setuiMode(2)
    }
    catch(reason) {
      console.error(reason)
    }
  }

  const onButtonClick = () => {
    navigate('/login');
  }

  const onUsernameClick = () => {
    getAuth().signOut()
    navigate("/login")
  }

  const servicesItems = [
    "Service 1",
    "Service 2",
    "Service 3",
    "Service 3",
    "Service 3",
    "Service 3",
    "Service 3",
    "Service 4"
  ];

  return (
    <div id="root">
        <div className="container-fluid bg-dark min-vh-100 d-flex flex-column justify-content-between">
            <div className="row justify-content-start">
                <div className="col-4">
                    <button className="btn btn-primary btn-lg mt-3 mb-3">New chat</button>
                    <div className="card mb-3">
                        <div className="card-header bg-primary text-white">Previous Prompts</div>
                        <div className="card-body promptsCardDiv" style={{ maxHeight: "165px", overflowY: "auto" }}>
                            <button className="btn btn-secondary mb-2">Service 1</button>
                            <button className="btn btn-secondary mb-2">Service 2</button>
                            <button className="btn btn-secondary mb-2">Service 3</button>
                            <button className="btn btn-secondary mb-2">Service 4</button>
                        </div>
                    </div>
                    <div className="card">
                        <div className="card-header bg-primary text-white">Services</div>
                        <div className="card-body promptsCardDiv text-center" style={{ maxHeight: "165px", overflowY: "auto" }}>
                            <div className="row">
                            <div className="col">
                                <button className="btn btn-secondary mb-2 btn-block" style={{ width: "200px" }}>Service 1</button>
                            </div>
                            <div className="col">
                                <button className="btn btn-secondary mb-2 btn-block" style={{ width: "200px" }}>Service 2</button>
                            </div>
                            <div className="col">
                                <button className="btn btn-secondary mb-2 btn-block" style={{ width: "200px" }}>Service 3</button>
                            </div>
                            <div className="col">
                                <button className="btn btn-secondary mb-2 btn-block" style={{ width: "200px" }}>Service 4</button>
                            </div>
                            </div>
                        </div>
                    </div>
                    <button className="btn btn-primary btn-lg mt-4">Username</button>
                </div>
                <div className="col-md-8 d-flex flex-column align-items-center justify-content-center" style={{ marginLeft: "-150px" }}>
                    <div className="modelPickerDiv">
                        <select name="models" id="models" className="form-control mt-4" style={{ maxWidth: "150px" }}>
                            <option value="gpt-4">ChatGPT-4</option>
                            <option value="claude">Claude</option>
                        </select>
                    </div>
                    <div className="Welcome text-white mt-5 text-center">How can I help you today?</div>
                    <div className="SuggestionsDiv mt-5 d-flex justify-content-center">
                        <div className="SuggestionBigCard1 bg-primary rounded p-4 text-white mr-3">
                            <div className="SuggestionTitleText">Honeypot</div>
                            <div className="SuggestionSmallCards mt-3">
                                <button className="btn btn-outline-light mb-2">Generate a nginx server with CVE- 2022-41741</button>
                            </div>
                        </div>
                        <div className="SuggestionBigCard2 bg-primary rounded p-4 text-white ml-3">
                            <div className="SuggestionTitleText">Honeypot</div>
                            <div className="SuggestionSmallCards mt-3">
                                <button className="btn btn-outline-light mb-2">Generate a nginx server with CVE- 2022-41741</button>
                            </div>
                        </div>
                    </div>
                    <div className="send-prompt-container mt-5 mb-4">
                        <input type="text" className="form-control prompt-input" placeholder="Insert your message here..."/>
                        <div className="input-group-append">
                            <button className="btn btn-primary send-prompt-button">➤</button>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>
  )
}

export default Home2

function renderChatOrSuggestions(mode:number,currentPrompts:Array<Prompt> | undefined): ReactNode{

  let element
  if(mode == 1) {
    element = <>
    <div className='Welcome'>
      How can I help you today ?
    </div>
    {renderSuggestions()}
  </>
  } else element = renderChatDynamic(currentPrompts)

  return (
    element
  )
}

function renderSuggestions(): ReactNode{
  return (
    <div className='SuggestionsDiv'>
    {renderSuggestion(1)}
    {renderSuggestion(2)}
    </div>
  )
}

function renderSuggestion(type:number): ReactNode{

  const className: string = type == 1 ? "SuggestionBigCard1" : "SuggestionBigCard2"
  return (
    <div className={className}>
  <div className="SuggestionTitleText">
      Honeypot
    </div>
  <div className="SuggestionSmallCards">
  <button className="SuggestionSmallCardButton">
    Generate a nginx server with CVE-
2022-41741
  </button>  
  <button className="SuggestionSmallCardButton">
    Generate a nginx server with CVE-
2022-41741
  </button>  
  <button className="SuggestionSmallCardButton">
    Generate a nginx server with CVE-
2022-41741
  </button>  
  </div>
</div>
  )
}


function renderChatDynamic(items: Array<Prompt>| undefined){
  if(items)
  return (
    <div className="chat-container">
      {items.map((prompt) => (
          <div className="chat-message chat-sender">
            <div className="chat-message-info">
            <div className="chat-icon">{prompt.author?"👤":"🖥️"}</div>
            <div className="chat-username">{prompt.author?"User":"Model"}</div>
          </div>
          <div className="chat-message-content">
            <p>{prompt.text}</p>
          </div>
          </div>))
      }
    </div>
  )
}

function renderPromptsSidebarBox(mode:boolean, items: PromptsInfo | undefined, callback: (id:String) => void): ReactNode{
  
  console.log("Chats again")
  console.log(items)
  let list: String[] = []
  if(items !== undefined && items.result?.body){
    let chatList = items.result?.body
    chatList.map((chat) => {
    return list.push(chat)
  })
  }

  console.log("Chats finally")
  console.log(list)
  return (
    <div className="card">
      <div className="previousPromptsTitle">
        Previous Prompts
      </div>
      <div className="promptsCardDiv" style={{ overflowY: 'auto', maxHeight: '165px' }}>
        {list.map((item, index) => (
          <button key={index} className="promptCard" onClick={() => {callback(item)}}>
              {item}
          </button>
        ))}
      </div>
    </div>
  )
}

function renderSidebarBox(mode:boolean, items:  string[]): ReactNode{
  return (
    <div className="card">
      <div className="previousPromptsTitle">
        {mode ? "Previous Prompts" : "Services"}
      </div>
      <div className="promptsCardDiv" style={{ overflowY: 'auto', maxHeight: '165px' }}>
        {items.map((item, index) => (
          <button key={index} className="promptCard">
              {item}
          </button>
        ))}
      </div>
    </div>
  )
}

export function createService(chatsUrl: URL,pingUrl: URL): BootstrapService {
  return getService(chatsUrl,pingUrl)
}
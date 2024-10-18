import { Children, ReactNode, SetStateAction, useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import './Page.css'
import { AddPromptModel, Service, getService } from './Service'
import * as API from '../common/FetchUtils'
import { getAuth, getIdToken } from "firebase/auth";
import { Chat, Container, ContainerAction, CurrentPromptInfo, DockerContainer, Prompt } from './Model'
import ReactMarkdown from 'react-markdown';
import hljs from 'highlight.js';

import 'highlight.js/styles/github.css'; // Choose your preferred theme here

type PageProps = {
  service: Service
}

type PromptsUpdate = API.Request<Array<Chat>>
type PromptsInfo = API.FetchInfo<Array<Chat>>

type ContainersUpdate = API.Request<Array<DockerContainer>>
type ContainersInfo = API.FetchInfo<Array<DockerContainer>>

const Home = (props: PageProps) => {
  const navigate = useNavigate()

  //List of prompts state
  const [promptsInfo, setPromptsInfo] = useState<PromptsInfo| undefined>()

  //Selected prompt state
  const [currentPromptInfo, setCurrentPromptInfo] = useState<CurrentPromptInfo | undefined>()

  //List of Containers state
  const [containersInfo, setContainersInfo] = useState<ContainersInfo| undefined>()

  //Selected container state
  const [currentContainerInfo, setCurrentContainerInfo] = useState<Container | undefined>()

  //States for updates purposes
  const [promptsUpdate, setpromptsUpdate] = useState<PromptsUpdate>()
  const [containersUpdate, setContainersUpdate] = useState<ContainersUpdate>()

  //User input in text view
  const [inputValue, setInputValue] = useState('');

  //LLM picker
  const [selectedModel, setSelectedModel] = useState<string>('gpt-4');

  //Button states to distinguish which prompt or container is selected
  const [selectedButtonPrompts, setSelectedButtonPrompts] = useState<string>()
  const [selectedButtonServices, setSelectedButtonServices] = useState<string>()


  const [uiMode, setuiMode] = useState<number>(1)

  //Mechanism to update list of prompts and container each refresh
  useEffect(() => {

    async function getUserIdToken(){
      const user = getAuth().currentUser
      if (user == null||undefined) throw ("User not found")
      return await getIdToken(user,true)
    }
    async function sendChatsRequest() {
      try {
      setPromptsInfo({ status: API.FetchState.NOT_READY })
      const result: API.Result<Array<Chat>> = await props.service.getChats(await getUserIdToken()).send()
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

    async function sendContainersRequest() {
      try {
        setContainersInfo({ status: API.FetchState.NOT_READY })
      const result: API.Result<Array<DockerContainer>> = await props.service.getContainers(await getUserIdToken()).send()
      
      setContainersInfo({ 
        status: result.header.ok && result.body ? API.FetchState.SUCCESS : API.FetchState.ERROR,
        result
      })
      }
      catch(reason) {
        setContainersInfo({ status: API.FetchState.ERROR })
      }
    }

    sendChatsRequest()
    sendContainersRequest()
  }, [props.service, promptsUpdate,containersUpdate])

  //Access firebase user idToken
  async function getUserIdToken(){
    const user = getAuth().currentUser
    if (user == null||undefined) throw ("User not found")
    return await getIdToken(user,true)
  }

  //Obtain user chat messages
  async function getChatMessages(id:string) {
    try {
      const result = await props.service.getChat(id,await getUserIdToken()).send()
      if(result.body)
      setCurrentPromptInfo({id: id, prompts:result.body})
      setuiMode(2)
    }
    catch(reason) {
      console.error(reason)
    }
  }



  //Sends the prompt to Backend and updates the currentPrompt state
  async function sendChatMessage(message:string){
    let prompt: AddPromptModel = {
      text : message,
      type : "Other",
      model : selectedModel,
      chatId: currentPromptInfo?.id
    }

    setInputValue('')

    let currentDate = getCurrentDate()

    let userPrompt: Prompt = {
      text: prompt.text,
      author: true,
      date: currentDate
    }
  
    const currentUiMode = uiMode
    let chat: Chat

    chat = {
      id:'',
      owner:"",
      date:currentDate,
      description:userPrompt.text
    } 

    if(currentUiMode === 1) {
      setCurrentPromptInfo({id: "tempChatId" , prompts:[userPrompt]})
      setuiMode(2)

      promptsInfo?.result?.body?.unshift(chat)
      setPromptsInfo(promptsInfo)
    } else {
      currentPromptInfo?.prompts.push(userPrompt)
      setCurrentPromptInfo(currentPromptInfo)
    }

    let modelPrompt: Prompt = {
      text: "**Loading**",
      author: false,
      date: currentDate
    }

    if(currentPromptInfo !== undefined) {
      const prompts = currentPromptInfo.prompts
      prompts.push(modelPrompt)
      setCurrentPromptInfo({id:prompt.chatId?prompt.chatId:"tempChatId", prompts:prompts})
    } else {
      const prompts = [userPrompt]
      prompts.push(modelPrompt)
      setCurrentPromptInfo({id:prompt.chatId?prompt.chatId:"tempChatId", prompts:prompts})
    }

    const result = await props.service.addPrompt(prompt, await getUserIdToken()).send()
    
    const modelAnswer = result.body?.contents[0].text
      const newId = result.body?.id
      modelPrompt = {
        text: modelAnswer ? modelAnswer: "No answer available",
        author: false,
        date:getCurrentDate()
      }

      if(currentUiMode === 1) {
        chat.id = newId?newId:""
        promptsInfo?.result?.body?.shift()
        promptsInfo?.result?.body?.unshift(chat)
        setPromptsInfo(promptsInfo)
      }


      //PROBLEMS HERE
      if(currentPromptInfo !== undefined && newId !== undefined) {
        const prompts = currentPromptInfo.prompts
        if(prompts.at(prompts.length - 1) !== undefined){
          prompts[prompts.length - 1].text = modelPrompt.text
          prompts[prompts.length - 1].date = modelPrompt.date
        }
        setCurrentPromptInfo({id:newId, prompts:prompts})
      } else {
        const prompts = [userPrompt]
        prompts.push(modelPrompt)
        setCurrentPromptInfo({id:newId?newId:"tempChatId", prompts:prompts})
      }
  }

  //Opens new chat menos, unselecteds any pressed button
  async function newChat(){
    setSelectedButtonPrompts(undefined)
    setSelectedButtonServices(undefined)
    setuiMode(1)
    setCurrentPromptInfo(undefined)
  }

  //Logout
  const onLogoutClick = () => {
    getAuth().signOut()
    navigate("/login")
  }

  //Text view input listener 
  const handleInputChange = (event: React.ChangeEvent<HTMLInputElement>) => {
    setInputValue(event.target.value);
  };

  //Enter button pressed handler in the text view
  const handleInputPressEnter = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === 'Enter') {
      handleSendText()
    }
  };

  //Method to send user´s message to backend
  const handleSendText = () => {
    sendChatMessage(inputValue)
  }

  //LLM picker
  const handlePickerChange = (event: React.ChangeEvent<HTMLSelectElement>) => {
    setSelectedModel(event.target.value);
  };

  //Method to send default message to backend
  const createChatThroughSuggestion = (message: string) => {
    sendChatMessage(message)
  }

  //Create container
  const createAndRenderContainer = (dockerfile:string|undefined) => {
    if(dockerfile !== undefined)
    createContainer(dockerfile)
  }


  const selectChat = (chatId: string | undefined) => {
    setSelectedButtonPrompts(chatId)
    setSelectedButtonServices(undefined)
  }

  const selectContainer = (containerId: string | undefined) => {
    setSelectedButtonServices(containerId)
    setSelectedButtonPrompts(undefined)
  }

  const startOrStopContainer = (action:ContainerAction,containerId: string | undefined) => {
    if(containerId !== undefined)
    if(action == ContainerAction.Start) {
      startContainer(containerId)
    } else {
      stopContainer(containerId)
    }
  }

  async function startContainer(id:string) {
    try {
      const result = await props.service.startContainer(id,await getUserIdToken()).send()
      changeContainerState(id,ContainerAction.Start)

    }
    catch(reason) {
      console.error(reason)
    }
  };

  async function stopContainer(id:string) {
    try {
      const result = await props.service.stopContainer(id,await getUserIdToken()).send()
      changeContainerState(id,ContainerAction.Stop)
    }
    catch(reason) {
      console.error(reason)
    }
  };

  //Method to automatically update visually the state of a selected container
  async function changeContainerState(id:string,state:ContainerAction){
    let currentContainer = JSON.parse(JSON.stringify(currentContainerInfo));
    let containersList = JSON.parse(JSON.stringify(containersInfo));
    
    if(currentContainer !== undefined && currentContainer.state !== undefined && containersInfo?.result?.body !== undefined){

      const index = containersInfo?.result?.body.findIndex(item => item.id === id);
      

      if (state == ContainerAction.Start){
      
        currentContainer.state.status = "running"
        containersList.result.body[index].state = "running"
      } else {
        currentContainer.state.status = "exited"
        containersList.result.body[index].state = "exited"
      }

      setContainersInfo(containersList)
      setCurrentContainerInfo(currentContainer)
    }
  }

  //Obtain containers from the backend
  async function getContainers(){
    const result: API.Result<Array<DockerContainer>> = await props.service.getContainers(await getUserIdToken()).send()
      
      setContainersInfo({ 
        status: result.header.ok && result.body ? API.FetchState.SUCCESS : API.FetchState.ERROR,
        result
      })
  }


  //Create container in the backend
  async function createContainer(dockerfile:string) {
    try {
      let chatId = currentPromptInfo?.id
      if(chatId == undefined) {
        throw "Chat not selected"
      }
      const result = await props.service.createContainer(dockerfile,chatId,await getUserIdToken()).send()
      if(result.body){
        getContainerInfo(result.body?.id)
        getContainers()
        selectContainer(result.body?.id)
        
      }
      
    }
    catch(reason) {
      console.error(reason)
    }
  }
  
  async function getContainerInfo(id:string) {
    
    try {
      const result = await props.service.getContainer(id,await getUserIdToken()).send()
      if(result.body){
        setCurrentContainerInfo(result.body)
        setuiMode(3)
      }
      
    }
    catch(reason) {
      console.error(reason)
    }
  };



  return (
    <div className="container" >
      <div>
        <button className="newChatButton" style={{ fontFamily: 'Poppins, sans-serif'}} onClick={newChat}>
          New chat
        </button>
      {renderSidebarBox(promptsInfo, getChatMessages,selectedButtonPrompts,selectChat)}
      {renderSidebarBox(containersInfo,getContainerInfo,selectedButtonServices,selectContainer)}
      
      <button className="userButton" onClick={onLogoutClick} style={{ fontFamily: 'Poppins, sans-serif'}}>
        Log out
      </button>
    </div>
    <div className="content">
      <div className='modelPickerDiv'>
        <select name="models" id="models" className="modelPicker" style={{ fontFamily: 'Poppins, sans-serif'}} value={selectedModel} 
        onChange={handlePickerChange}>
        <option value="gpt-4">GPT-4</option>
        <option value="claude">Claude</option>
        </select>
      </div>
      {renderChatOrSuggestions(uiMode,currentPromptInfo,currentContainerInfo,createChatThroughSuggestion,createAndRenderContainer,startOrStopContainer)}
      {uiMode !== 3 ? <div className="send-prompt-container">
        <input onKeyDown={handleInputPressEnter} onChange={handleInputChange} value={inputValue} type="text" className="prompt-input" placeholder="Insert your message here..." style={{ fontFamily: 'Poppins, sans-serif'}}/>
        <div onClick={handleSendText} className="send-prompt-button">&#10148;</div>
      </div>:<></>}
    </div>
  </div>
  )
}

export default Home

function renderChatOrSuggestions(mode:number,currentPrompts:CurrentPromptInfo | undefined,currentContainer: Container | undefined,createChatThroughSuggestion: (message: string) => void,createContainer: (dockerfile: string | undefined) => void,containerButtonCalback: (action:ContainerAction,containerId:string|undefined) => void): ReactNode{

  let element
  if(mode === 1) {
    element = <>
    <div className='Welcome' style={{ fontFamily: 'Poppins, sans-serif'}}>
      How can I help you today ?
    </div>
    {renderSuggestions(createChatThroughSuggestion)}
  </>
  } else if(mode === 2){
    element = renderChatDynamic(currentPrompts?.prompts,createContainer)
  } else {
    element = renderContainerTab(currentContainer,containerButtonCalback)
  }

  return (
    element
  )
}

//TODO: More advanced network management, the localhost address should not be hardcoded
function renderContainerTab(currentContainer: Container | undefined,buttonCalback: (action:ContainerAction,containerId:string|undefined) => void): ReactNode{
  
  let hrefClassName = "behind-left-text"
  if(currentContainer?.state.status !== "running") {
    hrefClassName = "behind-left-text disabled-link"
  }
  let portBinding = undefined
  let containerUrl = ""
  if(currentContainer?.hostConfig?.portBindings !== undefined && currentContainer?.hostConfig?.portBindings["80/tcp"] !== undefined)
  portBinding = currentContainer.hostConfig.portBindings["80/tcp"]
  if(portBinding !== undefined)
  containerUrl = portBinding[0].HostIp?portBinding[0].HostIp:"http://localhost" + ":" + portBinding[0].HostPort
  return (
    <div className='ContainerDiv'>
      <div className="ContainerBigCard">
        <div style={{ fontFamily: 'Poppins, sans-serif'}}>
          <div className="container-div">
            <div className="left-div">
              <div className="left-span-container">
                <span className="behind-left-text">Container name:</span>
                <span className="left-span">{currentContainer?.name}</span>
              </div>
              <div className="left-span-container">
                <span className="behind-left-text">Container Id:</span>
                <span className="left-span">{currentContainer?.id}</span>
              </div>
              <div className="left-span-container">
                <span className="behind-left-text">Image Id:</span>
                <span className="left-span">{currentContainer?.image}</span>
              </div>
              <div className="left-span-container">
                <span className="behind-left-text">Access Container:</span>
                {portBinding!==undefined && portBinding?.length!==0 && portBinding[0].HostPort !== undefined ?
                <a className={hrefClassName} href={containerUrl}>{containerUrl}</a>
                :
                <span className="behind-left-text">Container not accessible currently</span>
                }
              </div>
            </div>
            <div className="status-div">
              <div className="status-text-group">
                <span className="status-text status-bold">Status</span>
                <span className="status-text">{currentContainer?.state.status}</span>
              </div>
              <div className="button-group">
                <button className="status-button status-button-stop" onClick={() => {
                  buttonCalback(ContainerAction.Stop,currentContainer?.id)
                }}>◼️</button>
                <button className="status-button status-button-start" onClick={() => {
                  buttonCalback(ContainerAction.Start,currentContainer?.id)
                }}>▶️</button>
              </div>
            </div>
          </div>
          <div className="container-div" style={{ borderTop: '1px solid #706c82' }}>
            <div className="left-div">
              <div className="left-span-container">
                <span className="behind-left-text">Logs:</span>
                <span className="status-text">Disabled</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  )
}

//Render default messages
function renderSuggestions(createChatThroughSuggestion: (message: string) => void): ReactNode{
  return (
    <div className='SuggestionsDiv'>
    {renderSuggestion(1,createChatThroughSuggestion)}
    {renderSuggestion(2,createChatThroughSuggestion)}
    </div>
  )
}

function renderSuggestion(type:number, createChatThroughSuggestion: (message: string) => void): ReactNode {
    let suggestion1 = ""
    let suggestion2 = ""
    let suggestion3 = ""
    let title = ""
  if( type === 1) {
    suggestion1 = "Generate a nginx server with CVE-2022-41741"
    suggestion2 = "Generate a nginx server with CVE-2021-23017"
    suggestion3 = "Generate a nginx server with CVE-2019-9511"
    title = "Honeypot"
  } else {
    suggestion1 = "Generate a CTF about cracking a hash password"
    suggestion2 = "Generate a CTF about Android vulnerabilities"
    suggestion3 = "Generate a CTF"
    title = "CTF"
  }
  

  const className: string = type === 1 ? "SuggestionBigCard1" : "SuggestionBigCard2"
  return (
    <div className={className}>
  <div className="SuggestionTitleText" style={{ fontFamily: 'Poppins, sans-serif'}}>
    {title}
    </div>
  <div className="SuggestionSmallCards">
  <button className="SuggestionSmallCardButton" style={{ fontFamily: 'Poppins, sans-serif'}} onClick={() => {createChatThroughSuggestion(suggestion1)}}>
    {suggestion1}
  </button>  
  <button className="SuggestionSmallCardButton" style={{ fontFamily: 'Poppins, sans-serif'}} onClick={() => {createChatThroughSuggestion(suggestion2)}}>
    {suggestion2}
  </button>  
  <button className="SuggestionSmallCardButton" style={{ fontFamily: 'Poppins, sans-serif'}} onClick={() => {createChatThroughSuggestion(suggestion3)}}>
    {suggestion3}
  </button>  
  </div>
</div>
  )
}

//Method used to render chat messages, it can render code snippets, dockerfiles and other specific representations.
function renderChatDynamic(items: Array<Prompt> | undefined, launchContainerCallback: (dockerfile:string | undefined) => void) {
  if (items)
    return (
      <div className="chat-outer-container">
        <div className="chat-container">
          {items.map((prompt, index) => (
            <div className="chat-message chat-sender" key={index}>
              <div className="chat-message-info">
                <div className="chat-icon">{prompt.author ? "👤" : "🖥️"}</div>
                <div className="chat-username">{prompt.author ? "User" : "Model"}</div>
              </div>
              {prompt.text === "**Loading**" ? 
              <div className="loader-container">
                  <div className="bouncing-dots">
                      <div className="dot"></div>
                      <div className="dot"></div>
                      <div className="dot"></div>
                  </div>
              </div> : 
              <div className="chat-message-content">
                <ReactMarkdown components={{
                  p: ({ node, ...props }) => (
                    <div className="markdown-paragraph" {...props} />
                  ),
                  code: ({ node, className, children, ...props }) => {
                    const match = /language-(\w+)/.exec(className || '');
                    let language = match ? match[1] : 'markdown';
                    const keywords = ['FROM', 'EXPOSE'];
                    if(children && typeof children === 'string' && keywords.every(keyword => children.includes(keyword))){
                      language = 'dockerfile'
                    }
                    const highlightedCode = children && typeof children === 'string' ? hljs.highlight(language, children).value : '';
                    return (
                      <pre className={`language-${language} markdown-code-div`}>
                        <div className='markdown-title-div'>
                          <p className='markdown-title'>
                            {language}
                          </p>
                          {
                            language === 'dockerfile' && children !== undefined ? <button className='launch-service-button' onClick={()=>{
                              launchContainerCallback(children?.toString())
                            }}>🚀 Launch service</button> : <></>
                          }
                        </div>
                        <div className='markdown-code-inner-div'>
                        <code className={`language-${language} markdown-code-paragraph`} {...props} dangerouslySetInnerHTML={{ __html: highlightedCode }} />
                        </div>
                      </pre>
                    );
                  },
                }}>
                  {prompt.text}
                </ReactMarkdown>
              </div>}
              
            </div>
          ))}
        </div>
      </div>
    );
}


//Similar to loading chat messages or new chat 
function renderSidebarBox(
  items: ContainersInfo | PromptsInfo | undefined, 
  callback: (id: string) => void,
  selectedButton: string|undefined, 
  setSelectedButton: (id: string) => void): ReactNode {

  let list:DockerContainer[]|Chat[] = [];
  if (items && 'result' in items && items.result?.body) {
    list = items.result.body;
  }

  return (
    <div className="card">
      <div className="previousPromptsTitle" style={{ fontFamily: 'Poppins, sans-serif' }}>
        {isChat(list[0]) ? "Previous Prompts": "Services"} 
      </div>
      {items?.status === API.FetchState.NOT_READY && <div className="loading-animation"></div>}
      {items?.status !== API.FetchState.NOT_READY && 
      <div className="promptsCardDiv" style={{ overflowY: 'auto', maxHeight: '165px' }}>
        {list.map((item, index) => (
          <>
          <button key={index} className={`promptCard ${ selectedButton !== undefined && selectedButton === ((item as Chat).id||(item as DockerContainer).id) ? "promptCard-selected" : ""}`} onClick={() => { 
            sidebarBoxCallback(item.id,callback,setSelectedButton)
            }} 
            style={{ fontFamily: 'Poppins, sans-serif', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            { 
              isChat(item) ? 
              <><span>{((item as Chat).description.substring(0, 26))}</span></> : 
              <>
              <span>{(item as DockerContainer).id.substring(0, 20)}</span>
              <div style={{ width: '10px', height: '10px', borderRadius: '50%', backgroundColor: getStatusColor(typeof item === 'string' ? '':     (item as DockerContainer).state) }}>
              </div>
              </>
            }
          </button>
          </>
        ))}
      </div>}
      
    </div>
  );
}


function sidebarBoxCallback(buttonId: string,callback: (id: string) => void, setSelectedButton: (id: string) => void){
setSelectedButton(buttonId)
callback(buttonId)
}


function isChat(item: Chat | DockerContainer){
  if(item !== undefined)
  return 'date' in item && 'description' in item && 'owner' in item
}

function getStatusColor(status: string): string {
  switch (status.toLowerCase()) {
    case 'running':
      return 'green';
    case 'exited':
      return 'red';
    case 'paused':
      return 'yellow';
    default:
      return 'grey';
  }
}

function getCurrentDate(): string {
  const date = new Date();

  // Get the timezone offset in minutes and convert it to hours and minutes
  const timezoneOffset = date.getTimezoneOffset();
  const offsetHours = String(Math.floor(Math.abs(timezoneOffset) / 60)).padStart(2, '0');
  const offsetMinutes = String(Math.abs(timezoneOffset) % 60).padStart(2, '0');
  const offsetSign = timezoneOffset > 0 ? '-' : '+';

  // Format the date as YYYY-MM-DDTHH:mm:ss.sss±hh:mm
  const formattedDate = date.getFullYear() +
      '-' + String(date.getMonth() + 1).padStart(2, '0') +
      '-' + String(date.getDate()).padStart(2, '0') +
      'T' + String(date.getHours()).padStart(2, '0') +
      ':' + String(date.getMinutes()).padStart(2, '0') +
      ':' + String(date.getSeconds()).padStart(2, '0') +
      '.' + String(date.getMilliseconds()).padStart(3, '0') +
      offsetSign + offsetHours + ':' + offsetMinutes;

  return formattedDate;
}

export function createService(chatsUrl: URL,servicesUrl:URL,pingUrl: URL): Service {
  return getService(chatsUrl,servicesUrl,pingUrl)
}